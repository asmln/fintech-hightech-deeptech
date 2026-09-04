package com.github.asmln.fintech_app.tx_processor.controller;

import com.github.asmln.fintech_app.tx_processor.dto.TransactionRequest;
import com.github.asmln.fintech_app.tx_processor.dto.TransactionResponse;
import com.github.asmln.fintech_app.tx_processor.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/transactions")
public class TransactionController {
    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    public ResponseEntity<TransactionResponse> processTransaction(@Valid @RequestBody TransactionRequest request) {
        TransactionResponse response = transactionService.saveTransaction(request);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }
}
