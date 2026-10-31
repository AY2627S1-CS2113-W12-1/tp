# CakeQueue Developer Guide

CakeQueue is a command-line app that helps home bakers manage custom cake orders. This guide is for
developers who want to understand how it is built or add to it. This guide describes the
simplified baker-only command set in the accompanying source. Build the JAR from that same
source version when publishing these guides.

## Table of Contents

* [Acknowledgements](#acknowledgements)
* [Setting Up](#setting-up)
* [Design](#design)
    * [Architecture](#architecture)
    * [Parser](#parser)
    * [Commands](#commands)
    * [Model](#model)
    * [Storage](#storage)
    * [Error handling](#error-handling)
* [Implementation](#implementation)
    * [Add Order and customer details](#add-order-and-customer-details)
    * [Order progress and storage](#order-progress-and-storage)
    * [Find feature](#find-feature)
    * [Daily capacity feature](#daily-capacity-feature)
    * [Customer and history features](#customer-and-history-features)
    * [View feature](#view-feature)
* [Appendix A: Product Scope](#appendix-a-product-scope)
* [Appendix B: User Stories](#appendix-b-user-stories)
* [Appendix C: Non-Functional Requirements](#appendix-c-non-functional-requirements)
* [Appendix D: Glossary](#appendix-d-glossary)
* [Appendix E: Instructions for Manual Testing](#appendix-e-instructions-for-manual-testing)

## Acknowledgements

* This project started from the CS2113 team project template, which is why the code keeps the
  `seedu.duke` package name.
* The structure and style of this guide follow the
  [AddressBook-Level3 Developer Guide](https://se-education.org/addressbook-level3/DeveloperGuide.html).
* Diagrams are drawn with [PlantUML](https://plantuml.com). The `.puml` source of each diagram is in
  `docs/diagrams`.
* Tools and libraries: [JUnit 5](https://junit.org/junit5/) for tests, [Gradle](https://gradle.org) with the
  [Shadow plugin](https://gradleup.com/shadow/) to build the JAR, and
  [Checkstyle](https://checkstyle.org) for code style.

## Setting Up

Use JDK 25. Import the project folder as a Gradle project in IntelliJ IDEA, and select Java 25 as the
project SDK and the Gradle JVM. The entry point is `seedu.duke.Duke`. The README lists the Gradle
commands to run, test and build the JAR. `./gradlew check` runs the tests and Checkstyle together, and
it is also what the continuous integration build runs on Windows, macOS and Linux.

## Design

### Architecture

![Architecture overview](diagrams/ArchitectureOverview.png)

CakeQueue is split into a few small parts that each do one job:

| Part | What it does |
| --- | --- |
| `Duke` | Starts the app. It loads the saved records and runs the `BakerCli` command loop, giving it a *save* callback that is called after every change. |
| `BakerCli` | The command loop. It reads a line, picks the command by its first word, runs it, and prints the result or `Error: ...`. |
| `Parser` | Checks the text of a command and turns it into values such as an ID or a set of fields. |
| `command` package | Classes such as `FindCommand` and `CapacityCommand` carry out a command and return the text to print. Add Order uses helpers in `BakerCli`. |
| `model` package | The data: `OrderList`, `Order`, `Customer`, `Cake`, `OrderStatus`, and the read-only `CustomerList` and `CustomerProfile`. |
| `Storage` | Saves and loads the order list as a text file. |
| `CakeQueueException` | The one exception type for errors the user can fix. |

CakeQueue starts directly in the baker interface. Its command surface is `add`, `list`,
`view`, `edit`, `cancel`, `find`, `customer`, `history`, `capacity`, `mark`, `help` and `bye`.
The parser rejects the former `update` alias. Earlier enquiry records and notes remain
readable for data compatibility; their creation and management commands are removed.

**How a command runs.** A command line goes through these steps:

1. The command loop reads the line and looks at the first word to choose the command.
2. `Parser` checks the rest of the line. If the input is wrong, it throws a `CakeQueueException`.
3. Most commands create a command object and call `execute(orders)`. Add Order uses
   `BakerCli.addOrderAndReportCustomer` and `addOrder` helpers to create model objects directly.
4. The command reads or changes the `OrderList` and returns a message.
5. The loop prints the message. If the command changed anything, it then runs the save callback.

The sequence diagrams in [Find feature](#find-feature) and [Daily capacity feature](#daily-capacity-feature)
show this in detail for two commands.

### Parser

`Parser` is a utility class with only static methods, one per command shape, such as `parseFind`,
`parseCapacity`, `parseView` and `parseAdd`. Field-based commands (`add` and `edit`) use shared
routines that find the `n/`, `p/`, `d/`, `s/`, `f/` and `fi/` prefixes, reject unknown or repeated
prefixes and empty values, and check dates, phone numbers and cake sizes. The parser checks
syntax and field rules. Checks that need stored data, such as whether an order ID exists,
are done by the commands. `parseUpdate` is the internal method name for parsing `edit`;
the former `update` command word is rejected.

### Commands

Most commands are built with parsed values and expose `execute(OrderList)`, returning the text
to print. `HelpCommand.execute()` needs no order list. Add Order uses helpers in `BakerCli`,
and `bye` ends its loop directly. Command classes do not print or save; the loop prints their
result and invokes the save callback after successful record changes.

### Model

![Model class diagram](diagrams/ModelClassDiagram.png)

* `OrderList` holds all orders and any legacy records loaded from storage. It assigns Order and
  Customer IDs, preserves older IDs and keeps the daily capacity.
* `Order` holds the customer, the cake, the due date, the status and free-text notes. Apart from the
  status, the customer ID and the notes, its fields are fixed once the order is created. `edit` builds a changed copy with
  `Order#updatedCopy`.
* New orders use `QUEUED`, `PREPARING`, `READY`, `COLLECTED` and `CANCELLED`.
  `ENQUIRY` and `REJECTED` remain in `OrderStatus` for older saved records. Legacy notes also
  remain in the model and storage, but cannot be changed through the CLI.
* `CustomerList` and `CustomerProfile` are built fresh from the order list when needed. They are never
  saved separately; the order list remains the source of customer information.

### Storage

`Storage` keeps the order list in `data/cakequeue.txt` as plain text, with one record per line:
`ID | STATUS | NAME | PHONE | DUE | SIZE | FLAVOUR | FILLING | CUSTOMER_ID | NOTES`, where `-` marks an empty
value and `\|` is an escaped `|` inside a value. The customer ID and notes were added in v2.0; lines with 8 or 9
fields from older versions still load. A 9-field line normally ends with a customer ID, but some files were saved
with notes in that position, so a 9th field that does not look like an ID (one letter then digits) is read as
notes. A loaded O-prefixed order without a Customer ID receives one through `OrderList.add`;
E-prefixed legacy records do not. Backslashes are escaped as `\\`. Plain text
keeps the file readable, easy to compare in Git, and fixable by hand. Two choices keep the data safe:

* A save writes a temporary file in the same folder, then replaces the real file. It attempts
  an atomic move and falls back to a normal replacement if atomic moves are unsupported. This
  avoids writing directly into the current data file; the fallback has weaker crash guarantees.
* A line that cannot be read when loading is skipped with a warning, so one bad line does not stop the
  app or lose the other records.

The daily capacity is not part of the saved data yet. See [Daily capacity feature](#daily-capacity-feature).

### Error handling

Every error the user can fix is thrown as a `CakeQueueException`, either by the parser (bad input) or by a
command (for example, an unknown ID or a step that is not allowed). The command loop catches it, prints
`Error: <message>`, and carries on. A mistake never ends the session and never changes the data. The
message says what is wrong and, where it helps, shows the correct format, such as `Use: find KEYWORD`.

## Implementation

### Add Order and customer details

This section covers adding an order and recording who it is for (features 1 and 7).
The baker supplies a name, phone number, due date, cake size, flavour, and filling.
One successful command creates a usable order, displays its ID and details, and
requests a save. The integrated flow also reports the Customer ID and warns about
capacity. Customer IDs and capacity checks use the team's shared components;
the customer/history section below explains customer grouping in more detail.

#### Responsibilities

![Classes involved in adding an order](images/add-order-class.png)

| Component | Responsibility in this flow |
| --- | --- |
| `BakerCli` | Receives `add`, creates the objects, adds the order, and prints the result. |
| `Parser` | Checks the command and returns an `AddOrderDetails` value object. |
| `Customer` | Holds the name and phone recorded for this order. |
| `Cake` | Holds the size, flavour, and filling. |
| `Order` | Combines these details with an ID, due date, and status. |
| `OrderList` | Generates Order IDs, assigns Customer IDs, holds records, and counts active orders by date. |
| `Duke` and `Storage` | Supply the save callback and write the records to disk. |

The class diagram shows only the members relevant to adding an order. Although
older `Order` constructors allow missing cake details, Add Order always supplies
one `Customer` and one `Cake`. Each add creates its own customer object, even if
another order has the same name or phone. These per-order objects keep name edits
independent. Orders with matching phone numbers can share a Customer ID, which
allows the customer/history commands to group them without merging the orders.

#### Validation before changing data

The syntax is `add n/NAME p/PHONE d/DATE s/SIZE f/FLAVOUR fi/FILLING`.
`BakerCli.addOrderAndReportCustomer` validates the input before looking up the
customer. It then calls `addOrder`, which validates the input again before creating
the order. Both calls use `Parser.parseAdd`, which delegates to the shared
six-field parsing helper. The parser identifies prefixes and
collects their values, then checks
the required fields and converts the date and size. No order ID is requested
until parsing has finished successfully.

* All six fields must be present and non-blank. Their order may vary.
* Prefixes must be lowercase. Unknown or repeated prefixes are rejected.
* Surrounding whitespace is removed from field values. Internal spaces,
  punctuation, and Unicode characters in customer names are preserved.
* Phone numbers must have at least 8 digits, and may also contain spaces, `+`
  and `-`, so formatted numbers such as `+65 9123-4567` are retained.
* Dates use strict `dd-MM-uuuu` parsing, so impossible dates are rejected.
  Dates before today are rejected. `edit` applies the same date and phone rules.
* Cake sizes are limited to 6, 8, or 10 inches.

Missing-field messages identify both the field and the syntax needed to fix it.
For example, a blank name produces
`Missing value for n/. Please provide a customer name using n/NAME.`
`BakerCli` catches `CakeQueueException`, prefixes the message with `Error:`, and
continues accepting commands. A failed parse neither consumes an ID nor changes
the in-memory list or the saved file.

Keeping these checks in `Parser` avoids repeating six-field parsing in each CLI.
The parser returns structured details rather than creating an order itself, so
validation can be tested without generating IDs or writing files. The same phone
validation applies to `add` and `edit`: at least eight digits, with spaces, `+` and `-` allowed.

#### Creating and saving an order

![Add Order interaction sequence](images/add-order-sequence.png)

After successful parsing, `BakerCli.addOrder` requests an Order ID from `OrderList`,
constructs the customer and cake, and creates an `Order` with status `QUEUED`.
`OrderList.add` assigns or reuses a Customer ID by phone number before adding the
record. The existing `Cake` constructor also checks size, flavour, and filling.
The CLI logs the Order ID rather than the customer's name or phone.

The add helper appends a capacity warning when active orders on the due date
exceed the configured limit. It still adds the order. The default limit is 5,
can be changed to 1–50 using `capacity`, and resets on restart. Enquiries and
closed records do not count. The wrapper then appends the Customer ID and whether
the customer is new or returning; it does not generate another order.

The ID counter produces values such as `O001` and `O002`. When `Storage.load`
restores existing records, `OrderList.add` advances the counters past their IDs.
Consequently, the next add continues after the restored IDs instead of starting
again at `O001`. Rejected command input does not advance the counter because
parsing precedes ID generation.

`BakerCli.run` prints the confirmation and invokes its `afterChange` callback.
`Duke` supplies this callback, keeping file handling outside the CLI. `Storage`
writes the full list to a temporary file and then replaces the data file. The
default path is `data/cakequeue.txt`, relative to the application's launch folder.

A successful in-memory add and a successful disk save are separate outcomes.
If saving fails, the order remains available during the current session and
`Duke` displays a warning that the changes are kept for this session only.
The application does not roll back the add. The sequence diagram therefore shows
confirmation before saving, and a separate warning on failure.

#### Regression tests

| Test class | Behaviour checked |
| --- | --- |
| `AddOrderValidationTest` | Guidance for each missing field, blank names, punctuation, Unicode, and formatted phones. |
| `AddOrderReliabilityTest` | Rejected commands preserve saved data and IDs; names survive restarting; customer IDs and capacity warnings, and independent name edits work together. |
| `ParserTest` and `ParserErrorTest` | Existing command syntax, date/size validation, unknown prefixes, and duplicate fields. |

CLI tests run against JUnit temporary folders. They restore standard input and
output after each session and never use the baker's real data file. Persistence
tests inspect restored model values as well as CLI messages, so a correct-looking
confirmation alone is not enough to pass.

Diagram sources are in `docs/diagrams/add-order-class.puml` and
`docs/diagrams/add-order-sequence.puml`. Their PNG exports are in `docs/images`.
Update both the source and its export when the implementation changes.

### Order progress and storage

#### Record lifecycle

![Order state diagram](diagrams/OrderStateDiagram.png)

New orders start in `QUEUED`, progress through `PREPARING` and `READY` to `COLLECTED`,
or can be cancelled before collection. Closed orders remain stored for history; the CLI
does not offer permanent deletion. Legacy `ENQUIRY` and `REJECTED` statuses are retained
only so older files still load. Legacy pending records can be viewed, found or cancelled.

#### Production status marking (`mark`)

`mark ORDER_ID preparing|ready|collected` moves a confirmed order exactly one step forward. `Parser.parseMark`
checks the *shape* (an `O` ID and one of the three words), while `MarkCommand` checks the *transition* against the
order's current status. Skipping a step, repeating one, or marking an enquiry or a closed record produces an
error explaining the restriction, and the status is left unchanged. `COLLECTED` records disappear from `list`
because `ListCommand` excludes them.

![Mark-and-save sequence diagram](diagrams/rendered/MarkAndSaveSequenceDiagram.png)

*Alternative considered:* a `done` command that jumps straight to `COLLECTED` is simpler, but the baker would lose
the difference between "in the oven" and "waiting for pickup", which is the point of tracking status.

#### Saving after every change

`Duke` owns a `Storage` bound to a `Path` (default `data/cakequeue.txt`; tests pass a temporary path). At startup,
`Duke` loads the file into the `OrderList`. After every successful command that changes records, `BakerCli` runs an
`afterChange` callback supplied by `Duke`, which saves. Passing a callback instead of giving `BakerCli` a `Storage`
keeps the command loop unaware of files, and keeps the "warn the user if saving failed" rule in one place.

*Alternative considered:* saving only on `bye` means less file writing, but any crash or closed terminal would lose
the whole session.

When records are loaded, `OrderList#add` moves the next-ID counters past every loaded ID, so a new record never gets
an ID that is already in use.

### Find feature

#### What it does

`find KEYWORD` shows every record whose customer name, cake flavour or cake filling contains
the keyword. The match ignores case and may be part of a word.

#### How it works

![Find sequence diagram](diagrams/FindSequence.png)

1. `BakerCli` sees the command word `find` and calls `Parser.parseFind(input)`. The parser rejects a
   missing keyword with `Use: find KEYWORD` and returns the rest of the line, trimmed, as one phrase.
2. `BakerCli` creates a `FindCommand` with the keyword and calls `execute(orders)`.
3. `FindCommand` goes through `orders.getAll()`. For each record it lower-cases the name, flavour and
   filling (using `Locale.ROOT`, so the result does not depend on the user's language settings) and checks
   whether any of them contains the lower-cased keyword. A record with no cake details is matched on the
   name only.
4. It returns the matches as a numbered list, in the order the records were added, or
   `No orders found matching "KEYWORD".`
5. `BakerCli` prints the message. `find` changes nothing, so nothing is saved.

#### Design considerations

**Which fields to search.**

* *Alternative 1 (chosen): customer name, flavour and filling.* Pros: covers the ways a baker actually
  looks for an order ("Alice's order", "the chocolate ones"). Cons: it does not search phone numbers or
  dates. A customer's orders can be listed with `history`, and `list` is ordered by date.
* *Alternative 2: customer name only.* Pros: simpler. Cons: the baker cannot find all orders for a flavour,
  for example when they need to buy more of an ingredient.

**How to treat a keyword that has several words.**

* *Alternative 1 (chosen): one phrase.* `find Alice Tan` matches `Alice Tan` only. Pros: simple to explain
  and predictable, and a longer phrase narrows the result.
* *Alternative 2: match any of the words.* Pros: finds more records. Cons: a longer search finds *more*
  results instead of fewer, which surprises users. It also needs rules for ordering the results.

**Which records to search.** `find` searches all records, including legacy enquiries and orders that are
collected or cancelled, and shows each record's status. Pros: it can find an old order, which `list` hides.
Cons: results can include records the baker no longer cares about, but the status makes them easy to skip.

### Daily capacity feature

#### What it does

A home baker can only make so many cakes in a day. `capacity NUMBER` sets the most active orders the baker
can take for one due date (1 to 50, default 5), and `capacity` shows the limit and any due dates already
over it. When `add` or `edit` leaves a due date over the limit, the baker is warned. The change is
still saved.

#### How it works

![Capacity sequence diagram](diagrams/CapacitySequence.png)

*Setting and showing the capacity.*

1. `Parser.parseCapacity` returns the new number, or `null` for a bare `capacity`. It throws
   `Daily capacity must be a whole number from 1 to 50.` for a number out of range or not a number, and
   `Use: capacity [NUMBER]` for extra words.
2. `CapacityCommand` calls `orders.setDailyCapacity(n)` when it got a number. `OrderList` checks the range
   again and keeps the old value if it fails.
3. It then asks `orders.countOrdersByDueDate()` for the number of active orders on each date, and lists the
   dates with more orders than the capacity.

*Warning after a change.*

1. `add` and `edit` run exactly as before. After the order list has changed, `BakerCli` calls
   `CapacityCommand.warningFor(orders, dueDate)` with the due date of the order that was added
   or edited. For `edit`, it reads the due date back from the order list, because the command may not have
   changed it.
2. `warningFor` returns an empty string if the order has no due date, or if
   `orders.countOrdersDueOn(dueDate)` is not more than `orders.getDailyCapacity()`. Otherwise, it returns
   the warning text with the date and the count.
3. `BakerCli` appends the warning on a new line after the normal message.

![Capacity activity diagram](diagrams/CapacityActivity.png)

An *active* order is one with status `QUEUED`, `PREPARING` or `READY`. Legacy enquiries,
collected orders, cancelled orders and rejected records do not count.

#### Design considerations

**Warn or refuse an order that goes over capacity?**

* *Alternative 1 (chosen): warn and still save.* Pros: the baker decides. A baker may take a big order on a
  busy day (a family member helps, or an old customer asks), and a hard refusal would force them to raise the
  limit first. Cons: the baker could ignore the warning.
* *Alternative 2: refuse the order.* Pros: it enforces the limit. Cons: it gets in the way of a normal
  business decision; the baker would need to raise the limit before adding an order.

**Where to keep the capacity.**

* *Alternative 1 (chosen): in `OrderList`.* Pros: the order list is already passed to every command, so no
  command signatures change, and the counting code sits next to the data it counts. Cons: `OrderList` now
  has a small setting in it.
* *Alternative 2: a separate `Settings` class.* Pros: cleaner separation. Cons: every command loop and
  command would need a new argument, which is a larger change for one number.

**What to count.** *Orders per date* is used instead of baking hours per date. Pros: easy for the baker to
understand and to set, with no need to estimate the time of each cake. Cons: a wedding cake and a cupcake
count the same.

**Not saved between runs (current limitation).** `Storage` writes one order per line, so saving the capacity
would change the file format, which the whole team depends on. The capacity therefore goes back to 5 on each
start. The user guide lists this under Current Limitations.

*Proposed implementation.* Write the capacity as a first line such as `#capacity 3` in the data file. Make
`Storage.load` read that line and call `setDailyCapacity`, and make it skip other lines that start with `#`
instead of treating them as corrupted. Older files without the line keep working with the default of 5.

### Customer and history features

#### What they do

- `customer CUSTOMER_ID` shows a customer's ID, name, phone number, number of orders and whether they are a repeat customer.
- `history CUSTOMER_ID` lists every order the customer has placed, including cancelled ones, with each order's status.
- `add` also prints the customer's ID and whether they are new or returning. `view` shows the customer's ID for an order.

#### Design

Every confirmed order (ID starting with `O`) carries a **Customer ID** (`C001`, `C002`, ...), stored in `Order#customerId`. The ID is assigned **once**, inside `OrderList#add`, and never changes afterwards:

- A new order takes the Customer ID of the most recent order with the same phone number.
- If no order has that phone number, it takes the next unused ID (`OrderList` keeps a counter, advanced past any ID loaded from disk).
- Editing an order copies its Customer ID (`Order#updatedCopy`), so editing a name or phone number never changes an ID.
- Legacy enquiries (IDs starting with `E`) do not receive Customer IDs. They remain readable
  for compatibility, but cannot be created or converted into orders through the CLI.

The Customer ID is saved as the 9th field in the data file (`-` when absent). `Storage` still reads older 8-field files, and those orders get IDs by phone number when loaded.

`CustomerList.fromOrders(OrderList)` builds a read-only snapshot by grouping the confirmed orders by Customer ID. A customer's name and phone number are those on their most recently added order,
regardless of due date or status. Grouping follows insertion order, not due-date order.

| Class | Responsibility |
|---|---|
| `Order` | Holds the Customer ID (`getCustomerId`, `setCustomerId`) and keeps it when copied by `updatedCopy`. |
| `OrderList` | Assigns the Customer ID when a confirmed order is added (`assignCustomerId`). |
| `Storage` | Saves and loads the Customer ID as the 9th field. |
| `CustomerProfile` | Holds one customer's ID, phone number and orders. Gives the name (from their latest order) and whether they are a repeat customer (more than one order). |
| `CustomerList` | Groups orders into `CustomerProfile`s. Looks customers up by ID (`getById`) or phone number (`findByPhone`). |
| `CustomerCommand` | Produces the output of `customer CUSTOMER_ID`. |
| `HistoryCommand` | Produces the output of `history CUSTOMER_ID`. |
| `Parser` | `parseCustomer` and `parseHistory` check the input format and return the upper-cased ID. |
| `BakerCli` | Routes `customer` and `history`, and adds the customer line to the output of `add`. |

#### Sequence of `customer C001`

![Customer sequence diagram](diagrams/CustomerSequenceDiagram.png)

`history` works the same way, with `HistoryCommand` building a numbered list from `profile.getOrders()` instead.

#### Error handling

| Input | Result |
|---|---|
| `customer` or `customer X1` | `Error: Use: customer CUSTOMER_ID` |
| `history` or `history X1` | `Error: Use: history CUSTOMER_ID` |
| `customer C999` (no such customer) | `Error: Customer not found: C999` |
| A data file line with a malformed Customer ID | The line is skipped with a warning, like any other corrupted line. |

All user errors are `CakeQueueException`s, which `BakerCli` catches and prints, so the program keeps running.

#### Design considerations

**Alternative 1 (chosen): store the Customer ID on each order and save it with the order.**
- Pros: an ID always refers to the same customer, even after a phone number is edited and after a restart. No separate customer file to keep in sync. Old save files still load.
- Cons: changes the data file format (one optional field) and touches `Order`, `OrderList` and `Storage`, which other team members own. Changing the phone number on an order does not move it to another customer.

**Alternative 2: work the IDs out from the order list each time (by order of first appearance of each phone number).**
- Pros: no change to the data file or other classes.
- Cons: rejected after review. Editing the phone number on an order could make an existing ID refer to a different customer.

**Alternative 3: store a separate `CustomerList` in its own file.**
- Pros: IDs would never change.
- Cons: a second file to load and save, which could disagree with the order file.

**Customer matching.** Initial grouping uses the exact phone string, not the name. Two people
can share a name, and one person may use different spellings. The code does not normalise
phone formatting: `91234567` and `9123-4567` create different customer groups. Later edits
retain the assigned ID, so changing a phone never reassigns an existing order.

#### Manual testing

The full session in [Appendix E](#appendix-e-instructions-for-manual-testing) covers new and
returning customers, history, cancellation and restart. After that session, try
`edit O003 p/90000000`, `view O001`, and `customer C001`. O001 retains its original phone;
C001 shows `90000000` from its newest order O003. Both orders keep C001 after restarting.

### View feature

`view ORDER_ID` shows the full details of one order. Legacy E-prefixed IDs are also
accepted by `Parser.parseView` for accessing older records. `BakerCli` passes the ID through `Parser.parseView` to `ViewOrderCommand`, which calls `OrderList.getById` and returns `Order.toDetailedString()`. For confirmed orders, it also appends a `Customer ID:` line using `Order#getCustomerId`.

| Input | Result |
|---|---|
| `view` with no ID, or a badly formatted ID | `Error: Use: view ORDER_ID` |
| `view O999` (no such record) | `Error: Order not found: O999` |

Manual test: `add` an order, then `view O001`. Expected: the order's details, ending with a `Customer ID: C001` line. Then `view O999`. Expected: the not-found error.

## Appendix A: Product Scope

**Target user profile**

* A home baker who takes custom cake orders as a side business, working alone from home
* Receives orders through chat apps or phone calls, and has to remember what each cake involves
* Needs to know what is due when, and cannot make more than a few cakes in a day
* Is comfortable typing short commands on a laptop, and prefers that to a heavy app

**Value proposition**

CakeQueue keeps track of upcoming custom orders and what each one involves, so the baker knows what is due
when, can track production progress, and receives a warning when a due date exceeds the daily capacity.
Customer details and order history help the baker handle returning customers.

**Out of scope.** CakeQueue does not handle payments, ingredient stock, recipes or online ordering by customers.

## Appendix B: User Stories

Priorities: High (must have), Medium (nice to have), Low (unlikely to have).

| Version | Priority | As a ... | I want to ... | So that I can ... |
| --- | --- | --- | --- | --- |
| v1.0 | High | new user | see usage instructions | refer to them when I forget how to use the application |
| v1.0 | High | baker | add an order with the customer, due date and cake details | keep a record of every cake I have promised |
| v1.0 | High | baker | list my upcoming orders, earliest first | see what I need to bake next |
| v1.0 | High | baker | view all the details of one order | check the cake before I start baking |
| v1.0 | Medium | baker | edit an order | handle a change request without entering the order again |
| v2.0 | High | baker | set a daily capacity and be warned when a date goes over it | avoid accepting more cakes than I can deliver in a day |
| v2.0 | Medium | baker | see which dates are over capacity | ask customers to move their dates or arrange extra help |
| v2.0 | High | baker | mark an order as preparing, ready or collected | know which cakes are in progress and which are done |
| v2.0 | Medium | baker | cancel an order and keep the record | keep my list accurate and still look back at it |
| v2.0 | Medium | baker | find orders by customer name, flavour or filling | locate an order without going through the whole list |
| v2.0 | Medium | baker | see a customer's order history | recognise a returning customer and recall their past cakes |
| v2.0 | High | baker | have my orders saved automatically | close the app without losing orders |
| Future (not implemented) | Low | baker | have my daily capacity remembered between sessions | avoid setting it again each time I start the app |

## Appendix C: Non-Functional Requirements

1. **Platform.** CakeQueue should work on Windows, macOS and Linux when Java 25 is the only Java version installed.
2. **Performance.** With up to 1,000 orders, each command should respond within 1 second on an ordinary laptop.
3. **Data safety.** Successful record changes trigger an immediate save attempt. A save failure
   produces a warning and leaves the change in memory. A malformed saved line should not prevent
   valid lines from loading; a warning identifies the skipped line.
4. **Readable data.** The data file should be plain text that a user can open, read and fix in a text editor.
5. **Error messages.** Every invalid command should show a message that says what is wrong, and should not end the
   session or change any data.
6. **Single user.** CakeQueue is used by one person at a time on one computer. It does not need a network connection.
7. **Code quality.** All code and tests should pass Checkstyle, and all tests should pass on Windows, macOS and Linux in
   the continuous integration build before a pull request is merged.

## Appendix D: Glossary

* **Baker** - The person who makes the cakes and uses CakeQueue.
* **Customer** - A person who orders cakes. The baker records their orders.
* **Legacy enquiry** - An older request with an `E` ID, retained for save-file compatibility.
* **Order** - A confirmed cake request. Its ID starts with `O`.
* **Active order** - An order that is `QUEUED`, `PREPARING` or `READY`. Only these count towards the daily capacity.
* **Daily capacity** - The most active orders the baker is willing to take for one due date.
* **Customer ID** - An ID such as `C001` that identifies one customer by phone number.
* **Mainstream OS** - Windows, Linux and macOS.

## Appendix E: Instructions for Manual Testing

Build with Java 25 using `./gradlew check shadowJar` (Windows: `.\gradlew.bat check shadowJar`).
Use a fresh empty folder containing `cakequeue.jar`, then run `java -ea -jar cakequeue.jar`.
The baker interface starts without a mode prompt. Enter these commands in order:

```text
help
capacity 1
add n/Alice Tan p/91234567 d/20-12-2030 s/8 f/Chocolate fi/Strawberry
add n/Bob Lim p/81234567 d/20-12-2030 s/6 f/Vanilla fi/Cream
add n/Alice Tan p/91234567 d/21-12-2030 s/8 f/Chocolate fi/Cream
list
view O001
edit O001 s/10
find chocolate
customer C001
history C001
cancel O002
mark O001 preparing
mark O001 ready
mark O001 collected
list
bye
```

The second add warns that its date exceeds capacity; the order is still added. Alice's
two orders share C001, so `customer C001` reports two orders and a repeat customer.
Editing changes only the selected size. Cancellation retains Bob's history. The final
active list contains O003; O001 is collected and O002 is cancelled.

Restart from the same folder. Expect three saved records, unchanged Customer IDs/statuses,
and a default capacity of five. Verify with `view O001`, `view O002`, `history C001`, and `capacity`.
After restarting, try these invalid inputs. Each must leave records unchanged and allow
the next valid command to run:

| Input | Expected result |
| --- | --- |
| `add` | Usage guidance; no order or Customer ID is created. |
| `edit O003 d/31-02-2030` | Invalid-date error. |
| `edit O003 d/01-01-2000` | Past-date error. |
| `edit O003 p/abc` | Phone validation error. |
| `edit O003 s/7` | Cake-size error. |
| `view O999` | Order-not-found error. |
| `mark O003 collected` | A skipped-step error; O003 stays QUEUED. |
| `update O003 s/10` | Unknown-command error; use `edit`. |

Run `view O003` afterwards: its size remains 8 and status remains QUEUED.

`ReducedCommandsCliTest` verifies that all retired commands are rejected, and that older
enquiry/rejected records and notes survive loading, editing, saving and restarting.
Storage keeps the existing ten-field format; removing CLI features does not migrate away
or erase legacy fields. `NotesAndCustomerIdStorageTest` retains format-compatibility checks.
