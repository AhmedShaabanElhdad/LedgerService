package com.example.ledgersystem.service;

import com.example.ledgersystem.domain.entity.Account;
import com.example.ledgersystem.domain.EntryType;
import com.example.ledgersystem.domain.entity.LedgerEntry;
import com.example.ledgersystem.domain.entity.LedgerTransaction;
import com.example.ledgersystem.dto.LedgerEntryCommand;
import com.example.ledgersystem.repository.AccountRepository;
import com.example.ledgersystem.repository.LedgerEntryRepository;
import com.example.ledgersystem.repository.LedgerTransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.security.auth.login.AccountNotFoundException;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class LedgerService {

    private final AccountRepository accountRepository;
    private final LedgerTransactionRepository transactionRepository;
    private final LedgerEntryRepository entryRepository;

    public LedgerService(
            AccountRepository accountRepository,
            LedgerTransactionRepository transactionRepository,
            LedgerEntryRepository entryRepository
    ) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.entryRepository = entryRepository;
    }

    @Transactional
    public LedgerTransaction post(
            String idempotencyKey,
            List<LedgerEntryCommand> commands
    ) throws AccountNotFoundException, UnbalancedLedgerException {

        validateEntries(commands);

        LedgerTransaction existing = transactionRepository
                .findByIdempotencyKey(idempotencyKey)
                .orElse(null);

        if (existing != null) {
            return existing;
        }

        LedgerTransaction transaction = new LedgerTransaction(
                UUID.randomUUID(),
                idempotencyKey
        );

        transactionRepository.save(transaction);

        for (LedgerEntryCommand command : commands) {

            Account account = accountRepository.findByAccountCode(command.accountCode())
                    .orElseThrow(() ->
                            new AccountNotFoundException(command.accountCode())
                    );

            validateCurrency(account, command);

            LedgerEntry entry = new LedgerEntry(
                    UUID.randomUUID(),
                    transaction,
                    account,
                    command.type(),
                    command.amount(),
                    command.currency()
            );

            entryRepository.save(entry);
        }

        return transaction;
    }

    private void validateEntries(
            List<LedgerEntryCommand> commands
    ) throws UnbalancedLedgerException {

        if (commands == null || commands.size() < 2) {
            throw new IllegalArgumentException(
                    "A ledger transaction requires at least two entries"
            );
        }

        BigDecimal debitTotal = commands.stream()
                .filter(c -> c.type() == EntryType.DEBIT)
                .map(LedgerEntryCommand::amount)
                .reduce(
                        BigDecimal.ZERO,
                        BigDecimal::add
                );

        BigDecimal creditTotal = commands.stream()
                .filter(c -> c.type() == EntryType.CREDIT)
                .map(LedgerEntryCommand::amount)
                .reduce(
                        BigDecimal.ZERO,
                        BigDecimal::add
                );

        if (debitTotal.compareTo(creditTotal) != 0) {
            throw new UnbalancedLedgerException(
                    debitTotal,
                    creditTotal
            );
        }
    }

    private void validateCurrency(
            Account account,
            LedgerEntryCommand command
    ) {

        if (!account.getCurrency().equals(command.currency())) {
            throw new IllegalArgumentException(
                    "Account currency does not match entry currency"
            );
        }
    }
}