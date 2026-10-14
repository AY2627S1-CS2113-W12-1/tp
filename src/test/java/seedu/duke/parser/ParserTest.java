package seedu.duke.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/**
 * Tests parsing and validation of Add Order, View Order, and Edit Order input.
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
    /**
     * Tests that an update command can contain multiple fields.
     */
    @Test
    public void parseUpdate_multipleFields_returnsCorrectDetails() {
        Parser.UpdateOrderDetails details =
                Parser.parseUpdate("update O001 n/Bob Tan s/10 f/Vanilla");

        // Check that the Order ID and supplied fields were parsed correctly.
        assertEquals("O001", details.getOrderId());
        assertEquals("Bob Tan", details.getName());
        assertEquals(10, details.getSize());
        assertEquals("Vanilla", details.getFlavour());

        // Fields not supplied should remain null.
        assertNull(details.getPhone());
        assertNull(details.getDueDate());
        assertNull(details.getFilling());
    }

    /**
     * Tests that updating only one field is allowed.
     */
    @Test
    public void parseUpdate_singleField_returnsCorrectDetails() {
        Parser.UpdateOrderDetails details =
                Parser.parseUpdate("update O002 d/25-10-2026");

        assertEquals("O002", details.getOrderId());
        assertEquals(LocalDate.of(2026, 10, 25), details.getDueDate());
        assertNull(details.getName());
        assertNull(details.getSize());
    }

    /**
     * Tests that invalid dates are rejected.
     */
    @Test
    public void parseUpdate_invalidDate_throwsException() {
        assertThrows(IllegalArgumentException.class,
                () -> Parser.parseUpdate("update O001 d/31-02-2026"));

        assertThrows(IllegalArgumentException.class,
                () -> Parser.parseUpdate("update O001 d/2026-10-25"));
    }

    /**
     * Tests that unsupported cake sizes are rejected.
     */
    @Test
    public void parseUpdate_invalidSize_throwsException() {
        assertThrows(IllegalArgumentException.class,
                () -> Parser.parseUpdate("update O001 s/7"));

        assertThrows(IllegalArgumentException.class,
                () -> Parser.parseUpdate("update O001 s/large"));
    }

    /**
     * Tests that the user must provide at least one field.
     */
    @Test
    public void parseUpdate_missingFields_throwsException() {
        assertThrows(IllegalArgumentException.class,
                () -> Parser.parseUpdate("update O001"));
    }

    /**
     * Tests that empty field values are rejected.
     */
    @Test
    public void parseUpdate_emptyField_throwsException() {
        assertThrows(IllegalArgumentException.class,
                () -> Parser.parseUpdate("update O001 n/"));

        assertThrows(IllegalArgumentException.class,
                () -> Parser.parseUpdate("update O001 s/"));
    }

    /**
     * Tests that the same field cannot be supplied twice.
     */
    @Test
    public void parseUpdate_duplicateField_throwsException() {
        assertThrows(IllegalArgumentException.class,
                () -> Parser.parseUpdate("update O001 n/Bob n/Alice"));

        assertThrows(IllegalArgumentException.class,
                () -> Parser.parseUpdate("update O001 s/8 s/10"));
    }

    /**
     * Tests that unknown field prefixes are rejected.
     */
    @Test
    public void parseUpdate_unknownField_throwsException() {
        assertThrows(IllegalArgumentException.class,
                () -> Parser.parseUpdate("update O001 x/Extra"));
    }

    /**
     * Tests that invalid Order IDs are rejected.
     */
    @Test
    public void parseUpdate_invalidOrderId_throwsException() {
        assertThrows(IllegalArgumentException.class,
                () -> Parser.parseUpdate("update 001 n/Bob"));

        assertThrows(IllegalArgumentException.class,
                () -> Parser.parseUpdate("update O1 n/Bob"));
    }

    @Test
    public void parseUpdate_editAliasAndLowercaseId_returnsCanonicalId() {
        Parser.UpdateOrderDetails details = Parser.parseUpdate("EDIT\to001\ts/10 d/22-10-2026");

        assertEquals("O001", details.getOrderId());
        assertEquals(10, details.getSize());
        assertEquals(LocalDate.of(2026, 10, 22), details.getDueDate());
        assertNull(details.getName());
    }

    @Test
    public void parseUpdate_invalidEdit_preservesValidation() {
        for (String input : new String[] {"edit O001", "edit O001 s/7", "edit O001 n/",
            "edit O001 d/31-02-2026", "edit O001 n/Bob n/Alice", "edit O001 x/Extra"}) {
            assertThrows(IllegalArgumentException.class, () -> Parser.parseUpdate(input));
        }
    }

    @Test
    public void parseView_lowercaseId_returnsCanonicalId() {
        assertEquals("O001", Parser.parseView("VIEW\to001"));
    }

    @Test
    public void parseView_invalidInput_throwsUsefulError() {
        for (String input : new String[] {"", "view", "view O1", "view 1", "view O001 extra", "viewpoint O001"}) {
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> Parser.parseView(input));
            assertEquals("Use: view ORDER_ID", exception.getMessage());
        }
        assertThrows(IllegalArgumentException.class, () -> Parser.parseView(null));
    }
}
