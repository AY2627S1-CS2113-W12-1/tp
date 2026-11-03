# Gitanjali Penumatsa - Project Portfolio Page

GitHub: [HammerHead-os](https://github.com/HammerHead-os)

## Overview

CakeQueue helps home bakers manage custom cake orders from the command line. It records customer and cake
details, tracks due dates and production progress, keeps each customer's order history, and warns the baker
before a day has more orders than they can make.

### Summary of Contributions

#### Code contributed

[RepoSense link](https://nus-cs2113-ay2627-s1.github.io/tp-dashboard/?search=HammerHead-os&breakdown=true)

#### Enhancements implemented

* **Production progress (`mark`).** Added `MarkCommand` and the `OrderStatus` steps a confirmed order follows:
  `QUEUED` → `PREPARING` → `READY` → `COLLECTED`. The parser checks that the command has an order ID and one
  of those three words. `MarkCommand` then allows only the next step. Skipping, repeating, or marking a
  collected, cancelled, or rejected record leaves the status unchanged and explains why.
  [Commit `d918b2b`](https://github.com/AY2627S1-CS2113-W12-1/tp/commit/d918b2b)
    * A single `done` command would have been shorter, but the baker would lose the difference between a cake
      that is still being made and one that is waiting for pickup.
* **Cancelling an order (`cancel`).** Added `CancelCommand`, which sets a record to `CANCELLED` and keeps it
  so the customer's history is still there. Collected, cancelled, and rejected records cannot be cancelled
  again. [Commit `ea7a098`](https://github.com/AY2627S1-CS2113-W12-1/tp/commit/ea7a098)
* **Saving after every change.** Added `Storage` and wired it through `Duke` and `BakerCli`. On startup the
  app loads `data/cakequeue.txt`. After `add`, `edit`, `mark`, or `cancel` succeeds, `BakerCli` runs a save
  callback from `Duke`. The command loop does not know about files, and a failed save warns the baker while
  keeping the change for the rest of the session. Records are stored as plain text, one per line, written to
  a temporary file and then moved into place.
  [Commit `d918b2b`](https://github.com/AY2627S1-CS2113-W12-1/tp/commit/d918b2b)
    * Saving only on `bye` would write the file less often, but closing the terminal mid-session would drop
      every change from that sitting.
    * Teammates later extended the same file with customer IDs, notes, a daily-capacity header, and stricter
      checks for corrupted data. Those later rules are theirs.
* **Baker command loop.** `Duke` starts in baker mode, and `BakerCli` reads one line at a time, runs the
  matching command, and prints `Error: ...` when input is invalid. A bad command ends that line only; the
  session and the saved file stay as they were.
* **Tests.** Added `MarkCommandTest` for the allowed step and for skipped or repeated steps, `ModeCliTest`
  for the baker session, and `StorageTest` for saving and loading records.

#### Contributions to the User Guide

* Wrote the `mark` and `cancel` sections: the one-step status sequence, which records can be marked or
  cancelled, and the fact that collected and cancelled orders stay available through `view` and `history`.

#### Contributions to the Developer Guide

* Wrote the **Order progress and storage** section: how `mark` checks the transition, how `Duke` saves after
  a successful change, and the two alternatives above.
* Added the mark-and-save sequence diagram for a successful `QUEUED` → `PREPARING` step.

#### Contributions to team-based tasks

* Added my details to the About Us page.
  [PR #1](https://github.com/AY2627S1-CS2113-W12-1/tp/pull/1)
* Put the shared command loop and save callback in place so later commands (`list`, `find`, `edit`,
  `customer`, `history`, `capacity`) could plug in without each one opening the data file.
