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
        assertTrue(help.contains("mark ORDER_ID preparing|ready|collected"));
        assertFalse(help.contains("Customer commands:"));
        assertFalse(help.contains("search"));
        assertFalse(help.contains("enquire"));
        assertFalse(help.contains("stats"));
        assertFalse(help.contains("note "));
        assertFalse(help.contains("delete "));
    }
}
