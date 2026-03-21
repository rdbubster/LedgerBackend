package com.fintech.ledger.exception;

import java.math.BigDecimal;

public class InsufficientBalanceException extends RuntimeException{

    public InsufficientBalanceException(Long accountId, BigDecimal balance, BigDecimal amount){
        super("Account"+accountId+" has insufficient balance."+" Available: "+balance+", Requested: "+amount);

    }
}
