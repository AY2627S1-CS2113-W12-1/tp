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

Example: `view O001`

```text
Order ID: O001
Customer: Alice Tan (91234567)
Due date: 20-10-2026
Size: 8 inches
Flavour: Chocolate
Filling: Strawberry
Status: QUEUED
```

If the order does not exist, CakeQueue shows `Error: Order not found: ORDER_ID`.

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
| Edit order | `edit ORDER_ID [n/NAME] [p/PHONE] [d/DATE] [s/SIZE] [f/FLAVOUR] [fi/FILLING]` |
| Update alias | `update ORDER_ID [n/NAME] [p/PHONE] [d/DATE] [s/SIZE] [f/FLAVOUR] [fi/FILLING]` |
| Exit | `bye` |

## Current Limitations

This version does not support saving to disk, cancellation, searching, customer IDs, customer
history, repeat-customer detection, or changing order status through the CLI.
