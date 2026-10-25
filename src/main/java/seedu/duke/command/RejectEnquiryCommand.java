package seedu.duke.command;

//@@author HammerHead-os

import seedu.duke.exception.CakeQueueException;
import seedu.duke.model.Order;
import seedu.duke.model.OrderList;
import seedu.duke.model.OrderStatus;

/**
 * Rejects a customer enquiry so it no longer waits for acceptance.
 */
public class RejectEnquiryCommand {
    private final String enquiryId;

    public RejectEnquiryCommand(String enquiryId) {
        this.enquiryId = enquiryId;
    }

    /**
     * Marks the enquiry as rejected.
     */
    public String execute(OrderList orders) {
        Order enquiry = orders.getById(enquiryId);
        if (!enquiry.isEnquiry()) {
            throw new CakeQueueException("Only enquiries can be rejected: " + enquiryId);
        }
        enquiry.setStatus(OrderStatus.REJECTED);
        return "Rejected enquiry " + enquiry;
    }
}
