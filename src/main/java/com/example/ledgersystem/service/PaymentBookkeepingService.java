package com.example.ledgersystem.service;

import com.example.ledgersystem.domain.EntryType;
import com.example.ledgersystem.domain.entity.LedgerTransaction;
import com.example.ledgersystem.domain.entity.Payment;
import com.example.ledgersystem.dto.LedgerEntryCommand;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class PaymentBookkeepingService {

    private final LedgerService ledgerService;

    public PaymentBookkeepingService(
            LedgerService ledgerService
    ) {
        this.ledgerService = ledgerService;
    }

    public LedgerTransaction recordCapture(
            Payment payment,
            BigDecimal amount
    ) {

        return ledgerService.post(
                "payment:capture:" + payment.getId()
                        + ":" + amount,
                List.of(
                        new LedgerEntryCommand(
                                "gateway_receivable",
                                EntryType.DEBIT,
                                amount,
                                payment.getCurrency()
                        ),
                        new LedgerEntryCommand(
                                "merchant_payable",
                                EntryType.CREDIT,
                                amount,
                                payment.getCurrency()
                        )
                )
        );
    }


    public LedgerTransaction recordFee(
            Payment payment,
            BigDecimal fee
    ) {

        return ledgerService.post(
                "payment:fee:" + payment.getId(),
                List.of(
                        new LedgerEntryCommand(
                                "merchant_payable",
                                EntryType.DEBIT,
                                fee,
                                payment.getCurrency()
                        ),
                        new LedgerEntryCommand(
                                "fee_revenue",
                                EntryType.CREDIT,
                                fee,
                                payment.getCurrency()
                        )
                )
        );
    }
}
