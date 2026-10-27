# Developer Guide

## Acknowledgements

{list here sources of all reused/adapted ideas, code, documentation, and third-party libraries -- include links to the original source as well}

## Design & implementation

### Add Order and customer details

This section covers adding an order and recording who it is for (features 1 and 7).
The baker supplies a name, phone number, due date, cake size, flavour, and filling.
One successful command creates a usable order, displays its ID and details, and
requests a save. Customer IDs, customer history, and repeat-customer detection are
separate features; the Add Order flow described here does not implement them.

#### Responsibilities

![Classes involved in adding an order](images/add-order-class.png)

| Component | Responsibility in this flow |
| --- | --- |
| `BakerCli` | Receives `add`, creates the objects, adds the order, and prints the result. |
| `Parser` | Checks the command and returns an `AddOrderDetails` value object. |
| `Customer` | Holds the name and phone recorded for this order. |
| `Cake` | Holds the size, flavour, and filling. |
| `Order` | Combines these details with an ID, due date, and status. |
| `OrderList` | Generates IDs and holds the shared records. |
| `Duke` and `Storage` | Supply the save callback and write the records to disk. |

The class diagram shows only the members relevant to adding an order. Although
older `Order` constructors allow missing cake details, Add Order always supplies
one `Customer` and one `Cake`. Each add creates its own customer object, even if
another order has the same name or phone. Recording the same customer details
does not create a shared customer account or merge orders.

#### Validation before changing data

The syntax is `add n/NAME p/PHONE d/DATE s/SIZE f/FLAVOUR fi/FILLING`.
`Parser.parseAdd` delegates to the shared six-field parser, which is also used by
`enquire`. It first identifies the prefixes and collects their values, then checks
the required fields and converts the date and size. No order ID is requested
until parsing has finished successfully.

* All six fields must be present and non-blank. Their order may vary.
* Prefixes must be lowercase. Unknown or repeated prefixes are rejected.
* Surrounding whitespace is removed from field values. Internal spaces,
  punctuation, and Unicode characters in customer names are preserved.
* Phone numbers must be non-blank. There is currently no digits-only or
  fixed-length rule; formatted numbers such as `+65 9123-4567` are retained.
* Dates use strict `dd-MM-uuuu` parsing, so impossible dates are rejected.
  The parser accepts past dates; it does not enforce a future-date rule.
* Cake sizes are limited to 6, 8, or 10 inches.

Missing-field messages identify both the field and the syntax needed to fix it.
For example, a blank name produces
`Missing value for n/. Please provide a customer name using n/NAME.`
`BakerCli` catches `CakeQueueException`, prefixes the message with `Error:`, and
continues accepting commands. A failed parse neither consumes an ID nor changes
the in-memory list or the saved file.

Keeping these checks in `Parser` avoids repeating six-field parsing in each CLI.
The parser returns structured details rather than creating an order itself, so
validation can be tested without generating IDs or writing files. A stricter
phone policy would change accepted input and should be agreed by the team before
being added to this shared parser.

#### Creating and saving an order

![Add Order interaction sequence](images/add-order-sequence.png)

After successful parsing, `BakerCli.addOrder` requests an ID from `OrderList`,
constructs the customer and cake, and creates an `Order` with status `QUEUED`.
It adds the order to the list, logs the generated ID, and returns the confirmation
message. Logging records the ID rather than the customer's name or phone.

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
| `AddOrderReliabilityTest` | Rejected commands preserve saved data and IDs; customer names survive restarting; editing one order leaves another unchanged. |
| `ParserTest` and `ParserErrorTest` | Existing command syntax, date/size validation, unknown prefixes, and duplicate fields. |

CLI tests run against JUnit temporary folders. They restore standard input and
output after each session and never use the baker's real data file. Persistence
tests inspect restored model values as well as CLI messages, so a correct-looking
confirmation alone is not enough to pass.

Diagram sources are in `docs/diagrams/add-order-class.puml` and
`docs/diagrams/add-order-sequence.puml`. Their PNG exports are in `docs/images`.
Update both the source and its export when the implementation changes.


## Product scope
### Target user profile

{Describe the target user profile}

### Value proposition

{Describe the value proposition: what problem does it solve?}

## User Stories

|Version| As a ... | I want to ... | So that I can ...|
|--------|----------|---------------|------------------|
|v1.0|new user|see usage instructions|refer to them when I forget how to use the application|
|v2.0|user|find a to-do item by name|locate a to-do without having to go through the entire list|

## Non-Functional Requirements

{Give non-functional requirements}

## Glossary

* *glossary item* - Definition

## Instructions for manual testing

### Add Order and customer details

Use a fresh folder containing `cakequeue.jar` to avoid changing existing data.
Run `java -ea -jar cakequeue.jar`, then enter `baker` at the mode prompt.
The IDs below assume this folder has no saved records.

1. Add a complete order:
   `add n/Alice Tan p/91234567 d/30-10-2026 s/8 f/Chocolate fi/Strawberry`.
   Expect a confirmation for `O001`, including all six values and `QUEUED`.
2. Enter `add p/91234567 d/30-10-2026 s/8 f/Chocolate fi/Strawberry`.
   Expect `Error: Missing value for n/. Please provide a customer name using n/NAME.`
   Enter `view O001`; the original order must remain unchanged.
3. Try an impossible date (`d/31-02-2026`) or unsupported size (`s/7`) in the
   complete command. Expect a date or size error, with no new order added.
4. Add another complete order with `n/Élodie O'Connor-Tan` and
   `p/+65 9123-4567`. Expect `O002`; the name and phone must be preserved.
5. Enter `bye`, relaunch from the same folder, and choose `baker`.
   Use `view O001` and `view O002` to check the saved details. Add one more
   valid order and check that its ID is `O003`.
6. Add two orders with the same name and phone. Edit the name of just one with
   `edit ORDER_ID n/Alicia Tan`, using its actual ID. View both records and
   restart again. Only the selected order should show the new name.

If a save warning appears, the changed records are only guaranteed for the
current session. Keep the application open while checking the destination
folder's write permissions, then make a successful change to retry saving.
