package com.fintech.ledger.service;

import com.fintech.ledger.exception.AccountNotFoundException;
import com.fintech.ledger.exception.DuplicateOperationException;
import com.fintech.ledger.exception.InsufficientBalanceException;
import com.fintech.ledger.model.Account;
import com.fintech.ledger.model.IdempotencyKey;
import com.fintech.ledger.model.LedgerEntry;
import com.fintech.ledger.model.LedgerEntryType;
import com.fintech.ledger.repository.AccountRepository;
import com.fintech.ledger.repository.IdempotencyKeyRepository;
import com.fintech.ledger.repository.LedgerEntryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class LedgerService {
    private final AccountRepository accountRepository;
    private final LedgerEntryRepository ledgerEntryRepository;
    private final IdempotencyKeyRepository idempotencyKeyRepository;

    public LedgerService(AccountRepository accountRepository, LedgerEntryRepository ledgerEntryRepository,IdempotencyKeyRepository idempotencyKeyRepository) {
        this.accountRepository = accountRepository;
        this.ledgerEntryRepository = ledgerEntryRepository;
        this.idempotencyKeyRepository=idempotencyKeyRepository;
    }

    @Transactional
    public LedgerEntry credit(Long accountId, BigDecimal amount, String referenceId)  {

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }
        // adding idempotency check
        if(idempotencyKeyRepository.existsByAccountIdAndReferenceId(accountId,referenceId)){
            throw new DuplicateOperationException(referenceId);
        }
        // Fetching account and throwing exception
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(accountId));

        //Saving the idempotency key

        idempotencyKeyRepository.save(new IdempotencyKey(accountId,referenceId));

        LedgerEntry entry = new LedgerEntry(account, amount,LedgerEntryType.CREDIT, referenceId);
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
        if(idempotencyKeyRepository.existsByAccountIdAndReferenceId(accountId,referenceId)){
            throw new DuplicateOperationException(referenceId);
        }
        //Lock the account
        Account account=accountRepository.findByIdWithLock(accountId).orElseThrow(()->new AccountNotFoundException(accountId));

        // To check the balance
        BigDecimal balance=ledgerEntryRepository.calculateBalance(accountId);

        // To check sufficient funds

        if(balance.compareTo(amount)<0){
            throw new InsufficientBalanceException(accountId,amount,balance);

        }

        idempotencyKeyRepository.save(new IdempotencyKey(accountId,referenceId));
        // to insert the debit entry
        LedgerEntry entry=new LedgerEntry(account,amount, LedgerEntryType.DEBIT,referenceId);
                return ledgerEntryRepository.save(entry);


    }
}