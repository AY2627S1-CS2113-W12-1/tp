package seedu.duke.command;

//@@author HammerHead-os

import seedu.duke.exception.CakeQueueException;
import seedu.duke.model.Order;
import seedu.duke.model.OrderList;
import seedu.duke.model.OrderStatus;

/**
 * Cancels an enquiry or order that has not yet been completed.
 */
public class CancelCommand {
    private final String recordId;

    public CancelCommand(String recordId) {
        this.recordId = recordId;
    }

    /**
     * Marks the record as CANCELLED when it is still in a cancellable state.
     * A record cannot be cancelled once it is collected, already cancelled, or rejected.
     */
    public String execute(OrderList orders) {
        Order record = orders.getById(recordId);
        OrderStatus status = record.getStatus();
        if (status == OrderStatus.COLLECTED || status == OrderStatus.CANCELLED
                || status == OrderStatus.REJECTED) {
            throw new CakeQueueException("Cannot cancel a record with status " + status + ".");
        }
        record.setStatus(OrderStatus.CANCELLED);
        return "Cancelled " + record;
    }
}
