package seedu.duke.command;

//@@author HammerHead-os

import seedu.duke.exception.CakeQueueException;
import seedu.duke.model.Order;
import seedu.duke.model.OrderList;
import seedu.duke.model.OrderStatus;

/**
 * Accepts a customer enquiry and converts it into a confirmed order.
 */
public class AcceptEnquiryCommand {
    private final String enquiryId;

    public AcceptEnquiryCommand(String enquiryId) {
        this.enquiryId = enquiryId;
    }

    /**
     * Replaces the enquiry with a queued order that has a new Order ID.
     */
    public String execute(OrderList orders) {
        Order enquiry = orders.getById(enquiryId);
        if (!enquiry.isEnquiry()) {
            throw new CakeQueueException("Only enquiries can be accepted: " + enquiryId);
        }
        orders.removeById(enquiryId);
        Order confirmed = new Order(orders.generateOrderId(),
                enquiry.getCustomer(),
                enquiry.getCake(),
                enquiry.getDueDate(),
                OrderStatus.QUEUED);
        orders.add(confirmed);
        return "Accepted enquiry " + enquiryId + " as order " + confirmed;
    }
}
