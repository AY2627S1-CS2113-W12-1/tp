package seedu.duke.model;

//@@author anannya-mk

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import seedu.duke.exception.CakeQueueException;

/**
 * Tests that customers are worked out correctly from the order list.
 */
public class CustomerListTest {
    private static Order order(String id, String name, String phone, OrderStatus status) {
        return new Order(id, new Customer(name, phone), new Cake(8, "Chocolate", "Cream"),
                LocalDate.of(2026, 12, 1), status);
    }

    @Test
    public void fromOrders_newPhones_assignedIdsInOrderOfFirstAppearance() {
        OrderList orders = new OrderList();
        orders.add(order("O001", "Alice", "91111111", OrderStatus.QUEUED));
        orders.add(order("O002", "Bob", "92222222", OrderStatus.QUEUED));
        CustomerList customers = CustomerList.fromOrders(orders);

        assertEquals(2, customers.size());
        assertEquals("C001", customers.findByPhone("91111111").orElseThrow().getCustomerId());
        assertEquals("C002", customers.findByPhone("92222222").orElseThrow().getCustomerId());
    }

    @Test
    public void fromOrders_samePhoneTwice_isRepeatCustomerWithLatestName() {
        OrderList orders = new OrderList();
        orders.add(order("O001", "Alice", "91111111", OrderStatus.QUEUED));
        orders.add(order("O002", "Alice Tan", "91111111", OrderStatus.CANCELLED));
        CustomerProfile customer = CustomerList.fromOrders(orders).getById("c001");

        assertEquals(1, CustomerList.fromOrders(orders).size());
        assertEquals(2, customer.getOrders().size());
        assertTrue(customer.isRepeatCustomer());
        assertEquals("Alice Tan", customer.getName());
    }

    @Test
    public void fromOrders_enquiriesIgnored() {
        OrderList orders = new OrderList();
        orders.add(order("E001", "Carl", "93333333", OrderStatus.ENQUIRY));
        orders.add(order("O001", "Alice", "91111111", OrderStatus.QUEUED));
        CustomerList customers = CustomerList.fromOrders(orders);

        assertEquals(1, customers.size());
        assertTrue(customers.findByPhone("93333333").isEmpty());
        assertFalse(customers.getById("C001").isRepeatCustomer());
    }

    @Test
    public void getById_unknownId_throws() {
        CakeQueueException exception = assertThrows(CakeQueueException.class,
                () -> CustomerList.fromOrders(new OrderList()).getById("C999"));
        assertEquals("Customer not found: C999", exception.getMessage());
    }
}
