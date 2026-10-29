package seedu.duke.command;

import seedu.duke.exception.CakeQueueException;
import seedu.duke.model.Order;
import seedu.duke.model.OrderList;
import seedu.duke.model.OrderStatus;

/**
 * Permanently removes a closed record (cancelled, rejected, or collected) from the list.
 * Only closed records may be deleted — active orders and pending enquiries are protected
 * so the baker cannot accidentally lose work in progress.
 */
public class DeleteCommand {
    private final String recordId;

    public DeleteCommand(String recordId) {
        this.recordId = recordId;
    }

    /**
     * Removes the record if it is in a closed state, then returns a confirmation.
     *
     * @throws CakeQueueException if the record is still active.
     */
    public String execute(OrderList orders) {
        Order record = orders.getById(recordId);
        OrderStatus status = record.getStatus();
        if (status != OrderStatus.CANCELLED
                && status != OrderStatus.REJECTED
                && status != OrderStatus.COLLECTED) {
            throw new CakeQueueException(
                    "Only closed records (cancelled, rejected, collected) can be deleted. "
                    + recordId + " is " + status + ".");
        }
        orders.removeById(recordId);
        return "Deleted " + recordId + " (" + status + ").";
    }
}
