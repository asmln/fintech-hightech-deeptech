package com.github.asmln.fintech_app.tx_processor.repository;

import com.github.asmln.fintech_app.tx_processor.entity.Transaction;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;

public interface TransactionRepository extends CrudRepository<Transaction, UUID> {
    Optional<Transaction> findByExternalId(UUID externalId);
}
