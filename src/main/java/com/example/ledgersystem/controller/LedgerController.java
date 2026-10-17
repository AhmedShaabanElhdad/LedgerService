package com.example.ledgersystem.controller;

import com.example.ledgersystem.domain.entity.LedgerTransaction;
import com.example.ledgersystem.dto.CreateLedgerTransactionRequest;
import com.example.ledgersystem.dto.LedgerTransactionResponse;
import com.example.ledgersystem.service.LedgerService;
import com.example.ledgersystem.service.UnbalancedLedgerException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.security.auth.login.AccountNotFoundException;

@RestController
@RequestMapping("/api/v1/ledger")
public class LedgerController {

    private final LedgerService ledgerService;

    public LedgerController(LedgerService ledgerService) {
        this.ledgerService = ledgerService;
    }

    @PostMapping("/transactions")
    public ResponseEntity<LedgerTransactionResponse> postTransaction(
            @RequestBody CreateLedgerTransactionRequest request
    ) throws UnbalancedLedgerException, AccountNotFoundException {

        LedgerTransaction transaction = ledgerService.post(
                request.idempotencyKey(),
                request.entries()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(LedgerTransactionResponse.from(transaction));
    }
}
