package com.github.asmln.fintech_app.tx_processor.repository;

import com.github.asmln.fintech_app.tx_processor.entity.OutboxTransactionEvent;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

public interface TransactionOutboxRepository extends Repository<OutboxTransactionEvent, UUID> {
    OutboxTransactionEvent save(OutboxTransactionEvent entity);

    @Query("SELECT o FROM OutboxTransactionEvent o WHERE o.sent = false")
    List<OutboxTransactionEvent> findPendingEvents(PageRequest page);
}
