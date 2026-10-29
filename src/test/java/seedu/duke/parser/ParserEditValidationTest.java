package seedu.duke.parser;

//@@author Kaishhhh

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.duke.exception.CakeQueueException;

/**
 * Tests that {@code edit} applies the same date and phone rules as {@code add}.
 */
public class ParserEditValidationTest {

    @Test
    public void parseUpdate_pastDate_throwsPastDateError() {
        CakeQueueException exception = assertThrows(CakeQueueException.class,
                () -> Parser.parseUpdate("edit O001 d/01-01-2020"));
        assertEquals("Due date cannot be in the past.", exception.getMessage());
    }

    @Test
    public void parseUpdate_invalidPhone_throwsPhoneError() {
        assertThrows(CakeQueueException.class, () -> Parser.parseUpdate("edit O001 p/abc"));
        assertThrows(CakeQueueException.class, () -> Parser.parseUpdate("edit O001 p/1234567"));
    }

    @Test
    public void parseUpdate_formattedPhone_isAccepted() {
        assertEquals("+65 9123-4567", Parser.parseUpdate("edit O001 p/+65 9123-4567").getPhone());
    }
}
