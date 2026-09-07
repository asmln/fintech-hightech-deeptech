package com.github.asmln.fintech_app.balance_viewer.entity;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "transactions")
public class Transaction {
    @Id
    private UUID id;

    public Transaction() {
    }

    public Transaction(UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }
}
