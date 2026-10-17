package com.example.ledgersystem.domain.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "ledger_transactions",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_ledger_transaction_idempotency",
                        columnNames = "idempotency_key"
                )
        }
)
public class LedgerTransaction {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String idempotencyKey;

    @Column(nullable = false)
    private Instant createdAt;

    protected LedgerTransaction() {
    }

    public LedgerTransaction(
            UUID id,
            String idempotencyKey
    ) {
        this.id = id;
        this.idempotencyKey = idempotencyKey;
        this.createdAt = Instant.now();
    }
}

