# Fintech Immutable Ledger

## What This Is

A production-style fintech backend that manages money movements using an
immutable append-only ledger. Instead of storing a balance directly, the
system derives balance by summing all credits and debits from the ledger
history. This prevents data corruption, enables full auditability, and
mirrors how real financial systems work.

## Tech Stack

- Java 17
- Spring Boot 4.x
- PostgreSQL
- Spring Data JPA / Hibernate
- Maven

## Architecture
```
HTTP Request → Controller → Service → Repository → PostgreSQL
```

**Controller** — handles HTTP only. Receives requests, validates input
via DTOs, calls the service, returns responses. No business logic here.

**Service** — owns all business rules. Enforces validation, manages
transactions, handles idempotency, controls concurrency.

**Repository** — talks to the database only. No logic. Spring Data JPA
generates implementations automatically.

**Database** — PostgreSQL stores three tables: accounts, ledger_entries,
idempotency_keys.

## Core Design Decisions

### 1. Immutable Ledger

Every credit and debit is stored as a new row in ledger_entries.
Rows are never updated or deleted. Balance is never stored — it is
computed on demand:
```sql
SELECT COALESCE(SUM(
    CASE WHEN type = 'CREDIT' THEN amount
         WHEN type = 'DEBIT'  THEN -amount
    END
), 0)
FROM ledger_entries
WHERE account_id = ?
```

This means the full financial history is always available and
auditable. If a balance is ever wrong, you can trace exactly
which entries caused it.

### 2. Transactional Integrity

Every write operation is wrapped in `@Transactional`. This means
all database operations in a method either commit together or
roll back together. If anything fails mid-operation — the
idempotency key save, the ledger entry insert, anything —
the database returns to its previous state. No partial records
ever land in the database.

### 3. Concurrency Safety

Without protection, two simultaneous debit requests can both
read the same balance before either commits, causing both to
succeed and the balance to go negative.

The debit operation acquires a pessimistic write lock on the
account row before reading the balance:
```sql
SELECT * FROM accounts WHERE id = ? FOR UPDATE
```

This forces any other transaction attempting to lock the same
row to wait. Only one thread can read and modify the balance
at a time, making double-spending impossible.

### 4. Idempotency

If a client sends a debit request and the network times out,
they don't know if the money was taken. If they retry, the
money could be debited twice.

Every credit and debit request includes a client-generated
`referenceId`. Before processing, the system checks whether
`(account_id, reference_id)` already exists in the
`idempotency_keys` table. If it does, the operation was already
processed — return without doing anything again. If not, process
and save the key.

This check and the key insert happen inside the same transaction,
so concurrent retries are also handled safely via the unique
constraint on `(account_id, reference_id)`.

### 5. Layered Validation

Before any money moves, three gates must pass:

1. **Input validation** — `@Valid` on the request DTO rejects
   null amounts, negative values, and blank referenceIds before
   the service is even called.
2. **Idempotency check** — reject duplicate operations before
   acquiring any database lock.
3. **Business rule check** — for debits, verify the account has
   sufficient balance after acquiring the lock.

Failing fast at the earliest possible gate is intentional —
it avoids unnecessary database work.

## API Endpoints

| Method | Path | Request Body | Response |
|--------|------|-------------|----------|
| POST | /accounts | `{"name": "Alice"}` | 201 AccountResponse |
| GET | /accounts/{id} | — | 200 AccountResponse |
| POST | /accounts/{id}/credit | `{"amount": 100, "referenceId": "ref-001"}` | 201 LedgerEntryResponse |
| POST | /accounts/{id}/debit | `{"amount": 50, "referenceId": "ref-002"}` | 201 LedgerEntryResponse |
| GET | /accounts/{id}/balance | — | 200 BigDecimal |
| GET | /accounts/{id}/transactions | ?page=0&size=10 | 200 Page<LedgerEntryResponse> |

## How to Run Locally

**Prerequisites:** Java 17+, PostgreSQL, Maven

1. Create a PostgreSQL database named "ledgerdb"
2. Clone the repository
3. Update `src/main/resources/application.properties` with your
   database credentials
4. Run: `mvn spring-boot:run`
5. Server starts on `http://localhost:8080`

## System Guarantees

**Atomicity** — every operation fully succeeds or nothing is
written. No partial state ever exists in the database.

**No double spending** — pessimistic locking ensures only one
debit can read and check the balance at a time. The balance
can never go negative due to concurrent requests.

**Retry safety** — clients can safely retry any operation with
the same referenceId. The system processes it exactly once
regardless of how many times it is sent.

**Immutable history** — ledger entries are never modified or
deleted. The complete transaction history is always available
and auditable.

**Fail fast** — invalid requests are rejected at the earliest
possible point before any database work is done.