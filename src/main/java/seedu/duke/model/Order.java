package seedu.duke.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Represents an order made by a customer.
 */
public class Order {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-uuuu");

    private final String orderId;
    private final Customer customer;
    private final Cake cake;
    private final int quantity;
    private final LocalDate dueDate;
    private OrderStatus status;

    public Order(Customer customer, Cake cake, int quantity) {
        this.orderId = null;
        this.customer = customer;
        this.cake = cake;
        this.quantity = quantity;
        this.dueDate = null;
        this.status = OrderStatus.QUEUED;
    }

    /**
     * Creates a basic order with a customer and due date.
     */
    public Order(String orderId, Customer customer, LocalDate dueDate) {
        this(orderId, customer, null, dueDate);
    }

    /**
     * Creates an order with customer contact details, cake details, and a due date.
     */
    public Order(String orderId, Customer customer, Cake cake, LocalDate dueDate) {
        this.orderId = orderId;
        this.customer = customer;
        this.cake = cake;
        this.quantity = 1;
        this.dueDate = dueDate;
        this.status = OrderStatus.QUEUED;
    }

    public String getOrderId() {
        return orderId;
    }

    public Customer getCustomer() {
        return customer;
    }

    public Cake getCake() {
        return cake;
    }

    public int getQuantity() {
        return quantity;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        if (orderId != null) {
            if (cake != null) {
                return orderId + ": " + customer + " | Due: " + dueDate.format(DATE_FORMATTER)
                        + " | Size: " + cake.getSize() + " inches | Flavour: " + cake.getFlavour()
                        + " | Filling: " + cake.getFilling() + " [" + status + "]";
            }
            return orderId + ": " + customer.getName() + " (due "
                    + dueDate.format(DATE_FORMATTER) + ") [" + status + "]";
        }
        return customer.getName() + ": " + quantity + " x " + cake.getName() + " [" + status + "]";
    }
}
