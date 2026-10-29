package seedu.duke.command;

//@@author anannya-mk

import seedu.duke.model.CustomerList;
import seedu.duke.model.CustomerProfile;
import seedu.duke.model.OrderList;

/**
 * Shows the details of one customer, found by Customer ID.
 */
public class CustomerCommand {
    private final String customerId;

    public CustomerCommand(String customerId) {
        this.customerId = customerId;
    }

    /**
     * Returns the customer's ID, name, phone number, number of orders and whether they are a repeat customer.
     * A repeat customer has more than one order on record. Cancelled orders are counted.
     *
     * @param orders The list of all orders and enquiries.
     * @return The customer's details.
     * @throws seedu.duke.exception.CakeQueueException If no customer has that ID.
     */
    public String execute(OrderList orders) {
        CustomerProfile customer = CustomerList.fromOrders(orders).getById(customerId);
        String nl = System.lineSeparator();
        return "Customer ID: " + customer.getCustomerId() + nl
                + "Name: " + customer.getName() + nl
                + "Phone: " + customer.getPhone() + nl
                + "Orders: " + customer.getOrders().size() + nl
                + "Repeat customer: " + (customer.isRepeatCustomer() ? "Yes" : "No");
    }
}
