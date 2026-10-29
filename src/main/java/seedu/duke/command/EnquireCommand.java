package seedu.duke.command;

import java.time.LocalDate;

import seedu.duke.model.Cake;
import seedu.duke.model.Customer;
import seedu.duke.model.Order;
import seedu.duke.model.OrderList;
import seedu.duke.model.OrderStatus;

/**
 * Creates a new customer enquiry on behalf of the baker.
 * The enquiry is assigned an E-prefixed ID and starts with status ENQUIRY.
 * The baker can then accept or reject it later.
 */
public class EnquireCommand {
    private final String name;
    private final String phone;
    private final LocalDate dueDate;
    private final int size;
    private final String flavour;
    private final String filling;

    /**
     * Creates the command with all enquiry details.
     */
    public EnquireCommand(String name, String phone, LocalDate dueDate,
                          int size, String flavour, String filling) {
        this.name = name;
        this.phone = phone;
        this.dueDate = dueDate;
        this.size = size;
        this.flavour = flavour;
        this.filling = filling;
    }

    /**
     * Adds the enquiry to the order list and returns a confirmation message.
     */
    public String execute(OrderList orders) {
        Order enquiry = new Order(
                orders.generateEnquiryId(),
                new Customer(name, phone),
                new Cake(size, flavour, filling),
                dueDate,
                OrderStatus.ENQUIRY);
        orders.add(enquiry);
        return "Submitted enquiry " + enquiry;
    }
}
