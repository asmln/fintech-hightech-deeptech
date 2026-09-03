package com.github.asmln.fintech_app.tx_processor.controller;

import com.github.asmln.fintech_app.tx_processor.dto.TransactionRequest;
import com.github.asmln.fintech_app.tx_processor.dto.TransactionResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequestMapping("/api/v1/transactions")
public class TransactionController {
    @PostMapping
    public ResponseEntity<TransactionResponse> processTransaction(
            @Valid @RequestBody TransactionRequest request) {

        TransactionResponse response = new TransactionResponse(
                "ACCEPTED",
                "Транзакция успешно принята в обработку",
                request.externalId(),
                request.userId(),
                Instant.now()
        );

        // Возвращаем статус 202 Accepted и тело ответа
        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(response);
    }
}
