package seedu.duke.command;

//@@author anannya-mk

import java.util.List;

import seedu.duke.model.CustomerList;
import seedu.duke.model.CustomerProfile;
import seedu.duke.model.Order;
import seedu.duke.model.OrderList;

/**
 * Shows every order recorded for one customer, found by Customer ID.
 */
public class HistoryCommand {
    private final String customerId;

    public HistoryCommand(String customerId) {
        this.customerId = customerId;
    }

    /**
     * Returns all confirmed orders belonging to the customer, oldest first.
     * Upcoming, completed and cancelled orders are all included, and each line ends with the order's status.
     *
     * @param orders The list of all orders and enquiries.
     * @return A numbered list of the customer's orders.
     * @throws seedu.duke.exception.CakeQueueException If no customer has that ID.
     */
    public String execute(OrderList orders) {
        CustomerProfile customer = CustomerList.fromOrders(orders).getById(customerId);
        List<Order> history = customer.getOrders();

        String nl = System.lineSeparator();
        StringBuilder result = new StringBuilder("Order history for " + customer.getName()
                + " (" + customer.getCustomerId() + "):");
        for (int i = 0; i < history.size(); i++) {
            result.append(nl).append(i + 1).append(". ").append(history.get(i));
        }
        return result.toString();
    }
}
