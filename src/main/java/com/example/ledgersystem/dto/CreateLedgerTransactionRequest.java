package com.example.ledgersystem.dto;

import java.util.List;

public record CreateLedgerTransactionRequest(
        String idempotencyKey,
        List<LedgerEntryCommand> entries
) {
}
