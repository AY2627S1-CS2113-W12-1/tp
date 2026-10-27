package seedu.duke.command;

//@@author anannya-mk

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import seedu.duke.exception.CakeQueueException;
import seedu.duke.model.Cake;
import seedu.duke.model.Customer;
import seedu.duke.model.Order;
import seedu.duke.model.OrderList;
import seedu.duke.model.OrderStatus;

/**
 * Tests the history command.
 */
public class HistoryCommandTest {
    private static OrderList sampleOrders() {
        OrderList orders = new OrderList();
        Cake cake = new Cake(8, "Chocolate", "Cream");
        LocalDate due = LocalDate.of(2026, 12, 1);
        orders.add(new Order("O001", new Customer("Alice", "91111111"), cake, due, OrderStatus.COLLECTED));
        orders.add(new Order("O002", new Customer("Bob", "92222222"), cake, due, OrderStatus.QUEUED));
        orders.add(new Order("O003", new Customer("Alice", "91111111"), cake, due, OrderStatus.CANCELLED));
        orders.add(new Order("E001", new Customer("Alice", "91111111"), cake, due, OrderStatus.ENQUIRY));
        return orders;
    }

    @Test
    public void execute_listsOnlyThisCustomersOrdersIncludingCancelled() {
        String result = new HistoryCommand("C001").execute(sampleOrders());
        assertTrue(result.contains("Order history for Alice (C001):"));
        assertTrue(result.contains("1. O001"));
        assertTrue(result.contains("2. O003"));
        assertTrue(result.contains("[CANCELLED]"));
        assertFalse(result.contains("O002"));
        assertFalse(result.contains("E001"));
    }

    @Test
    public void execute_unknownCustomer_throws() {
        assertThrows(CakeQueueException.class, () -> new HistoryCommand("C009").execute(sampleOrders()));
    }
}
