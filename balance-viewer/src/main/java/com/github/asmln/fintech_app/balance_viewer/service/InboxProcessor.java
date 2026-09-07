package com.github.asmln.fintech_app.balance_viewer.service;

import com.fasterxml.jackson.databind.JsonNode;
import java.math.BigDecimal;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Service
public class InboxProcessor {
    private static final Logger log = LoggerFactory.getLogger(InboxProcessor.class);
    private final UserBalanceService userBalanceService;

    public InboxProcessor(UserBalanceService userBalanceService) {
        this.userBalanceService = userBalanceService;
    }

    @KafkaListener(topics = "${app.kafka.transaction.outbox.topic}")
    public void consume(JsonNode payload, Acknowledgment ack) {
        log.info("Получено сообщение в Inbox: {}", payload);
        UUID transactionId = UUID.fromString(payload.get("transactionId").asText());
        UUID userId = UUID.fromString(payload.get("userId").asText());
        BigDecimal balance = new BigDecimal(payload.get("balance").asText());
        try {
            userBalanceService.updateBalance(userId, transactionId, balance);
        } catch (Exception e) {
            log.error("Ошибка при обработке транзакции из Inbox.", e);
        }
        ack.acknowledge();
        log.info("Подтверждена обработка транзакции {}", transactionId);
    }
}
