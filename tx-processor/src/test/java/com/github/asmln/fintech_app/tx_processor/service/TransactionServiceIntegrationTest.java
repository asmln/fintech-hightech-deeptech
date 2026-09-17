package com.github.asmln.fintech_app.tx_processor.service;

import static org.junit.jupiter.api.Assertions.*;

import com.github.asmln.fintech_app.domain.TransactionType;
import com.github.asmln.fintech_app.tx_processor.dto.TransactionRequest;
import com.github.asmln.fintech_app.tx_processor.dto.TransactionStatus;
import com.github.asmln.fintech_app.tx_processor.dto.TransactionalDuplicateException;
import com.github.asmln.fintech_app.tx_processor.repository.TransactionRepository;
import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Testcontainers
class TransactionServiceIntegrationTest {
    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:18.6");
    @Autowired
    private TransactionService transactionService;
    @Autowired
    private TransactionRepository transactionRepository;
    private final UUID externalId = UUID.randomUUID();
    private final UUID userId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        // Очищаем таблицу транзакций перед каждым тестом
        transactionRepository.deleteAll();
    }

    @Test
    void shouldSaveTransactionSuccessfully() {
        var transactionRequest =
                new TransactionRequest(userId, externalId, new BigDecimal("100.00"), TransactionType.DEPOSIT);
        var transactionResponse = transactionService.saveTransaction(transactionRequest);
        assertEquals(transactionRequest.userId(), transactionResponse.userId());
        assertEquals(transactionRequest.externalId(), transactionResponse.externalId());
        assertFalse(transactionResponse.duplicate());
        assertEquals(TransactionStatus.ACCEPTED, transactionResponse.status());
    }

    @Test
    void shouldSaveTwoTransactionsSuccessfully() {
        var transactionRequest =
                new TransactionRequest(userId, externalId, new BigDecimal("100.00"), TransactionType.DEPOSIT);
        var transactionResponse = transactionService.saveTransaction(transactionRequest);
        assertEquals(transactionRequest.userId(), transactionResponse.userId());
        assertEquals(transactionRequest.externalId(), transactionResponse.externalId());
        assertFalse(transactionResponse.duplicate());
        assertEquals(TransactionStatus.ACCEPTED, transactionResponse.status());
        var transactionRequest2 =
                new TransactionRequest(userId, UUID.randomUUID(), new BigDecimal("50.00"), TransactionType.WITHDRAWAL);
        var transactionResponse2 = transactionService.saveTransaction(transactionRequest2);
        assertEquals(transactionRequest2.userId(), transactionResponse2.userId());
        assertEquals(transactionRequest2.externalId(), transactionResponse2.externalId());
        assertFalse(transactionResponse2.duplicate());
        assertEquals(TransactionStatus.ACCEPTED, transactionResponse2.status());
        // Проверяем, что в базе 2 транзакции
        assertEquals(2L, transactionRepository.count());
    }

    @Test
    void shouldReceiveSuccessTransactionResponseWhenDuplicate() {
        var transactionRequest =
                new TransactionRequest(userId, externalId, new BigDecimal("100.00"), TransactionType.DEPOSIT);
        transactionService.saveTransaction(transactionRequest);
        assertEquals(1L, transactionRepository.count());
        var transactionResponse = transactionService.saveTransaction(transactionRequest);
        assertEquals(transactionRequest.userId(), transactionResponse.userId());
        assertEquals(transactionRequest.externalId(), transactionResponse.externalId());
        assertTrue(transactionResponse.duplicate());
        assertEquals(TransactionStatus.ACCEPTED, transactionResponse.status());
        // Проверяем, что в базе не появилось дополнительных транзакций
        assertEquals(1L, transactionRepository.count());
    }

    @Test
    void shouldThrowExceptionWhenDifferentUser() {
        var transactionRequest =
                new TransactionRequest(userId, externalId, new BigDecimal("100.00"), TransactionType.DEPOSIT);
        transactionService.saveTransaction(transactionRequest);
        assertEquals(1L, transactionRepository.count());
        var badTransactionRequest =
                new TransactionRequest(UUID.randomUUID(), externalId, new BigDecimal("100.00"), TransactionType.DEPOSIT);
        assertThrows(TransactionalDuplicateException.class, () -> transactionService.saveTransaction(
                badTransactionRequest
        ));
        // Проверяем, что в базе не появилось дополнительных транзакций
        assertEquals(1L, transactionRepository.count());
    }

    @Test
    void shouldThrowExceptionWhenDifferentTransactionType() {
        var transactionRequest =
                new TransactionRequest(userId, externalId, new BigDecimal("100.00"), TransactionType.DEPOSIT);
        transactionService.saveTransaction(transactionRequest);
        assertEquals(1L, transactionRepository.count());
        var badTransactionRequest =
                new TransactionRequest(userId, externalId, new BigDecimal("100.00"), TransactionType.WITHDRAWAL);
        assertThrows(TransactionalDuplicateException.class, () -> transactionService.saveTransaction(
                badTransactionRequest
        ));
        // Проверяем, что в базе не появилось дополнительных транзакций
        assertEquals(1L, transactionRepository.count());
    }

    @Test
    void shouldThrowExceptionWhenDifferentAmount() {
        var transactionRequest =
                new TransactionRequest(userId, externalId, new BigDecimal("100.00"), TransactionType.DEPOSIT);
        transactionService.saveTransaction(transactionRequest);
        assertEquals(1L, transactionRepository.count());
        var badTransactionRequest =
                new TransactionRequest(userId, externalId, new BigDecimal("200.00"), TransactionType.DEPOSIT);
        assertThrows(TransactionalDuplicateException.class, () -> transactionService.saveTransaction(
                badTransactionRequest
        ));
        // Проверяем, что в базе не появилось дополнительных транзакций
        assertEquals(1L, transactionRepository.count());
    }
}
