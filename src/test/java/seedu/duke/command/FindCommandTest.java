package seedu.duke.command;

//@@author Kaishhhh

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

/**
 * Tests {@link FindCommand}.
 */
public class FindCommandTest {

    /**
     * Creates O001 (Alice Tan, Chocolate/Strawberry), O002 (Bob Lim, Vanilla/Cream)
     * and E001 (Alicia Wong, Chocolate/Cream, an enquiry).
     */
    private OrderList createOrderList() {
        OrderList orders = new OrderList();
        orders.add(new Order("O001", new Customer("Alice Tan", "91234567"),
                new Cake(8, "Chocolate", "Strawberry"), LocalDate.of(2026, 10, 20),
                OrderStatus.QUEUED));
        orders.add(new Order("O002", new Customer("Bob Lim", "81111111"),
                new Cake(6, "Vanilla", "Cream"), LocalDate.of(2026, 10, 21),
                OrderStatus.QUEUED));
        orders.add(new Order("E001", new Customer("Alicia Wong", "82222222"),
                new Cake(10, "Chocolate", "Cream"), LocalDate.of(2026, 10, 22),
                OrderStatus.ENQUIRY));
        return orders;
    }

    @Test
    public void execute_nameKeyword_returnsMatchingRecordsOnly() {
        String result = new FindCommand("Alic").execute(createOrderList());

        assertTrue(result.startsWith("Orders matching \"Alic\":"));
        assertTrue(result.contains("1. O001"));
        assertTrue(result.contains("2. E001"));
        assertFalse(result.contains("O002"));
    }

    @Test
    public void execute_differentCase_stillMatches() {
        String result = new FindCommand("bOB").execute(createOrderList());

        assertTrue(result.contains("O002"));
        assertFalse(result.contains("O001"));
    }

    @Test
    public void execute_flavourOrFillingKeyword_matchesCakeDetails() {
        OrderList orders = createOrderList();

        String flavour = new FindCommand("vanilla").execute(orders);
        String filling = new FindCommand("strawberry").execute(orders);

        assertTrue(flavour.contains("O002"));
        assertFalse(flavour.contains("O001"));
        assertTrue(filling.contains("O001"));
        assertFalse(filling.contains("O002"));
    }

    @Test
    public void execute_phraseWithSpace_matchesAsOnePhrase() {
        OrderList orders = createOrderList();

        assertTrue(new FindCommand("Alice Tan").execute(orders).contains("O001"));
        assertEquals("No orders found matching \"Tan Alice\".",
                new FindCommand("Tan Alice").execute(orders));
    }

    @Test
    public void execute_surroundingSpacesInKeyword_areIgnored() {
        assertTrue(new FindCommand("  bob  ").execute(createOrderList()).contains("O002"));
    }

    @Test
    public void execute_noMatch_showsNoOrdersFoundMessage() {
        String result = new FindCommand("Zed").execute(createOrderList());

        assertEquals("No orders found matching \"Zed\".", result);
    }

    @Test
    public void execute_emptyList_showsNoOrdersFoundMessage() {
        String result = new FindCommand("Alice").execute(new OrderList());

        assertEquals("No orders found matching \"Alice\".", result);
    }

    @Test
    public void execute_orderWithoutCake_matchesNameWithoutError() {
        OrderList orders = new OrderList();
        orders.add(new Order("O001", new Customer("Alice Tan"), LocalDate.of(2026, 10, 20)));

        assertTrue(new FindCommand("alice").execute(orders).contains("O001"));
        assertEquals("No orders found matching \"chocolate\".",
                new FindCommand("chocolate").execute(orders));
    }

    @Test
    public void execute_afterSearch_doesNotChangeOrderList() {
        OrderList orders = createOrderList();

        new FindCommand("Alice").execute(orders);

        assertEquals(3, orders.size());
        assertEquals("O001", orders.get(1).getOrderId());
    }
}
