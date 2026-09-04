package com.github.asmln.fintech_app.tx_processor.service;

import com.github.asmln.fintech_app.tx_processor.entity.OutboxTransactionEvent;
import com.github.asmln.fintech_app.tx_processor.repository.TransactionOutboxRepository;
import java.util.concurrent.ExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import com.fasterxml.jackson.databind.JsonNode;

@Service
public class OutboxProcessor {
    private static final Logger log = LoggerFactory.getLogger(OutboxProcessor.class);
    private final TransactionOutboxRepository transactionOutboxRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String topicName;

    public OutboxProcessor(
            TransactionOutboxRepository transactionOutboxRepository,
            KafkaTemplate<String, Object> kafkaTemplate,
            @Value("${app.kafka.transaction.outbox.topic}") String topicName
    ) {
        this.transactionOutboxRepository = transactionOutboxRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.topicName = topicName;
    }

    // При большой нагрузке транзакция для каждой записи это слишком расточительно.
    // Можно сделать транзакционным метод, который порцию обрабатывает.
    // Откат порции это не так страшно, т.к. есть идемпотентность.
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = {
            ExecutionException.class,
            InterruptedException.class
    })
    public void send(OutboxTransactionEvent event) throws ExecutionException, InterruptedException {
        String key = event.getTransactionId().toString();
        JsonNode payload = event.getPayload();
        SendResult<String, Object> result = kafkaTemplate.send(topicName, key, payload).get();
        log.info(
                "Событие было отправлено в Kafka, партиция {}, offset {}.",
                result.getRecordMetadata().partition(),
                result.getRecordMetadata().offset()
        );
        event.setSent(true);
        transactionOutboxRepository.save(event);
    }
}
