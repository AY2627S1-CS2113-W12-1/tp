package seedu.duke.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
 * Tests cancelling enquiries and orders through {@link CancelCommand}.
 */
public class CancelCommandTest {

    /**
     * Creates an order list with one enquiry (E001) and one confirmed order (O001).
     */
    private OrderList createOrderList(OrderStatus enquiryStatus, OrderStatus orderStatus) {
        OrderList orders = new OrderList();
        orders.add(new Order("E001",
                new Customer("Alice Tan", "91234567"),
                new Cake(8, "Chocolate", "Strawberry"),
                LocalDate.of(2026, 10, 20),
                enquiryStatus));
        orders.add(new Order("O001",
                new Customer("Bob Tan", "81111111"),
                new Cake(6, "Vanilla", "Cream"),
                LocalDate.of(2026, 10, 21),
                orderStatus));
        return orders;
    }

    @Test
    public void execute_cancelsQueuedOrder() {
        OrderList orders = createOrderList(OrderStatus.ENQUIRY, OrderStatus.QUEUED);

        String result = new CancelCommand("O001").execute(orders);

        assertTrue(result.contains("Cancelled O001"));
        assertEquals(OrderStatus.CANCELLED, orders.getById("O001").getStatus());
    }

    @Test
    public void execute_cancelsEnquiry() {
        OrderList orders = createOrderList(OrderStatus.ENQUIRY, OrderStatus.QUEUED);

        String result = new CancelCommand("E001").execute(orders);

        assertTrue(result.contains("Cancelled E001"));
        assertEquals(OrderStatus.CANCELLED, orders.getById("E001").getStatus());
    }

    @Test
    public void execute_collectedOrder_throwsException() {
        OrderList orders = createOrderList(OrderStatus.ENQUIRY, OrderStatus.COLLECTED);

        CakeQueueException exception = assertThrows(CakeQueueException.class,
                () -> new CancelCommand("O001").execute(orders));

        assertTrue(exception.getMessage().contains("Cannot cancel a record with status COLLECTED"));
        assertEquals(OrderStatus.COLLECTED, orders.getById("O001").getStatus());
    }

    @Test
    public void execute_rejectedEnquiry_throwsException() {
        OrderList orders = createOrderList(OrderStatus.REJECTED, OrderStatus.QUEUED);

        assertThrows(CakeQueueException.class,
                () -> new CancelCommand("E001").execute(orders));

        assertEquals(OrderStatus.REJECTED, orders.getById("E001").getStatus());
    }

    @Test
    public void execute_alreadyCancelled_throwsException() {
        OrderList orders = createOrderList(OrderStatus.ENQUIRY, OrderStatus.CANCELLED);

        assertThrows(CakeQueueException.class,
                () -> new CancelCommand("O001").execute(orders));
    }
}
