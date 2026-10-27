package seedu.duke.model;

//@@author anannya-mk

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Describes one customer: their Customer ID, contact details and every confirmed order they have placed.
 * Profiles are built from the order list by {@link CustomerList}, so they are never saved separately.
 */
public class CustomerProfile {
    private final String customerId;
    private final String phone;
    private final List<Order> orders;

    /**
     * Creates a profile.
     *
     * @param customerId The Customer ID, for example C001.
     * @param phone The phone number that identifies the customer.
     * @param orders The customer's confirmed orders, oldest first. There must be at least one.
     */
    public CustomerProfile(String customerId, String phone, List<Order> orders) {
        assert customerId != null && phone != null : "Customer ID and phone must not be null";
        assert orders != null && !orders.isEmpty() : "A customer profile needs at least one order";
        this.customerId = customerId;
        this.phone = phone;
        this.orders = new ArrayList<>(orders);
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getPhone() {
        return phone;
    }

    /**
     * Returns the name used on the customer's most recent order.
     */
    public String getName() {
        return orders.get(orders.size() - 1).getCustomer().getName();
    }

    /**
     * Returns the customer's orders, oldest first. Cancelled and collected orders are included.
     */
    public List<Order> getOrders() {
        return Collections.unmodifiableList(orders);
    }

    /**
     * Returns true if the customer has more than one order on record.
     */
    public boolean isRepeatCustomer() {
        return orders.size() > 1;
    }
}
