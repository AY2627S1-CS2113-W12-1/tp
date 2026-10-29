package seedu.duke.command;

//@@author Kaishhhh

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;

import seedu.duke.model.Order;
import seedu.duke.model.OrderList;
import seedu.duke.model.OrderStatus;

/**
 * Represents a command that lists all upcoming confirmed orders.
 */
public class ListCommand {

    //@@author WillChin484848
    private static final int DUE_SOON_DAYS = 3;
    //@@author Kaishhhh

    /**
     * Returns uncollected confirmed orders with the earliest due dates first.
     * Enquiries and closed records are omitted. Orders without a due date
     * are listed last.
     *
     * @param orders The list of all orders.
     * @return The message to show the user.
     */
    public String execute(OrderList orders) {
        ArrayList<Order> all = orders.getAll();

        //@@author HammerHead-os
        // Exclude enquiries and orders that are already closed.
        all.removeIf(order -> order.getStatus() == OrderStatus.COLLECTED
                || order.getStatus() == OrderStatus.ENQUIRY
                || order.getStatus() == OrderStatus.CANCELLED
                || order.getStatus() == OrderStatus.REJECTED);
        //@@author Eethanzyj
        // Sort by due date, placing orders without a due date last.
        all.sort(Comparator.comparing(Order::getDueDate,
                Comparator.nullsLast(Comparator.naturalOrder())));

        //@@author WillChin484848
        LocalDate today = LocalDate.now();
        //@@author Kaishhhh
        StringBuilder listing = new StringBuilder();

        for (int i = 0; i < all.size(); i++) {
            Order order = all.get(i);

            listing.append(i + 1)
                    .append(". ")
                    .append(order);

            //@@author WillChin484848
            // Highlight overdue and approaching deadlines.
            if (order.getDueDate() != null) {
                long daysUntilDue = ChronoUnit.DAYS.between(today, order.getDueDate());

                if (daysUntilDue < 0) {
                    listing.append(" [OVERDUE]");
                } else if (daysUntilDue <= DUE_SOON_DAYS) {
                    listing.append(" [DUE SOON]");
                }
            }
            //@@author Kaishhhh

            listing.append(System.lineSeparator());
        }

        if (listing.length() == 0) {
            return "You have no upcoming orders.";
        }

        return "Here are your upcoming orders:"
                + System.lineSeparator() + listing;
    }
}
