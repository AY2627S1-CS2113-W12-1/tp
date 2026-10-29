package seedu.duke.command;

import java.util.ArrayList;

import seedu.duke.model.Order;
import seedu.duke.model.OrderList;

/**
 * Searches orders and enquiries by customer name or phone number.
 * The search is case-insensitive and matches any record whose name contains
 * the query, or whose phone equals the query exactly.
 */
public class SearchCommand {
    private final String query;

    /**
     * Creates the command with the search query.
     *
     * @param query The name fragment or phone number to search for.
     */
    public SearchCommand(String query) {
        this.query = query;
    }

    /**
     * Returns all records matching the query, with numbered results.
     */
    public String execute(OrderList orders) {
        ArrayList<Order> all = orders.getAll();
        ArrayList<Order> matches = new ArrayList<>();
        String lowerQuery = query.toLowerCase();

        for (Order order : all) {
            String name = order.getCustomer().getName().toLowerCase();
            String phone = order.getCustomer().getPhone();
            if (name.contains(lowerQuery) || phone.equals(query)) {
                matches.add(order);
            }
        }

        if (matches.isEmpty()) {
            return "No records found for: " + query;
        }

        StringBuilder result = new StringBuilder(
                "Found " + matches.size() + " record(s) for \"" + query + "\":");
        result.append(System.lineSeparator());
        for (int i = 0; i < matches.size(); i++) {
            result.append(i + 1).append(". ").append(matches.get(i))
                  .append(System.lineSeparator());
        }
        return result.toString().trim();
    }
}
