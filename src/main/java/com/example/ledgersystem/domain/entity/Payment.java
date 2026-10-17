package com.example.ledgersystem.domain.entity;

import com.example.ledgersystem.domain.PaymentStatus;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "payments",
        indexes = {
                @Index(name = "idx_payment_merchant", columnList = "merchant_id"),
                @Index(name = "idx_payment_status", columnList = "status")
        }
)
public class Payment {

    @Id
    private UUID id;

    @Column(name = "merchant_id", nullable = false)
    private UUID merchantId;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;

    @Column(nullable = false, length = 3)
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    @Column(name = "captured_amount", nullable = false, precision = 19, scale = 4)
    private BigDecimal capturedAmount;

    @Column(name = "refunded_amount", nullable = false, precision = 19, scale = 4)
    private BigDecimal refundedAmount;

    @Column(nullable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    protected Payment() {
    }

    public Payment(
            UUID id,
            UUID merchantId,
            BigDecimal amount,
            String currency
    ) {
        this.id = id;
        this.merchantId = merchantId;
        this.amount = amount;
        this.currency = currency;
        this.status = PaymentStatus.AUTHORIZED;
        this.capturedAmount = BigDecimal.ZERO;
        this.refundedAmount = BigDecimal.ZERO;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }


    public void capture(BigDecimal amount) {

        capturedAmount = capturedAmount.add(amount);

        updateStatus();

        updatedAt = Instant.now();
    }

    public void refund(BigDecimal amount) {

        refundedAmount =
                refundedAmount.add(amount);

        updateStatus();

        updatedAt = Instant.now();
    }

    private void updateStatus() {

        if (refundedAmount.compareTo(capturedAmount) == 0 && capturedAmount.signum() > 0) {
            status = PaymentStatus.REFUNDED;

        } else if (refundedAmount.signum() > 0) {
            status = PaymentStatus.PARTIALLY_REFUNDED;

        } else if (capturedAmount.compareTo(amount) == 0) {
            status = PaymentStatus.CAPTURED;
        }
    }

    // getters
}
