# CakeQueue User Guide

CakeQueue helps bakers record, list, view, and edit cake orders from the command line.

Orders are saved automatically to `data/cakequeue.txt` after every change and loaded again the next
time CakeQueue starts. Customer IDs are saved with the orders.

## Quick Start

1. Install Java 25 and check it with `java -version`.
2. Obtain `cakequeue.jar`, or build it from the project folder:
   - Windows: `.\gradlew.bat shadowJar`
   - macOS/Linux: `./gradlew shadowJar`
3. Run the JAR with assertions enabled:
   - After building: `java -ea -jar build/libs/cakequeue.jar`
   - For a downloaded JAR: `java -ea -jar cakequeue.jar`
4. Enter a command and press Enter. Enter `bye` to exit.

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
The phone number is recorded as entered; this version does not impose a phone-number format.

CakeQueue assigns a unique Order ID within the session, sets the order to `QUEUED`, and shows
a confirmation with all its details. Invalid input does not add an order.

After the order is added, CakeQueue also shows the customer's ID. A new phone number gets the next
unused Customer ID (`C001`, then `C002`, and so on). A phone number that already has an order reuses
that customer's ID. The ID is saved with the order and never changes, even if the phone number is
edited later.

```text
Customer ID: C001 (new customer)
```

If the phone number already has an order, it shows `Customer ID: C001 (returning customer)`.

Example:

```text
add n/Alice Tan p/91234567 d/20-10-2026 s/8 f/Chocolate fi/Strawberry
```
If the due date is already over your daily capacity, CakeQueue still adds the order and shows a
warning. See [`capacity`](#setting-a-daily-capacity-capacity).

## Listing Upcoming Orders: `list`

Format: `list`

Shows orders that have not been collected, including overdue orders, with the earliest due date
first. Orders with the same due date keep their original insertion order.
Legacy orders without a due date appear last.

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

Examples:

```text
edit O001 s/10
edit O001 d/22-10-2026 f/Vanilla
```

`update` is also accepted as an alias: `update O001 s/10`.

If the new due date is over your daily capacity, CakeQueue still saves the change and shows a warning.

## Setting a Daily Capacity: capacity

Format: `capacity [NUMBER]`

Sets the most orders you can take for one due date, from 1 to 50. The default is 5 orders per day.

Without a number, capacity shows the current limit and lists any due dates already over it.
Only active orders count: those with status QUEUED, PREPARING or READY.
Enquiries and collected, canceled or rejected records do not count.

When `add`, `accept` or `edit` leaves a due date over the limit, CakeQueue still saves the change and shows a warning,
for example: Warning: 21-10-2026 now has 4 orders, over your daily capacity of 3.
The limit is not saved when you exit. It returns to 5 the next time you start CakeQueue.

Examples:

```text
capacity
capacity 3
```

## Exiting: `bye`

Format: `bye`

Exits CakeQueue. Your orders are already saved after each change.

## Command Summary

| Action | Command |
| --- | --- |
| Add order | `add n/NAME p/PHONE d/DATE s/SIZE f/FLAVOUR fi/FILLING` |
| List orders | `list` |
| View order | `view ORDER_ID` |
| View customer | `customer CUSTOMER_ID` |
| View customer history | `history CUSTOMER_ID` |
| Edit order | `edit ORDER_ID [n/NAME] [p/PHONE] [d/DATE] [s/SIZE] [f/FLAVOUR] [fi/FILLING]` |
| Update alias | `update ORDER_ID [n/NAME] [p/PHONE] [d/DATE] [s/SIZE] [f/FLAVOUR] [fi/FILLING]` |
| Exit | `bye` |

## Current Limitations

A Customer ID is given out when an order is first added and never changes. If a phone number is
edited, the order keeps its original Customer ID, and a later order with the old phone number is
treated as a new customer.