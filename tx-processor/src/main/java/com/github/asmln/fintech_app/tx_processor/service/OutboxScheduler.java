package com.github.asmln.fintech_app.tx_processor.service;

import com.github.asmln.fintech_app.tx_processor.entity.OutboxTransactionEvent;
import com.github.asmln.fintech_app.tx_processor.repository.TransactionOutboxRepository;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class OutboxScheduler {
    private static final Logger log = LoggerFactory.getLogger(OutboxScheduler.class);
    private final TransactionOutboxRepository outboxRepository;
    private final OutboxProcessor outboxProcessor;

    public OutboxScheduler(TransactionOutboxRepository outboxRepository, OutboxProcessor outboxProcessor) {
        this.outboxRepository = outboxRepository;
        this.outboxProcessor = outboxProcessor;
    }

    @Scheduled(fixedDelay = 2000)
    public void processTransactionOutbox() {
        List<OutboxTransactionEvent> pendingEvents = outboxRepository.findPendingEvents(PageRequest.of(0, 10));
        if (pendingEvents.isEmpty()) {
            return;
        }
        log.info("Выбрано {} событий для отправки в Kafka.", pendingEvents.size());
        for (var event : pendingEvents) {
            try {
                outboxProcessor.send(event);
            } catch (InterruptedException e) {
                log.error("Отправка в Kafka была прервана.", e);
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                log.error(
                        "Kafka не подтвердила запись события {}, оно будет повторно отправлено.",
                        event.getTransactionId(),
                        e
                );
                break;
            }
        }
    }
}
