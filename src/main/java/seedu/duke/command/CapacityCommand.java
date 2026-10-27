package seedu.duke.command;

//@@author Kaishhhh

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import seedu.duke.model.OrderList;

/**
 * Shows or changes the most orders the baker can take for one due date,
 * and reports which due dates are already over that limit.
 */
public class CapacityCommand {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-uuuu");

    private final Integer newCapacity;

    /**
     * Creates a command to show or change the daily capacity.
     *
     * @param newCapacity The new daily capacity, or null to only show the current one.
     */
    public CapacityCommand(Integer newCapacity) {
        this.newCapacity = newCapacity;
    }

    /**
     * Sets the daily capacity if one was given, then returns the capacity
     * and every due date that has more active orders than the capacity allows.
     *
     * @param orders The list of all orders and enquiries.
     * @return The message to show the user.
     */
    public String execute(OrderList orders) {
        String nl = System.lineSeparator();
        StringBuilder message = new StringBuilder();
        if (newCapacity != null) {
            orders.setDailyCapacity(newCapacity);
            message.append("Daily capacity set to ");
        } else {
            message.append("Daily capacity is ");
        }
        int capacity = orders.getDailyCapacity();
        message.append(capacity).append(capacity == 1 ? " order per day." : " orders per day.");

        StringBuilder overloaded = new StringBuilder();
        for (Map.Entry<LocalDate, Integer> entry : orders.countOrdersByDueDate().entrySet()) {
            if (entry.getValue() > orders.getDailyCapacity()) {
                overloaded.append("  ").append(entry.getKey().format(DATE_FORMATTER))
                        .append(": ").append(entry.getValue()).append(" orders").append(nl);
            }
        }
        if (overloaded.length() > 0) {
            message.append(nl).append("These dates are over capacity:").append(nl).append(overloaded);
        }
        return message.toString().stripTrailing();
    }

    /**
     * Returns a warning if the date has more active orders than the daily capacity.
     *
     * @param orders The list of all orders and enquiries.
     * @param dueDate The due date to check, or null for an order without a date.
     * @return The warning, or an empty string if the date is within capacity.
     */
    public static String warningFor(OrderList orders, LocalDate dueDate) {
        if (dueDate == null) {
            return "";
        }
        int count = orders.countOrdersDueOn(dueDate);
        if (count <= orders.getDailyCapacity()) {
            return "";
        }
        return "Warning: " + dueDate.format(DATE_FORMATTER) + " now has " + count
                + " orders, over your daily capacity of " + orders.getDailyCapacity() + ".";
    }
}
