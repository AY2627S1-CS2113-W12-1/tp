package seedu.duke.parser;

//@@author Kaishhhh

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.duke.exception.CakeQueueException;

/**
 * Tests {@link Parser#parseFind(String)}.
 */
public class ParserFindTest {

    @Test
    public void parseFind_singleWord_returnsKeyword() {
        assertEquals("Alice", Parser.parseFind("find Alice"));
    }

    @Test
    public void parseFind_multipleWordsAndExtraSpaces_keepsPhrase() {
        assertEquals("Alice Tan", Parser.parseFind("  FIND   Alice Tan  "));
    }

    @Test
    public void parseFind_missingKeyword_throwsUsageError() {
        CakeQueueException exception = assertThrows(CakeQueueException.class,
                () -> Parser.parseFind("find"));
        assertEquals("Use: find KEYWORD", exception.getMessage());
        assertThrows(CakeQueueException.class, () -> Parser.parseFind("find    "));
    }

    @Test
    public void parseFind_nullBlankOrWrongCommand_throwsUsageError() {
        assertThrows(CakeQueueException.class, () -> Parser.parseFind(null));
        assertThrows(CakeQueueException.class, () -> Parser.parseFind("   "));
        assertThrows(CakeQueueException.class, () -> Parser.parseFind("search Alice"));
    }
}
