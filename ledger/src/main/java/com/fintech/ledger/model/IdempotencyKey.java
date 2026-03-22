package com.fintech.ledger.model;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(
        name="idempotency_keys",
        uniqueConstraints = @UniqueConstraint(columnNames ={"accountId","referenceId"})
)
public class IdempotencyKey {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "account",nullable = false)
    private Long accountId;

    @Column(name ="reference_id",nullable = false )
    private String referenceId;

    @Column(name="created_at",nullable = false,updatable = false)
    private Instant createdAt;

    public IdempotencyKey(Long accountId,String referenceId){
        this.accountId=accountId; // so we had an error where i wrote accoundId here and idk how everything got like this.
        this.referenceId=referenceId;
        this.createdAt=Instant.now(); // why like this

    }
    protected IdempotencyKey(){}


    public String getReferenceId() {
        return referenceId;
    }

    public Long getAccountId() {
        return accountId;
    }

    public Instant getCreatedAt(){
        return createdAt;
    }
}
