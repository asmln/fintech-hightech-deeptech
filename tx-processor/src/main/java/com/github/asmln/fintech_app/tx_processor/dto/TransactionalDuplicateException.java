package com.github.asmln.fintech_app.tx_processor.dto;

public class TransactionalDuplicateException extends RuntimeException {
    public TransactionalDuplicateException(String message) {
        super(message);
    }
}
