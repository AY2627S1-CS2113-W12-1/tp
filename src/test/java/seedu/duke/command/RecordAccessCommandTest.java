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
 * Tests the read-only record access commands {@link ViewOrderCommand} and
 * {@link ListEnquiriesCommand}, including their not-found error paths.
 */
public class RecordAccessCommandTest {

    /**
     * Creates a list with enquiry E001 and two confirmed orders O001 and O002.
     */
    private OrderList createOrderList() {
        OrderList orders = new OrderList();
        orders.add(new Order("E001", new Customer("Alice Tan", "91234567"),
                new Cake(8, "Chocolate", "Strawberry"), LocalDate.of(2026, 10, 20),
                OrderStatus.ENQUIRY));
        orders.add(new Order("O001", new Customer("Alice Tan", "91234567"),
                new Cake(8, "Chocolate", "Strawberry"), LocalDate.of(2026, 10, 20),
                OrderStatus.QUEUED));
        orders.add(new Order("O002", new Customer("Bob Tan", "81111111"),
                new Cake(6, "Vanilla", "Cream"), LocalDate.of(2026, 10, 21),
                OrderStatus.QUEUED));
        return orders;
    }

    // ---------- ViewOrderCommand ----------

    @Test
    public void view_existingOrderAndEnquiry_returnsDetailedString() {
        OrderList orders = createOrderList();

        assertTrue(new ViewOrderCommand("O001").execute(orders).contains("Order ID: O001"));
        assertTrue(new ViewOrderCommand("E001").execute(orders).contains("Enquiry ID: E001"));
        // ID lookup is case-insensitive
        assertTrue(new ViewOrderCommand("o001").execute(orders).contains("Alice Tan"));
    }

    @Test
    public void view_unknownOrNullId_throwsCakeQueueException() {
        OrderList orders = createOrderList();

        CakeQueueException exception = assertThrows(CakeQueueException.class,
                () -> new ViewOrderCommand("O999").execute(orders));
        assertEquals("Order not found: O999", exception.getMessage());
        assertThrows(CakeQueueException.class,
                () -> new ViewOrderCommand(null).execute(orders));
    }

    // ---------- ListEnquiriesCommand ----------

    @Test
    public void listEnquiries_onlyPendingEnquiriesListed() {
        String result = new ListEnquiriesCommand().execute(createOrderList());

        assertTrue(result.contains("Here are your pending enquiries:"));
        assertTrue(result.contains("E001"));
        assertTrue(!result.contains("O001"));
    }

    @Test
    public void listEnquiries_nonePending_returnsFriendlyMessage() {
        OrderList orders = createOrderList();
        orders.getById("E001").setStatus(OrderStatus.REJECTED);

        assertEquals("You have no pending enquiries.", new ListEnquiriesCommand().execute(orders));
        assertEquals("You have no pending enquiries.", new ListEnquiriesCommand().execute(new OrderList()));
    }
}
