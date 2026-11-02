package seedu.duke.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Locale;
import java.util.TreeMap;
import java.util.Objects;

import seedu.duke.exception.CakeQueueException;

/**
 * Represents the shared list of enquiries and confirmed orders.
 */
public class OrderList {
    public static final int DEFAULT_DAILY_CAPACITY = 5;
    public static final int MAX_DAILY_CAPACITY = 50;
    private final ArrayList<Order> orders;
    private int dailyCapacity;
    //@@author Eethanzyj
    private int nextOrderNumber;
    //@@author
    private int nextEnquiryNumber;
    //@@author anannya-mk
    private int nextCustomerNumber = 1;
    //@@author

    public OrderList() {
        orders = new ArrayList<>();
        //@@author Eethanzyj
        nextOrderNumber = 1;
        //@@author
        nextEnquiryNumber = 1;
        dailyCapacity = DEFAULT_DAILY_CAPACITY;
    }

    //@@author Eethanzyj
    /**
     * Returns the next order ID in the format O001.
     */
    public String generateOrderId() {
        //@@author WillChin484848
        if (nextOrderNumber <= 0) {
            throw new CakeQueueException("Order ID limit reached.");
        }
        //@@author Eethanzyj
        return String.format(Locale.ROOT, "O%03d", nextOrderNumber++);
    }
    //@@author

    /**
     * Returns the next enquiry ID in the format E001.
     */
    public String generateEnquiryId() {
        //@@author Eethanzyj
        //@@author WillChin484848
        if (nextEnquiryNumber <= 0) {
            throw new CakeQueueException("Enquiry ID limit reached.");
        }
        //@@author Eethanzyj
        return String.format(Locale.ROOT, "E%03d", nextEnquiryNumber++);
        //@@author
    }

    /**
     * Adds an order or enquiry to the list.
     * If the record already has an ID (e.g., it was loaded from disk), the
     * next-ID counters are advanced past it so newly generated IDs can never
     * collide with a restored record.
     */
    public void add(Order order) {
        //@@author WillChin484848
        // Reject invalid records before assigning a customer ID or modifying the list.
        if (order == null) {
            throw new CakeQueueException("Cannot add a null record.");
        }
        String recordId = order.getOrderId();
        if (recordId == null) {
            throw new CakeQueueException("Cannot add a record without an ID.");
        }
        validateRecordNumber(recordId);
        for (Order existing : orders) {
            if (recordId.equalsIgnoreCase(existing.getOrderId())) {
                throw new CakeQueueException("Duplicate record ID: " + recordId);
            }
        }
        //@@author
        assignCustomerId(order);
        orders.add(order);
        //@@author WillChin484848
        advanceCountersPast(recordId);
        //@@author
    }

    //@@author anannya-mk
    /**
     * Gives a confirmed order its customer ID.
     * A customer ID stays with the order for good: an order that already has one (for example, loaded
     * from disk) keeps it. A new order takes the ID of the most recent order with the same phone number,
     * or the next unused ID if that phone number has not been seen before. Enquiries get no customer ID.
     */
    private void assignCustomerId(Order order) {
        String recordId = order.getOrderId();
        if (recordId == null || !recordId.startsWith("O")) {
            return;
        }
        String existingId = order.getCustomerId();
        if (existingId != null) {
            if (existingId.matches("C[0-9]+")) {
                //@@author WillChin484848
                nextCustomerNumber = Math.max(nextCustomerNumber, safeNextNumber(existingId.substring(1)));
                //@@author anannya-mk
            }
            return;
        }
        for (int i = orders.size() - 1; i >= 0; i--) {
            Order earlier = orders.get(i);
            if (earlier.getCustomerId() != null
                    && Objects.equals(earlier.getCustomer().getPhone(), order.getCustomer().getPhone())) {
                order.setCustomerId(earlier.getCustomerId());
                return;
            }
        }
        //@@author Eethanzyj
        // IDs must keep ASCII digits so commands and saved files can recognise them in every locale.
        //@@author WillChin484848
        if (nextCustomerNumber <= 0) {
            throw new CakeQueueException("Customer ID limit reached.");
        }
        //@@author Eethanzyj
        order.setCustomerId(String.format(Locale.ROOT, "C%03d", nextCustomerNumber++));
        //@@author anannya-mk
    }
    //@@author

    private void advanceCountersPast(String recordId) {
        if (recordId == null || !recordId.matches("[OE][0-9]+")) {
            return;
        }
        //@@author WillChin484848
        int nextNumber = safeNextNumber(recordId.substring(1));
        //@@author
        if (recordId.startsWith("O")) {
            //@@author WillChin484848
            nextOrderNumber = Math.max(nextOrderNumber, nextNumber);
        //@@author
        } else {
            //@@author WillChin484848
            nextEnquiryNumber = Math.max(nextEnquiryNumber, nextNumber);
        }
    }

    /** Reserves existing customer IDs before assigning IDs to legacy records. */
    public void reserveCustomerId(String customerId) {
        if (customerId != null && customerId.matches("C[0-9]+")) {
            nextCustomerNumber = Math.max(nextCustomerNumber, safeNextNumber(customerId.substring(1)));
        }
    }

    private void validateRecordNumber(String recordId) {
        if (!recordId.matches("[OE][0-9]{3,}")) {
            throw new CakeQueueException("Invalid record ID: " + recordId);
        }
        safeNextNumber(recordId.substring(1));
    }

    private int safeNextNumber(String digits) {
        try {
            int number = Integer.parseInt(digits);
            if (number < 0 || number == Integer.MAX_VALUE) {
                throw new CakeQueueException("Record ID limit reached.");
            }
            return number + 1;
        } catch (NumberFormatException exception) {
            throw new CakeQueueException("Record ID is too large.");
            //@@author
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
        //@@author WillChin484848
        if (updatedOrder == null || updatedOrder.getOrderId() == null) {
            throw new CakeQueueException("Cannot update a record without an ID.");
        }
        //@@author
        for (int i = 0; i < orders.size(); i++) {
            //@@author WillChin484848
            if (updatedOrder.getOrderId().equalsIgnoreCase(orders.get(i).getOrderId())) {
                // Preserve the canonical stored ID even if a caller supplies different casing.
                Order original = orders.get(i);
                Order replacement = updatedOrder;
                if (!updatedOrder.getOrderId().equals(original.getOrderId())) {
                    replacement = new Order(original.getOrderId(), updatedOrder.getCustomer(),
                            updatedOrder.getCake(), updatedOrder.getDueDate(), updatedOrder.getStatus());
                    replacement.setCustomerId(updatedOrder.getCustomerId());
                    replacement.setNotes(updatedOrder.getNotes());
                }
                //@@author
                orders.set(i, replacement);
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
        //@@author WillChin484848
        if (date == null) {
            return 0;
        }
        //@@author
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
