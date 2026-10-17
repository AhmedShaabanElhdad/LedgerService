package com.example.ledgersystem.service;

import java.math.BigDecimal;

public class UnbalancedLedgerException extends Throwable {
    public UnbalancedLedgerException(BigDecimal debitTotal, BigDecimal creditTotal) {
    }
}
