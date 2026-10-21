package seedu.duke.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import seedu.duke.model.Cake;
import seedu.duke.model.Customer;
import seedu.duke.model.Order;
import seedu.duke.model.OrderList;
import seedu.duke.model.OrderStatus;

/**
 * Tests rejecting customer enquiries through {@link RejectEnquiryCommand}.
 */
public class RejectEnquiryCommandTest {

    /**
     * Creates an order list containing one enquiry (E001) and one confirmed order (O001).
     *
     * @return An order list with an ENQUIRY record and a QUEUED record.
     */
    private OrderList createOrderList() {
        OrderList orders = new OrderList();
        orders.add(new Order("E001",
                new Customer("Alice Tan", "91234567"),
                new Cake(8, "Chocolate", "Strawberry"),
                LocalDate.of(2026, 10, 20),
                OrderStatus.ENQUIRY));
        orders.add(new Order("O001",
                new Customer("Bob Tan", "81111111"),
                new Cake(6, "Vanilla", "Cream"),
                LocalDate.of(2026, 10, 21),
                OrderStatus.QUEUED));
        return orders;
    }

    /**
     * Tests that rejecting a pending enquiry marks it as rejected.
     */
    @Test
    public void execute_pendingEnquiry_marksItRejected() {
        OrderList orders = createOrderList();

        String result = new RejectEnquiryCommand("E001").execute(orders);

        assertTrue(result.contains("Rejected enquiry E001"));
        assertEquals(OrderStatus.REJECTED, orders.getById("E001").getStatus());
        // The confirmed order must be untouched.
        assertEquals(OrderStatus.QUEUED, orders.getById("O001").getStatus());
    }

    /**
     * Tests that a confirmed order cannot be rejected as if it were an enquiry.
     */
    @Test
    public void execute_confirmedOrder_throwsException() {
        OrderList orders = createOrderList();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new RejectEnquiryCommand("O001").execute(orders));

        assertTrue(exception.getMessage().contains("Only enquiries can be rejected"));
        assertEquals(OrderStatus.QUEUED, orders.getById("O001").getStatus());
    }

    /**
     * Tests that rejecting an unknown ID produces an error instead of changing any record.
     */
    @Test
    public void execute_unknownId_throwsException() {
        OrderList orders = createOrderList();

        assertThrows(IllegalArgumentException.class,
                () -> new RejectEnquiryCommand("E999").execute(orders));

        assertEquals(OrderStatus.ENQUIRY, orders.getById("E001").getStatus());
    }
}
