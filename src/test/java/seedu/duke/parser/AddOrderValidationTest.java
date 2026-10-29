package seedu.duke.parser;

//@@author Eethanzyj

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import org.junit.jupiter.api.Test;

import seedu.duke.exception.CakeQueueException;

/**
 * Tests actionable Add Order errors and preservation of customer-supplied text.
 */
public class AddOrderValidationTest {
    private static final String VALID_INPUT =
            "add n/Alice Tan p/91234567 d/30-10-2026 s/8 f/Chocolate fi/Cream";

    @Test
    public void parseAdd_missingFields_explainsWhatToProvide() {
        Map<String, String> missingFields = Map.of(
                "n/Alice Tan ", "a customer name using n/NAME",
                "p/91234567 ", "a phone number using p/PHONE",
                "d/30-10-2026 ", "a due date using d/DD-MM-YYYY",
                "s/8 ", "a cake size using s/6, s/8, or s/10",
                "f/Chocolate ", "a cake flavour using f/FLAVOUR",
                "fi/Cream", "a cake filling using fi/FILLING");

        for (Map.Entry<String, String> missing : missingFields.entrySet()) {
            CakeQueueException exception = assertThrows(CakeQueueException.class,
                    () -> Parser.parseAdd(VALID_INPUT.replace(missing.getKey(), "")));
            assertTrue(exception.getMessage().contains("Please provide " + missing.getValue() + "."));
        }
    }

    @Test
    public void parseAdd_whitespaceOnlyName_requestsCustomerName() {
        CakeQueueException exception = assertThrows(CakeQueueException.class,
                () -> Parser.parseAdd(VALID_INPUT.replace("n/Alice Tan", "n/   ")));

        assertEquals("Missing value for n/. Please provide a customer name using n/NAME.",
                exception.getMessage());
    }

    @Test
    public void parseAdd_punctuationAndUnicode_preservesNameAndTrimsEdges() {
        Parser.AddOrderDetails details = Parser.parseAdd(
                VALID_INPUT.replace("n/Alice Tan", "n/   Élodie O'Connor-Tan   "));

        assertEquals("Élodie O'Connor-Tan", details.getCustomerName());
    }

    @Test
    public void parseAdd_formattedPhone_preservesCurrentNonEmptyPhonePolicy() {
        Parser.AddOrderDetails details = Parser.parseAdd(
                VALID_INPUT.replace("p/91234567", "p/   +65 9123-4567   "));

        assertEquals("+65 9123-4567", details.getPhone());
    }
}
