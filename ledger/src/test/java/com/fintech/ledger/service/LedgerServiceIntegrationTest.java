package com.fintech.ledger.service;

import com.fintech.ledger.exception.AccountNotFoundException;
import com.fintech.ledger.exception.InsufficientBalanceException;
import com.fintech.ledger.exception.DuplicateOperationException;
import com.fintech.ledger.model.Account;
import com.fintech.ledger.repository.AccountRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class LedgerServiceIntegrationTest {

    @Autowired
    private LedgerService ledgerService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private AccountRepository accountRepository;

    @Test
    void credit_shouldIncreaseBalance() {
        Account account = accountService.createAccount("Alice");

        ledgerService.credit(account.getId(), new BigDecimal("100"), "ref-001");

        BigDecimal balance = ledgerService.getBalance(account.getId());
        assertEquals(new BigDecimal("100.00"), balance);
    }

    @Test
    void debit_shouldDecreaseBalance() {
        Account account = accountService.createAccount("Bob");

        ledgerService.credit(account.getId(), new BigDecimal("500"), "ref-001");
        ledgerService.debit(account.getId(), new BigDecimal("200"), "ref-002");

        BigDecimal balance = ledgerService.getBalance(account.getId());
        assertEquals(new BigDecimal("300.00"), balance);
    }

    @Test
    void debit_shouldFail_whenInsufficientBalance() {
        Account account = accountService.createAccount("Charlie");

        ledgerService.credit(account.getId(), new BigDecimal("100"), "ref-001");

        assertThrows(InsufficientBalanceException.class, () ->
                ledgerService.debit(account.getId(), new BigDecimal("200"), "ref-002")
        );
    }

    @Test
    void credit_shouldFail_onDuplicateReferenceId() {
        Account account = accountService.createAccount("Diana");

        ledgerService.credit(account.getId(), new BigDecimal("100"), "ref-001");

        assertThrows(DuplicateOperationException.class, () ->
                ledgerService.credit(account.getId(), new BigDecimal("100"), "ref-001")
        );
    }

    @Test
    void getAccount_shouldFail_whenAccountNotFound() {
        assertThrows(AccountNotFoundException.class, () ->
                accountService.getAccount(999L)
        );
    }
}
