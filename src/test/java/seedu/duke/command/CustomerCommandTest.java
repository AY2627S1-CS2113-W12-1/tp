package seedu.duke.command;

//@@author anannya-mk

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
 * Tests the customer command.
 */
public class CustomerCommandTest {
    private static OrderList sampleOrders() {
        OrderList orders = new OrderList();
        Cake cake = new Cake(8, "Chocolate", "Cream");
        LocalDate due = LocalDate.of(2026, 12, 1);
        orders.add(new Order("O001", new Customer("Alice", "91111111"), cake, due, OrderStatus.QUEUED));
        orders.add(new Order("O002", new Customer("Bob", "92222222"), cake, due, OrderStatus.QUEUED));
        orders.add(new Order("O003", new Customer("Alice", "91111111"), cake, due, OrderStatus.CANCELLED));
        return orders;
    }

    @Test
    public void execute_repeatCustomer_showsDetails() {
        String result = new CustomerCommand("C001").execute(sampleOrders());
        assertTrue(result.contains("Customer ID: C001"));
        assertTrue(result.contains("Name: Alice"));
        assertTrue(result.contains("Phone: 91111111"));
        assertTrue(result.contains("Orders: 2"));
        assertTrue(result.contains("Repeat customer: Yes"));
    }

    @Test
    public void execute_singleOrderCustomer_notRepeat() {
        String result = new CustomerCommand("c002").execute(sampleOrders());
        assertTrue(result.contains("Orders: 1"));
        assertTrue(result.contains("Repeat customer: No"));
    }

    @Test
    public void execute_unknownCustomer_throws() {
        assertThrows(CakeQueueException.class, () -> new CustomerCommand("C009").execute(sampleOrders()));
    }
}
