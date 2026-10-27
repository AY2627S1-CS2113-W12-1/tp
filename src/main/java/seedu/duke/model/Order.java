package seedu.duke.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Represents a customer enquiry or a confirmed cake order.
 */
public class Order {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-uuuu");

    private final String orderId;
    private final Customer customer;
    private final Cake cake;
    private final int quantity;
    private final LocalDate dueDate;
    private OrderStatus status;
    //@@author anannya-mk
    private String customerId;
    //@@author

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
        this(orderId, customer, null, dueDate, OrderStatus.QUEUED);
    }

    /**
     * Creates a confirmed order with cake details and a due date.
     */
    public Order(String orderId, Customer customer, Cake cake, LocalDate dueDate) {
        this(orderId, customer, cake, dueDate, OrderStatus.QUEUED);
    }

    /**
     * Creates an enquiry or order with an explicit starting status.
     */
    public Order(String orderId, Customer customer, Cake cake, LocalDate dueDate, OrderStatus status) {
        this.orderId = orderId;
        this.customer = customer;
        this.cake = cake;
        this.quantity = 1;
        this.dueDate = dueDate;
        this.status = status;
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

    //@@author anannya-mk
    /**
     * Returns the ID of the customer who placed this order, such as C001.
     * It is null for enquiries, which do not have a customer ID until they are accepted.
     */
    public String getCustomerId() {
        return customerId;
    }

    /**
     * Sets the customer ID. It is assigned once when the order is added and never changes afterwards,
     * even if the customer's name or phone number is edited.
     */
    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }
    //@@author

    /**
     * Returns true when this record is still a customer enquiry.
     */
    public boolean isEnquiry() {
        return status == OrderStatus.ENQUIRY;
    }

    /**
     * Creates an updated copy of this order.
     * Unchanged fields retain their original values.
     *
     * @param newCustomer Updated customer, or null to keep the existing customer.
     * @param newCake Updated cake, or null to keep the existing cake.
     * @param newDueDate Updated due date, or null to keep the existing date.
     * @return The updated order.
     */
    public Order updatedCopy(Customer newCustomer, Cake newCake, LocalDate newDueDate) {
        Customer updatedCustomer = newCustomer != null ? newCustomer : customer;
        Cake updatedCake = newCake != null ? newCake : cake;
        LocalDate updatedDueDate = newDueDate != null ? newDueDate : dueDate;

        Order updatedOrder = new Order(orderId, updatedCustomer, updatedCake, updatedDueDate, status);
        updatedOrder.customerId = customerId;
        return updatedOrder;
    }

    /**
     * Returns the full details of this order or enquiry.
     */
    public String toDetailedString() {
        String nl = System.lineSeparator();
        String idLabel = isEnquiry() ? "Enquiry ID: " : "Order ID: ";
        StringBuilder details = new StringBuilder();
        details.append(idLabel).append(orderId).append(nl)
                .append("Customer: ").append(customer).append(nl);
        if (dueDate != null) {
            details.append("Due date: ").append(dueDate.format(DATE_FORMATTER)).append(nl);
        }
        if (cake != null) {
            details.append("Size: ").append(cake.getSize()).append(" inches").append(nl)
                    .append("Flavour: ").append(cake.getFlavour()).append(nl)
                    .append("Filling: ").append(cake.getFilling()).append(nl);
        }
        details.append("Status: ").append(status);
        return details.toString();
    }


    @Override
    public String toString() {
        if (orderId != null) {
            // Display a placeholder when an order has no due date.
            String dueDateText = dueDate != null
                    ? dueDate.format(DATE_FORMATTER) : "Not specified";

            if (cake != null) {
                return orderId + ": " + customer + " | Due: " + dueDateText
                        + " | Size: " + cake.getSize() + " inches | Flavour: " + cake.getFlavour()
                        + " | Filling: " + cake.getFilling() + " [" + status + "]";
            }

            return orderId + ": " + customer.getName() + " (due "
                    + dueDateText + ") [" + status + "]";
        }

        // Preserve the existing display format for orders without an ID.
        return customer.getName() + ": " + quantity + " x " + cake.getName() + " [" + status + "]";
    }
}
