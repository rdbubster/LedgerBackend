package com.fintech.ledger.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(
        name = "ledger_entries",
        uniqueConstraints = @UniqueConstraint(columnNames = {"account_id","reference_id"})
)
public class LedgerEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name = "account_id",nullable = false)
    private Account account;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false,precision = 19,scale = 2)
    private LedgerEntryType type;


    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private String referenceId;

    @Column(nullable = false,updatable = false)
    private Instant createdAt;

    public LedgerEntry(Account account,BigDecimal amount,String referenceId,LedgerEntryType type){
        this.account=account;
        this.amount=amount;
        this.referenceId=referenceId;
        this.type=type;
        this.createdAt=Instant.now();
    }
    protected LedgerEntry(){}// this thing we need to look at up as it is something for hibernate.

    public Long getId(){
        return id;
    }
    public Account getAccount(){
        return account;
    }
    public BigDecimal getAmount(){
        return amount;
    }
    public String getReferenceId(){
        return referenceId;
    }
    public Instant getCreatedAt(){
        return createdAt;
    }
    public LedgerEntryType getType(){
        return type;
    }
}
