# Kai Sheng - Project Portfolio Page

## Overview

CakeQueue is a command-line application that helps bakers manage cake orders and customer details.

### Summary of Contributions

#### Code contributed

[RepoSense link](https://nus-cs2113-ay2627s1.github.io/tp-dashboard/?search=Kaishhhh&breakdown=true)

#### Enhancements implemented

* **List upcoming orders (`list`).** Added `ListCommand`, which shows uncollected orders with the earliest due
  date first, and `OrderList#getAll`, which returns a copy so that sorting for display never changes the stored
  order. [PR #10](https://github.com/AY2627S1-CS2113-W12-1/tp/pull/10)
* **Find orders (`find KEYWORD`).** Lets the baker search all orders and enquiries by customer name, cake flavour
  or filling. The match ignores case, can be part of a word, and treats a keyword with spaces as one phrase.
  Records without cake details are matched on the name only, and the search works the same on any system language
  setting. [PR #18](https://github.com/AY2627S1-CS2113-W12-1/tp/pull/18)
* **Daily capacity (`capacity [NUMBER]`).** Lets the baker set how many orders they can take for one due date
  (1 to 50, default 5) and see which dates are already over it. `add`, `accept` and `edit` now warn when a date
  goes over the limit. Only active orders (`QUEUED`, `PREPARING`, `READY`) count.
  [PR #22](https://github.com/AY2627S1-CS2113-W12-1/tp/pull/22)
    * Design choice: the app warns but still saves the order, so the baker can still take a big order on a busy day.
      The capacity is kept in `OrderList`, which every command already receives, so no command signatures had to
      change.
* **Tests.** Wrote 36 JUnit tests for `find` and `capacity`, covering the command classes, the parser and complete
  command-line sessions. The tests check edge cases such as phrase matching, records with no cake details,
  out-of-range and non-numeric capacities, the at-limit and over-limit warnings, and inactive statuses not being
  counted. [PR #29](https://github.com/AY2627S1-CS2113-W12-1/tp/pull/29)

#### Contributions to the User Guide

* Wrote the sections for `find` and `capacity`, including the capacity warning behaviour and its limitation
  (the limit is not saved between sessions).
* Added the sections that were missing for v2.0: choosing a mode and how an enquiry becomes an order, `enquiries`,
  `accept`, `reject`, `mark`, `cancel` and the customer-mode commands (`enquire`, `mine`, `track`, `edit`,
  `cancel`).
* Extended the Command Summary to cover both baker and customer modes, and added items to Current Limitations.

#### Contributions to the Developer Guide

* Wrote the implementation sections for the **Find** and **Daily capacity** features, including the design
  alternatives considered and a proposed implementation for saving the capacity between sessions.
* Wrote Appendices A to E: product scope, user stories, non-functional requirements, glossary and instructions
  for manual testing.
* Drew five UML diagrams in PlantUML, covering three diagram types:
    * Class: architecture overview and model class diagram.
    * Sequence: `find` and `capacity`.
    * Activity: the capacity warning check.

#### Contributions to team-based tasks

* Kept the Developer Guide and User Guide consistent when teammates' branches were merged. I resolved conflicts
  in both guides, keeping every teammate's sections.
  [PR #30](https://github.com/AY2627S1-CS2113-W12-1/tp/pull/30)
* Checked the guides against the v2.0 requirements: commands missing from the User Guide, required DG sections,
  `.puml` sources in `docs/diagrams`, and the `//@@author` tags around my code.

#### Review/mentoring contributions

* Reviewed teammates' pull requests: {add the PR numbers you reviewed, e.g. [#19](https://github.com/AY2627S1-CS2113-W12-1/tp/pull/19)}

#### Contributions beyond the project team

* Reviewed the Developer Guide of team CS2113-W10-3 (BudgetBites) and gave comments on diagrams, placeholder
  sections and inconsistent explanations.
  [PR #10](https://github.com/NUS-CS2113-AY2627-S1/tp/pull/10)
