# Data Flow Diagrams

These diagrams summarize how a library staff member interacts with the Library Management System and how the application reads and writes its MySQL data. They are based on the current Spring MVC controllers, services, repositories, and JPA entities.

## Level 0: Context diagram

```mermaid
flowchart LR
    Staff[Library staff]
    LMS((Library Management System))

    Staff -->|Book and student details; issue and return actions; search and report criteria| LMS
    LMS -->|Dashboard, search results, validation feedback, overdue lists, and reports| Staff
```

## Level 1: Main processes and data stores

```mermaid
flowchart LR
    Staff[Library staff]

    subgraph System[Library Management System]
        P1((1.0 Manage books))
        P2((2.0 Manage students))
        P3((3.0 Issue and return books))
        P4((4.0 Search and view circulation))
        P5((5.0 Dashboard and reports))

        D1[(Books)]
        D2[(Students)]
        D3[(Issued books)]
    end

    Staff -->|Book form, edit, delete, or search request| P1
    P1 -->|Book details and search results| Staff
    P1 <-->|Create, read, update, delete, search| D1

    Staff -->|Student form, edit, delete, or search request| P2
    P2 -->|Student details and search results| Staff
    P2 <-->|Create, read, update, delete, search| D2

    Staff -->|Student, book, due date, or return action| P3
    P3 -->|Issue/return confirmation or validation feedback| Staff
    P3 -->|Read selected student| D2
    D2 -->|Student record| P3
    P3 -->|Read and update available copies| D1
    D1 -->|Book record and availability| P3
    P3 <-->|Save issue, return date, and status| D3

    Staff -->|Keyword, status, overdue, or page request| P4
    P4 -->|Matching, filtered, or overdue records| Staff
    P4 -->|Read issue records| D3
    D3 -->|Circulation records| P4
    P4 -->|Read associated book details| D1
    D1 -->|Book details| P4
    P4 -->|Read associated student details| D2
    D2 -->|Student details| P4

    Staff -->|Date range and ALL/ISSUED/RETURNED selection| P5
    P5 -->|Summary counts and date-wise report| Staff
    P5 -->|Read book and availability totals| D1
    D1 -->|Book counts| P5
    P5 -->|Read student totals| D2
    D2 -->|Student counts| P5
    P5 -->|Read status, issue, due, and return data| D3
    D3 -->|Circulation and report records| P5
```

## Process notes

1. **Manage books:** accepts book form actions and searches, then uses the book repository to persist or retrieve records. Book validation includes required title, author, and ISBN fields; ISBN is unique.
2. **Manage students:** accepts student form actions and searches. Student validation includes required name, roll number, and email; roll number and email are unique.
3. **Issue and return books:** issue requests require existing student and book records, available copies, and a valid due date. Issuing decrements available copies. Returning records the return date and `RETURNED` status, then increments available copies.
4. **Search and view circulation:** supports issued-book search/filter and overdue records. Overdue records are currently issued records whose due date is before the current date.
5. **Dashboard and reports:** dashboard figures are derived from the book, student, and circulation records. Date-wise reports accept a start date, end date, and `ALL`, `ISSUED`, or `RETURNED` type.

## Scope

The current project presents server-rendered web pages to a staff user. This diagram does not include authentication or authorization flows because none are represented in the reviewed project routes. Data store names correspond to the JPA entity tables documented in `DATABASE.md`.
