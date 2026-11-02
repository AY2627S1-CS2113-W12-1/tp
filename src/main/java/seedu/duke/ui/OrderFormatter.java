package seedu.duke.ui;

//@@author Eethanzyj

import java.time.format.DateTimeFormatter;

import seedu.duke.model.Cake;
import seedu.duke.model.Customer;
import seedu.duke.model.Order;

/**
 * Formats a numbered order as a readable block for list, find and history results.
 * Single-order confirmations and storage keep their existing formats.
 */
public final class OrderFormatter {
    /** Separates neighbouring orders without relying on terminal width or special characters. */
    public static final String SEPARATOR = "------------------------------------------------------------";

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-uuuu");

    private OrderFormatter() {
    }

    /**
     * Formats one entry, beginning with its separator, position, Order ID and status.
     *
     * @param order The record to display, including legacy records with missing details.
     * @param position The display position, starting at one; commands still use the Order ID.
     * @param warning An optional deadline label, including its leading space, or an empty string.
     * @return The order block without a trailing newline or closing separator.
     */
    public static String format(Order order, int position, String warning) {
        assert order != null && warning != null : "Order and warning must not be null";
        String nl = System.lineSeparator();
        String id = order.getOrderId() == null ? "Not assigned" : order.getOrderId();
        Customer customer = order.getCustomer();
        StringBuilder result = new StringBuilder(SEPARATOR).append(nl)
                .append(position).append(". ").append(id)
                .append(" [").append(order.getStatus()).append("]").append(warning).append(nl)
                .append("   Customer: ").append(customer.getName());
        if (customer.getPhone() != null && !customer.getPhone().isBlank()) {
            result.append(" (").append(customer.getPhone()).append(")");
        }
        result.append(nl).append("   Due: ")
                .append(order.getDueDate() == null ? "Not specified" : order.getDueDate().format(DATE_FORMATTER));
        Cake cake = order.getCake();
        if (cake == null) {
            result.append(nl).append("   Cake details: Not specified");
        } else {
            result.append(nl).append("   Size: ").append(cake.getSize()).append(" inches")
                    .append(nl).append("   Flavour: ").append(cake.getFlavour())
                    .append(" | Filling: ").append(cake.getFilling());
        }
        return result.toString();
    }
}
