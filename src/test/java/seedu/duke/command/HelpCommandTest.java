package seedu.duke.command;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Tests that baker help text lists all available commands.
 */
public class HelpCommandTest {

    @Test
    public void execute_listsBakerCommands() {
        String help = new HelpCommand().execute();

        assertTrue(help.contains("Baker commands:"));
        assertTrue(help.contains("add n/NAME p/PHONE d/DATE s/SIZE f/FLAVOUR fi/FILLING"));
        assertTrue(help.contains("enquire n/NAME p/PHONE d/DATE s/SIZE f/FLAVOUR fi/FILLING"));
        assertTrue(help.contains("accept ENQUIRY_ID"));
        assertTrue(help.contains("reject ENQUIRY_ID"));
        assertTrue(help.contains("mark ORDER_ID preparing|ready|collected"));
        assertTrue(help.contains("stats"));
        assertTrue(help.contains("delete ORDER_ID|ENQUIRY_ID"));
        assertTrue(help.contains("note ORDER_ID"));
        assertFalse(help.contains("Customer commands:"));
        assertFalse(help.contains("search"));
    }
}
