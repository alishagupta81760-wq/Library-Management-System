# User Manual

This guide explains how to use the Library Management System through its web interface. It is intended for the library staff member maintaining the book catalogue and student records and recording book circulation.

## 1. Start the application

Follow the setup instructions in [README.md](README.md), including the local MySQL configuration. Start the Spring Boot application and open:

[http://localhost:8080](http://localhost:8080)

The application opens on the dashboard. For screenshots of the current interface, see [SCREENSHOTS.md](SCREENSHOTS.md).

## 2. Use the dashboard

The dashboard summarizes total books, students, currently issued books, returned books, and overdue books. It also shows recent circulation activity and shortcuts to Books, Students, Issue Book, Issued Books, Overdue Books, and Reports & Analytics.

Use **View Books**, **View Students**, **View Issued**, **View Returned**, or **View Overdue** to open the corresponding list. Use **Reports** or **Reports & Analytics** to open reporting.

## 3. Manage books

### Add a book

1. Open **Manage Books** from the dashboard.
2. Select **Add New Book**.
3. Enter the book title, author, ISBN, category, and quantity.
4. Select **Save Book**.

Title, author, and ISBN are required. ISBN values must be unique. Quantity must be at least 1. On creation, available copies are set to the entered quantity.

### Search, edit, and delete books

- Search by book title, author, or ISBN, then select **Search**. Use **Reset** to clear the search.
- Select **Edit** in a book row to update its details. Save the changes from the edit form.
- Select **Delete** to remove a book. Confirm the browser prompt to continue.
- Use the records-per-page control and page links to move through longer lists.

When quantity is changed during an edit, the application accounts for copies already issued when updating the available-copy count.

## 4. Manage students

### Add a student

1. Open **Manage Students** from the dashboard.
2. Select **Add Student**.
3. Enter the student's name, roll number, and email. Phone and course can also be entered.
4. Select a course from the available list (BCA, MCA, BBA, MBA, or Other) if applicable.
5. Select **Save Student**.

Name, roll number, and email are required. Roll number and email must be unique. Email must be valid. Phone is optional; if provided, it must contain exactly 10 digits.

### Search, edit, and delete students

- Search by student name, roll number, or email.
- Select **Edit** to update a record and save the changes.
- Select **Delete** and confirm the browser prompt to remove the student.
- Use the records-per-page control and page links to browse longer lists.

## 5. Issue a book

1. Open **Issue Book** from the dashboard or **Issue New Book** from the Issued Books page.
2. Choose a student and a book. The book list shows the number of available copies.
3. Check the issue date shown on the form and enter a due date.
4. Select **Issue Book**.

The selected student and book must exist, the book must have at least one available copy, and the due date must be today or later. After a successful issue, the record appears with status `ISSUED` and the available-copy count decreases by one. If an issue cannot be completed, the form shows an error message.

## 6. View and return books

Open **Issued Books** to see circulation records. Use the search field to find a student or book, and use **Filter by Status** to choose all statuses, `ISSUED`, or `RETURNED`. The records-per-page control and page links help navigate the list.

To return a book:

1. Find the record with status `ISSUED`.
2. Select **Return Book**.
3. Confirm the browser prompt.

The application records the return date, changes the status to `RETURNED`, and increases the book's available-copy count. A returned record displays as completed and cannot be returned again.

## 7. View overdue books

Select **Overdue Books** from the dashboard. The page lists records that are still `ISSUED` and have a due date before the current date. If no records meet that condition, the page displays an empty-state message. An overdue book can be returned from this page using **Return Book**.

## 8. Generate date-wise reports

1. Open **Reports** from the dashboard navigation or **Reports & Analytics** from Quick Actions.
2. Set **From Date** and **To Date**.
3. Choose a report type:
   - **All Records**: issue records with an issue date in the selected range.
   - **Issued Books**: records with status `ISSUED` and an issue date in the selected range.
   - **Returned Books**: records with status `RETURNED` and a return date in the selected range.
4. Select **Generate Report**.

The page displays summary counts and matching records. The start date must not be after the end date. The first visit defaults to the previous 30 days through the current date.

## 9. Common issues

- **Application cannot connect to MySQL:** Confirm MySQL is running, the `library_management` database exists, and the local datasource settings in the [README setup guide](README.md) are correct.
- **A form stays open after Save:** Read the validation message beside the field; check required values, unique ISBN/roll number/email, quantity, email format, or phone length.
- **A book cannot be issued:** Check that the selected book has available copies and the due date is today or later.
- **A report shows no rows:** Check the date range and report type. `Returned Books` uses the return date for its range.

## Screenshots

See the [project screenshot gallery](SCREENSHOTS.md) for the dashboard, catalogue and student pages, circulation screens, and overdue-books view.
