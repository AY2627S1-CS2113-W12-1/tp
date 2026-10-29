package seedu.duke.command;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import seedu.duke.model.Order;
import seedu.duke.model.OrderList;
import seedu.duke.model.OrderStatus;

/**
 * Displays a summary of all records in the queue: counts per status and the
 * next upcoming due date so the baker can see workload at a glance.
 */
public class StatsCommand {
    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd-MM-uuuu");

    /**
     * Counts records by status and finds the earliest due date among active orders.
     */
    public String execute(OrderList orders) {
        ArrayList<Order> all = orders.getAll();
        if (all.isEmpty()) {
            return "No records yet.";
        }

        int enquiries = 0;
        int queued = 0;
        int preparing = 0;
        int ready = 0;
        int collected = 0;
        int cancelled = 0;
        int rejected = 0;
        LocalDate nextDue = null;

        for (Order order : all) {
            OrderStatus s = order.getStatus();
            if (s == OrderStatus.ENQUIRY) {
                enquiries++;
            } else if (s == OrderStatus.QUEUED) {
                queued++;
            } else if (s == OrderStatus.PREPARING) {
                preparing++;
            } else if (s == OrderStatus.READY) {
                ready++;
            } else if (s == OrderStatus.COLLECTED) {
                collected++;
            } else if (s == OrderStatus.CANCELLED) {
                cancelled++;
            } else if (s == OrderStatus.REJECTED) {
                rejected++;
            }

            // Track the earliest due date among active (non-closed) orders.
            boolean isActive = s == OrderStatus.QUEUED
                    || s == OrderStatus.PREPARING
                    || s == OrderStatus.READY;
            if (isActive && order.getDueDate() != null) {
                if (nextDue == null || order.getDueDate().isBefore(nextDue)) {
                    nextDue = order.getDueDate();
                }
            }
        }

        int activeOrders = queued + preparing + ready;
        String nl = System.lineSeparator();
        StringBuilder sb = new StringBuilder("Queue summary:");
        sb.append(nl);
        sb.append("  Active orders : ").append(activeOrders)
          .append(" (queued: ").append(queued)
          .append(", preparing: ").append(preparing)
          .append(", ready: ").append(ready).append(")").append(nl);
        sb.append("  Enquiries     : ").append(enquiries).append(nl);
        sb.append("  Collected     : ").append(collected).append(nl);
        sb.append("  Cancelled     : ").append(cancelled).append(nl);
        sb.append("  Rejected      : ").append(rejected).append(nl);
        if (nextDue != null) {
            sb.append("  Next due date : ").append(nextDue.format(DATE_FORMATTER));
        } else {
            sb.append("  Next due date : none");
        }
        return sb.toString();
    }
}
