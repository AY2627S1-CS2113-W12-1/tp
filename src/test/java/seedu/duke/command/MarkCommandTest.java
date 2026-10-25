package seedu.duke.command;

//@@author HammerHead-os

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
import seedu.duke.parser.Parser;

/**
 * Tests the baker mark command and its parser.
 */
public class MarkCommandTest {
    private OrderList ordersWithQueuedOrder() {
        OrderList orders = new OrderList();
        orders.add(new Order(orders.generateOrderId(), new Customer("Alice Tan", "91234567"),
                new Cake(8, "Chocolate", "Strawberry"), LocalDate.of(2026, 10, 20)));
        return orders;
    }

    @Test
    public void execute_forwardSteps_moveThroughWorkflow() {
        OrderList orders = ordersWithQueuedOrder();

        assertTrue(new MarkCommand("O001", OrderStatus.PREPARING).execute(orders).contains("[PREPARING]"));
        assertTrue(new MarkCommand("O001", OrderStatus.READY).execute(orders).contains("[READY]"));
        assertTrue(new MarkCommand("O001", OrderStatus.COLLECTED).execute(orders).contains("[COLLECTED]"));
        assertEquals(OrderStatus.COLLECTED, orders.getById("O001").getStatus());
    }

    @Test
    public void execute_skippingOrRepeatingStep_throwsAndKeepsStatus() {
        OrderList orders = ordersWithQueuedOrder();

        assertThrows(IllegalArgumentException.class,
                () -> new MarkCommand("O001", OrderStatus.READY).execute(orders));
        assertThrows(IllegalArgumentException.class,
                () -> new MarkCommand("O001", OrderStatus.COLLECTED).execute(orders));
        new MarkCommand("O001", OrderStatus.PREPARING).execute(orders);
        assertThrows(IllegalArgumentException.class,
                () -> new MarkCommand("O001", OrderStatus.PREPARING).execute(orders));
        assertEquals(OrderStatus.PREPARING, orders.getById("O001").getStatus());
    }

    @Test
    public void execute_enquiryOrUnknownOrder_throws() {
        OrderList orders = new OrderList();
        orders.add(new Order(orders.generateEnquiryId(), new Customer("Bob Lim", "81234567"),
                new Cake(6, "Vanilla", "Cream"), LocalDate.of(2026, 10, 21), OrderStatus.ENQUIRY));

        assertThrows(IllegalArgumentException.class,
                () -> new MarkCommand("E001", OrderStatus.PREPARING).execute(orders));
        assertThrows(IllegalArgumentException.class,
                () -> new MarkCommand("O999", OrderStatus.PREPARING).execute(ordersWithQueuedOrder()));
    }

    @Test
    public void parseMark_validInput_returnsIdAndStatus() {
        Parser.MarkDetails details = Parser.parseMark("mark o001 Ready");

        assertEquals("O001", details.getOrderId());
        assertEquals(OrderStatus.READY, details.getTargetStatus());
    }

    @Test
    public void parseMark_invalidStatusOrShape_throws() {
        assertThrows(IllegalArgumentException.class, () -> Parser.parseMark("mark O001 queued"));
        assertThrows(IllegalArgumentException.class, () -> Parser.parseMark("mark E001 preparing"));
        assertThrows(IllegalArgumentException.class, () -> Parser.parseMark("mark O001"));
        assertThrows(IllegalArgumentException.class, () -> Parser.parseMark(null));
    }
}
