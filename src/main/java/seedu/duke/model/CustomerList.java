package seedu.duke.model;

//@@author anannya-mk

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import seedu.duke.exception.CakeQueueException;

/**
 * A snapshot of every customer, worked out from the confirmed orders in an {@link OrderList}.
 *
 * <p>Every confirmed order carries a Customer ID (C001, C002, ...) that is given out once, when the
 * order is added, and saved with the order. The ID never changes afterwards, even if the order's name
 * or phone number is edited, so an ID always refers to the same customer. This list simply groups the
 * orders by that ID. A customer's name and phone number are those on their most recent order.</p>
 *
 * <p>Only confirmed orders (IDs starting with O) count. Enquiries (IDs starting with E) do not,
 * because an enquiry is not an order until the baker accepts it.</p>
 */
public class CustomerList {
    private final List<CustomerProfile> customers;
    private final Map<String, String> customerIdByPhone;

    private CustomerList(List<CustomerProfile> customers, Map<String, String> customerIdByPhone) {
        this.customers = customers;
        this.customerIdByPhone = customerIdByPhone;
    }

    /**
     * Builds the customer list from the confirmed orders in the given order list.
     *
     * @param orders The list of all orders and enquiries.
     * @return The customers, sorted by Customer ID.
     */
    public static CustomerList fromOrders(OrderList orders) {
        assert orders != null : "Order list must not be null";
        Map<String, List<Order>> ordersByCustomerId = new LinkedHashMap<>();
        Map<String, String> customerIdByPhone = new HashMap<>();
        for (Order order : orders.getAll()) {
            if (isConfirmedOrder(order)) {
                ordersByCustomerId.computeIfAbsent(order.getCustomerId(), id -> new ArrayList<>()).add(order);
                customerIdByPhone.put(order.getCustomer().getPhone(), order.getCustomerId());
            }
        }

        List<CustomerProfile> profiles = new ArrayList<>();
        for (Map.Entry<String, List<Order>> entry : ordersByCustomerId.entrySet()) {
            List<Order> customerOrders = entry.getValue();
            String latestPhone = customerOrders.get(customerOrders.size() - 1).getCustomer().getPhone();
            profiles.add(new CustomerProfile(entry.getKey(), latestPhone, customerOrders));
        }
        profiles.sort(Comparator.comparingInt(profile -> numberOf(profile.getCustomerId())));
        return new CustomerList(profiles, customerIdByPhone);
    }

    private static boolean isConfirmedOrder(Order order) {
        String orderId = order.getOrderId();
        return orderId != null && orderId.startsWith("O") && order.getCustomerId() != null;
    }

    private static int numberOf(String customerId) {
        return Integer.parseInt(customerId.substring(1));
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
     * Finds the customer who most recently placed an order under the given phone number.
     *
     * @param phone The phone number to look for.
     * @return The matching customer, or empty if no confirmed order has that phone number.
     */
    public Optional<CustomerProfile> findByPhone(String phone) {
        String customerId = customerIdByPhone.get(phone);
        if (customerId == null) {
            return Optional.empty();
        }
        return Optional.of(getById(customerId));
    }

    /**
     * Returns the number of customers.
     */
    public int size() {
        return customers.size();
    }
}
