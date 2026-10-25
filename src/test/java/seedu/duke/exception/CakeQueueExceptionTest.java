package seedu.duke.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.format.DateTimeParseException;

import org.junit.jupiter.api.Test;

import seedu.duke.parser.Parser;

/**
 * Tests the contract of {@link CakeQueueException}: it carries a user-facing
 * message, optionally preserves its cause, and stays compatible with code
 * that historically caught {@link IllegalArgumentException}.
 */
public class CakeQueueExceptionTest {

    @Test
    public void constructor_messageOnly_preservesMessageAndHasNoCause() {
        CakeQueueException exception = new CakeQueueException("Something went wrong.");

        assertEquals("Something went wrong.", exception.getMessage());
        assertNull( exception.getCause());
    }

    @Test
    public void constructor_messageAndCause_preservesBoth() {
        Throwable cause = new IllegalStateException("root cause");
        CakeQueueException exception = new CakeQueueException("Friendly message.", cause);

        assertEquals("Friendly message.", exception.getMessage());
        assertSame(cause, exception.getCause());
    }

    @Test
    public void exception_isIllegalArgumentException_forBackwardCompatibility() {
        // Existing handlers/tests catching IllegalArgumentException must keep working.
        assertInstanceOf(IllegalArgumentException.class, new CakeQueueException("x"));

        IllegalArgumentException caught = assertThrows(IllegalArgumentException.class,
                () -> {
                    throw new CakeQueueException("caught as supertype");
                });
        assertInstanceOf(CakeQueueException.class, caught);
    }

    @Test
    public void parser_invalidDate_throwsCakeQueueExceptionWithDateTimeCause() {
        CakeQueueException exception = assertThrows(CakeQueueException.class,
                () -> Parser.parseAdd("add n/Alice p/91234567 d/31-02-2026 "
                        + "s/8 f/Chocolate fi/Strawberry"));

        assertEquals("Date must use DD-MM-YYYY and be valid.", exception.getMessage());
        assertInstanceOf(DateTimeParseException.class, exception.getCause());
    }

    @Test
    public void parser_nonNumericSize_throwsCakeQueueExceptionWithNumberFormatCause() {
        CakeQueueException exception = assertThrows(CakeQueueException.class,
                () -> Parser.parseAdd("add n/Alice p/91234567 d/20-10-2026 "
                        + "s/large f/Chocolate fi/Strawberry"));

        assertTrue(exception.getMessage().contains("Cake size must be 6, 8, or 10"));
        assertInstanceOf(NumberFormatException.class, exception.getCause());
    }

    @Test
    public void parser_invalidMarkStatus_throwsCakeQueueExceptionWithCause() {
        CakeQueueException exception = assertThrows(CakeQueueException.class,
                () -> Parser.parseMark("mark O001 banana"));

        assertEquals("Use: mark ORDER_ID preparing|ready|collected", exception.getMessage());
        assertInstanceOf(IllegalArgumentException.class, exception.getCause());
    }
}
