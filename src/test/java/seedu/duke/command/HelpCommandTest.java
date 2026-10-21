package seedu.duke.command;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import seedu.duke.UserMode;

/**
 * Tests that help text matches the commands available in each user mode.
 */
public class HelpCommandTest {

    /**
     * Tests that baker help lists baker-only commands such as accept and reject.
     */
    @Test
    public void execute_bakerMode_listsBakerCommandsOnly() {
        String help = new HelpCommand(UserMode.BAKER).execute();

        assertTrue(help.contains("Baker commands:"));
        assertTrue(help.contains("accept ENQUIRY_ID"));
        assertTrue(help.contains("reject ENQUIRY_ID"));
        assertTrue(help.contains("mode customer"));
        assertFalse(help.contains("Customer commands:"));
        assertFalse(help.contains("enquire n/NAME"));
    }

    /**
     * Tests that customer help lists customer-only commands such as enquire and track.
     */
    @Test
    public void execute_customerMode_listsCustomerCommandsOnly() {
        String help = new HelpCommand(UserMode.CUSTOMER).execute();

        assertTrue(help.contains("Customer commands:"));
        assertTrue(help.contains("enquire n/NAME"));
        assertTrue(help.contains("track ID p/PHONE"));
        assertTrue(help.contains("mode baker"));
        assertFalse(help.contains("Baker commands:"));
        assertFalse(help.contains("accept ENQUIRY_ID"));
    }
}
