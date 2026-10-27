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

    //@@author Eethanzyj
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
    //@@author

    //@@author WillChin484848
    /**
     * Verifies that an order due before today is marked as overdue.
     */
    @Test
    void execute_overdueOrder_displaysOverdueWarning() {
        OrderList orders = new OrderList();

        // Set the due date to yesterday to guarantee an overdue order.
        LocalDate yesterday = LocalDate.now().minusDays(1);
        Order order = new Order("O001", new Customer("Alice"),
                new Cake(6, "Chocolate", "Cream"), yesterday);
        orders.add(order);

        String result = new ListCommand().execute(orders);

        assertTrue(result.contains("[OVERDUE]"));
        assertFalse(result.contains("[DUE SOON]"));
    }

    /**
     * Verifies that an order due exactly three days from today
     * is marked as due soon.
     */
    @Test
    void execute_dueInThreeDays_displaysDueSoonWarning() {
        OrderList orders = new OrderList();

        // Three days is the inclusive boundary for due-soon warnings.
        LocalDate dueDate = LocalDate.now().plusDays(3);
        Order order = new Order("O001", new Customer("Alice"),
                new Cake(8, "Vanilla", "Cream"), dueDate);
        orders.add(order);

        String result = new ListCommand().execute(orders);

        assertTrue(result.contains("[DUE SOON]"));
        assertFalse(result.contains("[OVERDUE]"));
    }

    /**
     * Verifies that an order due today is marked as due soon.
     */
    @Test
    void execute_dueToday_displaysDueSoonWarning() {
        OrderList orders = new OrderList();

        // An order due today must be included in the due-soon range.
        Order order = new Order("O001", new Customer("Alice"),
                new Cake(10, "Matcha", "Cream"), LocalDate.now());
        orders.add(order);

        String result = new ListCommand().execute(orders);

        assertTrue(result.contains("[DUE SOON]"));
        assertFalse(result.contains("[OVERDUE]"));
    }

    /**
     * Verifies that an order due more than three days away
     * does not display a warning.
     */
    @Test
    void execute_dueInFourDays_displaysNoWarning() {
        OrderList orders = new OrderList();

        // Four days is outside the due-soon warning range.
        LocalDate dueDate = LocalDate.now().plusDays(4);
        Order order = new Order("O001", new Customer("Alice"),
                new Cake(6, "Strawberry", "Cream"), dueDate);
        orders.add(order);

        String result = new ListCommand().execute(orders);

        assertFalse(result.contains("[DUE SOON]"));
        assertFalse(result.contains("[OVERDUE]"));
    }
    //@@author
}
