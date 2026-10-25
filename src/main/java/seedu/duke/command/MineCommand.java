package seedu.duke.command;

//@@author HammerHead-os

import java.util.ArrayList;

import seedu.duke.model.Order;
import seedu.duke.model.OrderList;

/**
 * Lists enquiries and orders belonging to one customer phone number.
 */
public class MineCommand {
    private final String phone;

    public MineCommand(String phone) {
        this.phone = phone;
    }

    /**
     * Returns every record linked to the given phone number.
     */
    public String execute(OrderList orders) {
        ArrayList<Order> matches = orders.findByPhone(phone);
        if (matches.isEmpty()) {
            return "No enquiries or orders found for phone " + phone + ".";
        }
        StringBuilder listing = new StringBuilder("Records for phone " + phone + ":");
        listing.append(System.lineSeparator());
        for (int i = 0; i < matches.size(); i++) {
            listing.append(i + 1)
                    .append(". ")
                    .append(matches.get(i))
                    .append(System.lineSeparator());
        }
        return listing.toString();
    }
}
