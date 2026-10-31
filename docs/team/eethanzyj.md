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
- **Reliability testing:** Added tests checking that rejected input preserves saved records and Order IDs, and that
  recorded names and phone numbers survive restarting. [PR #25](https://github.com/AY2627S1-CS2113-W12-1/tp/pull/25).
- **Command structure:** Extracted `AddOrderCommand` so the CLI parses input once and delegates
  order creation. Preserved the shared Customer ID, capacity-warning and saving behaviour.
  Added direct command tests. Submitted in [PR #34](https://github.com/AY2627S1-CS2113-W12-1/tp/pull/34).
- **Documentation:** Wrote and maintained the Add Order sections of the UG and DG, including
  class and sequence diagrams. Updated the Add Order explanations and diagrams to match
  the command refactor.
- **Reviews:** Reviewed the customer/history implementation and identified a Customer ID
  stability issue. [PR #19](https://github.com/AY2627S1-CS2113-W12-1/tp/pull/19).
