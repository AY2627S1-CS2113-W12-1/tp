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
 * Tests the read-only record access commands {@link ViewOrderCommand},
 * {@link TrackCommand}, {@link MineCommand} and {@link ListEnquiriesCommand},
 * including their not-found and phone-mismatch error paths.
 */
public class RecordAccessCommandTest {

    /**
     * Creates a list with enquiry E001 and order O001 (both Alice, 91234567)
     * plus order O002 (Bob, 81111111).
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

    // ---------- TrackCommand ----------

    @Test
    public void track_matchingPhone_returnsDetails() {
        String details = new TrackCommand("E001", "91234567").execute(createOrderList());

        assertTrue(details.contains("Enquiry ID: E001"));
        assertTrue(details.contains("Status: ENQUIRY"));
    }

    @Test
    public void track_wrongOrNullPhone_throwsAndRevealsNothing() {
        OrderList orders = createOrderList();

        CakeQueueException wrongPhone = assertThrows(CakeQueueException.class,
                () -> new TrackCommand("O001", "81111111").execute(orders));
        assertEquals("Phone number does not match this record.", wrongPhone.getMessage());

        // Another customer's valid phone must not work either.
        assertThrows(CakeQueueException.class,
                () -> new TrackCommand("O002", "91234567").execute(orders));
        assertThrows(CakeQueueException.class,
                () -> new TrackCommand("O001", null).execute(orders));
    }

    @Test
    public void track_unknownId_throwsCakeQueueException() {
        assertThrows(CakeQueueException.class,
                () -> new TrackCommand("E999", "91234567").execute(createOrderList()));
    }

    // ---------- MineCommand ----------

    @Test
    public void mine_matchingPhone_listsOnlyThatCustomersRecords() {
        String result = new MineCommand("91234567").execute(createOrderList());

        assertTrue(result.contains("Records for phone 91234567:"));
        assertTrue(result.contains("E001"));
        assertTrue(result.contains("O001"));
        assertTrue(!result.contains("O002"));
    }

    @Test
    public void mine_noMatches_returnsFriendlyMessageNotError() {
        assertEquals("No enquiries or orders found for phone 0000.",
                new MineCommand("0000").execute(createOrderList()));
        assertEquals("No enquiries or orders found for phone 91234567.",
                new MineCommand("91234567").execute(new OrderList()));
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

    // ---------- EnquireCommand ----------

    @Test
    public void enquire_valid_createsEnquiryWithSequentialIds() {
        OrderList orders = new OrderList();

        String first = new EnquireCommand("Alice", "91234567", LocalDate.of(2026, 10, 20),
                8, "Chocolate", "Strawberry").execute(orders);
        String second = new EnquireCommand("Bob", "81111111", LocalDate.of(2026, 10, 21),
                6, "Vanilla", "Cream").execute(orders);

        assertTrue(first.contains("Submitted enquiry E001"));
        assertTrue(second.contains("Submitted enquiry E002"));
        assertEquals(OrderStatus.ENQUIRY, orders.getById("E001").getStatus());
        assertEquals(2, orders.size());
    }
}
