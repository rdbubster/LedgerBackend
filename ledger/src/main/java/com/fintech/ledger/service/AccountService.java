package com.fintech.ledger.service;

import com.fintech.ledger.exception.AccountNotFoundException;
import com.fintech.ledger.model.Account;
import com.fintech.ledger.repository.AccountRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Transactional
    public Account createAccount(String name) {
        Account account = new Account(name);
        return accountRepository.save(account);
    }

    @Transactional(readOnly = true)
    public Account getAccount(Long id) {
        Optional<Account> result=accountRepository.findById(id);
        return result.orElseThrow(()-> new AccountNotFoundException(id));


    }
}