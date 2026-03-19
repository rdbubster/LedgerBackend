package com.fintech.ledger.repository;

import com.fintech.ledger.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account,Long> {
}
