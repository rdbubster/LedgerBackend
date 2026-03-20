package com.fintech.ledger.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.sql.Update;

import java.time.Instant;


@Entity
@Table(name="accounts")
public class Account {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

@Column(nullable = false)
    private String name;

    @Column(nullable = false,updatable = false,name="created_at")
    private Instant createdAt;


    public Account(String name){
        if(name==null||name.isBlank()){
            throw new IllegalArgumentException("Account name must not be blanked");
        }
        this.name=name;
        this.createdAt=Instant.now();
    }

    protected Account(){}

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }


}
