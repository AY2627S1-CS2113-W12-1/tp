package seedu.duke.parser;

//@@author Kaishhhh

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.duke.exception.CakeQueueException;

/**
 * Tests {@link Parser#parseCapacity(String)}.
 */
public class ParserCapacityTest {

    @Test
    public void parseCapacity_noNumber_returnsNull() {
        assertNull(Parser.parseCapacity("capacity"));
        assertNull(Parser.parseCapacity("  CAPACITY  "));
    }

    @Test
    public void parseCapacity_validNumber_returnsNumber() {
        assertEquals(3, Parser.parseCapacity("capacity 3"));
        assertEquals(1, Parser.parseCapacity("capacity 1"));
        assertEquals(50, Parser.parseCapacity("capacity 50"));
    }

    @Test
    public void parseCapacity_outOfRangeOrNotANumber_throwsRangeError() {
        String expected = "Daily capacity must be a whole number from 1 to 50.";
        List<String> invalidInputs = List.of("capacity 0", "capacity -2", "capacity 51",
                "capacity abc", "capacity 2.5", "capacity 99999999999");
        for (String input : invalidInputs) {
            CakeQueueException exception = assertThrows(CakeQueueException.class,
                    () -> Parser.parseCapacity(input), input);
            assertEquals(expected, exception.getMessage(), input);
        }
    }

    @Test
    public void parseCapacity_extraWordsOrWrongCommand_throwsUsageError() {
        assertEquals("Use: capacity [NUMBER]", assertThrows(CakeQueueException.class,
                () -> Parser.parseCapacity("capacity 1 2")).getMessage());
        assertThrows(CakeQueueException.class, () -> Parser.parseCapacity("limit 3"));
        assertThrows(CakeQueueException.class, () -> Parser.parseCapacity(null));
        assertThrows(CakeQueueException.class, () -> Parser.parseCapacity("   "));
    }
}
