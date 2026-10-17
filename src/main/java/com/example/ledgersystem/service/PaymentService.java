package com.example.ledgersystem.service;

import com.example.ledgersystem.domain.PaymentStatus;
import com.example.ledgersystem.domain.entity.Payment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentBookkeepingService bookkeepingService;

    public PaymentService(
            PaymentRepository paymentRepository,
            PaymentBookkeepingService bookkeepingService
    ) {
        this.paymentRepository = paymentRepository;
        this.bookkeepingService = bookkeepingService;
    }

    @Transactional
    public void capture(
            UUID paymentId,
            BigDecimal amount
    ) {

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() ->
                        new PaymentNotFoundException(paymentId)
                );

        validateCapture(payment, amount);

        bookkeepingService.recordCapture(payment, amount);

        payment.capture(amount);

        paymentRepository.save(payment);
    }

    @Transactional
    public void refund(
            UUID paymentId,
            UUID refundId,
            BigDecimal amount
    ) {

        Payment payment =
                paymentRepository.findById(paymentId)
                        .orElseThrow(() ->
                                new PaymentNotFoundException(paymentId)
                        );

        validateRefund(payment, amount);

        bookkeepingService.recordRefund(
                payment,
                refundId,
                amount
        );

        payment.refund(amount);

        paymentRepository.save(payment);
    }

    private void validateCapture(
            Payment payment,
            BigDecimal amount
    ) {

        if (payment.getStatus() != PaymentStatus.AUTHORIZED) {
            throw new IllegalStateException(
                    "Payment cannot be captured"
            );
        }

        if (amount.signum() <= 0) {
            throw new IllegalArgumentException(
                    "Capture amount must be positive"
            );
        }

        if (amount.compareTo(payment.getAmount()) > 0) {
            throw new IllegalArgumentException(
                    "Capture exceeds payment amount"
            );
        }

    }

    private void validateRefund(
            Payment payment,
            BigDecimal amount
    ) {

        if (amount.signum() <= 0) {
            throw new IllegalArgumentException(
                    "Refund must be positive"
            );
        }

        BigDecimal refundable = payment.getCapturedAmount()
                .subtract(payment.getRefundedAmount());

        if (amount.compareTo(refundable) > 0) {
            throw new IllegalArgumentException(
                    "Refund exceeds refundable amount"
            );
        }
    }
}
