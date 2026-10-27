package seedu.duke.parser;

//@@author anannya-mk

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.duke.exception.CakeQueueException;

/**
 * Tests parsing of the customer and history commands.
 */
public class ParserCustomerHistoryTest {
    @Test
    public void parseCustomer_validId_returnsUpperCaseId() {
        assertEquals("C001", Parser.parseCustomer("customer c001"));
        assertEquals("C012", Parser.parseCustomer("  CUSTOMER   C012 "));
    }

    @Test
    public void parseHistory_validId_returnsUpperCaseId() {
        assertEquals("C002", Parser.parseHistory("history c002"));
    }

    @Test
    public void parseCustomer_badInput_throwsUsage() {
        CakeQueueException missing = assertThrows(CakeQueueException.class,
                () -> Parser.parseCustomer("customer"));
        assertEquals("Use: customer CUSTOMER_ID", missing.getMessage());
        assertThrows(CakeQueueException.class, () -> Parser.parseCustomer("customer O001"));
        assertThrows(CakeQueueException.class, () -> Parser.parseCustomer("customer C1"));
        assertThrows(CakeQueueException.class, () -> Parser.parseCustomer("customer C001 C002"));
    }

    @Test
    public void parseHistory_badInput_throwsUsage() {
        CakeQueueException exception = assertThrows(CakeQueueException.class,
                () -> Parser.parseHistory("history abc"));
        assertEquals("Use: history CUSTOMER_ID", exception.getMessage());
    }
}
