# Library Management System

A web-based Library Management System developed as a BCA final-year major project. It helps library staff organize books and student records, manage book issue and return activity, identify overdue books, and review circulation reports.

## Features

- Dashboard with book, student, issue, return, and overdue counts, plus recent issue activity
- Book management with create, view, update, delete, search, pagination, and form validation
- Student management with create, view, update, delete, search, pagination, and form validation
- Issue books to students and record returns
- Search and filter issued-book records, with pagination
- Dedicated overdue-books view
- Date-wise reports with `ALL`, `ISSUED`, and `RETURNED` filters
- Server-rendered pages styled with Bootstrap

## Technology stack

| Area | Technology |
| --- | --- |
| Language | Java 21 |
| Application framework | Spring Boot 3.5.7 |
| Build | Maven |
| Persistence | Spring Data JPA / Hibernate |
| Database | MySQL (`library_management`) |
| View layer | Thymeleaf |
| UI styling | Bootstrap 5.3.3 |

## Prerequisites

- JDK 21
- Maven 3.6.3 or later
- MySQL Server (running locally or reachable from the application)
- A MySQL account with permission to create/use the project database

## Setup and run

1. Clone or download this repository and open a terminal in the project directory.
2. Create the database in MySQL:

   ```sql
   CREATE DATABASE library_management;
   ```

3. Configure the database connection. The application reads Spring Boot's standard datasource settings. Set your own account details as environment variables before starting it; do not commit real credentials.

   **PowerShell:**

   ```powershell
   $env:SPRING_DATASOURCE_URL = "jdbc:mysql://localhost:3306/library_management?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Kolkata"
   $env:SPRING_DATASOURCE_USERNAME = "<your-mysql-username>"
   $env:SPRING_DATASOURCE_PASSWORD = "<your-mysql-password>"
   ```

   **macOS/Linux shell:**

   ```bash
   export SPRING_DATASOURCE_URL='jdbc:mysql://localhost:3306/library_management?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Kolkata'
   export SPRING_DATASOURCE_USERNAME='<your-mysql-username>'
   export SPRING_DATASOURCE_PASSWORD='<your-mysql-password>'
   ```

   The same settings can be placed in `src/main/resources/application.properties` for local use, but keep private credentials out of version control. The checked-in configuration uses Hibernate `ddl-auto=update`, so Hibernate updates the schema when the application starts. Review this setting before using the project with important data.

4. Start the application from the repository root:

   ```bash
   mvn spring-boot:run
   ```

   Alternatively, build an executable JAR and run it:

   ```bash
   mvn clean package
   java -jar target/library-management-0.0.1-SNAPSHOT.jar
   ```

5. Open [http://localhost:8080](http://localhost:8080) in a browser.

The project does not currently include a Maven Wrapper, so Maven must be installed separately.

## Main routes

| Route | Purpose |
| --- | --- |
| `/` | Dashboard |
| `/books` | Browse books (supports `page` and `size`) |
| `/books/add` | Open the add-book form |
| `/books/search?keyword=...` | Search books (also supports `page` and `size`) |
| `/students` | Browse students (supports `page` and `size`) |
| `/students/add` | Open the add-student form |
| `/students/search?keyword=...` | Search students (also supports `page` and `size`) |
| `/issued-books` | Browse issued-book records |
| `/issued-books/search?keyword=...&status=...` | Search/filter issue records (supports `page` and `size`) |
| `/issued-books/issue` | Open the issue-book form |
| `/issued-books/overdue` | View overdue books |
| `/reports` | Dashboard and date-wise report; accepts `fromDate`, `toDate`, and `reportType` (`ALL`, `ISSUED`, or `RETURNED`) |

Book and student edit, update, and delete actions are available from their respective list pages. Issue and return actions are available from the issued-books area.

## Project structure

```text
src/
└── main/
    ├── java/com/library/library/management/system/
    │   ├── controller/     # Web routes and page actions
    │   ├── entity/          # Book, Student, and IssuedBook entities
    │   ├── repository/      # Spring Data repositories
    │   ├── service/         # Library and circulation logic
    │   └── LibraryManagementSystemApplication.java
    └── resources/
        ├── templates/       # Thymeleaf pages
        └── application.properties
pom.xml
```

## Contributors

**BCA Session 2024–2026**  
Alisha Gupta · Sweta Srivastava

**Department/Institution:** Microtek College of Management and Technology  
**Affiliation:** Mahatma Gandhi Kashi Vidyapith  
**Project Guide:** Mr. Ravi Bhushan Tiwari

