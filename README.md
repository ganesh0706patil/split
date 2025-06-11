# Split App - Expense Splitter Backend

This Spring Boot backend system helps groups split expenses fairly and calculate settlements. It tracks who paid for what and determines who owes whom.

**Deployed Application URL:** `https://split-w3fz.onrender.com`
*(Note: As this application is hosted on Render's free tier, it may take up to a minute to start if it has been inactive for 15 minutes. Please be patient on the first request after a period of inactivity.)*

## Core Features

*   **Expense Tracking:** Add, view, edit, and delete expenses with amount, description, and payer.
*   **Automatic Person Creation:** People are added when mentioned in expenses.
*   **Flexible Splitting:** Supports EQUAL, EXACT, and PERCENTAGE splits.
*   **Settlement Calculations:** Determines individual balances (owes/owed) and provides simplified payment summaries to minimize transactions.
*   **Data Validation:** Ensures input integrity (positive amounts, required fields) with clear error messages and proper HTTP status codes.

## Tech Stack

*   **Backend:** Java 17, Spring Boot 3.x
*   **Database:** PostgreSQL
*   **Deployment:** Render.com (Backend), Railway.app (PostgreSQL)
*   **API Testing:** Postman

## API Endpoints

The base URL for the API is `https://split-w3fz.onrender.com`.

*   **Expense Management:**
    *   `GET /expenses`
    *   `POST /expenses`
    *   `PUT /expenses/{id}`
    *   `DELETE /expenses/{id}`
*   **Settlements & People:**
    *   `GET /settlements`
    *   `GET /balances`
    *   `GET /people`

## Postman Collection

A comprehensive Postman collection with pre-populated data and examples for all endpoints is available. This is the primary resource for testing and understanding API usage.

*   **Access the Collection:** [YOUR_PUBLIC_GIST_LINK_HERE]
    *   Import the JSON file into Postman. The `{{https://gist.github.com/ganesh0706patil/2842bfbc2900697af44349276dafdda7}}` is pre-configured.

## Local Development Setup

### Prerequisites

*   Java JDK 17+
*   Maven 3.6+
*   PostgreSQL

### Steps

1.  **Clone the repository:**
    ```bash
    git clone <https://github.com/ganesh0706patil/split>
    ```
2.  **Configure Database:** Update `src/main/resources/application.properties` with your local PostgreSQL connection details (URL, username, password).
    ```properties
    spring.datasource.url=jdbc:postgresql://localhost:5432/your_db_name
    spring.datasource.username=your_user
    spring.datasource.password=your_password
    spring.jpa.hibernate.ddl-auto=update
    ```
3.  **Run:**
    ```bash
    mvn clean install
    mvn spring-boot:run
    ```
    The application will start on `http://localhost:8080`.

## Database Schema

The system uses JPA entities:
*   `UserEntity` (or `PersonEntity`): Stores user information (primarily name).
*   `ExpenseEntity`: Stores details of each expense (description, amount, payer, split type).
*   `ExpenseSplitEntity`: Stores how much each user owes for a specific expense.

The schema is managed by Hibernate (`ddl-auto=update`).

## Settlement Calculation Logic

1.  **Calculate Balances:** For each user, `Net Balance = Total Paid - Total Owed`.
2.  **Simplify:** A greedy algorithm identifies users who owe money (debtors) and users who are owed money (creditors). It then matches the largest debtor with the largest creditor to minimize the number of payments required to settle all debts. `BigDecimal` is used for precision.

## Key Assumptions & Limitations

*   **No Authentication/Authorization:** Open access for this assignment.
*   **User Identification:** Users identified by unique `userName` (String).
*   **Single Currency:** Assumes all amounts are in the same currency.
*   **Basic Grouping:** Groups are implicit based on expense participants.
