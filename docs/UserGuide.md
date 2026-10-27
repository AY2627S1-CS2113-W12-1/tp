# CakeQueue V1 User Guide

CakeQueue helps bakers record, list, view, and edit cake orders from the command line.

Orders are stored in memory only. Closing the application clears all orders, and IDs restart
from `O001` in the next session.

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

After the order is added, CakeQueue also shows the customer's ID. Customers are identified by
phone number: the first phone number to place an order becomes `C001`, the next `C002`, and so on.

```text
Customer ID: C001 (new customer)
```

If the phone number already has an order, it shows `Customer ID: C001 (returning customer)`.

Example:

```text
add n/Alice Tan p/91234567 d/20-10-2026 s/8 f/Chocolate fi/Strawberry
```

## Listing Upcoming Orders: `list`

Format: `list`

Shows orders that have not been collected, including overdue orders, with the earliest due date
first. Orders with the same due date keep their original insertion order.
Legacy orders without a due date appear last.

List numbers indicate display positions only. Use an Order ID such as `O001` for `view` and `edit`.
Listing orders does not change their IDs or their stored order.

If no orders are available, CakeQueue shows `You have no upcoming orders.`

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
customer (more than one order, including cancelled ones). The name is taken from their most
recent order. Customer IDs are not case-sensitive: `customer c001` also works.

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
for `add` apply to supplied fields. The Order ID and status remain unchanged.
Invalid input or an unknown ID leaves existing orders unchanged.

Examples:

```text
edit O001 s/10
edit O001 d/22-10-2026 f/Vanilla
```

`update` is also accepted as an alias: `update O001 s/10`.

## Exiting: `bye`

Format: `bye`

Exits CakeQueue. Orders from this session are not saved.

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

This version does not support saving to disk, cancellation, searching, or changing order status
through the CLI. Customer IDs are worked out from the phone numbers on saved orders, so editing
the phone number on a customer's first order can change the IDs of later customers.