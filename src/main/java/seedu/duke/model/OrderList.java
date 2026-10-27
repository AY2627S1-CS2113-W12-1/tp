package seedu.duke.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.TreeMap;

import seedu.duke.exception.CakeQueueException;

/**
 * Represents the shared list of enquiries and confirmed orders.
 */
public class OrderList {
    public static final int DEFAULT_DAILY_CAPACITY = 5;
    public static final int MAX_DAILY_CAPACITY = 50;
    private final ArrayList<Order> orders;
    private int dailyCapacity;
    private int nextOrderNumber;
    private int nextEnquiryNumber;

    public OrderList() {
        orders = new ArrayList<>();
        nextOrderNumber = 1;
        nextEnquiryNumber = 1;
        dailyCapacity = DEFAULT_DAILY_CAPACITY;
    }

    /**
     * Returns the next order ID in the format O001.
     */
    public String generateOrderId() {
        return String.format("O%03d", nextOrderNumber++);
    }

    /**
     * Returns the next enquiry ID in the format E001.
     */
    public String generateEnquiryId() {
        return String.format("E%03d", nextEnquiryNumber++);
    }

    /**
     * Adds an order or enquiry to the list.
     * If the record already has an ID (e.g., it was loaded from disk), the
     * next-ID counters are advanced past it so newly generated IDs can never
     * collide with a restored record.
     */
    public void add(Order order) {
        assert order != null : "Order must not be null";
        orders.add(order);
        advanceCountersPast(order.getOrderId());
    }

    private void advanceCountersPast(String recordId) {
        if (recordId == null || !recordId.matches("[OE][0-9]+")) {
            return;
        }
        int number = Integer.parseInt(recordId.substring(1));
        if (recordId.startsWith("O")) {
            nextOrderNumber = Math.max(nextOrderNumber, number + 1);
        } else {
            nextEnquiryNumber = Math.max(nextEnquiryNumber, number + 1);
        }
    }

    /**
     * Gets the order at the given index. Index starts from 1.
     *
     * @throws CakeQueueException if the index is out of range.
     */
    public Order get(int index) {
        if (index < 1 || index > orders.size()) {
            throw new CakeQueueException(
                    "Order number must be between 1 and " + orders.size());
        }
        return orders.get(index - 1);
    }

    /**
     * Finds an order or enquiry using its unique ID (not case-sensitive).
     *
     * @param orderId The ID of the record to find.
     * @return The matching order.
     * @throws CakeQueueException If the record does not exist.
     */
    public Order getById(String orderId) {
        if (orderId == null) {
            throw new CakeQueueException("Order not found: null");
        }
        for (Order order : orders) {
            if (orderId.equalsIgnoreCase(order.getOrderId())) {
                return order;
            }
        }
        throw new CakeQueueException("Order not found: " + orderId);
    }

    /**
     * Returns every record whose customer phone matches the given value.
     */
    public ArrayList<Order> findByPhone(String phone) {
        ArrayList<Order> matches = new ArrayList<>();
        for (Order order : orders) {
            if (order.getCustomer().getPhone().equals(phone)) {
                matches.add(order);
            }
        }
        return matches;
    }

    /**
     * Replaces an existing order with an updated order.
     *
     * @param updatedOrder The order containing the updated details.
     * @throws CakeQueueException If the order ID does not exist.
     */
    public void update(Order updatedOrder) {
        for (int i = 0; i < orders.size(); i++) {
            if (updatedOrder.getOrderId().equals(orders.get(i).getOrderId())) {
                orders.set(i, updatedOrder);
                return;
            }
        }
        throw new CakeQueueException("Order not found: " + updatedOrder.getOrderId());
    }

    /**
     * Removes the record with the given ID.
     *
     * @return The removed record.
     */
    public Order removeById(String orderId) {
        if (orderId == null) {
            throw new CakeQueueException("Order not found: null");
        }
        for (int i = 0; i < orders.size(); i++) {
            if (orderId.equalsIgnoreCase(orders.get(i).getOrderId())) {
                return orders.remove(i);
            }
        }
        throw new CakeQueueException("Order not found: " + orderId);
    }

    /**
     * Deletes the order at the given index. Index starts from 1.
     */
    public Order deleteOrder(int index) {
        if (index < 1 || index > orders.size()) {
            throw new CakeQueueException(
                    "Order number must be between 1 and " + orders.size());
        }
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

    /**
     * Returns the most orders the baker can take for one due date.
     */
    public int getDailyCapacity() {
        return dailyCapacity;
    }

    /**
     * Sets the most orders the baker can take for one due date.
     *
     * @param capacity The new limit, from 1 to {@value #MAX_DAILY_CAPACITY}.
     * @throws CakeQueueException If the limit is out of range.
     */
    public void setDailyCapacity(int capacity) {
        if (capacity < 1 || capacity > MAX_DAILY_CAPACITY) {
            throw new CakeQueueException("Daily capacity must be a whole number from 1 to "
                    + MAX_DAILY_CAPACITY + ".");
        }
        dailyCapacity = capacity;
    }

    /**
     * Returns how many active orders (queued, preparing or ready) are due on a date.
     * Enquiries and collected, cancelled or rejected records are not counted.
     */
    public int countOrdersDueOn(LocalDate date) {
        int count = 0;
        for (Order order : orders) {
            if (isActive(order) && date.equals(order.getDueDate())) {
                count++;
            }
        }
        return count;
    }

    /**
     * Returns the number of active orders for each due date, earliest date first.
     */
    public TreeMap<LocalDate, Integer> countOrdersByDueDate() {
        TreeMap<LocalDate, Integer> counts = new TreeMap<>();
        for (Order order : orders) {
            if (isActive(order) && order.getDueDate() != null) {
                counts.merge(order.getDueDate(), 1, Integer::sum);
            }
        }
        return counts;
    }

    private boolean isActive(Order order) {
        OrderStatus status = order.getStatus();
        return status == OrderStatus.QUEUED || status == OrderStatus.PREPARING
                || status == OrderStatus.READY;
    }
}
