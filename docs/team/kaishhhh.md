# Kai Sheng - Project Portfolio Page

GitHub: [Kaishhhh](https://github.com/Kaishhhh)

## Overview

CakeQueue helps home bakers manage custom cake orders from the command line. It records customer and cake
details, tracks due dates and production progress, keeps each customer's order history, and warns the baker
before a day has more orders than they can make.

### Summary of Contributions

#### Code contributed

[RepoSense link](https://nus-cs2113-ay2627-s1.github.io/tp-dashboard/?search=Kaishhhh&breakdown=true)

#### Enhancements implemented

* **List upcoming orders (`list`).** Added `ListCommand`, which shows uncollected orders with the earliest due
  date first, and `OrderList#getAll`, which returns a copy so that sorting for display never changes the stored
  order. [PR #10](https://github.com/AY2627S1-CS2113-W12-1/tp/pull/10)
* **Find orders (`find KEYWORD`).** Lets the baker search all records by customer name, cake flavour or filling.
  The match ignores case, can be part of a word, and treats a keyword with spaces as one phrase. Records without
  cake details are matched on the name only, and the search works the same on any system language setting.
  [PR #18](https://github.com/AY2627S1-CS2113-W12-1/tp/pull/18)
* **Daily capacity (`capacity [NUMBER]`).** Lets the baker set how many orders they can take for one due date
  (1 to 50, default 5) and see which dates are already over it. `add` and `edit` warn when a date goes over the
  limit. Only active orders (`QUEUED`, `PREPARING`, `READY`) count.
  [PR #22](https://github.com/AY2627S1-CS2113-W12-1/tp/pull/22)
    * Design choice: the app warns but still saves the order, so the baker can still take a big order on a busy day.
      The capacity is kept in `OrderList`, which every command already receives, so no command signatures had to
      change.
* **Restoring master before the v2.0 release.** A merge had removed several features and left master unable to
  compile. I redid that merge so that the team's features (Customer IDs, `find`, `capacity`, `customer`,
  `history`) and the new baker-only changes both worked again. [PR #33](https://github.com/AY2627S1-CS2113-W12-1/tp/pull/33)
    * Combined Customer IDs and notes in one data format, so that every older format still loads, including
      lines that held notes where a Customer ID was expected.
    * Made `edit` apply the same past-date and phone checks as `add`, and accepted formatted phone numbers
      such as `+65 9123-4567`.
* **Tests.** Wrote JUnit tests for `find` and `capacity`, covering the command classes, the parser and complete
  command-line sessions, including edge cases such as phrase matching, records with no cake details,
  out-of-range and non-numeric capacities, and the at-limit and over-limit warnings.
  [PR #29](https://github.com/AY2627S1-CS2113-W12-1/tp/pull/29).
  Added regression tests for the data format and `edit` validation.
  [PR #33](https://github.com/AY2627S1-CS2113-W12-1/tp/pull/33)

#### Contributions to the User Guide

* Wrote the sections for `find` and `capacity`, including the capacity warning behaviour. In v2.0, the
  capacity was not saved between sessions, so I documented that as a limitation; it is saved from v2.1.
* Wrote the v2.0 User Guide for the baker-only version, covering every command, the command format and the
  command summary. [PR #29](https://github.com/AY2627S1-CS2113-W12-1/tp/pull/29),
  [PR #33](https://github.com/AY2627S1-CS2113-W12-1/tp/pull/33)

#### Contributions to the Developer Guide

* Wrote most of the Design section: Architecture, Parser, Model, Storage and Error handling.
* Wrote the implementation sections for the **Find** and **Daily capacity** features, including the design
  alternatives considered and, before v2.1, a proposed way to save the capacity between sessions.
* Wrote Appendices A to D: product scope, user stories, non-functional requirements and glossary, and the first
  version of the instructions for manual testing (Appendix E).
* Drew the first versions of these UML diagrams in PlantUML, covering four diagram types:
    * Class: architecture overview and model class diagram.
    * Sequence: `find`, `capacity` and `customer`.
    * Activity: the capacity warning check.
    * State: the lifecycle of an order.

#### Contributions to team-based tasks

* Fixed master before the v2.0 deadline after a merge broke the build, and published the v2.0 release (JAR, User
  Guide and Developer Guide). [PR #33](https://github.com/AY2627S1-CS2113-W12-1/tp/pull/33)
* Re-enabled GitHub Actions on the team repository, which had been disabled, so that continuous integration and
  the GitHub Pages site build again.
* Kept the Developer Guide and User Guide consistent when teammates' branches were merged, resolving conflicts
  in both guides while keeping every teammate's sections.
  [PR #30](https://github.com/AY2627S1-CS2113-W12-1/tp/pull/30)

#### Contributions beyond the project team

* Reviewed the Developer Guide of team CS2113-W10-3 (BudgetBites) and gave comments on diagrams, placeholder
  sections and inconsistent explanations.
  [PR #10](https://github.com/NUS-CS2113-AY2627-S1/tp/pull/10)