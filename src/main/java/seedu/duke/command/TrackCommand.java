package seedu.duke.command;

//@@author HammerHead-os

import seedu.duke.exception.CakeQueueException;
import seedu.duke.model.Order;
import seedu.duke.model.OrderList;

/**
 * Shows one enquiry or order after verifying the customer phone number.
 */
public class TrackCommand {
    private final String recordId;
    private final String phone;

    public TrackCommand(String recordId, String phone) {
        this.recordId = recordId;
        this.phone = phone;
    }

    /**
     * Returns detailed information when the phone matches the record owner.
     */
    public String execute(OrderList orders) {
        Order record = orders.getById(recordId);
        if (!record.getCustomer().getPhone().equals(phone)) {
            throw new CakeQueueException("Phone number does not match this record.");
        }
        return record.toDetailedString();
    }
}
