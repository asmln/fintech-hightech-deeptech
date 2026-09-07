package com.github.asmln.fintech_app.balance_viewer.repository;

import com.github.asmln.fintech_app.balance_viewer.entity.Transaction;
import java.util.UUID;
import org.springframework.data.repository.Repository;

public interface TransactionRepository extends Repository<Transaction, UUID> {
    Transaction save(Transaction tx);
}
