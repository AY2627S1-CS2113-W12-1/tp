package seedu.duke.command;

//@@author anannya-mk

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import seedu.duke.model.Cake;
import seedu.duke.model.Customer;
import seedu.duke.model.Order;
import seedu.duke.model.OrderList;
import seedu.duke.model.OrderStatus;

/**
 * Tests the view command.
 */
public class ViewOrderCommandTest {
    private static OrderList sampleOrders() {
        OrderList orders = new OrderList();
        Cake cake = new Cake(8, "Chocolate", "Cream");
        LocalDate due = LocalDate.of(2026, 12, 1);
        orders.add(new Order("O001", new Customer("Alice", "91111111"), cake, due, OrderStatus.QUEUED));
        orders.add(new Order("E001", new Customer("Bob", "92222222"), cake, due, OrderStatus.ENQUIRY));
        return orders;
    }

    @Test
    public void execute_existingOrder_showsDetailsAndCustomerId() {
        String result = new ViewOrderCommand("O001").execute(sampleOrders());
        assertTrue(result.contains("Order ID: O001"));
        assertTrue(result.contains("Customer ID: C001"));
    }

    @Test
    public void execute_lowerCaseId_findsOrder() {
        String result = new ViewOrderCommand("o001").execute(sampleOrders());
        assertTrue(result.contains("Order ID: O001"));
    }

    @Test
    public void execute_enquiry_showsNoCustomerId() {
        String result = new ViewOrderCommand("E001").execute(sampleOrders());
        assertFalse(result.contains("Customer ID: C"));
    }
}
