# Bank Management System (BMS)

## Overview

The Bank Management System (BMS) is a Java Spring Boot application designed to manage bank operations efficiently. It supports role-based access control, account management, transactions, loans, payments, and card operations.

This project is built with clean architecture principles, leveraging DTOs, service layers, repositories, and mappers for maintainability and scalability.

## Features

* **User Authentication & Authorization**

    * JWT-based authentication.
    * Role-based access control (Admin, Employee, Customer).

* **Accounts**

    * Create, view, and close accounts.
    * Supports multiple account types and statuses.

* **Transactions**

    * Deposit, withdraw, and transfer funds.
    * Automatic fee handling.
    * Transaction status tracking.

* **Loans**

    * Apply for loans.
    * Loan approval, rejection, and disbursement by authorized roles.

* **Cards**

    * Create, and block cards.
    * Unique card number and CVV generation.
    * Expiry date handling.

* **Payments**

    * Make loan payments.
    * Track payments.

* **Audit & Security**

    * RBAC enforced in Spring Security.
    * Transaction atomicity with Spring `@Transactional`.
    * Optimistic locking for high concurrency scenarios.

## Technology Stack

* Java 17+
* Spring Boot 3
* Spring Data JPA / Hibernate
* MySQL / MariaDB
* Spring Security with JWT
* Lombok
* Maven

## Setup Instructions

1. Clone the repository:

```bash
git clone https://github.com/Hassan-Tarek/bms.git
```

2. Configure the application properties (`application.yml` or `application.properties`):

```yaml
datasource:
  url: jdbc:mysql://localhost:3306/bms
  username: root
  password: password
spring:
  jpa:
    hibernate:
      ddl-auto: update
```

3. Build the project:

```bash
mvn clean install
```

4. Run the application:

```bash
mvn spring-boot:run
```

5. Access API endpoints via Postman or any HTTP client.

## Contributing

Contributions are welcome! Please fork the repository and submit a pull request.

## License

MIT License
