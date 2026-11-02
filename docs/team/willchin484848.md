# Will Chin - Project Portfolio Page

## Overview

CakeQueue is a command-line application that helps bakers manage cake orders, customer information, and upcoming deadlines.

### Summary of Contributions

#### Code contributed

[RepoSense link](https://nus-cs2113-ay2627s1.github.io/tp-dashboard/?search=WillChin484848&breakdown=true)

#### Enhancements implemented

* **Update orders (`edit`).** Implemented the command to modify customer names, phone numbers, due dates, cake sizes, flavours, and fillings while preserving unchanged fields. Added input validation, error handling, and JUnit tests.

* **Cake details validation.** Added model-level validation for supported cake sizes, flavours, and fillings. Improved exception handling and support for orders without due dates.

* **Due-date warnings.** Implemented `[OVERDUE]` and `[DUE SOON]` indicators for overdue orders and orders due within three days. Added JUnit tests for warning boundaries.

* **Storage reliability.** Fixed a data-loss risk where unreadable save files could be treated as empty databases. Improved corrupted-file protection, save error handling, temporary-file management, and daily capacity persistence.

* **Order and customer reliability.** Added duplicate and malformed order-ID checks, improved customer-ID allocation, overflow protection, null validation, and consistent ID matching.

* **Code quality and testing.** Removed redundant customer lookups, added Java assertions for internal invariants, and added regression tests for storage safety and validation.

#### Contributions to the User Guide

* Updated documentation for cake validation and due-date warning behaviour.

#### Contributions to the Developer Guide

* Documented the implementation of the `edit` command and its validation logic.
* Updated storage-related explanations to reflect improved data protection and persistence behaviour.

#### Contributions to team-based tasks

* Improved the reliability of shared CakeQueue components while preserving existing command functionality.
* Tested changes against the project's existing JUnit test suite.

#### Review/mentoring contributions

* Reviewed teammates' code and provided feedback on implementation and testing.