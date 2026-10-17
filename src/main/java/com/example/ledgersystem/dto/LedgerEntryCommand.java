package com.example.ledgersystem.dto;

import com.example.ledgersystem.domain.EntryType;

import java.math.BigDecimal;

public record LedgerEntryCommand(
        String accountCode,
        EntryType type,
        BigDecimal amount,
        String currency
) {
}
