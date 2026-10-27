
package seedu.duke.model;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Tests the display of order details.
 */
public class OrderTest {

    /**
     * Verifies that an order with cake details but no due date
     * displays a placeholder instead of throwing an exception.
     */
    @Test
    public void toString_missingDueDateWithCake_displaysPlaceholder() {
        Customer customer = new Customer("Alice", "91234567");
        Cake cake = new Cake(6, "Chocolate", "Vanilla");
        Order order = new Order("O001", customer, cake, null);

        // Missing dates should be displayed safely.
        assertTrue(order.toString().contains("Due: Not specified"));
    }

    /**
     * Verifies that an order without cake details or a due date
     * can still be displayed.
     */
    @Test
    public void toString_missingDueDateWithoutCake_displaysPlaceholder() {
        Customer customer = new Customer("Alice", "91234567");
        Order order = new Order("O002", customer, null);

        // The basic order display should also handle missing dates.
        assertTrue(order.toString().contains("due Not specified"));
    }
}
