package seedu.duke.command;

//@@author WillChin484848

import java.time.LocalDate;

import seedu.duke.exception.CakeQueueException;
import seedu.duke.model.Cake;
import seedu.duke.model.Customer;
import seedu.duke.model.Order;
import seedu.duke.model.OrderList;

/**
 * Represents a command that updates an existing order.
 */
public class UpdateOrderCommand {
    private final String orderId;
    private final String name;
    private final String phone;
    private final LocalDate dueDate;
    private final Integer size;
    private final String flavour;
    private final String filling;

    /**
     * Creates a command with the details to update.
     * Null values indicate fields that should remain unchanged.
     */
    public UpdateOrderCommand(String orderId, String name, String phone,
                              LocalDate dueDate, Integer size,
                              String flavour, String filling) {
        this.orderId = orderId;
        this.name = name;
        this.phone = phone;
        this.dueDate = dueDate;
        this.size = size;
        this.flavour = flavour;
        this.filling = filling;
    }

    /**
     * Updates the specified order while preserving unspecified fields.
     *
     * @param orders The list containing the order.
     * @return A confirmation message.
     */
    public String execute(OrderList orders) {
        Order original = orders.getById(orderId);

        Customer oldCustomer = original.getCustomer();
        Cake oldCake = original.getCake();

        Customer updatedCustomer = new Customer(
                name != null ? name : oldCustomer.getName(),
                phone != null ? phone : oldCustomer.getPhone()
        );

        Cake updatedCake = oldCake;
        if (size != null || flavour != null || filling != null) {
            if (oldCake == null) {
                throw new CakeQueueException("This order has no cake details.");
            }
            try {
                // Preserve existing cake details when a field is not updated.
                updatedCake = new Cake(
                        size != null ? size : oldCake.getSize(),
                        flavour != null ? flavour : oldCake.getFlavour(),
                        filling != null ? filling : oldCake.getFilling()
                );
            } catch (IllegalArgumentException exception) {
                // Convert model validation errors into user-facing command errors.
                throw new CakeQueueException(exception.getMessage());
            }

        }

        Order updatedOrder = original.updatedCopy(
                updatedCustomer, updatedCake, dueDate
        );

        orders.update(updatedOrder);

        // Display the updated order.
        // The order's toString() already includes its Order ID.
        return "Updated order " + updatedOrder;
    }
}
