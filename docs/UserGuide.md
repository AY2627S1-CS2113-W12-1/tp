# CakeQueue User Guide

CakeQueue helps bakers record, list, view, and edit cake orders from the command line.

This page covers adding orders, recording customer details, and the basic baker commands.
Enter `help` in the application to see all commands available in your current mode.

Successful changes are saved automatically to `data/cakequeue.txt`, relative to the folder
you launch the application from. When you reopen CakeQueue from the same folder, saved
records are loaded and Order IDs continue after the existing IDs. Customer IDs are saved with
the orders too.

## Quick Start

1. Install Java 25 and check it with `java -version`.
2. Obtain `cakequeue.jar`, or build it from the project folder:
   - Windows: `.\gradlew.bat shadowJar`
   - macOS/Linux: `./gradlew shadowJar`
3. Run the JAR with assertions enabled:
   - After building: `java -ea -jar build/libs/cakequeue.jar`
   - For a downloaded JAR: `java -ea -jar cakequeue.jar`
4. Enter `baker` at the mode prompt to use the commands below.
5. Enter a command and press Enter. Enter `bye` to exit.

Alternatively, run from the project folder with `.\gradlew.bat run` on Windows or
`./gradlew run` on macOS/Linux.

## Command Format

* Words in uppercase are values you supply, such as `NAME` and `DATE`.
* Square brackets mark optional fields; do not type the brackets.
* Commands and Order IDs are case-insensitive: `view o001` also works.
* Use lowercase field prefixes (`n/`, `p/`, `d/`, `s/`, `f/`, and `fi/`).
* Fields may appear in any order, with whitespace between fields. Values may contain spaces.
* Dates must be real dates in `DD-MM-YYYY` format.
* Cake sizes must be `6`, `8`, or `10` inches.

## Adding an Order: `add`

Format: `add n/NAME p/PHONE d/DATE s/SIZE f/FLAVOUR fi/FILLING`

All six fields are required and must be non-empty. Duplicate and unknown prefixes are rejected.

| Field | What to enter |
| --- | --- |
| `n/NAME` | The customer's name, including spaces, punctuation, or Unicode characters if needed. |
| `p/PHONE` | A non-empty phone number. Numbers such as `+65 9123-4567` are accepted; no fixed format is enforced. |
| `d/DATE` | A real date in `DD-MM-YYYY` format, such as `30-10-2026`. Past dates are accepted. |
| `s/SIZE` | `6`, `8`, or `10`, without the word `inches`. |
| `f/FLAVOUR` | A non-empty cake flavour, such as `Dark Chocolate`. |
| `fi/FILLING` | A non-empty cake filling, such as `Strawberry`. |

Spaces at the start and end of each value are removed. Spaces inside values are preserved.
Do not put field prefixes inside values: a separate word such as `n/` starts a new field.

CakeQueue assigns a unique Order ID, sets the order to `QUEUED`, and shows a confirmation
with all its details. Invalid input does not add an order or use up an Order ID.
Each order stores its own customer details; adding the same name or phone again creates
another order.

After the order is added, CakeQueue also shows the customer's ID. A new phone number gets the next
unused Customer ID (`C001`, then `C002`, and so on). A phone number that already has an order reuses
that customer's ID. The ID is saved with the order and never changes, even if the phone number is
edited later.

```text
Customer ID: C001 (new customer)
```

If the phone number already has an order, it shows `Customer ID: C001 (returning customer)`.

Use the Order ID printed by your application, such as `O001`, to view or edit the order.

If you omit the name, the command fails with:

```text
Error: Missing value for n/. Please provide a customer name using n/NAME.
```

An impossible date gives `Error: Date must use DD-MM-YYYY and be valid.`
An unsupported size gives `Error: Cake size must be 6, 8, or 10 inches.`
Correct the input and enter the full command again.

If adding the order puts its due date over the daily capacity, the order is still added.
CakeQueue displays a warning with the date, number of active orders, and current limit.
Use `capacity` to check the limit and overloaded dates.

If a save fails after adding an order, CakeQueue displays:

```text
Warning: Could not save your data. Your changes are kept for this session only.
```

The order remains available while the app is open. Keep the app open, check that its
data folder is writable, then make a successful change to retry saving the current records.

Example:

```text
add n/Alice Tan p/91234567 d/20-10-2026 s/8 f/Chocolate fi/Strawberry
```
If the due date is already over your daily capacity, CakeQueue still adds the order and shows a
warning. See [`capacity`](#setting-a-daily-capacity-capacity).

### Cake Details Validation

- Cake sizes must be 6, 8, or 10 inches.
- Cake flavours and fillings cannot be empty or contain only whitespace.
- Invalid cake details are rejected with an error message.

## Listing Upcoming Orders: `list`

Format: `list`

Shows active confirmed orders, including overdue orders, with the earliest due date first.
Enquiries, cancelled, rejected, and collected records are excluded.
Orders with the same due date keep their original insertion order.
Legacy orders without a due date appear last.

### Due-Date Warnings

CakeQueue highlights orders with approaching or missed deadlines:

- `[OVERDUE]` appears when an order's due date is before today.
- `[DUE SOON]` appears when an order is due today or within the next 3 days.
- Orders due more than 3 days away do not display a warning.
- Orders without a due date do not display a warning.

These warnings are calculated using the current date whenever `list` is executed.

List numbers indicate display positions only. Use an Order ID such as `O001` for `view` and `edit`.
Listing orders does not change their IDs or their stored order.

If no orders are available, CakeQueue shows `You have no upcoming orders.`

An order whose due date has passed ends with `[OVERDUE]`. An order due within the next 3 days
(including today) ends with `[DUE SOON]`.

Enquiries and orders that are collected, cancelled or rejected are not shown. Use `enquiries` for
enquiries and `find` or `view` for any record.

## Finding Orders: `find`

Format: `find KEYWORD`

Shows every order and enquiry whose customer name, cake flavour or cake filling contains the
keyword.

* The search ignores upper and lower case: `find alice` finds `Alice Tan`.
* The keyword can be part of a word: `find choc` finds `Chocolate`.
* A keyword with spaces is searched as one phrase: `find Alice Tan` finds `Alice Tan` but not
  `Tan Alice`.
* All records are searched, including enquiries and collected or cancelled orders. Each result
  ends with its status.

Examples:

```text
find alice
find chocolate
```

If nothing matches, CakeQueue shows `No orders found matching "KEYWORD".`

## Viewing an Order: `view`

Format: `view ORDER_ID`

Shows the order's ID, customer name and phone, due date, cake size, flavour, filling, and status.
For a confirmed order (ID starting with `O`), the details also end with the customer's ID.

Example: `view O001`

```text
Order ID: O001
Customer: Alice Tan (91234567)
Due date: 20-10-2026
Size: 8 inches
Flavour: Chocolate
Filling: Strawberry
Status: QUEUED
Customer ID: C001
```

If the order does not exist, CakeQueue shows `Error: Order not found: ORDER_ID`.

## Viewing a Customer: `customer`

Format: `customer CUSTOMER_ID`

Shows a customer's ID, name, phone number, number of orders, and whether they are a repeat
customer (more than one order, including cancelled ones). The name and phone number are taken from
their most recent order. Customer IDs are not case-sensitive: `customer c001` also works.

Example: `customer C001`

```text
Customer ID: C001
Name: Alice Tan
Phone: 91234567
Orders: 2
Repeat customer: Yes
```

If the customer does not exist, CakeQueue shows `Error: Customer not found: CUSTOMER_ID`.

## Viewing a Customer's History: `history`

Format: `history CUSTOMER_ID`

Lists every order placed by that customer, oldest first, including cancelled and collected
orders. Each line ends with the order's status. Enquiries are not included until they are accepted.

Example: `history C001`

```text
Order history for Alice Tan (C001):
1. O001: Alice Tan (91234567) | Due: 22-12-2026 | Size: 8 inches | Flavour: Chocolate | Filling: Strawberry [QUEUED]
2. O003: Alice Tan (91234567) | Due: 25-12-2026 | Size: 6 inches | Flavour: Vanilla | Filling: Cream [CANCELLED]
```

## Editing an Order: `edit`

Format: `edit ORDER_ID [n/NAME] [p/PHONE] [d/DATE] [s/SIZE] [f/FLAVOUR] [fi/FILLING]`

Provide at least one field. Omitted fields remain unchanged, and the same validation rules used
for `add` apply to supplied fields. The Order ID, Customer ID and status remain unchanged.
Invalid input or an unknown ID leaves existing orders unchanged.
Changing the name on one order does not change names on other orders with the same phone.

Examples:

```text
edit O001 s/10
edit O001 d/22-10-2026 f/Vanilla
```

`update` is also accepted as an alias: `update O001 s/10`.

If the new due date is over your daily capacity, CakeQueue still saves the change and shows a warning.

## Setting a Daily Capacity: `capacity`

Format: `capacity [NUMBER]`

Sets the most orders you can take for one due date, from 1 to 50. The default is 5 orders per day.

Without a number, `capacity` shows the current limit and lists any due dates already over it.
Only active orders count: those with status `QUEUED`, `PREPARING` or `READY`.
Enquiries and collected, cancelled or rejected records do not count.

When `add`, `accept` or `edit` leaves a due date over the limit, CakeQueue still saves the change and shows a
warning, for example: `Warning: 21-10-2026 now has 4 orders, over your daily capacity of 3.`
The limit is not saved when you exit. It returns to 5 the next time you start CakeQueue.

Examples:

```text
capacity
capacity 3
```

## Exiting: `bye`

Format: `bye`

Exits CakeQueue. Successfully saved changes remain available when you launch the app
again from the same folder. Check for save warnings before exiting.

## Command Summary

| Action | Command |
| --- | --- |
| Add order | `add n/NAME p/PHONE d/DATE s/SIZE f/FLAVOUR fi/FILLING` |
| List orders | `list` |
| Daily capacity | `capacity [NUMBER]` |
| View order | `view ORDER_ID` |
| View customer | `customer CUSTOMER_ID` |
| View customer history | `history CUSTOMER_ID` |
| Edit order | `edit ORDER_ID [n/NAME] [p/PHONE] [d/DATE] [s/SIZE] [f/FLAVOUR] [fi/FILLING]` |
| Update alias | `update ORDER_ID [n/NAME] [p/PHONE] [d/DATE] [s/SIZE] [f/FLAVOUR] [fi/FILLING]` |
| Available commands | `help` |
| Exit | `bye` |

## Current Limitations

A Customer ID is given out when an order is first added and never changes. If a phone number is
edited, the order keeps its original Customer ID, and a later order with the old phone number is
treated as a new customer only if no other order still uses that phone number.

Phone numbers have no fixed format. Daily capacity is a warning threshold and is not saved
between runs. Use `help` for the existing cancellation, search, and order-status commands.
