package seedu.duke.model;

import java.util.ArrayList;

/**
 * Represents the list of orders.
 */
public class OrderList {
    private final ArrayList<Order> orders;
    private int nextOrderNumber;

    public OrderList() {
        orders = new ArrayList<>();
        nextOrderNumber = 1;
    }

    /**
     * Returns the next order ID in the format O001.
     */
    public String generateOrderId() {
        return String.format("O%03d", nextOrderNumber++);
    }

    /**
     * Adds an order to the list.
     */
    public void add(Order order) {
        assert order != null : "Order must not be null";
        orders.add(order);
    }

    /**
     * Returns the order at the given index. Index starts from 1.
     */
    public Order get(int index) {
        return orders.get(index - 1);
    }

    /**
     * Deletes the order at the given index. Index starts from 1.
     */
    public Order deleteOrder(int index) {
        return orders.remove(index - 1); // arraylist starts from 0
    }

    /**
     * Returns the number of orders in the list.
     */
    public int size() {
        return orders.size();
    }

    @Override
    public String toString() {
        StringBuilder listing = new StringBuilder();
        for (int i = 0; i < orders.size(); i++) {
            listing.append(i + 1)
                    .append(". ")
                    .append(orders.get(i))
                    .append(System.lineSeparator());
        }
        return listing.toString();
    }
    /**
     * Finds an order using its unique Order ID.
     *
     * @param orderId The ID of the order to find.
     * @return The matching order.
     * @throws IllegalArgumentException If the order does not exist.
     */
    public Order getById(String orderId) {
        for (Order order : orders) {
            if (orderId.equals(order.getOrderId())) {
                return order;
            }
        }
        throw new IllegalArgumentException("Order not found: " + orderId);
    }
    /**
     * Replaces an existing order with an updated order.
     *
     * @param updatedOrder The order containing the updated details.
     * @throws IllegalArgumentException If the order ID does not exist.
     */
    public void update(Order updatedOrder) {
        for (int i = 0; i < orders.size(); i++) {
            if (updatedOrder.getOrderId().equals(orders.get(i).getOrderId())) {
                orders.set(i, updatedOrder);
                return;
            }
        }
        throw new IllegalArgumentException("Order not found: " + updatedOrder.getOrderId());
    }
}
