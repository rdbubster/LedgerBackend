package com.fintech.ledger.dto;

import com.fintech.ledger.model.LedgerEntryType;

import java.math.BigDecimal;
import java.time.Instant;

public class LedgerEntryResponse {
    private Long id;
    private BigDecimal amount;
    private LedgerEntryType type;
    private String referenceId;
    private Instant createdAt;

    public LedgerEntryResponse(Long id, BigDecimal amount, LedgerEntryType type, String referenceId, Instant createdAt){
        this.id=id;
        this.amount=amount;
        this.type=type;
        this.referenceId=referenceId;
        this.createdAt=Instant.now();
    }



    public Long getId() {
        return id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public LedgerEntryType getType() {
        return type;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
