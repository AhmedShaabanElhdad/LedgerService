package com.example.ledgersystem.domain.entity;

import com.example.ledgersystem.domain.EntryType;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "ledger_entries")
public class LedgerEntry {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "transaction_id")
    private LedgerTransaction transaction;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "account_id")
    private Account account;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EntryType type;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;

    @Column(nullable = false)
    private String currency;

    protected LedgerEntry() {
    }

    public LedgerEntry(
            UUID id,
            LedgerTransaction transaction,
            Account account,
            EntryType type,
            BigDecimal amount,
            String currency
    ) {
        this.id = id;
        this.transaction = transaction;
        this.account = account;
        this.type = type;
        this.amount = amount;
        this.currency = currency;
    }
}
