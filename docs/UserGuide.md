# CakeQueue User Guide

CakeQueue helps home bakers record orders, track customer history, check daily workload,
and follow each cake from baking to collection. The baker is the only user of this CLI;
customer details are records managed by the baker. There is no customer login or mode.

Successful changes are saved automatically to `data/cakequeue.txt`, relative to the folder you
launch CakeQueue from. When you reopen CakeQueue from the same folder, saved records are loaded and
new IDs continue after the existing ones.

* [Quick Start](#quick-start)
* [Command Format](#command-format)
* [Commands](#adding-an-order-add)
* [Command Summary](#command-summary)
* [Current Limitations](#current-limitations)
* [Existing Data from Earlier Versions](#existing-data-from-earlier-versions)

## Quick Start

1. Install Java 25 and check it with `java -version`.
2. Download `cakequeue.jar` from the release matching this guide, or build it from the same
   source version as this guide:
   - Windows: `.\gradlew.bat shadowJar`
   - macOS/Linux: `./gradlew shadowJar`
3. Run the JAR with assertions enabled:
   - After building: `java -ea -jar build/libs/cakequeue.jar`
   - For a downloaded JAR: `java -ea -jar cakequeue.jar`
4. CakeQueue shows `Baker mode. Type help to see commands.` Enter a command and press Enter.
5. Enter `help` to see all commands, and `bye` to exit.

Alternatively, run from the project folder with `.\gradlew.bat run` on Windows or
`./gradlew run` on macOS/Linux.

## Command Format

* Words in uppercase are values you supply, such as `NAME` and `DATE`.
* Square brackets mark optional fields; do not type the brackets.
* Commands and IDs are case-insensitive: `view o001` also works.
* Use lowercase field prefixes (`n/`, `p/`, `d/`, `s/`, `f/`, and `fi/`).
* Fields may appear in any order, with whitespace between fields. Values may contain spaces.
* `ORDER_ID` is an ID such as `O001`, and `CUSTOMER_ID` is an ID such as `C001`.
  CakeQueue assigns these IDs automatically.
* When adding an order or editing its due date, enter a real date in `DD-MM-YYYY` format,
  today or later. Existing orders can become overdue and remain available.
* Cake sizes must be `6`, `8`, or `10` inches.
* If a command is wrong, CakeQueue shows `Error: ...` and carries on. Nothing is changed.
* Enter one command per line. The available commands are listed in the Command Summary.

## Adding an Order: `add`

Format: `add n/NAME p/PHONE d/DATE s/SIZE f/FLAVOUR fi/FILLING`

All six fields are required and must be non-empty. Duplicate and unknown prefixes are rejected.

| Field | What to enter |
| --- | --- |
| `n/NAME` | The customer's name, including spaces, punctuation, or Unicode characters if needed. |
| `p/PHONE` | At least 8 digits. Spaces, `+` and `-` are allowed, so `+65 9123-4567` is accepted. |
| `d/DATE` | A real date in `DD-MM-YYYY` format, such as `20-12-2030`. Today or later. |
| `s/SIZE` | `6`, `8`, or `10`, without the word `inches`. |
| `f/FLAVOUR` | A non-empty cake flavour, such as `Dark Chocolate`. |
| `fi/FILLING` | A non-empty cake filling, such as `Strawberry`. |

Spaces at the start and end of each value are removed. Spaces inside values are preserved.
Do not put field prefixes inside values: a separate word such as `n/` starts a new field.

CakeQueue assigns a unique Order ID, sets the order to `QUEUED`, and shows a confirmation
with all its details. Invalid input does not add an order or use up an Order ID.

After the order is added, CakeQueue also shows the customer's ID. A new phone number gets the next
unused Customer ID (`C001`, then `C002`, and so on). A phone number that already has an order reuses
that customer's ID. The ID is saved with the order and never changes, even if the phone number is
edited later. Phone matching uses the exact stored text: `91234567`, `9123-4567` and
`+65 91234567` are treated as different numbers. Use the same format for returning customers.

Example:

```text
add n/Alice Tan p/91234567 d/20-12-2030 s/8 f/Chocolate fi/Strawberry
```

```text
Added order O001: Alice Tan (91234567) | Due: 20-12-2030 | Size: 8 inches | Flavour: Chocolate | Filling: Strawberry [QUEUED]
Customer ID: C001 (new customer)
```

If the phone number already has an order, it shows `Customer ID: C001 (returning customer)`.

Common errors:

* A missing field, e.g. `Error: Missing value for n/. Please provide a customer name using n/NAME.`
* An impossible date: `Error: Date must use DD-MM-YYYY and be valid.`
* A past date: `Error: Due date cannot be in the past.`
* An invalid phone: `Error: Phone must have at least 8 digits, and may only contain digits, spaces, + and -.`
* An unsupported size: `Error: Cake size must be 6, 8, or 10 inches.`

If the due date is already over your daily capacity, CakeQueue still adds the order and shows a
warning. See [`capacity`](#setting-a-daily-capacity-capacity).

If a save fails after a change, CakeQueue displays
`Warning: Could not save your data. Your changes are kept for this session only.`
Keep the app open, check that its data folder is writable, then make another change to retry saving.

## Listing Upcoming Orders: `list`

Format: `list`

Shows active confirmed orders, including overdue orders, with the earliest due date first.
Cancelled and collected orders, and legacy enquiry/rejected records, are excluded.
Use `find` or `view` to inspect records outside the active list.

Orders with the same due date keep their original order. List numbers are display positions only;
use an Order ID such as `O001` in other commands.

CakeQueue highlights orders with approaching or missed deadlines:

- `[OVERDUE]` appears when an order's due date is before today.
- `[DUE SOON]` appears when an order is due today or within the next 3 days.

If no orders are available, CakeQueue shows `You have no upcoming orders.`

## Finding Orders: `find`

Format: `find KEYWORD`

Shows records whose customer name, cake flavour or cake filling contains the keyword.

* The search ignores upper and lower case: `find alice` finds `Alice Tan`.
* The keyword can be part of a word: `find choc` finds `Chocolate`.
* A keyword with spaces is searched as one phrase: `find Alice Tan` finds `Alice Tan` but not
  `Tan Alice`.
* All records are searched, including legacy enquiries and collected or cancelled orders. Each result
  ends with its status.

Examples:

```text
find alice
find chocolate
```

If nothing matches, CakeQueue shows `No orders found matching "KEYWORD".`

## Viewing an Order: `view`

Format: `view ORDER_ID`

Shows the record's ID, customer name and phone, due date, cake size, flavour, filling, status and
any notes saved by an earlier version. Orders also show their Customer ID.
Existing notes are read-only; there is no command to add or change them.

Example: `view O001`

```text
Order ID: O001
Customer: Alice Tan (91234567)
Due date: 20-12-2030
Size: 8 inches
Flavour: Chocolate
Filling: Strawberry
Status: QUEUED
Customer ID: C001
```

If the record does not exist, CakeQueue shows `Error: Order not found: ID`.

## Viewing a Customer: `customer`

Format: `customer CUSTOMER_ID`

Shows a customer's ID, name, phone number, number of orders, and whether they are a repeat
customer (more than one order, including cancelled ones). The name and phone number are taken from
their most recently added order, regardless of its due date or status. Customer IDs are not
case-sensitive: `customer c001` also works. Editing an older order does not replace the contact
details shown here if a newer order exists.

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

Lists every order placed by that customer in the order it was added, including cancelled and
collected orders. The history is not sorted by due date. Each line ends with the order's status. Legacy enquiries are not confirmed orders and are excluded.

Example: `history C001`

```text
Order history for Alice Tan (C001):
1. O001: Alice Tan (91234567) | Due: 22-12-2030 | Size: 8 inches | Flavour: Chocolate | Filling: Strawberry [QUEUED]
2. O003: Alice Tan (91234567) | Due: 25-12-2030 | Size: 6 inches | Flavour: Vanilla | Filling: Cream [CANCELLED]
```

## Editing an Order: `edit`

Format: `edit ORDER_ID [n/NAME] [p/PHONE] [d/DATE] [s/SIZE] [f/FLAVOUR] [fi/FILLING]`

Provide at least one field. Omitted fields remain unchanged, and the same rules used for `add`
apply to the fields you give, including no past dates. The Order ID, Customer ID, status and notes
remain unchanged. Invalid input or an unknown ID leaves existing orders unchanged.
Changing the name or phone on one order does not update other orders or move it to another
Customer ID. Collected and cancelled orders can also be edited; their status stays unchanged.
The `update` alias is no longer supported; use `edit`.

Examples:

```text
edit O001 s/10
edit O001 d/22-12-2030 f/Vanilla
```

If the new due date is over your daily capacity, CakeQueue still saves the change and shows a warning.

## Updating an Order's Progress: `mark`

Format: `mark ORDER_ID preparing|ready|collected`

Moves an order one step along `QUEUED` → `PREPARING` → `READY` → `COLLECTED`.

* Choose one status word: `preparing`, `ready` or `collected`; do not type the `|` separators.
* Steps cannot be skipped, repeated or reversed.
* Orders that are collected, cancelled or rejected cannot be marked.

For a new order, run these commands in order:

```text
mark O001 preparing
mark O001 ready
mark O001 collected
```

Collected orders leave the active list but remain available through `view` and `history`.

## Cancelling an Order: `cancel`

Format: `cancel ORDER_ID`

Sets the status to `CANCELLED`. The record is kept for the customer's history but no longer
appears in `list`. Records that are collected, cancelled or rejected cannot be cancelled.

Example: `cancel O001`

## Setting a Daily Capacity: `capacity`

Format: `capacity [NUMBER]`

Sets the most orders you can take for one due date, from 1 to 50. The default is 5 orders per day.

* Without a number, `capacity` shows the current limit and lists any due dates already over it.
* Only active orders count: those with status `QUEUED`, `PREPARING` or `READY`. Legacy enquiries and
  collected, cancelled or rejected records do not count.
* When `add` or `edit` leaves a due date over the limit, CakeQueue still saves the change
  and shows a warning, for example:
  `Warning: 21-12-2030 now has 4 orders, over your daily capacity of 3.`
* The limit is not saved when you exit. It returns to 5 the next time you start CakeQueue.

Examples:

```text
capacity
capacity 3
```

## Getting Help: `help`

Format: `help`

Lists every command with its format.

## Exiting: `bye`

Format: `bye`

Exits CakeQueue. Every successful change was already saved as you worked, so there is nothing
extra to save on exit. Check for save warnings before exiting.

## Command Summary

| Action | Command |
| --- | --- |
| Add order | `add n/NAME p/PHONE d/DATE s/SIZE f/FLAVOUR fi/FILLING` |
| List upcoming orders | `list` |
| Find by name, flavour or filling | `find KEYWORD` |
| View record | `view ORDER_ID` |
| View customer | `customer CUSTOMER_ID` |
| View customer history | `history CUSTOMER_ID` |
| Edit order | `edit ORDER_ID [n/NAME] [p/PHONE] [d/DATE] [s/SIZE] [f/FLAVOUR] [fi/FILLING]` |
| Update progress | `mark ORDER_ID preparing\|ready\|collected` |
| Cancel | `cancel ORDER_ID` |
| Show or set daily capacity | `capacity [NUMBER]` |
| Help | `help` |
| Exit | `bye` |

## Current Limitations

* The daily capacity limit is not saved between sessions. It returns to 5 each time CakeQueue starts.
* A Customer ID is given out when an order is first added and never changes. If a phone number is
  edited, the order keeps its original Customer ID.
* `find` searches name, flavour and filling, but not phone numbers or dates. To look up a customer's
  orders, use `history CUSTOMER_ID`.
* Only one copy of CakeQueue should use the same `data` folder at a time.
* To start over with no records, exit the app and delete `data/cakequeue.txt`.

## Existing Data from Earlier Versions

Saved records and their Customer IDs remain compatible. Old enquiry/rejected records and
notes are preserved when loading and saving. `view` and `find` can still show old records,
and `cancel` can close a legacy pending request. Existing notes remain visible in `view`
but cannot be added or changed through the CLI. No new enquiries can be created or accepted.

The command summary lists ten feature commands plus `help` and `bye`.
Commands removed from earlier versions, including `update`, produce an unknown-command error.
