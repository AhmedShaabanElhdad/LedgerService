package com.example.ledgersystem.domain;

public enum PaymentStatus {
    AUTHORIZED,
    CAPTURED,
    // todo check when wen need this and different betweeen and REFUNDE
    PARTIALLY_REFUNDED,
    REFUNDED,
    FAILED,
    CHARGEBACK
}
