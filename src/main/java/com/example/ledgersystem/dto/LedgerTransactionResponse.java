package com.example.ledgersystem.dto;

import com.example.ledgersystem.domain.entity.LedgerTransaction;

import java.util.UUID;

public record LedgerTransactionResponse(
        UUID transactionId,
        String idempotencyKey
) {

    public static LedgerTransactionResponse from(
            LedgerTransaction transaction
    ) {
        return new LedgerTransactionResponse(
                transaction.getId(),
                transaction.getIdempotencyKey()
        );
    }
}