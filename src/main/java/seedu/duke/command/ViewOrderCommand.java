package seedu.duke.command;

//@@author anannya-mk

import seedu.duke.model.OrderList;

/**
 * Shows the full details of one order, found by its order ID.
 */
public class ViewOrderCommand {
    private final String orderId;

    public ViewOrderCommand(String orderId) {
        this.orderId = orderId;
    }

    /**
     * Returns the full details of the order with this ID.
     *
     * @throws seedu.duke.exception.CakeQueueException if no order has that ID.
     */
    public String execute(OrderList orders) {
        return orders.getById(orderId).toDetailedString();
    }
}
