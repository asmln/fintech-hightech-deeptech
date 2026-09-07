package com.github.asmln.fintech_app.tx_processor.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.asmln.fintech_app.domain.TransactionEvent;
import com.github.asmln.fintech_app.domain.TransactionType;
import com.github.asmln.fintech_app.tx_processor.dto.TransactionRequest;
import com.github.asmln.fintech_app.tx_processor.dto.TransactionResponse;
import com.github.asmln.fintech_app.tx_processor.dto.TransactionalDuplicateException;
import com.github.asmln.fintech_app.tx_processor.entity.OutboxTransactionEvent;
import com.github.asmln.fintech_app.tx_processor.entity.Transaction;
import com.github.asmln.fintech_app.tx_processor.repository.TransactionOutboxRepository;
import com.github.asmln.fintech_app.tx_processor.repository.TransactionRepository;
import com.github.asmln.fintech_app.tx_processor.repository.UserBalanceRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransactionService {
    private static final Logger log = LoggerFactory.getLogger(TransactionService.class);
    private final TransactionRepository transactionRepository;
    private final UserBalanceRepository userBalanceRepository;
    private final TransactionOutboxRepository transactionOutboxRepository;
    private final ObjectMapper objectMapper;

    public TransactionService(
            TransactionRepository transactionRepository,
            UserBalanceRepository userBalanceRepository,
            TransactionOutboxRepository transactionOutboxRepository,
            ObjectMapper objectMapper
    ) {
        this.transactionRepository = transactionRepository;
        this.userBalanceRepository = userBalanceRepository;
        this.transactionOutboxRepository = transactionOutboxRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public TransactionResponse saveTransaction(TransactionRequest transactionRequest) {
        Optional<Transaction> txOptional = transactionRepository.findByExternalId(transactionRequest.externalId());
        if (txOptional.isPresent()) {
            return duplicateOkOrExceptionThrow(transactionRequest, txOptional.get());
        }
        var transaction = new Transaction(
                transactionRequest.userId(),
                transactionRequest.externalId(),
                transactionRequest.amount(),
                transactionRequest.type(),
                Instant.now()
        );
        transaction = transactionRepository.save(transaction);
        log.info("Сохранена транзакция UUID {}", transaction.getId());
        var balance = saveBalance(transaction);
        saveOutbox(transaction, balance);
        return new TransactionResponse(
                "ACCEPTED",
                "Транзакция успешно принята в обработку",
                transaction.getExternalId(),
                transaction.getUserId(),
                Instant.now()
        );
    }

    private TransactionResponse duplicateOkOrExceptionThrow(TransactionRequest transactionRequest, Transaction tx) {
        if (!tx.getUserId().equals(transactionRequest.userId())) {
            log.error(
                    "Транзакция UUID {} пользователя {} приходила от другого пользователя {}",
                    tx.getId(),
                    transactionRequest.userId(),
                    tx.getUserId()
            );
            throw new TransactionalDuplicateException("Транзакция с таким UUID приходила от другого пользователя");
        }
        if (!tx.getType().equals(transactionRequest.type())) {
            log.error(
                    "Транзакция UUID {} с типом {} приходила с другим типом {}",
                    tx.getId(),
                    transactionRequest.type(),
                    tx.getType()
            );
            throw new TransactionalDuplicateException("Транзакция с таким UUID приходила с другим типом");
        }
        if (!tx.getAmount().equals(transactionRequest.amount())) {
            log.error(
                    "Транзакция UUID {} с суммой {} приходила с другой суммой {}",
                    tx.getId(),
                    transactionRequest.amount(),
                    tx.getAmount()
            );
            throw new TransactionalDuplicateException("Транзакция с таким UUID приходила с другой суммой");
        }
        log.info("Транзакция UUID {} уже приходила", tx.getId());
        return new TransactionResponse(
                "ACCEPTED",
                "Транзакция успешно принята в обработку",
                tx.getExternalId(),
                tx.getUserId(),
                Instant.now(),
                true
        );
    }

    private BigDecimal saveBalance(Transaction transaction) {
        var amount = TransactionType.DEPOSIT == transaction.getType()
                ? transaction.getAmount()
                : transaction.getAmount().negate();
        return userBalanceRepository.upsertBalance(transaction.getUserId(), amount);
    }

    private void saveOutbox(Transaction transaction, BigDecimal balance) {
        var event = new OutboxTransactionEvent();
        event.setTransactionId(transaction.getId());
        event.setUserId(transaction.getUserId());
        event.setSent(false);
        var txEvent = new TransactionEvent(
                transaction.getId(),
                transaction.getExternalId(),
                transaction.getUserId(),
                transaction.getAmount(),
                balance,
                transaction.getType(),
                transaction.getCreatedAt()
        );
        event.setPayload(objectMapper.valueToTree(txEvent));
        transactionOutboxRepository.save(event);
    }
}
