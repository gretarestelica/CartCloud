# CartCloud – Backend (Spring Boot)

## Project Overview

CartCloud is a **Spring Boot–based e‑commerce backend** that provides REST APIs for managing users, products, carts, orders, and payments. The system is designed using a layered architecture and follows best practices for RESTful API design, data persistence, and transactional consistency.

The backend was developed as an academic project and is fully testable using **Postman** with an **H2 in‑memory database**.

---

## Architecture Overview

The application follows a **layered architecture**:

- **Controller Layer** – REST endpoints (`@RestController`)
- **Service Layer** – Business logic & transactions
- **Repository Layer** – Data access using Spring Data JPA
- **Model Layer** – JPA entities
- **Configuration Layer** – Security & application configuration

### Main Modules

- User Management
- Product & Category Management
- Cart Management
- Order Management
- Payment Processing

---

## Technologies Used

- Java 17+
- Spring Boot
- Spring Web (REST)
- Spring Data JPA
- Spring Security (basic configuration)
- H2 In‑Memory Database
- Maven
- Lombok
- Postman (API testing)

---

## Database Configuration

The application uses **H2 in‑memory database**.

```properties
spring.datasource.url=jdbc:h2:mem:cartclouddb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

spring.h2.console.enabled=true
spring.h2.console.path=/h2-console
```

### H2 Console Access

```
http://localhost:8081/h2-console
```

JDBC URL:

```
jdbc:h2:mem:cartclouddb
```

---

## ▶ Running the Application

```bash
mvn clean spring-boot:run
```

The application starts on:

```
http://localhost:8081
```

---

## Security

For development and testing purposes, security is configured to allow all requests:

```java
authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
```

This simplifies API testing with Postman.

---

## API Endpoints (Main)

### Cart

- `GET /api/cart/{userId}` – Get cart for user

### Orders

- `GET /api/orders` – Get all orders
- `POST /api/orders` – Create order

### Payments

- `POST /api/payments/order/{orderId}` – Create payment for an order

### Categories & Products

- `GET /api/categories`
- `GET /api/products`

---

## Testing with Postman

### Example Flow

1. **Get Cart**

```
GET /api/cart/1
```

2. **Create Order**

```
POST /api/orders
```

3. **Create Payment**

```
POST /api/payments/order/1
Content-Type: application/json

{
  "method": "CASH"
}
```

### Notes

- Empty tables (Orders, Payments) are **normal before checkout**
- Data is stored in memory and resets on restart

---

## Design Decisions

- **Transactional consistency**: Payments require an existing Order
- **Foreign key constraints** ensure data integrity
- **Layered architecture** improves maintainability
- **In‑memory DB** simplifies development and testing

---

## Project Status

Controllers implemented
Business logic implemented
Database relationships enforced
API tested with Postman
Ready for submission

---

## Frontend (Vite + React)

The CartCloud frontend is built using React with Vite for fast development and modern tooling. It consumes the backend REST APIs to display products, manage the shopping cart, and place orders.

## Frontend Structure

frontend/frontend

## Running the Frontend

cd frontend/frontend
npm install
npm run dev

## Conclusion

The CartCloud backend demonstrates a complete and functional e‑commerce API built with Spring Boot. It follows clean architecture principles, ensures data integrity, and supports end‑to‑end flows from cart management to order creation and payment processing.

---
