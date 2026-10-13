package seedu.duke.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
    void execute_mixedOrders_keepsOriginalNumbering() {
        OrderList orders = new OrderList();
        Order first = new Order(new Customer("Alex", "91234567"), cake, 1);
        first.setStatus(OrderStatus.COLLECTED);
        orders.add(first);
        orders.add(new Order(new Customer("Bella", "98765432"), cake, 2));

        String result = new ListCommand().execute(orders);
        assertTrue(result.contains("2. Bella"));
        assertFalse(result.contains("Alex"));
    }
}
