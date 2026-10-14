# User Guide

## Introduction

CakeQueue helps bakers record and track cake orders.

## Quick Start

{Give steps to get started quickly}

1. Ensure that you have Java 25 or above installed.
1. Down the latest version of `Duke` from [here](http://link.to/duke).

## Features

### Adding an order: `add`

Adds an order with customer contact details, a due date, and cake details.

Format: `add n/NAME p/PHONE d/DATE s/SIZE f/FLAVOUR fi/FILLING`

* `DATE` must use the `DD-MM-YYYY` format.
* All six fields are required, must be non-empty, and must appear exactly once.
* Parameters can be entered in any order. Unknown prefixes are rejected.
* `SIZE` must be `6`, `8`, or `10` inches.
* `PHONE` is recorded as entered and must be non-empty.
* CakeQueue assigns sequential Order IDs such as `O001` and starts each order as `QUEUED`.
* Orders are stored only while the application is running.
* Customer IDs and matching returning customers are planned for a later version.

Example: `add n/Alice Tan p/91234567 d/20-10-2026 s/8 f/Chocolate fi/Strawberry`

### Exiting CakeQueue: `bye`

Exits the application.

Format: `bye`

## FAQ

**Q**: How do I transfer my data to another computer? 

**A**: {your answer here}

## Command Summary

* Add order: `add n/NAME p/PHONE d/DATE s/SIZE f/FLAVOUR fi/FILLING`
* Exit: `bye`
