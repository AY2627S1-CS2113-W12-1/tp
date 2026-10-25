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
 * Tests cancelling enquiries and orders through {@link CancelCommand},
 * for both the baker (no phone check) and customer (phone-verified) variants.
 */
public class CancelCommandTest {

    /**
     * Creates an order list with one enquiry (E001, Alice) and one order (O001, Bob)
     * in the given statuses.
     *
     * @param enquiryStatus Status to give the enquiry record.
     * @param orderStatus Status to give the confirmed order record.
     * @return An order list containing E001 and O001.
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

    /**
     * Tests that a baker can cancel a queued order without a phone check.
     */
    @Test
    public void execute_bakerCancelsQueuedOrder_marksItCancelled() {
        OrderList orders = createOrderList(OrderStatus.ENQUIRY, OrderStatus.QUEUED);

        String result = new CancelCommand("O001").execute(orders);

        assertTrue(result.contains("Cancelled O001"));
        assertEquals(OrderStatus.CANCELLED, orders.getById("O001").getStatus());
    }

    /**
     * Tests that a customer can cancel their own enquiry with the matching phone number.
     */
    @Test
    public void execute_customerCancelsOwnEnquiry_marksItCancelled() {
        OrderList orders = createOrderList(OrderStatus.ENQUIRY, OrderStatus.QUEUED);

        String result = new CancelCommand("E001", "91234567").execute(orders);

        assertTrue(result.contains("Cancelled E001"));
        assertEquals(OrderStatus.CANCELLED, orders.getById("E001").getStatus());
    }

    /**
     * Tests that a customer cannot cancel a record using someone else's phone number.
     */
    @Test
    public void execute_customerWrongPhone_throwsException() {
        OrderList orders = createOrderList(OrderStatus.ENQUIRY, OrderStatus.QUEUED);

        CakeQueueException exception = assertThrows(CakeQueueException.class,
                () -> new CancelCommand("E001", "99999999").execute(orders));

        assertTrue(exception.getMessage().contains("Phone number does not match"));
        assertEquals(OrderStatus.ENQUIRY, orders.getById("E001").getStatus());
    }

    /**
     * Tests that a customer cannot cancel a confirmed order even with the matching phone.
     */
    @Test
    public void execute_customerCancelsConfirmedOrder_throwsException() {
        OrderList orders = createOrderList(OrderStatus.ENQUIRY, OrderStatus.QUEUED);

        CakeQueueException exception = assertThrows(CakeQueueException.class,
                () -> new CancelCommand("O001", "81111111").execute(orders));

        assertTrue(exception.getMessage().contains("Customers may only cancel enquiries"));
        assertEquals(OrderStatus.QUEUED, orders.getById("O001").getStatus());
    }

    /**
     * Tests that a record in a final state (collected) cannot be cancelled by the baker.
     */
    @Test
    public void execute_collectedOrder_throwsException() {
        OrderList orders = createOrderList(OrderStatus.ENQUIRY, OrderStatus.COLLECTED);

        CakeQueueException exception = assertThrows(CakeQueueException.class,
                () -> new CancelCommand("O001").execute(orders));

        assertTrue(exception.getMessage().contains("Cannot cancel a record with status COLLECTED"));
        assertEquals(OrderStatus.COLLECTED, orders.getById("O001").getStatus());
    }

    /**
     * Tests that an already rejected enquiry cannot be cancelled again.
     */
    @Test
    public void execute_rejectedEnquiry_throwsException() {
        OrderList orders = createOrderList(OrderStatus.REJECTED, OrderStatus.QUEUED);

        assertThrows(CakeQueueException.class,
                () -> new CancelCommand("E001").execute(orders));

        assertEquals(OrderStatus.REJECTED, orders.getById("E001").getStatus());
    }
}
