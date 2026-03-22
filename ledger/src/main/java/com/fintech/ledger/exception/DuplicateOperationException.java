package com.fintech.ledger.exception;

public class DuplicateOperationException extends RuntimeException {
    public DuplicateOperationException(String referenceId) {
        super("Operation with referenceId "+referenceId+" has already been processed");
    }
}
