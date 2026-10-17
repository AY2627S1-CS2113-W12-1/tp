package seedu.duke.command;

import seedu.duke.model.Order;
import seedu.duke.model.OrderList;
import seedu.duke.model.OrderStatus;

/**
 * Cancels an enquiry or order that has not been completed.
 */
public class CancelCommand {
    private final String recordId;
    private final String phone;
    private final boolean requirePhoneMatch;

    /**
     * Creates a baker cancel command that does not check phone ownership.
     */
    public CancelCommand(String recordId) {
        this(recordId, null, false);
    }

    /**
     * Creates a customer cancel command that requires a matching phone number.
     */
    public CancelCommand(String recordId, String phone) {
        this(recordId, phone, true);
    }

    private CancelCommand(String recordId, String phone, boolean requirePhoneMatch) {
        this.recordId = recordId;
        this.phone = phone;
        this.requirePhoneMatch = requirePhoneMatch;
    }

    /**
     * Marks the record as cancelled when cancellation is still allowed.
     */
    public String execute(OrderList orders) {
        Order record = orders.getById(recordId);
        if (requirePhoneMatch && !record.getCustomer().getPhone().equals(phone)) {
            throw new IllegalArgumentException("Phone number does not match this record.");
        }
        OrderStatus status = record.getStatus();
        if (status == OrderStatus.COLLECTED || status == OrderStatus.CANCELLED
                || status == OrderStatus.REJECTED) {
            throw new IllegalArgumentException("Cannot cancel a record with status " + status + ".");
        }
        if (requirePhoneMatch && status != OrderStatus.ENQUIRY) {
            throw new IllegalArgumentException("Customers may only cancel enquiries.");
        }
        record.setStatus(OrderStatus.CANCELLED);
        return "Cancelled " + record;
    }
}
