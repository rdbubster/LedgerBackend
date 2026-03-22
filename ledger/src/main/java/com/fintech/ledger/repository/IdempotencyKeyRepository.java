package com.fintech.ledger.repository;

import com.fintech.ledger.model.IdempotencyKey;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IdempotencyKeyRepository extends JpaRepository<IdempotencyKey, Long> {

    boolean existsByAccountIdAndReferenceId(Long accountId, String referenceId);
}