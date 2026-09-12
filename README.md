#CampusCredit-Java Backend Application
[![Java CI with Docker](https://github.com/cloudKranthi/CampusCredit/actions/workflows/ci.yml/badge.svg)](https://github.com/cloudKranthi/CampusCredit/actions)

CampusCredit is an enterprise-grade backend service designed for campus ecosystems where financial integrity is paramount. Built with Java 17 and Spring Boot, the system ensures ACID compliance through atomic transactions and manual rollback mechanisms. By combining Pessimistic Locking with a custom Idempotency Filter, the application guarantees that every credit update is unique, thread-safe, and resilient to network retries or race conditions.


To handle high-concurrency "Money Updates," the service implements a strict validation and locking flow. Below is the conceptual implementation of the transfer logic:
@Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
public void moneyTransfer(String senderPhone, String receiverPhone, BigDecimal amount, String idempotencyKey, ...) {
    
    // 1. Idempotency Check: Prevent duplicate processing
    if(transactionRepository.findByIdempotencyKey(idempotencyKey).isPresent()) {
        throw new BusinessException("Transaction already processed", HttpStatus.CONFLICT);
    }

    // 2. State Validation: Ensure both Purses are ACTIVE
    Purse senderPurse = purseRepository.findByUserPhone(senderPhone).orElseThrow(...);
    Purse receiverPurse = purseRepository.findByUserPhone(receiverPhone).orElseThrow(...);

    if (isInactive(senderPurse) || isInactive(receiverPurse)) {
        throw new BusinessException("Account is Inactive", HttpStatus.FORBIDDEN);
    }

    // 3. Balance Validation & Atomic Update
    if (senderPurse.getBalance().compareTo(amount) < 0) {
        throw new BusinessException("Insufficient balance", HttpStatus.BAD_REQUEST);
    }

    // 4. Execution: Deduct, Credit, and Log Transaction
    senderPurse.subtract(amount);
    receiverPurse.add(amount);
    
    // 5. Async Messaging: Offload notification to RabbitMQ
    NotificationMessageResponse msg = new NotificationMessageResponse(senderPhone, receiverPhone, amount);
    rabbitTemplate.convertAndSend("notificationExchange", "routing.key", msg);

    // 6. Audit Logging: Record activity for compliance
    auditService.logAudit(senderPhone, "MoneyTransfer", "Success", clientIp);
}

   
src/main/java/com/wallet
├── config/             # Security & Bean configurations
├── controllers/        # REST Endpoints
├── services/           # Business logic (Transaction handling)
├── filters/            # JWT/Security interceptors
├── exception/          # Global Exception Handling (@ControllerAdvice)
└── repository/ # Postgres Data Access

## Technical Architecture & Design

### 1. User & Identity Management:

- **Secure Persistence:** User passwords and transaction PINs are stored using strong cryptographic hashing.

- **Role-Based Access Control (RBAC):** Permissions are managed via Enum types to ensure strict access levels (e.g., Student, Merchant, Admin).

- **Entity Relationship:** A strict 1:1 relationship is maintained between the User and their Purse, ensuring a single source of truth for every account.

### 2. Financial Integrity (The Purse)

- **High-Precision Math:** All financial values use BigDecimal to prevent the rounding errors common with floating-point types.**

- **Negative Balance Protection:** The system implements a Non-Negative Check at the database and application levels, ensuring a purse can never be overdrawn during a transaction.

- **Atomic Initialization:** A Purse is automatically generated and linked during the User registration lifecycle.

### 3. High-Performance Caching (Redis)
- **Latency Reduction:** Frequently accessed data, such as wallet balances and transaction statuses, are cached in Redis to offload heavy read operations from PostgreSQL.

- **Resilience & Fallback:** The system features a Manual Rollback/Failover mechanism. If the Redis cluster is unreachable, the application gracefully fails over to the primary database to ensure 100% uptime.

### 4. Asynchronous Messaging (RabbitMQ)
- **Event-Driven Notifications:** Transaction alerts for both senders and receivers are decoupled from the main execution thread.

- **Guaranteed Delivery:** Messages are persisted in RabbitMQ queues. If a   notification service is temporarily down, the messages remain queued until they are successfully processed and delivered, ensuring no alert is ever lost.
##  Technical Stack & Architecture
- **Framework:** Spring Boot 3.x (Java 21)
- **Database:** PostgreSQL (Primary Store with Row-Level Locking)
- **Caching:** Redis (Idempotency Store & Session Management)
- **Messaging:** RabbitMQ (Asynchronous Event-Driven Notifications)
- **DevOps:** Docker Compose, GitHub Actions (CI/CD)

##  System Resilience And Error handling
- **Pessimistic Locking:** Prevents "Double-Spending" via `SELECT FOR UPDATE`.
- **Database Idempotency:** Custom filter ensures network retries don't create duplicate transactions.
- **Fail-Over Logic:** Manual rollback mechanisms and graceful degradation if Redis/RabbitMQ are unreachable.
-  **Error Response Format:** each and every request has its own unique request id method from where it came timestamps seperation of fatal and operational error
     and show stack trace only in development.
-  **MDC :**  the filter chain runs after using getting authenticated it logs the thing with authenticated user phone number  during error response it shows with whiich phone numvebr it got error.
 ## End to End Transaction Logic

**Every transfer follows a high-integrity execution pipeline to ensure zero data loss and prevent double-spending:**
### 1)Request Sanitization:
- **Validates Transaction PIN and converts the String amount to BigDecimal.**
-  **Pre-check: Instantly rejects negative amounts or malformed inputs to save processing power.**
### 2)Idempotency Guard (Redis Layer):
- **Intercepts the request using a unique Idempotency-Key.**
- **If the key exists in Redis, the system refuses the duplicate request, protecting against network retries or double-clicks.**

### 3) Atomic Execution (The Vault):
- **Starts a @Transactional session with Propagation.REQUIRED.**
- **Uses Pessimistic Locking on the Sender and Receiver Purses to ensure thread-safety during high-concurrency "Money Updates."**
### 4) State Mutation & Validation:
-  **Finds the purses, validates balances, and creates a Transaction entity marked as COMPLETED.**
- **Simultaneously deducts from the sender and credits the receiver within the database boundary.**
###  5)Asynchronous Notification (RabbitMQ):
- **Offloads the notification payload to RabbitMQ via RabbitTemplate using a dedicated routing.key.**
- **This decouples the notification delay from the core financial transaction.**
###  6)Cache Invalidation & Audit:
- **Triggers an Audit Log for compliance.**

## System Observability & Debugging

To ensure the system is maintainable in a production environment, CampusCredit implements structured logging and request tracing:

- **MDC (Mapped Diagnostic Context):** Every request is assigned a unique `traceId`. This ID is propagated across all logs (Controller -> Service -> Repository), allowing us to trace a specific transaction's journey through the logs.
- **Global Exception Handling:** Uses a centralized `@ControllerAdvice` to catch and transform internal system errors into meaningful, secure API responses while masking sensitive stack traces.
- **Structured Logging:** Logs are categorized by severity (`INFO`, `WARN`, `ERROR`). Transaction failures trigger detailed error logs including the User ID and Idempotency Key for rapid debugging.



##  API Usage & Documentation

The system follows RESTful principles with structured request/response bodies. 

| Feature | Method | Endpoint | Description |
| :--- | :--- | :--- | :--- |
| **Purse** | `GET` | `/api/money/getBalance` | Fetch real-time balance (Redis-backed). |
| **Transfer** | `POST` | `/api/transactions/money-transfer` | Execute atomic money transfer. |
| **History** | `GET` | `/api/transactions/getHistory` | Paginated transaction history. |


**Example Request Header:**
- `X-Idempotency-Key`: `uuid-v4-string`
- `Authorization`: `Bearer <JWT_TOKEN>`



##  Infrastructure & Deployment (Local)

This project is built to be **Environment Agnostic** using Docker.

1. **Spin up Services:**
   ```bash
   docker-compose up --build

- **Cache Eviction: Deletes the old Redis cache keys for both users, forcing the system to fetch the updated balance from PostgreSQL on the next request.**

- **Persistence: Updates the paginated Transaction History service for both parties.**


