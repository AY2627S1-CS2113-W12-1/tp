package seedu.duke.model;

//@@author anannya-mk

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Tests {@link CustomerProfile}.
 */
public class CustomerProfileTest {
    private static Order order(String id, String name) {
        Order order = new Order(id, new Customer(name, "91111111"), new Cake(8, "Chocolate", "Cream"),
                LocalDate.of(2026, 12, 1), OrderStatus.QUEUED);
        order.setCustomerId("C001");
        return order;
    }

    @Test
    public void isRepeatCustomer_oneOrder_false() {
        CustomerProfile profile = new CustomerProfile("C001", "91111111", List.of(order("O001", "Alice")));
        assertFalse(profile.isRepeatCustomer());
    }

    @Test
    public void isRepeatCustomer_twoOrders_true() {
        CustomerProfile profile = new CustomerProfile("C001", "91111111",
                List.of(order("O001", "Alice"), order("O002", "Alice")));
        assertTrue(profile.isRepeatCustomer());
    }

    @Test
    public void getName_usesMostRecentOrder() {
        CustomerProfile profile = new CustomerProfile("C001", "91111111",
                List.of(order("O001", "Alice"), order("O002", "Alice Tan")));
        assertEquals("Alice Tan", profile.getName());
    }

    @Test
    public void getOrders_returnsReadOnlyList() {
        CustomerProfile profile = new CustomerProfile("C001", "91111111", List.of(order("O001", "Alice")));
        assertThrows(UnsupportedOperationException.class, () -> profile.getOrders().add(order("O002", "Bob")));
    }

    @Test
    public void constructor_copiesTheOrderList() {
        List<Order> source = new ArrayList<>();
        source.add(order("O001", "Alice"));
        CustomerProfile profile = new CustomerProfile("C001", "91111111", source);
        source.add(order("O002", "Alice"));

        assertEquals(1, profile.getOrders().size());
        assertFalse(profile.isRepeatCustomer());
    }
}
