package com.example.ledgersystem.controller;

import com.example.ledgersystem.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(
            PaymentService paymentService
    ) {
        this.paymentService = paymentService;
    }

    @PostMapping("/{paymentId}/capture")
    public ResponseEntity<Void> capture(
            @PathVariable UUID paymentId,
            @RequestBody CapturePaymentRequest request
    ) {

        paymentService.capture(
                paymentId,
                request.amount()
        );

        return ResponseEntity.ok().build();
    }
}
