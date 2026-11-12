# Database Documentation

This document describes the database model represented by the project's JPA entities and how to prepare the database for a local run.

## Database

- **Database name:** `library_management`
- **Database engine:** MySQL
- **Connection default:** `localhost:3306`
- **ORM:** Hibernate through Spring Data JPA
- **Schema setting:** `spring.jpa.hibernate.ddl-auto=update`

The application creates or updates mapped tables at startup when the configured MySQL user has the required privileges. The project does not include a versioned SQL migration or a standalone schema dump; the entity classes are the source for the current model. For important data or production use, review schema changes and use controlled backups/migrations.

## Entity relationship overview

```text
students  1 ──────── * issued_books * ──────── 1  books
```

Each issue record references exactly one student and one book. A student and a book can each appear in many issue records over time.

## Tables

### `books`

| Column | Java field | Mapping / rule |
| --- | --- | --- |
| `id` | `id` | Auto-generated `BIGINT` primary key |
| `title` | `title` | Required; up to 100 characters |
| `author` | `author` | Required; up to 100 characters |
| `isbn` | `isbn` | Required and unique |
| `category` | `category` | Optional |
| `quantity` | `quantity` | Required; validation requires at least 1 |
| `available_copies` | `availableCopies` | Primitive integer; validation requires 0 or greater |

### `students`

| Column | Java field | Mapping / rule |
| --- | --- | --- |
| `id` | `id` | Auto-generated `BIGINT` primary key |
| `name` | `name` | Required; up to 100 characters |
| `roll_number` | `rollNumber` | Required and unique; up to 30 characters |
| `email` | `email` | Required, unique, and validated as an email address |
| `phone` | `phone` | Optional; if provided, validation requires 10 digits |
| `course` | `course` | Optional |

### `issued_books`

| Column | Java field | Mapping / rule |
| --- | --- | --- |
| `id` | `id` | Auto-generated `BIGINT` primary key |
| `student_id` | `student` | Required foreign key to `students.id` |
| `book_id` | `book` | Required foreign key to `books.id` |
| `issue_date` | `issueDate` | `LocalDate`; issue date |
| `due_date` | `dueDate` | `LocalDate`; due date |
| `return_date` | `returnDate` | `LocalDate`; empty until returned |
| `status` | `status` | String status used by issue/return and report filters |

Field types and physical column names shown above follow the Java entity mappings and Spring Boot's default Hibernate naming conventions. MySQL's exact generated column definitions can vary with Hibernate and dialect versions. Validation annotations are application-level constraints; they do not necessarily create matching database `CHECK` constraints.

## Create the database

Create the empty database before starting the application:

```sql
CREATE DATABASE library_management;
```

Configure credentials locally using environment variables. For example, in PowerShell:

```powershell
$env:SPRING_DATASOURCE_URL = "jdbc:mysql://localhost:3306/library_management?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Kolkata"
$env:SPRING_DATASOURCE_USERNAME = "<your-mysql-username>"
$env:SPRING_DATASOURCE_PASSWORD = "<your-mysql-password>"
```

Do not commit real credentials. The checked-in `application.properties` contains local datasource defaults; environment variables let you provide your own values without documenting or sharing those credentials. Verify the active local configuration before running the app.

Run the application from the repository root:

```bash
mvn spring-boot:run
```

At startup, Hibernate's `update` setting synchronizes the entity model with the database. The current application also enables SQL logging through `spring.jpa.show-sql`.

## Integrity notes

- ISBN values must be unique across books.
- Student roll numbers and email addresses must each be unique.
- Each issue record must refer to an existing student and book.
- Quantity/availability and form validation are enforced in application logic and validation annotations. Review service behavior before changing or importing circulation data directly in SQL.
