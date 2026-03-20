package com.fintech.ledger.controller;

import com.fintech.ledger.dto.AccountRequest;
import com.fintech.ledger.dto.AccountResponse;
import com.fintech.ledger.dto.CreditRequest;
import com.fintech.ledger.dto.LedgerEntryResponse;
import com.fintech.ledger.model.Account;
import com.fintech.ledger.model.LedgerEntry;
import com.fintech.ledger.service.AccountService;
import com.fintech.ledger.service.LedgerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.function.BiFunction;

@RestController
@RequestMapping("/account")
public class AccountController {

    private final AccountService accountService;
    private final LedgerService ledgerService;
    public AccountController(AccountService accountService,LedgerService ledgerService){
        this.accountService=accountService;
        this.ledgerService=ledgerService;
    }

    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(@Valid @RequestBody AccountRequest request) {

        Account account = accountService.createAccount(request.getName());
        AccountResponse response = new AccountResponse(
                account.getId(), account.getName(), account.getCreatedAt()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountResponse> getAccount(@PathVariable Long id){

        Account account=accountService.getAccount(id);
        AccountResponse response=new AccountResponse(account.getId(),account.getName(),account.getCreatedAt());
        return ResponseEntity.ok(response);

    }
    @PostMapping("/{id}/credit")
    public ResponseEntity<LedgerEntryResponse> credit(@PathVariable Long id ,@Valid @RequestBody CreditRequest request){
        LedgerEntry entry= ledgerService.credit(id,request.getAmount(),request.getReferenceId());
        LedgerEntryResponse response=new LedgerEntryResponse(
                entry.getId(),
                entry.getAmount(),
                entry.getType(),
                entry.getReferenceId(),
                entry.getCreatedAt()

        );
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}/balance")
    public ResponseEntity<BigDecimal> balance(@PathVariable Long id){
        BigDecimal balance=ledgerService.getBalance(id);
        return ResponseEntity.ok(balance);
    }





}
