package seedu.duke.parser;

//@@author Eethanzyj

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import seedu.duke.exception.CakeQueueException;

/**
 * Checks quoted field values without weakening the existing add and edit validation rules.
 */
public class QuotedFieldParserTest {
    private static final String ADD =
            "add n/Alice Tan p/91234567 d/30-10-2030 s/8 f/Chocolate fi/Cream";

    @Test
    public void parseAdd_quotedNames_preservesPrefixLikeText() {
        for (String name : List.of("John s/o Doe", "Mary d/o Jane", "Alice n/Junior")) {
            Parser.AddOrderDetails details = Parser.parseAdd(ADD.replace("n/Alice Tan", "n/\"" + name + "\""));
            assertEquals(name, details.getCustomerName());
            assertEquals(8, details.getSize());
            assertEquals("91234567", details.getPhone());
        }
    }

    @Test
    public void parseAdd_reorderedQuotedFields_trimsAndValidatesValues() {
        Parser.AddOrderDetails details = Parser.parseAdd(
                "add fi/\" Cream \" f/\"Dark Chocolate/Vanilla\" s/\"8\" d/\"30-10-2030\" "
                        + "p/\" +65 9123-4567 \" n/ \" John s/o Doe \"");
        assertEquals("John s/o Doe", details.getCustomerName());
        assertEquals("+65 9123-4567", details.getPhone());
        assertEquals(LocalDate.of(2030, 10, 30), details.getDueDate());
        assertEquals(8, details.getSize());
        assertEquals("Dark Chocolate/Vanilla", details.getFlavour());
        assertEquals("Cream", details.getFilling());
    }

    @Test
    public void parseUpdate_quotedValues_keepsOnlyRequestedChanges() {
        Parser.UpdateOrderDetails details = Parser.parseUpdate(
                "edit o001 n/\"Mary d/o Jane\" f/\"Dark Chocolate/Vanilla\" fi/\"Cream x/extra\"");
        assertEquals("O001", details.getOrderId());
        assertEquals("Mary d/o Jane", details.getName());
        assertEquals("Dark Chocolate/Vanilla", details.getFlavour());
        assertEquals("Cream x/extra", details.getFilling());
        assertNull(details.getPhone());
        assertNull(details.getDueDate());
        assertNull(details.getSize());
    }

    @Test
    public void parse_quotedEscapes_preservesQuotesAndBackslashes() {
        String value = "\"John \\\"Jay\\\" s/o Doe \\\\ Tan\"";
        String expected = "John \"Jay\" s/o Doe \\ Tan";
        assertEquals(expected, Parser.parseAdd(ADD.replace("n/Alice Tan", "n/" + value)).getCustomerName());
        assertEquals(expected, Parser.parseUpdate("edit O001 n/" + value).getName());
        assertEquals("Mary\\", Parser.parseUpdate("edit O001 n/\"Mary\\\\\"").getName());
        assertEquals("Mary\\Tan", Parser.parseUpdate("edit O001 n/\"Mary\\Tan\"").getName());
    }

    @Test
    public void parse_unquotedEmbeddedQuotes_preservesExistingText() {
        String name = "Mary \"Ann\" O'Connor\\Tan";
        assertEquals(name, Parser.parseAdd(ADD.replace("n/Alice Tan", "n/" + name)).getCustomerName());
        assertEquals(name, Parser.parseUpdate("edit O001 n/" + name).getName());
    }

    @Test
    public void parse_malformedQuotes_rejectsIncompleteOrTrailingText() {
        for (String value : List.of("\"John", "\"John\" Doe", "\"John\"n/Bob", "\"John\" \"Doe\"")) {
            assertThrows(CakeQueueException.class,
                    () -> Parser.parseAdd(ADD.replace("n/Alice Tan", "n/" + value)));
            assertThrows(CakeQueueException.class, () -> Parser.parseUpdate("edit O001 n/" + value));
        }
    }

    @Test
    public void parse_quotedInvalidValues_keepsRequiredAndDomainChecks() {
        Map<String, String> invalidFields = Map.of(
                "n/Alice Tan", "n/\"   \"",
                "p/91234567", "p/\"1234567\"",
                "d/30-10-2030", "d/\"31-02-2030\"",
                "s/8", "s/\"7\"",
                "f/Chocolate", "f/\"\"",
                "fi/Cream", "fi/\"\"");
        for (Map.Entry<String, String> field : invalidFields.entrySet()) {
            assertThrows(CakeQueueException.class,
                    () -> Parser.parseAdd(ADD.replace(field.getKey(), field.getValue())));
            assertThrows(CakeQueueException.class, () -> Parser.parseUpdate("edit O001 " + field.getValue()));
        }
    }

    @Test
    public void parse_prefixesOutsideQuotes_stillRejectsDuplicatesAndUnknownFields() {
        assertThrows(CakeQueueException.class, () -> Parser.parseAdd(ADD + " n/\"Bob s/o Tan\""));
        assertThrows(CakeQueueException.class, () -> Parser.parseAdd(ADD + " x/\"Extra n/text\""));
        assertThrows(CakeQueueException.class, () -> Parser.parseUpdate("edit O001 n/\"Alice s/o Tan\" n/Bob"));
        assertThrows(CakeQueueException.class, () -> Parser.parseUpdate("edit O001 n/\"Alice s/o Tan\" x/Extra"));
    }
}
