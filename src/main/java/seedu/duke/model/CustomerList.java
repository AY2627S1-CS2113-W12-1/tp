package seedu.duke.model;

//@@author anannya-mk

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import seedu.duke.exception.CakeQueueException;

/**
 * A snapshot of every customer, worked out from the confirmed orders in an {@link OrderList}.
 *
 * <p>A customer is identified by their phone number. Customer IDs (C001, C002, ...) are given out in
 * the order in which each phone number first appears among the confirmed orders. Because the IDs are
 * worked out from the orders every time, nothing extra has to be saved to disk, and the IDs stay the
 * same after the application is closed and reopened.</p>
 *
 * <p>Only confirmed orders (IDs starting with O) count. Enquiries (IDs starting with E) do not,
 * because an enquiry is not an order until the baker accepts it.</p>
 */
public class CustomerList {
    private final List<CustomerProfile> customers;

    private CustomerList(List<CustomerProfile> customers) {
        this.customers = customers;
    }

    /**
     * Builds the customer list from the confirmed orders in the given order list.
     *
     * @param orders The list of all orders and enquiries.
     * @return The customers, with IDs assigned in order of first appearance.
     */
    public static CustomerList fromOrders(OrderList orders) {
        assert orders != null : "Order list must not be null";
        Map<String, List<Order>> ordersByPhone = new LinkedHashMap<>();
        for (Order order : orders.getAll()) {
            if (isConfirmedOrder(order)) {
                ordersByPhone.computeIfAbsent(order.getCustomer().getPhone(), phone -> new ArrayList<>())
                        .add(order);
            }
        }

        List<CustomerProfile> profiles = new ArrayList<>();
        int nextNumber = 1;
        for (Map.Entry<String, List<Order>> entry : ordersByPhone.entrySet()) {
            String customerId = String.format("C%03d", nextNumber++);
            profiles.add(new CustomerProfile(customerId, entry.getKey(), entry.getValue()));
        }
        return new CustomerList(profiles);
    }

    private static boolean isConfirmedOrder(Order order) {
        String orderId = order.getOrderId();
        return orderId != null && orderId.startsWith("O");
    }

    /**
     * Finds a customer using their Customer ID (not case-sensitive).
     *
     * @param customerId The ID of the customer to find, for example C001.
     * @return The matching customer.
     * @throws CakeQueueException If no customer has that ID.
     */
    public CustomerProfile getById(String customerId) {
        for (CustomerProfile customer : customers) {
            if (customer.getCustomerId().equalsIgnoreCase(customerId)) {
                return customer;
            }
        }
        throw new CakeQueueException("Customer not found: " + customerId);
    }

    /**
     * Finds the customer with the given phone number, if they have any confirmed orders.
     *
     * @param phone The phone number to look for.
     * @return The matching customer, or empty if the phone number has no confirmed orders.
     */
    public Optional<CustomerProfile> findByPhone(String phone) {
        for (CustomerProfile customer : customers) {
            if (customer.getPhone().equals(phone)) {
                return Optional.of(customer);
            }
        }
        return Optional.empty();
    }

    /**
     * Returns the number of customers.
     */
    public int size() {
        return customers.size();
    }
}
