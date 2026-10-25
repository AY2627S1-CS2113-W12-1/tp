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
 * Tests accepting enquiries, including every error path of
 * {@link AcceptEnquiryCommand}: non-enquiries, unknown IDs, and accepting
 * the same enquiry twice. Each error must leave the stored records unchanged.
 */
public class AcceptEnquiryCommandTest {

    /**
     * Creates a list with enquiry E001 (Alice) and confirmed order O001 (Bob).
     */
    private OrderList createOrderList() {
        OrderList orders = new OrderList();
        orders.add(new Order("E001", new Customer("Alice Tan", "91234567"),
                new Cake(8, "Chocolate", "Strawberry"), LocalDate.of(2026, 10, 20),
                OrderStatus.ENQUIRY));
        orders.add(new Order("O001", new Customer("Bob Tan", "81111111"),
                new Cake(6, "Vanilla", "Cream"), LocalDate.of(2026, 10, 21),
                OrderStatus.QUEUED));
        return orders;
    }

    @Test
    public void execute_pendingEnquiry_convertsToQueuedOrderPreservingDetails() {
        OrderList orders = createOrderList();

        String result = new AcceptEnquiryCommand("E001").execute(orders);

        assertTrue(result.contains("Accepted enquiry E001 as order O002"));
        Order confirmed = orders.getById("O002");
        assertEquals(OrderStatus.QUEUED, confirmed.getStatus());
        assertEquals("Alice Tan", confirmed.getCustomer().getName());
        assertEquals("91234567", confirmed.getCustomer().getPhone());
        assertEquals(8, confirmed.getCake().getSize());
        assertEquals(LocalDate.of(2026, 10, 20), confirmed.getDueDate());
        // The enquiry ID itself is gone, and the other order is untouched.
        assertThrows(CakeQueueException.class, () -> orders.getById("E001"));
        assertEquals(2, orders.size());
    }

    @Test
    public void execute_confirmedOrder_throwsAndKeepsOrderUnchanged() {
        OrderList orders = createOrderList();

        CakeQueueException exception = assertThrows(CakeQueueException.class,
                () -> new AcceptEnquiryCommand("O001").execute(orders));

        assertEquals("Only enquiries can be accepted: O001", exception.getMessage());
        assertEquals(OrderStatus.QUEUED, orders.getById("O001").getStatus());
        assertEquals(2, orders.size());
    }

    @Test
    public void execute_unknownOrNullId_throwsAndKeepsListUnchanged() {
        OrderList orders = createOrderList();

        assertThrows(CakeQueueException.class,
                () -> new AcceptEnquiryCommand("E999").execute(orders));
        CakeQueueException nullId = assertThrows(CakeQueueException.class,
                () -> new AcceptEnquiryCommand(null).execute(orders));
        assertTrue(nullId.getMessage().contains("not found"));

        assertEquals(OrderStatus.ENQUIRY, orders.getById("E001").getStatus());
        assertEquals(2, orders.size());
    }

    @Test
    public void execute_acceptSameEnquiryTwice_secondAttemptThrows() {
        OrderList orders = createOrderList();
        new AcceptEnquiryCommand("E001").execute(orders);

        assertThrows(CakeQueueException.class,
                () -> new AcceptEnquiryCommand("E001").execute(orders));
        assertEquals(2, orders.size());
    }

    @Test
    public void execute_rejectedOrCancelledEnquiry_throws() {
        OrderList orders = createOrderList();
        orders.getById("E001").setStatus(OrderStatus.REJECTED);
        assertThrows(CakeQueueException.class,
                () -> new AcceptEnquiryCommand("E001").execute(orders));

        orders.getById("E001").setStatus(OrderStatus.CANCELLED);
        assertThrows(CakeQueueException.class,
                () -> new AcceptEnquiryCommand("E001").execute(orders));
        assertEquals(OrderStatus.CANCELLED, orders.getById("E001").getStatus());
    }
}
