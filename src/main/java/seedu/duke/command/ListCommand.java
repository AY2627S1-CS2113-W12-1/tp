package seedu.duke.command;

import java.util.ArrayList;
import java.util.Comparator;

import seedu.duke.model.Order;
import seedu.duke.model.OrderList;
import seedu.duke.model.OrderStatus;

/**
 * Represents a command that lists all upcoming orders.
 */
public class ListCommand {

    /**
     * Returns uncollected orders with the earliest due dates first.
     * Legacy orders without a due date are listed last.
     *
     * @param orders The list of all orders.
     * @return The message to show the user.
     */
    public String execute(OrderList orders) {
        ArrayList<Order> all = orders.getAll();
        all.removeIf(order -> order.getStatus() == OrderStatus.COLLECTED);
        all.sort(Comparator.comparing(Order::getDueDate, Comparator.nullsLast(Comparator.naturalOrder())));
        StringBuilder listing = new StringBuilder();
        for (int i = 0; i < all.size(); i++) {
            Order order = all.get(i);
            listing.append(i + 1)
                    .append(". ")
                    .append(order)
                    .append(System.lineSeparator());
        }
        if (listing.length() == 0) {
            return "You have no upcoming orders.";
        }
        return "Here are your upcoming orders:" + System.lineSeparator() + listing;
    }
}
