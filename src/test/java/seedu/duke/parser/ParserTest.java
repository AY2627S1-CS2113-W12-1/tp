package seedu.duke.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/**
 * Tests parsing and validation of all six Add Order fields.
 */
public class ParserTest {
    private static final String VALID_INPUT = "add n/Alice Tan p/91234567 d/20-10-2026 "
            + "s/8 f/Dark Chocolate fi/Strawberry Cream";

    @Test
    public void parseAdd_validInput_returnsAllDetails() {
        Parser.AddOrderDetails details = Parser.parseAdd(VALID_INPUT);

        assertEquals("Alice Tan", details.getCustomerName());
        assertEquals("91234567", details.getPhone());
        assertEquals(LocalDate.of(2026, 10, 20), details.getDueDate());
        assertEquals(8, details.getSize());
        assertEquals("Dark Chocolate", details.getFlavour());
        assertEquals("Strawberry Cream", details.getFilling());
    }

    @Test
    public void parseAdd_reorderedFields_returnsAllDetails() {
        Parser.AddOrderDetails details = Parser.parseAdd(
                "add fi/Strawberry Cream f/Dark Chocolate s/8 d/20-10-2026 p/91234567 n/Alice Tan");

        assertEquals("Alice Tan", details.getCustomerName());
        assertEquals("91234567", details.getPhone());
        assertEquals(LocalDate.of(2026, 10, 20), details.getDueDate());
        assertEquals(8, details.getSize());
        assertEquals("Dark Chocolate", details.getFlavour());
        assertEquals("Strawberry Cream", details.getFilling());
    }

    @Test
    public void parseAdd_invalidDate_throwsException() {
        assertThrows(IllegalArgumentException.class,
                () -> Parser.parseAdd(VALID_INPUT.replace("20-10-2026", "31-02-2026")));
        assertThrows(IllegalArgumentException.class,
                () -> Parser.parseAdd(VALID_INPUT.replace("20-10-2026", "2026-10-20")));
    }

    @Test
    public void parseAdd_missingFields_throwsException() {
        String[] fields = {"n/Alice Tan ", "p/91234567 ", "d/20-10-2026 ",
                           "s/8 ", "f/Dark Chocolate ", "fi/Strawberry Cream"};
        for (String field : fields) {
            assertThrows(IllegalArgumentException.class,
                    () -> Parser.parseAdd(VALID_INPUT.replace(field, "")));
        }
    }

    @Test
    public void parseAdd_emptyFields_throwsException() {
        String[] values = {"Alice Tan", "91234567", "20-10-2026", "8", "Dark Chocolate", "Strawberry Cream"};
        for (String value : values) {
            assertThrows(IllegalArgumentException.class,
                    () -> Parser.parseAdd(VALID_INPUT.replace(value, "")));
        }
    }

    @Test
    public void parseAdd_invalidSizes_throwsException() {
        for (String size : new String[] {"7", "large"}) {
            assertThrows(IllegalArgumentException.class,
                    () -> Parser.parseAdd(VALID_INPUT.replace("s/8", "s/" + size)));
        }
    }

    @Test
    public void parseAdd_allowedSizes_returnsSize() {
        for (int size : new int[] {6, 8, 10}) {
            assertEquals(size, Parser.parseAdd(VALID_INPUT.replace("s/8", "s/" + size)).getSize());
        }
    }

    @Test
    public void parseAdd_duplicateField_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> Parser.parseAdd(VALID_INPUT + " n/Bob"));
        assertThrows(IllegalArgumentException.class, () -> Parser.parseAdd(VALID_INPUT + " fi/Vanilla"));
    }

    @Test
    public void parseAdd_unknownField_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> Parser.parseAdd(VALID_INPUT + " x/Extra"));
    }

    @Test
    public void parseAdd_invalidCommand_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> Parser.parseAdd(""));
        assertThrows(IllegalArgumentException.class, () -> Parser.parseAdd("list"));
        assertThrows(IllegalArgumentException.class, () -> Parser.parseAdd(VALID_INPUT.replace("add ", "add extra ")));
    }
}
