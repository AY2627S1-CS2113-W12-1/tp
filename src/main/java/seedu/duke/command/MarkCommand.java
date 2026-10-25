package seedu.duke.command;

//@@author HammerHead-os

import seedu.duke.exception.CakeQueueException;
import seedu.duke.model.Order;
import seedu.duke.model.OrderList;
import seedu.duke.model.OrderStatus;

/**
 * Moves a confirmed order one step forward in the production workflow:
 * {@code QUEUED -> PREPARING -> READY -> COLLECTED}.
 *
 * <p>Only single forward steps are allowed. Enquiries must first be accepted
 * (they become {@code QUEUED} orders), and closed records
 * ({@code COLLECTED}, {@code CANCELLED}, {@code REJECTED}) cannot move.</p>
 */
public class MarkCommand {
    private final String orderId;
    private final OrderStatus targetStatus;

    /**
     * Creates a command to move the given order to the given status.
     */
    public MarkCommand(String orderId, OrderStatus targetStatus) {
        this.orderId = orderId;
        this.targetStatus = targetStatus;
    }

    /**
     * Changes the order's status if the requested step is the next allowed step.
     *
     * @param orders The list containing the order.
     * @return A confirmation message showing the order with its new status.
     * @throws CakeQueueException If the order does not exist, is not a
     *         confirmed order, or the requested step skips/goes backwards.
     */
    public String execute(OrderList orders) {
        assert orders != null : "Order list must not be null";
        assert orderId != null && targetStatus != null : "Order ID and target status must not be null";
        Order order = orders.getById(orderId);
        OrderStatus current = order.getStatus();
        if (!isNextStep(current, targetStatus)) {
            throw new CakeQueueException(describeProblem(current));
        }
        order.setStatus(targetStatus);
        assert order.getStatus() == targetStatus : "Status must equal the target status after marking";
        return "Updated order " + order;
    }

    private boolean isNextStep(OrderStatus current, OrderStatus target) {
        return (current == OrderStatus.QUEUED && target == OrderStatus.PREPARING)
                || (current == OrderStatus.PREPARING && target == OrderStatus.READY)
                || (current == OrderStatus.READY && target == OrderStatus.COLLECTED);
    }

    private String describeProblem(OrderStatus current) {
        if (current == OrderStatus.ENQUIRY) {
            return "Enquiry " + orderId + " is not a confirmed order yet. "
                    + "Accept it before marking its status.";
        }
        if (current == OrderStatus.COLLECTED || current == OrderStatus.CANCELLED
                || current == OrderStatus.REJECTED) {
            return "Order " + orderId + " is already " + current + " and cannot be marked.";
        }
        return "Cannot change order " + orderId + " from " + current + " to " + targetStatus
                + ". Orders move one step at a time: QUEUED -> PREPARING -> READY -> COLLECTED.";
    }
}
