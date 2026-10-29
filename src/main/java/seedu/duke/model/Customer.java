package seedu.duke.model;

/**
 * Represents a customer.
 */
public class Customer {
    private final String name;
    private final String phone;

    //@@author Eethanzyj
    /**
     * Creates a customer when only a name is required.
     */
    public Customer(String name) {
        this(name, "");
    }
    //@@author

    public Customer(String name, String phone) {
        this.name = name;
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    @Override
    public String toString() {
        return name + " (" + phone + ")";
    }
}
