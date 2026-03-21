package com.fintech.ledger.service;

import com.fintech.ledger.exception.AccountNotFoundException;
import com.fintech.ledger.exception.InsufficientBalanceException;
import com.fintech.ledger.model.Account;
import com.fintech.ledger.model.LedgerEntry;
import com.fintech.ledger.model.LedgerEntryType;
import com.fintech.ledger.repository.AccountRepository;
import com.fintech.ledger.repository.LedgerEntryRepository;
import org.springframework.data.util.QTypeContributor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class LedgerService {
    private final AccountRepository accountRepository;
    private final LedgerEntryRepository ledgerEntryRepository;

    public LedgerService(AccountRepository accountRepository, LedgerEntryRepository ledgerEntryRepository) {
        this.accountRepository = accountRepository;
        this.ledgerEntryRepository = ledgerEntryRepository;
    }

    @Transactional
    public LedgerEntry credit(Long accountId, BigDecimal amount, String referenceId) {

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }
        // Fetching account and throwing exception
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(accountId));

        LedgerEntry entry = new LedgerEntry(account, amount, referenceId, LedgerEntryType.CREDIT);
        return ledgerEntryRepository.save(entry);
    }

    @Transactional(readOnly = true)
    public BigDecimal getBalance(Long accountId) {

        accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(accountId));
        return ledgerEntryRepository.calculateBalance(accountId);

    }

    @Transactional
    public LedgerEntry debit(Long accountId, BigDecimal amount, String referenceId) {

        // Check if amount is greater than O
        if(amount==null || amount.compareTo(BigDecimal.ZERO) <=0){
            throw new IllegalArgumentException("Amount must be greater than zero");
        }
        //Lock the account
        Account account=accountRepository.findByIdWithLock(accountId).orElseThrow(()->new AccountNotFoundException(accountId));

        // To check the balance
        BigDecimal balance=ledgerEntryRepository.calculateBalance(accountId);

        // To check sufficient funds

        if(balance.compareTo(amount)<0){
            throw new InsufficientBalanceException(accountId,amount,balance);

        }
        // to insert the debit entry
        LedgerEntry entry=new LedgerEntry(account,amount, referenceId,LedgerEntryType.DEBIT);
                return ledgerEntryRepository.save(entry);


    }
}