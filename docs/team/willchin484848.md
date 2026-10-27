
# Will Chin - Project Portfolio Page

## Project: CakeQueue

CakeQueue is a command-line application that helps bakers manage
cake orders, customer information, and upcoming deadlines.

## Summary of Contributions

### 1. Update Order Feature

- Implemented the `edit` command to update existing cake orders.
- Supported editing customer names, phone numbers, due dates,
  cake sizes, flavours, and fillings.
- Ensured unspecified fields remain unchanged during updates.
- Added validation and error handling for invalid updates.
- Wrote JUnit tests to verify the update functionality.

### 2. Cake Details Improvements

- Added model-level validation for supported cake sizes.
- Improved validation of cake flavours and fillings.
- Improved exception handling when updating cake details.
- Added handling for orders without due dates.

### 3. Due-Date Warnings

- Implemented `[OVERDUE]` warnings for orders past their due dates.
- Implemented `[DUE SOON]` warnings for orders due within 3 days.
- Added JUnit tests covering overdue orders and warning boundaries.

### 4. Documentation

- Updated the User Guide to explain cake validation and
  due-date warning behaviour.
