package com.fintech.ledger.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public class CreditRequest {

    @NotNull
    @Positive
    private BigDecimal amount;

    @NotBlank
    private String referenceId;

    public BigDecimal getAmount(){
        return amount;
    }
    public String getReferenceId(){
        return referenceId;

    }
    public void setAmount(BigDecimal amount){
        this.amount=amount;
    }
    public void setReferenceId(String referenceId){
        this.referenceId=referenceId;
    }
}
