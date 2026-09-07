package com.github.asmln.fintech_app.balance_viewer.service;

import com.github.asmln.fintech_app.balance_viewer.dto.BalanceDto;
import com.github.asmln.fintech_app.balance_viewer.entity.Transaction;
import com.github.asmln.fintech_app.balance_viewer.entity.UserBalance;
import com.github.asmln.fintech_app.balance_viewer.repository.TransactionRepository;
import com.github.asmln.fintech_app.balance_viewer.repository.UserBalanceRepository;
import java.math.BigDecimal;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserBalanceService {
    private static final Logger log = LoggerFactory.getLogger(UserBalanceService.class);
    private final TransactionRepository transactionRepository;
    private final UserBalanceRepository userBalanceRepository;

    public UserBalanceService(TransactionRepository transactionRepository, UserBalanceRepository userBalanceRepository) {
        this.transactionRepository = transactionRepository;
        this.userBalanceRepository = userBalanceRepository;
    }

    @Transactional
    public void updateBalance(UUID userId, UUID transactionId, BigDecimal balance) {
        userBalanceRepository.upsertBalance(userId, balance);
        transactionRepository.save(new Transaction(transactionId));
    }

    public Page<BalanceDto> obtainBalancesPage(Pageable pageable) {
        Page<UserBalance> balancesPage = userBalanceRepository.findAll(pageable);
        return balancesPage.map(b -> new BalanceDto(b.getUserId(), b.getBalance(), b.getUpdatedAt()));
    }
}
