# Testing Plan

This document records a prior verification reported by the project team and provides a manual checklist for future or repeatable checks. The cases are derived from the current routes, controller/service behavior, validation rules, and supplied application screenshots.

**Previous verification (project-team report):** The user reports that they tested the project earlier and all features worked correctly. The exact test date, application revision, and individual case results were not provided. The detailed checklist below was prepared for documentation and was not run or independently verified while preparing this file.

## Test environment

- Java 21 and Maven
- MySQL with a disposable `library_management` database
- Application running at `http://localhost:8080`
- Browser access to the dashboard and management pages

Use a disposable database or backup before testing create, update, and delete flows. The application is configured with Hibernate `ddl-auto=update`; use local datasource credentials through environment variables or local configuration, and do not add credentials to this document or commit them.

## Execution checklist

The checklist result column remains unmarked because the previously reported testing was not mapped to these individual cases. For a future repeat run, record each result only after carrying out that specific case.

| ID | Area | Manual action | Expected result | Result |
| --- | --- | --- | --- | --- |
| APP-01 | Startup | Start the application with MySQL available and open `/`. | Dashboard renders without a connection or template error. | ☐ Not run |
| APP-02 | Dashboard | Compare total books, students, issued, returned, and overdue counts with the records in the test database. | Counts match the stored records and the recent issue table is displayed. | ☐ Not run |
| BOOK-01 | Books | Open `/books`; add a book with valid title, author, unique ISBN, category, and quantity. | Book appears in the list; available copies start at the entered quantity. | ☐ Not run |
| BOOK-02 | Validation | Submit the add-book form with a missing title, author, or ISBN, and then with quantity below 1. | Form stays open and shows validation feedback; no invalid book is added. | ☐ Not run |
| BOOK-03 | Duplicate ISBN | Try to add a book using an ISBN already in use. | Duplicate ISBN is rejected and the existing record remains unchanged. | ☐ Not run |
| BOOK-04 | Search | Search by a known title, author, and ISBN, then search for a value with no match. | Matching rows appear for each field; an unmatched search shows no matching results. | ☐ Not run |
| BOOK-05 | Edit and pagination | Edit a book, change quantity, and browse with a small page size when enough records exist. | Changes appear in the list; page navigation preserves search and page-size selections. | ☐ Not run |
| BOOK-06 | Delete | Delete a disposable test book from the list. | The selected book is removed and the list count updates. | ☐ Not run |
| STUDENT-01 | Students | Add a student with valid required details; leave optional phone/course blank in a second test. | Student records save and appear in the list; optional fields may be empty. | ☐ Not run |
| STUDENT-02 | Validation | Submit without a name, roll number, or email; try an invalid email and a non-empty phone that is not 10 digits. | Form stays open with validation feedback; invalid data is not saved. | ☐ Not run |
| STUDENT-03 | Duplicate details | Try to save a student with an existing roll number or email. | The duplicate field is rejected and no duplicate student is created. | ☐ Not run |
| STUDENT-04 | Search, edit, delete | Search by name, roll number, and email; edit a disposable record; delete a disposable record. | Search returns matching records; edit updates the selected student; delete removes it. | ☐ Not run |
| CIRC-01 | Issue a book | Select a student, a book with available copies, and a valid due date on or after today; submit. | A new `ISSUED` record appears and the book's available-copy count decreases by one. | ☐ Not run |
| CIRC-02 | Issue validation | Try a past due date and a book with no available copies in the disposable test database. | Issue is rejected, feedback is shown, and no new issue record is created. | ☐ Not run |
| CIRC-03 | Return a book | Return an issued test record from the issued-books list. | Status becomes `RETURNED`, return date is recorded, and available copies increase by one. | ☐ Not run |
| CIRC-04 | Repeat return | Attempt to return a record already marked returned. | Duplicate return is rejected and available copies are not incremented again. | ☐ Not run |
| CIRC-05 | Search and status filter | Search issue records by student/book keyword; filter by `ISSUED` and `RETURNED`; try pagination. | Results match the keyword/status and page navigation keeps the current filters. | ☐ Not run |
| CIRC-06 | Overdue list | In a disposable database, prepare an issued record with a due date before today and open `/issued-books/overdue`. | Only currently issued records past their due date appear; an empty result has a clear empty state. | ☐ Not run |
| REPORT-01 | Date-wise reports | Open `/reports`; select a valid date range and each report type: `ALL`, `ISSUED`, and `RETURNED`. | Results reflect the selected date range and status; summary counts are displayed. | ☐ Not run |
| REPORT-02 | Date validation | Select a start date after the end date. | The report view displays a date-range error and does not present misleading results. | ☐ Not run |

### Notes on overdue test data

The issue workflow rejects due dates earlier than today. To test an overdue record, create the fixture only in a disposable test database (for example, insert an `ISSUED` record with a past due date using a valid student and book). Do not modify important or production data for this test.

## Existing automated test

The repository contains `LibraryManagementSystemApplicationTests.contextLoads()`, a Spring Boot application-context smoke test. It does not cover the manual feature cases above. The test suite was not run while preparing this document.

For a future automated rerun, start MySQL, configure the local datasource, then run from the project root:

```bash
mvn test
```

## Test record

- Tester: Project team (previous testing reported by user)
- Date: ____________________
- Application revision: ____________________
- Database/environment: ____________________
- Summary and follow-up issues: User reports prior testing found all project features working; individual results and follow-up issues were not supplied.


