package seedu.duke;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import seedu.duke.model.Cake;
import seedu.duke.model.Customer;
import seedu.duke.model.Order;
import seedu.duke.model.OrderList;

class DukeTest {
    @Test
    public void deleteOrder_removesTheChosenOrder() {
        Customer alex = new Customer("Alex", "91234567");
        OrderList orders = new OrderList();
        orders.add(new Order("O001", alex, new Cake(6, "Vanilla", "Cream"),
                LocalDate.of(2026, 10, 20)));
        orders.add(new Order("O002", alex, new Cake(8, "Chocolate", "Cream"),
                LocalDate.of(2026, 10, 21)));

        Order removed = orders.deleteOrder(1);

        assertEquals("O001", removed.getOrderId());
        assertEquals("Vanilla", removed.getCake().getFlavour());
        assertEquals(1, orders.size());
    }

    @Test
    public void getOrder_validIndex_returnsTheChosenOrder() {
        Customer alex = new Customer("Alex", "91234567");
        OrderList orders = new OrderList();
        orders.add(new Order("O001", alex, new Cake(6, "Vanilla", "Cream"),
                LocalDate.of(2026, 10, 20)));
        orders.add(new Order("O002", alex, new Cake(8, "Chocolate", "Cream"),
                LocalDate.of(2026, 10, 21)));

        Order found = orders.get(2);

        assertEquals("O002", found.getOrderId());
        assertEquals("Chocolate", found.getCake().getFlavour());
        assertEquals(8, found.getCake().getSize());
    }

    @Test
    public void getOrder_invalidIndex_throwsException() {
        OrderList orders = new OrderList();

        assertThrows(IllegalArgumentException.class, () -> orders.get(1));
        assertThrows(IllegalArgumentException.class, () -> orders.get(0));
    }

    @Test
    public void toDetailedString_containsAllOrderDetails() {
        OrderList orders = new OrderList();
        Order order = new Order(orders.generateOrderId(), new Customer("Alex", "91234567"),
                new Cake(8, "Chocolate", "Strawberry"), LocalDate.of(2026, 10, 20));
        orders.add(order);

        String details = order.toDetailedString();

        assertTrue(details.contains("O001"));
        assertTrue(details.contains("Alex"));
        assertTrue(details.contains("20-10-2026"));
        assertTrue(details.contains("Chocolate"));
        assertTrue(details.contains("Strawberry"));
        assertTrue(details.contains("QUEUED"));
    }

    @Test
    public void getById_validId_returnsTheChosenOrder() {
        OrderList orders = new OrderList();
        Customer alex = new Customer("Alex", "91234567");
        orders.add(new Order(orders.generateOrderId(), alex,
                new Cake(6, "Vanilla", "Cream"), LocalDate.of(2026, 10, 20)));
        orders.add(new Order(orders.generateOrderId(), alex,
                new Cake(8, "Chocolate", "Strawberry"), LocalDate.of(2026, 10, 21)));

        Order found = orders.getById("O002");

        assertEquals("Chocolate", found.getCake().getFlavour());
    }

    @Test
    public void getById_unknownId_throwsException() {
        OrderList orders = new OrderList();

        assertThrows(IllegalArgumentException.class, () -> orders.getById("O999"));
    }
}
