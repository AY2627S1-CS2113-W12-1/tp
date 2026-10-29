package seedu.duke.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/**
 * Tests parsing and validation of Add Order, View Order, and Edit Order input.
 * All due dates use 2028 to remain in the future.
 */
public class ParserTest {
    private static final String VALID_INPUT = "add n/Alice Tan p/91234567 d/20-10-2028 "
            + "s/8 f/Dark Chocolate fi/Strawberry Cream";

    @Test
    public void parseAdd_validInput_returnsAllDetails() {
        Parser.AddOrderDetails details = Parser.parseAdd(VALID_INPUT);

        assertEquals("Alice Tan", details.getCustomerName());
        assertEquals("91234567", details.getPhone());
        assertEquals(LocalDate.of(2028, 10, 20), details.getDueDate());
        assertEquals(8, details.getSize());
        assertEquals("Dark Chocolate", details.getFlavour());
        assertEquals("Strawberry Cream", details.getFilling());
    }

    @Test
    public void parseAdd_reorderedFields_returnsAllDetails() {
        Parser.AddOrderDetails details = Parser.parseAdd(
                "add fi/Strawberry Cream f/Dark Chocolate s/8 d/20-10-2028 p/91234567 n/Alice Tan");

        assertEquals("Alice Tan", details.getCustomerName());
        assertEquals("91234567", details.getPhone());
        assertEquals(LocalDate.of(2028, 10, 20), details.getDueDate());
        assertEquals(8, details.getSize());
        assertEquals("Dark Chocolate", details.getFlavour());
        assertEquals("Strawberry Cream", details.getFilling());
    }

    @Test
    public void parseAdd_invalidDate_throwsException() {
        // 31-02 is always invalid regardless of year.
        assertThrows(IllegalArgumentException.class,
                () -> Parser.parseAdd(VALID_INPUT.replace("20-10-2028", "31-02-2028")));
        // Wrong format (yyyy-MM-dd instead of dd-MM-yyyy).
        assertThrows(IllegalArgumentException.class,
                () -> Parser.parseAdd(VALID_INPUT.replace("20-10-2028", "2028-10-20")));
    }

    @Test
    public void parseAdd_pastDate_throwsException() {
        // A past date must be rejected.
        assertThrows(IllegalArgumentException.class,
                () -> Parser.parseAdd(VALID_INPUT.replace("20-10-2028", "01-01-2020")));
    }

    @Test
    public void parseAdd_invalidPhone_throwsException() {
        // Phone with letters must be rejected.
        assertThrows(IllegalArgumentException.class,
                () -> Parser.parseAdd(VALID_INPUT.replace("91234567", "9123ABCD")));
        // Phone shorter than 8 digits must be rejected.
        assertThrows(IllegalArgumentException.class,
                () -> Parser.parseAdd(VALID_INPUT.replace("91234567", "1234567")));
    }

    @Test
    public void parseAdd_missingFields_throwsException() {
        String[] fields = {"n/Alice Tan ", "p/91234567 ", "d/20-10-2028 ",
                           "s/8 ", "f/Dark Chocolate ", "fi/Strawberry Cream"};
        for (String field : fields) {
            assertThrows(IllegalArgumentException.class,
                    () -> Parser.parseAdd(VALID_INPUT.replace(field, "")));
        }
    }

    @Test
    public void parseAdd_emptyFields_throwsException() {
        String[] values = {"Alice Tan", "91234567", "20-10-2028", "8",
                           "Dark Chocolate", "Strawberry Cream"};
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
        assertThrows(IllegalArgumentException.class,
                () -> Parser.parseAdd(VALID_INPUT.replace("add ", "add extra ")));
    }

    /**
     * Tests that an update command can contain multiple fields.
     * parseUpdate has no past-date check — dates are informational edits.
     */
    @Test
    public void parseUpdate_multipleFields_returnsCorrectDetails() {
        Parser.UpdateOrderDetails details =
                Parser.parseUpdate("update O001 n/Bob Tan s/10 f/Vanilla");

        assertEquals("O001", details.getOrderId());
        assertEquals("Bob Tan", details.getName());
        assertEquals(10, details.getSize());
        assertEquals("Vanilla", details.getFlavour());
        assertNull(details.getPhone());
        assertNull(details.getDueDate());
        assertNull(details.getFilling());
    }

    @Test
    public void parseUpdate_singleField_returnsCorrectDetails() {
        Parser.UpdateOrderDetails details =
                Parser.parseUpdate("update O002 d/25-10-2028");

        assertEquals("O002", details.getOrderId());
        assertEquals(LocalDate.of(2028, 10, 25), details.getDueDate());
        assertNull(details.getName());
        assertNull(details.getSize());
    }

    @Test
    public void parseUpdate_invalidDate_throwsException() {
        assertThrows(IllegalArgumentException.class,
                () -> Parser.parseUpdate("update O001 d/31-02-2028"));
        assertThrows(IllegalArgumentException.class,
                () -> Parser.parseUpdate("update O001 d/2028-10-25"));
    }

    @Test
    public void parseUpdate_invalidSize_throwsException() {
        assertThrows(IllegalArgumentException.class,
                () -> Parser.parseUpdate("update O001 s/7"));
        assertThrows(IllegalArgumentException.class,
                () -> Parser.parseUpdate("update O001 s/large"));
    }

    @Test
    public void parseUpdate_missingFields_throwsException() {
        assertThrows(IllegalArgumentException.class,
                () -> Parser.parseUpdate("update O001"));
    }

    @Test
    public void parseUpdate_emptyField_throwsException() {
        assertThrows(IllegalArgumentException.class,
                () -> Parser.parseUpdate("update O001 n/"));
        assertThrows(IllegalArgumentException.class,
                () -> Parser.parseUpdate("update O001 s/"));
    }

    @Test
    public void parseUpdate_duplicateField_throwsException() {
        assertThrows(IllegalArgumentException.class,
                () -> Parser.parseUpdate("update O001 n/Bob n/Alice"));
        assertThrows(IllegalArgumentException.class,
                () -> Parser.parseUpdate("update O001 s/8 s/10"));
    }

    @Test
    public void parseUpdate_unknownField_throwsException() {
        assertThrows(IllegalArgumentException.class,
                () -> Parser.parseUpdate("update O001 x/Extra"));
    }

    @Test
    public void parseUpdate_invalidOrderId_throwsException() {
        assertThrows(IllegalArgumentException.class,
                () -> Parser.parseUpdate("update 001 n/Bob"));
        assertThrows(IllegalArgumentException.class,
                () -> Parser.parseUpdate("update O1 n/Bob"));
    }

    @Test
    public void parseUpdate_editAliasAndLowercaseId_returnsCanonicalId() {
        Parser.UpdateOrderDetails details = Parser.parseUpdate("EDIT\to001\ts/10 d/22-10-2028");

        assertEquals("O001", details.getOrderId());
        assertEquals(10, details.getSize());
        assertEquals(LocalDate.of(2028, 10, 22), details.getDueDate());
        assertNull(details.getName());
    }

    @Test
    public void parseUpdate_invalidEdit_preservesValidation() {
        for (String input : new String[] {"edit O001", "edit O001 s/7", "edit O001 n/",
            "edit O001 d/31-02-2028", "edit O001 n/Bob n/Alice", "edit O001 x/Extra"}) {
            assertThrows(IllegalArgumentException.class, () -> Parser.parseUpdate(input));
        }
    }

    @Test
    public void parseView_lowercaseId_returnsCanonicalId() {
        assertEquals("O001", Parser.parseView("VIEW\to001"));
    }

    @Test
    public void parseView_invalidInput_throwsUsefulError() {
        for (String input : new String[] {"", "view", "view O1", "view 1",
                                          "view O001 extra", "viewpoint O001"}) {
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> Parser.parseView(input));
            assertEquals("Use: view ORDER_ID|ENQUIRY_ID", exception.getMessage());
        }
        assertThrows(IllegalArgumentException.class, () -> Parser.parseView(null));
    }
}
