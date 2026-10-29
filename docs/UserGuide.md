# CakeQueue User Guide

CakeQueue helps home bakers manage custom cake orders, and lets customers submit
enquiries from a separate command interface.

Orders and enquiries are saved automatically to `data/cakequeue.txt` (in the folder
you launch CakeQueue from) after every successful change, and loaded again the next
time you start the app. Order and enquiry IDs keep counting up across sessions, so
an ID is never reused.

## Quick Start

1. Install Java 25 and check it with `java -version`.
2. Obtain `cakequeue.jar`, or build it from the project folder:
   - Windows: `.\gradlew.bat shadowJar`
   - macOS/Linux: `./gradlew shadowJar`
3. Run the JAR with assertions enabled:
   - After building: `java -ea -jar build/libs/cakequeue.jar`
   - For a downloaded JAR: `java -ea -jar cakequeue.jar`
4. Choose a mode: `baker` or `customer`. If a `data/cakequeue.txt` file exists,
   CakeQueue loads it and tells you how many records were loaded.
5. Enter a command and press Enter. Enter `bye` to exit.

Alternatively, run from the project folder with `.\gradlew.bat run` on Windows or
`./gradlew run` on macOS/Linux.

## Choosing a Mode

On startup, CakeQueue asks:

```text
Choose a mode: baker or customer
```

| Mode | Who uses it | Purpose |
| --- | --- | --- |
| `baker` (also `1` or `b`) | The baker | Add, list, view, edit, accept/reject enquiries, cancel |
| `customer` (also `2` or `c`) | A customer | Submit an enquiry, track/edit/cancel their own request |

Type `help` in either mode to see the commands for that mode.

Switch at any time using `mode baker` or `mode customer`; both modes work on the
same records, and nothing is lost by switching.

## Command Format

* Words in uppercase are values you supply, such as `NAME` and `DATE`.
* Square brackets mark optional fields; do not type the brackets.
* Commands and IDs are case-insensitive: `view o001` also works.
* Use lowercase field prefixes (`n/`, `p/`, `d/`, `s/`, `f/`, and `fi/`).
* Fields may appear in any order, with whitespace between fields. Values may contain spaces.
* Dates must be real dates in `DD-MM-YYYY` format.
* Cake sizes must be `6`, `8`, or `10` inches.

## Baker Mode

### Adding an Order: `add`

Format: `add n/NAME p/PHONE d/DATE s/SIZE f/FLAVOUR fi/FILLING`

Creates a confirmed order with status `QUEUED` and an ID such as `O001`.

Example:

```text
add n/Alice Tan p/91234567 d/20-10-2026 s/8 f/Chocolate fi/Strawberry
```

### Listing Upcoming Orders: `list`

Format: `list`

Shows confirmed orders that are not collected, cancelled, or rejected. Pending
customer enquiries are listed separately with `enquiries`.

### Listing Enquiries: `enquiries`

Format: `enquiries`

Shows customer enquiries that still need accept or reject.

### Viewing a Record: `view`

Format: `view ORDER_ID|ENQUIRY_ID`

Example: `view O001` or `view E001`

### Editing an Order: `edit`

Format: `edit ORDER_ID [n/NAME] [p/PHONE] [d/DATE] [s/SIZE] [f/FLAVOUR] [fi/FILLING]`

`update` is accepted as an alias. Only confirmed order IDs (`O...`) can be edited here.

### Accepting an Enquiry: `accept`

Format: `accept ENQUIRY_ID`

Converts enquiry `E001` into a new queued order such as `O001`.

### Rejecting an Enquiry: `reject`

Format: `reject ENQUIRY_ID`

Marks the enquiry as `REJECTED`.

### Cancelling a Record: `cancel`

Format: `cancel ORDER_ID|ENQUIRY_ID`

Marks the record as `CANCELLED` if it is not already finished.

### Marking Production Progress: `mark`

Format: `mark ORDER_ID preparing|ready|collected`

Moves a confirmed order one step forward: `QUEUED` → `PREPARING` → `READY` →
`COLLECTED`. Steps cannot be skipped or repeated, and enquiries cannot be marked
(accept them first). Collected orders no longer appear in `list`.

Example:

```text
mark O001 preparing
```

## Customer Mode

### Submitting an Enquiry: `enquire`

Format: `enquire n/NAME p/PHONE d/DATE s/SIZE f/FLAVOUR fi/FILLING`

Creates an enquiry with status `ENQUIRY` and an ID such as `E001`. The baker must
accept it before it becomes a confirmed order.

Example:

```text
enquire n/Alice Tan p/91234567 d/20-10-2026 s/8 f/Chocolate fi/Strawberry
```

### Listing Your Records: `mine`

Format: `mine p/PHONE`

Shows every enquiry or order stored under that phone number.

### Tracking One Record: `track`

Format: `track ID p/PHONE`

Shows full details when the phone matches the record owner.

### Editing Your Enquiry: `edit`

Format: `edit ENQUIRY_ID p/PHONE [n/NAME] [d/DATE] [s/SIZE] [f/FLAVOUR] [fi/FILLING]`

Customers may edit only pending enquiries. `p/PHONE` proves ownership and is not
changed by the edit.

### Cancelling Your Enquiry: `cancel`

Format: `cancel ENQUIRY_ID p/PHONE`

Customers may cancel only their own pending enquiries.

## Exiting: `bye`

Format: `bye`

Exits CakeQueue. Every successful change was already saved as you worked, so
there is nothing extra to save on exit.

## Command Summary

### Baker

| Action | Command |
| --- | --- |
| Add order | `add n/NAME p/PHONE d/DATE s/SIZE f/FLAVOUR fi/FILLING` |
| List orders | `list` |
| List enquiries | `enquiries` |
| View record | `view ORDER_ID\|ENQUIRY_ID` |
| Edit order | `edit ORDER_ID [n/NAME] [p/PHONE] [d/DATE] [s/SIZE] [f/FLAVOUR] [fi/FILLING]` |
| Accept enquiry | `accept ENQUIRY_ID` |
| Reject enquiry | `reject ENQUIRY_ID` |
| Cancel record | `cancel ORDER_ID\|ENQUIRY_ID` |
| Mark progress | `mark ORDER_ID preparing\|ready\|collected` |
| Switch mode | `mode customer` |
| Help | `help` |
| Exit | `bye` |

### Customer

| Action | Command |
| --- | --- |
| Submit enquiry | `enquire n/NAME p/PHONE d/DATE s/SIZE f/FLAVOUR fi/FILLING` |
| List my records | `mine p/PHONE` |
| Track one record | `track ID p/PHONE` |
| Edit enquiry | `edit ENQUIRY_ID p/PHONE [n/NAME] [d/DATE] [s/SIZE] [f/FLAVOUR] [fi/FILLING]` |
| Cancel enquiry | `cancel ENQUIRY_ID p/PHONE` |
| Switch mode | `mode baker` |
| Help | `help` |
| Exit | `bye` |

## Current Limitations

This version does not support searching by customer name, customer accounts/login
(the phone number is only a simple ownership check, not a password), capacity
planning, or running two copies of CakeQueue at the same time against the same
`data/` folder. To start over with no records, exit the app and delete
`data/cakequeue.txt`.
