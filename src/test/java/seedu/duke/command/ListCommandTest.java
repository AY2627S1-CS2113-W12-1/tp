package seedu.duke.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import seedu.duke.model.Cake;
import seedu.duke.model.Customer;
import seedu.duke.model.Order;
import seedu.duke.model.OrderList;
import seedu.duke.model.OrderStatus;

class ListCommandTest {
    private static final String NO_ORDERS = "You have no upcoming orders.";
    private final Cake cake = new Cake("Chocolate", "chocolate", 25.00);

    @Test
    void execute_emptyList_showsNoOrdersMessage() {
        assertEquals(NO_ORDERS, new ListCommand().execute(new OrderList()));
    }

    @Test
    void execute_collectedOrderOnly_showsNoOrdersMessage() {
        OrderList orders = new OrderList();
        Order collected = new Order(new Customer("Alex", "91234567"), cake, 1);
        collected.setStatus(OrderStatus.COLLECTED);
        orders.add(collected);

        assertEquals(NO_ORDERS, new ListCommand().execute(orders));
    }

    @Test
    void execute_mixedOrders_numbersDisplayedOrders() {
        OrderList orders = new OrderList();
        Order first = new Order(new Customer("Alex", "91234567"), cake, 1);
        first.setStatus(OrderStatus.COLLECTED);
        orders.add(first);
        orders.add(new Order(new Customer("Bella", "98765432"), cake, 2));

        String result = new ListCommand().execute(orders);
        assertTrue(result.contains("1. Bella"));
        assertFalse(result.contains("Alex"));
    }

    @Test
    void execute_unsortedDates_sortsWithoutChangingStoredOrder() {
        OrderList orders = new OrderList();
        Order later = new Order("O001", new Customer("Alice", "91234567"),
                new Cake(8, "Chocolate", "Cream"), LocalDate.of(2026, 10, 22));
        Order earlier = new Order("O002", new Customer("Bob", "81234567"),
                new Cake(6, "Vanilla", "Cream"), LocalDate.of(2026, 10, 20));
        orders.add(later);
        orders.add(earlier);

        String result = new ListCommand().execute(orders);

        assertTrue(result.contains("1. O002"));
        assertTrue(result.contains("2. O001"));
        assertEquals(later, orders.get(1));
        assertEquals(earlier, orders.get(2));
        assertEquals(2, orders.size());
    }

    @Test
    void execute_sameDates_keepsInsertionOrder() {
        OrderList orders = new OrderList();
        LocalDate dueDate = LocalDate.of(2026, 10, 20);
        orders.add(new Order("O001", new Customer("Alice"), cake, dueDate));
        orders.add(new Order("O002", new Customer("Bob"), cake, dueDate));

        String result = new ListCommand().execute(orders);

        assertTrue(result.contains("1. O001"));
        assertTrue(result.contains("2. O002"));
    }

    @Test
    void execute_legacyOrderWithoutDate_listsItLast() {
        OrderList orders = new OrderList();
        orders.add(new Order(new Customer("Alex", "91234567"), cake, 1));
        orders.add(new Order("O001", new Customer("Bella"), cake, LocalDate.of(2026, 10, 20)));

        String result = new ListCommand().execute(orders);

        assertTrue(result.contains("1. O001"));
        assertTrue(result.contains("2. Alex"));
    }
}
