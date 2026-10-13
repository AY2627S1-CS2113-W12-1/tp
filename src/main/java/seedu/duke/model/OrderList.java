package seedu.duke.model;

import java.util.ArrayList;

/**
 * Represents the list of orders.
 */
public class OrderList {
    private final ArrayList<Order> orders;

    public OrderList() {
        orders = new ArrayList<>();
    }

    /**
     * Adds an order to the list.
     */
    public void add(Order order) {
        orders.add(order);
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
     * Returns a copy of all orders in the list, in the order they were added.
     *
     * @return A new list containing every order.
     */
    public ArrayList<Order> getAll() {
        return new ArrayList<>(orders);
    }
}
