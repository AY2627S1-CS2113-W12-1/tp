package seedu.duke.command;

//@@author Kaishhhh

import java.util.ArrayList;
import java.util.Locale;

import seedu.duke.model.Cake;
import seedu.duke.model.Order;
import seedu.duke.model.OrderList;

/**
 * Finds orders and enquiries whose customer name, cake flavour or cake filling
 * contains a keyword.
 */
public class FindCommand {
    private final String keyword;

    public FindCommand(String keyword) {
        assert keyword != null && !keyword.isBlank() : "Keyword must not be blank";
        this.keyword = keyword.trim();
    }

    /**
     * Returns every record matching the keyword, in the order they were added.
     * Matching ignores case and does not change the order list.
     *
     * @param orders The list of all orders and enquiries.
     * @return The message to show the user.
     */
    public String execute(OrderList orders) {
        String lowerKeyword = keyword.toLowerCase(Locale.ROOT);
        ArrayList<Order> matches = new ArrayList<>();
        for (Order order : orders.getAll()) {
            if (matches(order, lowerKeyword)) {
                matches.add(order);
            }
        }
        if (matches.isEmpty()) {
            return "No orders found matching \"" + keyword + "\".";
        }
        StringBuilder listing = new StringBuilder("Orders matching \"" + keyword + "\":");
        listing.append(System.lineSeparator());
        for (int i = 0; i < matches.size(); i++) {
            listing.append(i + 1)
                    .append(". ")
                    .append(matches.get(i))
                    .append(System.lineSeparator());
        }
        return listing.toString();
    }

    private boolean matches(Order order, String lowerKeyword) {
        if (contains(order.getCustomer().getName(), lowerKeyword)) {
            return true;
        }
        Cake cake = order.getCake();
        return cake != null && (contains(cake.getFlavour(), lowerKeyword)
                || contains(cake.getFilling(), lowerKeyword));
    }

    private boolean contains(String text, String lowerKeyword) {
        return text != null && text.toLowerCase(Locale.ROOT).contains(lowerKeyword);
    }
}
