package com.github.asmln.fintech_app.tx_processor.repository;

import com.github.asmln.fintech_app.tx_processor.entity.OutboxTransactionEvent;
import java.util.UUID;
import org.springframework.data.repository.Repository;

public interface TransactionOutboxRepository
        extends
                Repository<OutboxTransactionEvent, UUID>
{
    OutboxTransactionEvent save(OutboxTransactionEvent entity);
}
