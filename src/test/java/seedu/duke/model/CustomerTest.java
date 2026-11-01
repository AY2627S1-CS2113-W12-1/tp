package seedu.duke.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Checks customer details independently of the retired cake-menu model.
 */
public class CustomerTest {
    //@@author HammerHead-os
    @Test
    public void customer_nameOnlyConstructor_hasEmptyPhone() {
        Customer customer = new Customer("Alice Tan");

        assertEquals("Alice Tan", customer.getName());
        assertEquals("", customer.getPhone());
        assertEquals("Alice Tan (91234567)", new Customer("Alice Tan", "91234567").toString());
    }
    //@@author
}
