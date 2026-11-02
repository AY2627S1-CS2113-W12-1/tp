package seedu.duke.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

//@@author Eethanzyj
import java.util.List;
//@@author

import org.junit.jupiter.api.Test;

//@@author Eethanzyj
import seedu.duke.exception.CakeQueueException;
//@@author

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

    //@@author Eethanzyj
    @Test
    public void execute_eachTopic_matchesOverviewAndProvidesAnExample() {
        HelpCommand command = new HelpCommand();
        String overview = command.execute();
        String nl = System.lineSeparator();
        for (String topic : List.of("add", "list", "view", "edit", "cancel", "find", "customer", "history",
                "capacity", "mark", "help", "bye")) {
            String details = command.execute(topic);
            String format = details.split("Format:" + nl, 2)[1].split("\\R", 2)[0];
            String example = details.split("Example:" + nl, 2)[1].split("\\R", 2)[0];

            assertTrue(details.startsWith("Help: " + topic + nl), topic);
            assertTrue(overview.contains("  " + format + nl), topic);
            assertEquals(topic, format.split("\\s+", 2)[0]);
            assertEquals(topic, example.split("\\s+", 2)[0]);
        }
    }

    @Test
    public void execute_blankOrMixedCaseTopic_returnsExpectedHelp() {
        HelpCommand command = new HelpCommand();
        assertEquals(command.execute(), command.execute(null));
        assertEquals(command.execute(), command.execute(" \t "));
        assertEquals(command.execute("add"), command.execute("  ADD  "));
    }

    @Test
    public void execute_unknownOrMultipleTopics_reportsHowToRecover() {
        HelpCommand command = new HelpCommand();
        CakeQueueException unknown = assertThrows(CakeQueueException.class, () -> command.execute("search"));
        assertEquals("Unknown help topic: search. Type help to see available commands.", unknown.getMessage());
        CakeQueueException multiple = assertThrows(CakeQueueException.class, () -> command.execute("add extra"));
        assertEquals("Use: help [COMMAND]", multiple.getMessage());
    }
}
