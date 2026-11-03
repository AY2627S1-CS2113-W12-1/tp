package seedu.duke.model;

//@@author anannya-mk

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/**
 * Tests how {@link OrderList} gives out and remembers Customer IDs.
 */
public class CustomerIdAssignmentTest {
    private static Order order(String id, String name, String phone, OrderStatus status) {
        return new Order(id, new Customer(name, phone), new Cake(8, "Chocolate", "Cream"),
                LocalDate.of(2026, 12, 1), status);
    }

    private static Order order(String id, String name, String phone) {
        return order(id, name, phone, OrderStatus.QUEUED);
    }

    @Test
    public void add_samePhoneAsEarlierOrder_reusesCustomerId() {
        OrderList orders = new OrderList();
        orders.add(order("O001", "Alice", "91111111"));
        orders.add(order("O002", "Alice", "91111111"));

        assertEquals("C001", orders.getById("O002").getCustomerId());
    }

    @Test
    public void add_samePhoneAsCancelledOrder_stillReusesCustomerId() {
        OrderList orders = new OrderList();
        orders.add(order("O001", "Alice", "91111111", OrderStatus.CANCELLED));
        orders.add(order("O002", "Alice", "91111111"));

        assertEquals("C001", orders.getById("O002").getCustomerId());
    }

    @Test
    public void add_enquiry_getsNoCustomerId() {
        OrderList orders = new OrderList();
        orders.add(order("E001", "Carl", "93333333", OrderStatus.ENQUIRY));

        assertNull(orders.getById("E001").getCustomerId());
    }

    @Test
    public void add_orderThatAlreadyHasCustomerId_keepsItAndMovesCounterPast() {
        OrderList orders = new OrderList();
        Order loaded = order("O001", "Alice", "91111111");
        loaded.setCustomerId("C005");
        orders.add(loaded);
        orders.add(order("O002", "Bob", "92222222"));

        assertEquals("C005", orders.getById("O001").getCustomerId());
        assertEquals("C006", orders.getById("O002").getCustomerId());
    }

    @Test
    public void reserveCustomerId_validId_nextNewCustomerGetsLargerId() {
        OrderList orders = new OrderList();
        orders.reserveCustomerId("C010");
        orders.add(order("O001", "Alice", "91111111"));

        assertEquals("C011", orders.getById("O001").getCustomerId());
    }

    @Test
    public void reserveCustomerId_smallerId_doesNotMoveCounterBack() {
        OrderList orders = new OrderList();
        orders.reserveCustomerId("C010");
        orders.reserveCustomerId("C002");
        orders.add(order("O001", "Alice", "91111111"));

        assertEquals("C011", orders.getById("O001").getCustomerId());
    }

    @Test
    public void reserveCustomerId_invalidInput_isIgnored() {
        OrderList orders = new OrderList();
        orders.reserveCustomerId(null);
        orders.reserveCustomerId("");
        orders.reserveCustomerId("C");
        orders.reserveCustomerId("X5");
        orders.reserveCustomerId("Cabc");
        orders.add(order("O001", "Alice", "91111111"));

        assertEquals("C001", orders.getById("O001").getCustomerId());
    }

    @Test
    public void add_afterOnlyOrderRemoved_customerIdIsNotReused() {
        OrderList orders = new OrderList();
        orders.add(order("O001", "Alice", "91111111"));
        orders.removeById("O001");
        orders.add(order("O002", "Bob", "92222222"));

        assertEquals("C002", orders.getById("O002").getCustomerId());
    }
}
