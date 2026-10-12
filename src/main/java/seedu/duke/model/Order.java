package seedu.duke.model;

/**
 * Represents an order made by a customer.
 */
public class Order {
    private final Customer customer;
    private final Cake cake;
    private final int quantity;
    private OrderStatus status;

    public Order(Customer customer, Cake cake, int quantity) {
        this.customer = customer;
        this.cake = cake;
        this.quantity = quantity;
        this.status = OrderStatus.QUEUED;
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

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    /**
     * Returns the full details of this order.
     */
    public String toDetailedString() {
        String nl = System.lineSeparator();
        return "Customer: " + customer + nl
                + "Cake: " + cake + nl
                + "Quantity: " + quantity + nl
                + "Total: $" + String.format("%.2f", cake.getPrice() * quantity) + nl
                + "Status: " + status;
    }

    @Override
    public String toString() {
        return customer.getName() + ": " + quantity + " x " + cake.getName() + " [" + status + "]";
    }
}
