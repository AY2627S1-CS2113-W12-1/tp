# Eethan Zhang - Project Portfolio Page

GitHub: [Eethanzyj](https://github.com/Eethanzyj)

## Overview

CakeQueue helps home bakers manage custom cake orders from the command line. It records
customer and cake details, tracks due dates and production progress, and keeps order history.

## Summary of Contributions

- **Code contributed:** [RepoSense code dashboard](https://nus-cs2113-ay2627-s1.github.io/tp-dashboard/?search=Eethanzyj).
- **Add Order and customer details:** Implemented the six-field `add` command to record
  customer name, phone, due date, cake size, flavour and filling. Added input validation,
  Order ID generation and confirmation messages. [PR #11](https://github.com/AY2627S1-CS2113-W12-1/tp/pull/11).
- **Reliability testing:** Added tests checking that rejected input preserves saved records and
  Order IDs, and that recorded names and phone numbers survive restarting.
  [PR #25](https://github.com/AY2627S1-CS2113-W12-1/tp/pull/25).
- **Order ID testing:** Tested the transition from `O999` to `O1000`, including viewing,
  editing, saving and restarting. Checked that the next order receives `O1001`.
  [PR #34](https://github.com/AY2627S1-CS2113-W12-1/tp/pull/34).
- **Command structure:** Extracted `AddOrderCommand` and `AddOrderParser` to separate input
  validation from order creation. Kept the `Parser.parseAdd()` entry point and reused shared
  validation rules. Preserved the team's Customer ID, capacity-warning and saving behaviour.
  Added direct command tests. [PR #34](https://github.com/AY2627S1-CS2113-W12-1/tp/pull/34).
- **Validation fixes:** Made generated IDs independent of regional settings and enforced
  four-digit years in dates. Added regression tests. [PR #38](https://github.com/AY2627S1-CS2113-W12-1/tp/pull/38).
- **Quoted fields:** Added quoted-value handling shared by `add` and `edit`, allowing names
  such as `John s/o Doe`. Tested escaping, invalid quotes and saving/reloading.
  [PR #43](https://github.com/AY2627S1-CS2113-W12-1/tp/pull/43).
- **Contextual help:** Added `help COMMAND` with command formats, rules and copyable examples,
  with tests confirming help does not change saved data.
  [PR #41](https://github.com/AY2627S1-CS2113-W12-1/tp/pull/41).
- **Output formatting:** Added a shared formatter to separate orders into readable blocks in
  `list`, `find` and `history`, preserving teammates' filtering and matching logic.
  [PR #38](https://github.com/AY2627S1-CS2113-W12-1/tp/pull/38).
- **Documentation:** Wrote and maintained the Add Order sections of the UG and DG, including
  class and sequence diagrams. Split the Add Order flow into focused diagrams and corrected
  activation boxes in shared diagrams while retaining original authors' credit.
  [PR #38](https://github.com/AY2627S1-CS2113-W12-1/tp/pull/38),
  [PR #43](https://github.com/AY2627S1-CS2113-W12-1/tp/pull/43).
- **Reviews:** Reviewed the customer/history implementation and identified a Customer ID
  stability issue. [PR #19](https://github.com/AY2627S1-CS2113-W12-1/tp/pull/19).
