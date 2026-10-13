package seedu.duke.command;

import java.util.ArrayList;

import seedu.duke.model.Order;
import seedu.duke.model.OrderList;
import seedu.duke.model.OrderStatus;

/**
 * Represents a command that lists all upcoming orders.
 */
public class ListCommand {

    /**
     * Returns the message listing every order that has not been collected.
     * Each order keeps its position in the full order list as its number.
     *
     * @param orders The list of all orders.
     * @return The message to show the user.
     */
    public String execute(OrderList orders) {
        ArrayList<Order> all = orders.getAll();
        StringBuilder listing = new StringBuilder();
        for (int i = 0; i < all.size(); i++) {
            Order order = all.get(i);
            if (order.getStatus() != OrderStatus.COLLECTED) {
                listing.append(i + 1)
                        .append(". ")
                        .append(order)
                        .append(System.lineSeparator());
            }
        }
        if (listing.length() == 0) {
            return "You have no upcoming orders.";
        }
        return "Here are your upcoming orders:" + System.lineSeparator() + listing;
    }
}
