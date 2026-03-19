package com.fintech.ledger.controller;

import com.fintech.ledger.dto.AccountRequest;
import com.fintech.ledger.dto.AccountResponse;
import com.fintech.ledger.model.Account;
import com.fintech.ledger.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/account")
public class AccountController {

    private final AccountService accountService;
    public AccountController(AccountService accountService){
        this.accountService=accountService;
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


}
