package seedu.duke.command;

//@@author Eethanzyj

import java.util.logging.Level;
import java.util.logging.Logger;

import seedu.duke.model.Cake;
import seedu.duke.model.Customer;
import seedu.duke.model.CustomerList;
import seedu.duke.model.CustomerProfile;
import seedu.duke.model.Order;
import seedu.duke.model.OrderList;
import seedu.duke.parser.Parser;

/**
 * Adds a validated cake order and returns its order and customer confirmation.
 * The caller handles console output and saving after execution.
 */
public class AddOrderCommand {
    private static final Logger LOGGER = Logger.getLogger(AddOrderCommand.class.getName());

    /** The immutable values already validated by the parser. */
    private final Parser.AddOrderDetails details;

    /**
     * Creates a command from parsed Add Order details without parsing the input again.
     *
     * @param details The non-null details returned by {@link Parser#parseAdd(String)}.
     */
    public AddOrderCommand(Parser.AddOrderDetails details) {
        assert details != null : "Add Order details must not be null";
        this.details = details;
    }

    /**
     * Creates and stores an order, then returns its confirmation, optional capacity warning
     * and customer ID. Orders over capacity are still added.
     *
     * @param orders The non-null list receiving the order.
     * @return The complete message for the caller to display.
     */
    public String execute(OrderList orders) {
        assert orders != null : "Order list must not be null";
        //@@author anannya-mk
        String phone = details.getPhone();
        boolean isNewCustomer = CustomerList.fromOrders(orders).findByPhone(phone).isEmpty();
        //@@author Eethanzyj
        Order order = new Order(orders.generateOrderId(),
                new Customer(details.getCustomerName(), details.getPhone()),
                new Cake(details.getSize(), details.getFlavour(), details.getFilling()),
                details.getDueDate());
        orders.add(order);
        LOGGER.log(Level.INFO, "Added order {0}.", order.getOrderId());
        //@@author Kaishhhh
        String warning = CapacityCommand.warningFor(orders, order.getDueDate());
        String added = "Added order " + order
                + (warning.isEmpty() ? "" : System.lineSeparator() + warning);
        //@@author anannya-mk
        CustomerProfile customer = CustomerList.fromOrders(orders).findByPhone(phone).orElseThrow();
        return added + System.lineSeparator() + "Customer ID: " + customer.getCustomerId()
                + (isNewCustomer ? " (new customer)" : " (returning customer)");
        //@@author Eethanzyj
    }
}
