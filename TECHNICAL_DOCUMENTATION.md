# Technical Documentation

This document describes the current implementation of the Library Management System as represented by the repository. It is intended for project reviewers and developers maintaining the application. It documents existing behavior; it does not describe proposed or unimplemented features.

## 1. Technology and runtime

| Concern | Implementation |
| --- | --- |
| Language | Java 21 |
| Framework | Spring Boot 3.5.7 |
| Build | Maven (`com.library:library-management:0.0.1-SNAPSHOT`) |
| Web layer | Spring MVC (`spring-boot-starter-web`) |
| View rendering | Thymeleaf templates |
| Persistence | Spring Data JPA and Hibernate |
| Database | MySQL, database `library_management` |
| Validation | Jakarta Bean Validation / Spring Boot Validation |
| UI styles | Bootstrap 5.3.3 loaded from jsDelivr CDN |
| Default application port | 8080 |

The project uses Spring Boot's Maven parent and Spring Boot Maven plugin. No Maven Wrapper is present in the repository.

## 2. Application architecture

```text
Browser
  │ HTTP request / server-rendered HTML
  ▼
Spring MVC Controllers
  │ call services; populate Thymeleaf model
  ▼
Service layer
  │ validation and circulation rules; transactions
  ▼
Spring Data JPA repositories
  │ Hibernate ORM
  ▼
MySQL tables: books, students, issued_books
```

### Packages

- `controller/`: request mappings and Thymeleaf page actions (`BookController`, `StudentController`, `IssuedBookController`, `DashboardController`, `ReportController`).
- `service/`: application and business operations for books, students, and issued books.
- `repository/`: Spring Data JPA interfaces and derived/custom queries.
- `entity/`: persistence entities `Book`, `Student`, and `IssuedBook`.
- `src/main/resources/templates/`: Thymeleaf pages.
- `src/main/resources/application.properties`: application and datasource configuration.

The main entry point is `LibraryManagementSystemApplication` in the root application package.

## 3. HTTP routes

All routes below are server-rendered web routes. Unless otherwise noted, list/search endpoints accept `page` (default `0`) and `size` (default `10`); controllers clamp page to at least `0` and size to `1..100`.

| Method | Route | Behavior |
| --- | --- | --- |
| GET | `/` | Dashboard counts and recent issue records |
| GET | `/books` | Paginated book list |
| GET | `/books/add` | New-book form |
| POST | `/books/save` | Validate and create a book |
| GET | `/books/search` | Search title, author, or ISBN; query parameter `keyword` |
| GET | `/books/edit/{id}` | Load book edit form |
| POST | `/books/update/{id}` | Validate and update a book |
| GET | `/books/delete/{id}` | Delete a book and redirect to the list |
| GET | `/students` | Paginated student list |
| GET | `/students/add` | New-student form |
| POST | `/students/save` | Validate and create a student |
| GET | `/students/search` | Search name, roll number, or email; query parameter `keyword` |
| GET | `/students/edit/{id}` | Load student edit form |
| POST | `/students/update/{id}` | Validate and update a student |
| GET | `/students/delete/{id}` | Delete a student and redirect to the list |
| GET | `/issued-books` | Paginated circulation list |
| GET | `/issued-books/search` | Search/filter records; parameters `keyword` and `status` (`ISSUED` or `RETURNED`) |
| GET | `/issued-books/issue` | Load issue form |
| POST | `/issued-books/save` | Create an issue record; parameters `studentId`, `bookId`, and `dueDate` |
| GET | `/issued-books/return/{id}` | Return an issue record |
| GET | `/issued-books/overdue` | List overdue issue records |
| GET | `/reports` | Summary metrics and date-wise report; parameters `fromDate`, `toDate`, `reportType` |

**Implementation note:** Delete and return actions are currently mapped to GET routes. They change stored state. This documents the implementation as it exists; changing these to state-changing HTTP methods and adding appropriate access controls would be a separate code change.

## 4. Data model

See [DATABASE.md](DATABASE.md) for mapped columns and [ER_DIAGRAM.md](ER_DIAGRAM.md) for the entity relationship diagram.

- `Book` maps to `books`. It stores title, author, unique ISBN, optional category, total quantity, and available copies.
- `Student` maps to `students`. It stores name, unique roll number, unique email, optional phone, and optional course.
- `IssuedBook` maps to `issued_books`. It references one student and one book and stores issue, due, and optional return dates plus a string status.
- `IssuedBook.student` and `.book` are `@ManyToOne` relationships with required `student_id` and `book_id` join columns.
- Primary keys are generated `Long` IDs. The issue status is a string (`ISSUED` or `RETURNED`), not a Java enum.

## 5. Validation and business behavior

### Books

- Title and author are required and limited to 100 characters.
- ISBN is required and unique.
- Quantity must be at least 1; available copies cannot be negative under the entity validation rule.
- On creation, available copies are initialized to quantity.
- On update, the service calculates issued copies as `quantity - availableCopies`, then applies the new quantity while preserving the issued-copy count.
- Search matches title, author, or ISBN case-insensitively.

### Students

- Name is required and limited to 100 characters.
- Roll number is required, unique, and limited to 30 characters.
- Email is required, unique, and validated as an email address.
- Phone is optional; a provided value must contain exactly 10 digits.
- Course is optional. Search matches name, roll number, or email case-insensitively.

### Circulation

**Issue:** `IssuedBookService.issueBook` loads the student and book, rejects a book with no available copies, and requires a due date on or after the current date. It creates an issue record with today's issue date and `ISSUED` status, decrements available copies, and saves the record and book in a transaction.

**Return:** `returnBook` rejects missing or already returned records. It sets today's return date, changes status to `RETURNED`, increments available copies, and saves the updates in a transaction.

**Overdue:** a record is overdue when its status is `ISSUED` and its due date is before the current date.

### Reports and dashboard metrics

- The report form defaults to the last 30 days through today and report type `ALL`.
- `ALL` returns issue records whose issue date is in the selected range, regardless of current status.
- `ISSUED` returns records with status `ISSUED` and issue date in the range.
- `RETURNED` returns records with status `RETURNED` and return date in the range.
- A start date after the end date produces an error in the report view.
- The dashboard counts book rows, student rows, records currently marked issued/returned, and overdue records; it also shows recent issue records.

## 6. Configuration

The principal settings in `application.properties` are:

| Setting | Current behavior |
| --- | --- |
| `server.port` | 8080 |
| `spring.datasource.url` | Local MySQL at port 3306, schema `library_management`, Asia/Kolkata server timezone |
| `spring.datasource.driver-class-name` | MySQL Connector/J driver |
| `spring.jpa.hibernate.ddl-auto` | `update` (Hibernate updates mapped schema at startup) |
| `spring.jpa.show-sql` | SQL logging enabled |
| `spring.jpa.properties.hibernate.format_sql` | Formats SQL log output |

Datasource usernames and passwords are intentionally not reproduced in this document. Supply local credentials through Spring Boot environment variables such as `SPRING_DATASOURCE_USERNAME` and `SPRING_DATASOURCE_PASSWORD`, or through a local configuration excluded from version control. Review `ddl-auto=update` and SQL logging before using the application with important data.

Bootstrap CSS is referenced from the jsDelivr CDN by the templates, so styling depends on browser access to that external asset.

## 7. Build and run

From the project root, with Java 21, Maven, and a configured MySQL database available:

```bash
mvn spring-boot:run
```

To build and run the packaged JAR:

```bash
mvn clean package
java -jar target/library-management-0.0.1-SNAPSHOT.jar
```

For database creation and environment-variable examples, use [README.md](README.md).

## 8. Tests

The repository contains one Spring Boot context-load test, `LibraryManagementSystemApplicationTests.contextLoads()`. It is a startup smoke test and does not exercise the feature flows. The manual checklist is in [TESTING.md](TESTING.md); the project team reports prior successful testing, while this documentation task did not execute the tests.

## 9. Related project documentation

- [README.md](README.md): overview, prerequisites, setup, and run instructions
- [DATABASE.md](DATABASE.md): database model and local configuration guidance
- [ER_DIAGRAM.md](ER_DIAGRAM.md): entity relationships
- [DFD_DIAGRAMS.md](DFD_DIAGRAMS.md): process and data flows
- [TESTING.md](TESTING.md): reported verification and manual test checklist
- [USER_MANUAL.md](USER_MANUAL.md): end-user instructions
- [SCREENSHOTS.md](SCREENSHOTS.md): supplied interface screenshots
