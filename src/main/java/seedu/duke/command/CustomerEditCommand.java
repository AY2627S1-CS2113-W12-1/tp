package seedu.duke.command;

import java.time.LocalDate;

import seedu.duke.model.Cake;
import seedu.duke.model.Customer;
import seedu.duke.model.Order;
import seedu.duke.model.OrderList;
import seedu.duke.model.OrderStatus;

/**
 * Lets a customer change their own enquiry before the baker decides.
 */
public class CustomerEditCommand {
    private final String enquiryId;
    private final String phone;
    private final String name;
    private final LocalDate dueDate;
    private final Integer size;
    private final String flavour;
    private final String filling;

    /**
     * Creates a customer edit command.
     * {@code phone} authenticates ownership and is not written as a field change.
     */
    public CustomerEditCommand(String enquiryId, String phone, String name,
                               LocalDate dueDate, Integer size,
                               String flavour, String filling) {
        this.enquiryId = enquiryId;
        this.phone = phone;
        this.name = name;
        this.dueDate = dueDate;
        this.size = size;
        this.flavour = flavour;
        this.filling = filling;
    }

    /**
     * Updates an enquiry when the phone matches and the status is still ENQUIRY.
     */
    public String execute(OrderList orders) {
        Order original = orders.getById(enquiryId);
        if (original.getStatus() != OrderStatus.ENQUIRY) {
            throw new IllegalArgumentException("Customers may only edit pending enquiries.");
        }
        if (!original.getCustomer().getPhone().equals(phone)) {
            throw new IllegalArgumentException("Phone number does not match this record.");
        }
        if (name == null && dueDate == null && size == null && flavour == null && filling == null) {
            throw new IllegalArgumentException(
                    "Use: edit ENQUIRY_ID p/PHONE [n/NAME] [d/DATE] [s/SIZE] [f/FLAVOUR] [fi/FILLING]");
        }

        Customer oldCustomer = original.getCustomer();
        Cake oldCake = original.getCake();
        Customer updatedCustomer = new Customer(
                name != null ? name : oldCustomer.getName(),
                oldCustomer.getPhone()
        );
        Cake updatedCake = oldCake;
        if (size != null || flavour != null || filling != null) {
            if (oldCake == null) {
                throw new IllegalArgumentException("This enquiry has no cake details.");
            }
            updatedCake = new Cake(
                    size != null ? size : oldCake.getSize(),
                    flavour != null ? flavour : oldCake.getFlavour(),
                    filling != null ? filling : oldCake.getFilling()
            );
        }
        Order updated = original.updatedCopy(updatedCustomer, updatedCake, dueDate);
        orders.update(updated);
        return "Updated enquiry " + updated;
    }
}
