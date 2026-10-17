package seedu.duke.command;

import java.util.ArrayList;

import seedu.duke.model.Order;
import seedu.duke.model.OrderList;
import seedu.duke.model.OrderStatus;

/**
 * Lists customer enquiries that still need a baker decision.
 */
public class ListEnquiriesCommand {

    /**
     * Returns pending enquiries in insertion order.
     */
    public String execute(OrderList orders) {
        ArrayList<Order> enquiries = new ArrayList<>();
        for (Order order : orders.getAll()) {
            if (order.getStatus() == OrderStatus.ENQUIRY) {
                enquiries.add(order);
            }
        }
        if (enquiries.isEmpty()) {
            return "You have no pending enquiries.";
        }
        StringBuilder listing = new StringBuilder("Here are your pending enquiries:");
        listing.append(System.lineSeparator());
        for (int i = 0; i < enquiries.size(); i++) {
            listing.append(i + 1)
                    .append(". ")
                    .append(enquiries.get(i))
                    .append(System.lineSeparator());
        }
        return listing.toString();
    }
}
