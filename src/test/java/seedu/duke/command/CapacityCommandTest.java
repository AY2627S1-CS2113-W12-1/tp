package seedu.duke.command;

//@@author Kaishhhh

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
 * Tests {@link CapacityCommand} and the capacity counting in {@link OrderList}.
 */
public class CapacityCommandTest {
    private static final LocalDate DAY = LocalDate.of(2026, 10, 21);

    private void addOrder(OrderList orders, String id, LocalDate dueDate, OrderStatus status) {
        orders.add(new Order(id, new Customer("Alice Tan", "91234567"),
                new Cake(8, "Chocolate", "Strawberry"), dueDate, status));
    }

    @Test
    public void execute_noArgument_showsDefaultCapacity() {
        String result = new CapacityCommand(null).execute(new OrderList());

        assertEquals("Daily capacity is 5 orders per day.", result);
    }

    @Test
    public void execute_newCapacity_updatesOrderList() {
        OrderList orders = new OrderList();

        String result = new CapacityCommand(3).execute(orders);

        assertEquals("Daily capacity set to 3 orders per day.", result);
        assertEquals(3, orders.getDailyCapacity());
    }

    @Test
    public void execute_capacityOfOne_usesSingularWording() {
        assertEquals("Daily capacity set to 1 order per day.",
                new CapacityCommand(1).execute(new OrderList()));
    }

    @Test
    public void execute_outOfRangeCapacity_throwsAndKeepsOldValue() {
        OrderList orders = new OrderList();

        assertThrows(CakeQueueException.class, () -> new CapacityCommand(0).execute(orders));
        assertThrows(CakeQueueException.class,
                () -> new CapacityCommand(OrderList.MAX_DAILY_CAPACITY + 1).execute(orders));
        assertEquals(OrderList.DEFAULT_DAILY_CAPACITY, orders.getDailyCapacity());
    }

    @Test
    public void execute_overloadedDate_listsDateWithCount() {
        OrderList orders = new OrderList();
        addOrder(orders, "O001", DAY, OrderStatus.QUEUED);
        addOrder(orders, "O002", DAY, OrderStatus.PREPARING);
        addOrder(orders, "O003", DAY, OrderStatus.READY);
        addOrder(orders, "O004", DAY.plusDays(1), OrderStatus.QUEUED);

        String result = new CapacityCommand(2).execute(orders);

        assertTrue(result.contains("These dates are over capacity:"));
        assertTrue(result.contains("21-10-2026: 3 orders"));
        assertFalse(result.contains("22-10-2026"));
    }

    @Test
    public void execute_noOverloadedDate_doesNotListDates() {
        OrderList orders = new OrderList();
        addOrder(orders, "O001", DAY, OrderStatus.QUEUED);

        assertFalse(new CapacityCommand(null).execute(orders).contains("over capacity"));
    }

    @Test
    public void countOrdersDueOn_ignoresEnquiriesAndClosedRecords() {
        OrderList orders = new OrderList();
        addOrder(orders, "O001", DAY, OrderStatus.QUEUED);
        addOrder(orders, "E001", DAY, OrderStatus.ENQUIRY);
        addOrder(orders, "O002", DAY, OrderStatus.COLLECTED);
        addOrder(orders, "O003", DAY, OrderStatus.CANCELLED);
        addOrder(orders, "E002", DAY, OrderStatus.REJECTED);

        assertEquals(1, orders.countOrdersDueOn(DAY));
        assertEquals(0, orders.countOrdersDueOn(DAY.plusDays(1)));
    }

    @Test
    public void countOrdersByDueDate_groupsActiveOrdersEarliestFirst() {
        OrderList orders = new OrderList();
        addOrder(orders, "O001", DAY.plusDays(1), OrderStatus.QUEUED);
        addOrder(orders, "O002", DAY, OrderStatus.READY);
        addOrder(orders, "O003", DAY, OrderStatus.QUEUED);
        addOrder(orders, "O004", DAY, OrderStatus.CANCELLED);

        assertEquals("{2026-10-21=2, 2026-10-22=1}", orders.countOrdersByDueDate().toString());
    }

    @Test
    public void warningFor_atLimit_returnsNoWarning() {
        OrderList orders = new OrderList();
        orders.setDailyCapacity(2);
        addOrder(orders, "O001", DAY, OrderStatus.QUEUED);
        addOrder(orders, "O002", DAY, OrderStatus.QUEUED);

        assertEquals("", CapacityCommand.warningFor(orders, DAY));
    }

    @Test
    public void warningFor_overLimit_returnsWarning() {
        OrderList orders = new OrderList();
        orders.setDailyCapacity(1);
        addOrder(orders, "O001", DAY, OrderStatus.QUEUED);
        addOrder(orders, "O002", DAY, OrderStatus.QUEUED);

        assertEquals("Warning: 21-10-2026 now has 2 orders, over your daily capacity of 1.",
                CapacityCommand.warningFor(orders, DAY));
    }

    @Test
    public void warningFor_nullDate_returnsNoWarning() {
        assertEquals("", CapacityCommand.warningFor(new OrderList(), null));
    }
}
