package com.fintech.ledger.dto;

import jakarta.validation.constraints.NotBlank;

public class AccountRequest {

    @NotBlank(message = "name should not be blank")
    private String name;

    public String getName(){return name;}
    public void setName(String name){this.name=name;}
}
