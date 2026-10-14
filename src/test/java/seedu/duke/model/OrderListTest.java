package seedu.duke.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/**
 * Tests the basic order storage operations.
 */
public class OrderListTest {
    @Test
    public void generateOrderId_multipleCalls_returnsSequentialIds() {
        OrderList orders = new OrderList();

        assertEquals("O001", orders.generateOrderId());
        assertEquals("O002", orders.generateOrderId());
    }

    @Test
    public void add_basicOrder_storesOrder() {
        OrderList orders = new OrderList();
        Order order = new Order("O001", new Customer("Alice Tan"),
                LocalDate.of(2026, 10, 20));

        orders.add(order);

        assertEquals(1, orders.size());
        assertEquals(order, orders.get(1));
    }

    @Test
    public void add_completeOrder_preservesAllFields() {
        OrderList orders = new OrderList();
        Order order = new Order(orders.generateOrderId(), new Customer("Alice Tan", "91234567"),
                new Cake(8, "Chocolate", "Strawberry"), LocalDate.of(2026, 10, 20));

        orders.add(order);

        Order stored = orders.get(1);
        assertEquals("O001", stored.getOrderId());
        assertEquals("Alice Tan", stored.getCustomer().getName());
        assertEquals("91234567", stored.getCustomer().getPhone());
        assertEquals(LocalDate.of(2026, 10, 20), stored.getDueDate());
        assertEquals(8, stored.getCake().getSize());
        assertEquals("Chocolate", stored.getCake().getFlavour());
        assertEquals("Strawberry", stored.getCake().getFilling());
        assertEquals(OrderStatus.QUEUED, stored.getStatus());
    }
}
