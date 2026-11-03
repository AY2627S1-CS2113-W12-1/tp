# Anannya - Project Portfolio Page

GitHub: [anannya-mk](https://github.com/anannya-mk)

## Overview

CakeQueue helps home bakers manage custom cake orders from the command line. It records customer and cake
details, tracks due dates and production progress, keeps each customer's order history, and warns the baker
before a day has more orders than they can make.

### Summary of Contributions

#### Code contributed

[RepoSense link](https://nus-cs2113-ay2627-s1.github.io/tp-dashboard/?search=anannya-mk&breakdown=true)

#### Enhancements implemented

* **View an order (`view ORDER_ID`).** Added `ViewOrderCommand`, which looks up one order by its ID (not
  case-sensitive) and shows its full details: customer, due date, cake size, flavour, filling and status.
  It reports a clear message when no order has that ID or the format is wrong.
  [PR #8](https://github.com/AY2627S1-CS2113-W12-1/tp/pull/8)
* **Customer IDs and order history (`customer`, `history`).** Gave every customer an ID that is shown when an
  order is added and reused when the same customer orders again, so the baker can see who is a returning
  customer.
  [PR #19](https://github.com/AY2627S1-CS2113-W12-1/tp/pull/19)
  * Added `CustomerList` and `CustomerProfile` to the model to store each customer and match returning
    customers.
  * Added `CustomerCommand`, which shows a customer's details and whether they are a repeat customer, and
    `HistoryCommand`, which lists all of one customer's orders.
  * Customer IDs are assigned in `OrderList` when an order is added: a returning customer (same phone number
    as an earlier order) keeps their existing ID, and a new customer gets the next ID (`C001`, `C002`, ...).
    Loading saved data moves the counter past existing IDs so that IDs are never reused.
  * Extended `Parser`, `Storage`, `Order` and `HelpCommand` to support the new commands and save the
    customer ID with each order.
  * Updated `view` so that it shows the customer ID, and checked that `edit` keeps an order's customer ID.
  * Why it was involved: the feature touches the model, the parser, storage and the command layer, and the
    customer ID has to be saved and loaded correctly.
* **Tests.** Wrote JUnit tests for the customer features: `CustomerListTest`, `CustomerCommandTest`,
  `HistoryCommandTest`, `ParserCustomerHistoryTest` and the end-to-end
  `CustomerHistoryCliTest`, which covers assigning and reusing customer IDs, the repeat-customer flag, listing a
  customer's orders, `view` showing the customer ID, and `edit` keeping it.
  [PR #19](https://github.com/AY2627S1-CS2113-W12-1/tp/pull/19)
  Later added unit tests for Customer ID assignment (`CustomerIdAssignmentTest`), `CustomerProfile`
  (`CustomerProfileTest`) and the `view` command (`ViewOrderCommandTest`).

#### Contributions to the User Guide

* Wrote the sections for the `customer` and `history` commands and updated the `view` section to show the
  customer ID.
* Updated statements about saving data so that they match how the app now behaves.
  [PR #19](https://github.com/AY2627S1-CS2113-W12-1/tp/pull/19)

#### Contributions to the Developer Guide

* Wrote the Developer Guide section for the **Customer and history features**: what `customer` and `history`
  do, how Customer IDs are assigned, error handling, design considerations and manual testing.
  [PR #19](https://github.com/AY2627S1-CS2113-W12-1/tp/pull/19)

#### Contributions to team-based tasks

* Added the team's details to the About Us page.
  [PR #4](https://github.com/AY2627S1-CS2113-W12-1/tp/pull/4)

#### Contributions beyond the project team

* Reviewed the Developer Guide of team CS2113-W13-1 (snAp) and gave comments on unfinished template sections,
  diagram consistency, and unclear explanations of the design.
  [PR #22](https://github.com/NUS-CS2113-AY2627-S1/tp/pull/22)
