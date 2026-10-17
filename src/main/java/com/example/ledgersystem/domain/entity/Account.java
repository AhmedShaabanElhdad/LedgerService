package com.example.ledgersystem.domain.entity;

import com.example.ledgersystem.domain.AccountType;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "accounts")
public class Account {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true)
    private String accountCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountType type;

    @Column(nullable = false)
    private String currency;

    protected Account() {
    }

    public Account(
            UUID id,
            String accountCode,
            AccountType type,
            String currency
    ) {
        this.id = id;
        this.accountCode = accountCode;
        this.type = type;
        this.currency = currency;
    }

    // getters
}

