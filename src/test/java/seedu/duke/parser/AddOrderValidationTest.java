package seedu.duke.parser;

//@@author Eethanzyj

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.Map;

import org.junit.jupiter.api.Test;

import seedu.duke.exception.CakeQueueException;

/**
 * Tests actionable Add Order errors and preservation of customer-supplied text.
 */
public class AddOrderValidationTest {
    private static final String VALID_INPUT =
            "add n/Alice Tan p/91234567 d/30-10-2030 s/8 f/Chocolate fi/Cream";

    @Test
    public void parseAdd_missingFields_explainsWhatToProvide() {
        Map<String, String> missingFields = Map.of(
                "n/Alice Tan ", "a customer name using n/NAME",
                "p/91234567 ", "a phone number using p/PHONE",
                "d/30-10-2030 ", "a due date using d/DD-MM-YYYY",
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
    public void parseAdd_dueDateBoundary_acceptsTodayAndRejectsYesterday() {
        LocalDate today = LocalDate.now();
        for (LocalDate accepted : new LocalDate[]{today, today.plusDays(1)}) {
            String input = VALID_INPUT.replace("30-10-2030", accepted.format(Parser.DATE_FORMATTER));
            assertEquals(accepted, Parser.parseAdd(input).getDueDate());
        }
        String pastInput = VALID_INPUT.replace("30-10-2030", today.minusDays(1).format(Parser.DATE_FORMATTER));
        CakeQueueException exception = assertThrows(CakeQueueException.class, () -> Parser.parseAdd(pastInput));
        assertEquals("Due date cannot be in the past.", exception.getMessage());
    }

    @Test
    public void parseAdd_yearOutsideFourDigits_rejectsDate() {
        for (String date : new String[]{"30-10-+10000", "30-10-02030", "30-10-203"}) {
            CakeQueueException exception = assertThrows(CakeQueueException.class,
                    () -> Parser.parseAdd(VALID_INPUT.replace("30-10-2030", date)));
            assertEquals("Date must use DD-MM-YYYY and be valid.", exception.getMessage());
        }
        assertEquals(LocalDate.of(9999, 12, 31),
                Parser.parseAdd(VALID_INPUT.replace("30-10-2030", "31-12-9999")).getDueDate());
    }

    @Test
    public void parseAdd_formattedPhone_preservesCurrentNonEmptyPhonePolicy() {
        Parser.AddOrderDetails details = Parser.parseAdd(
                VALID_INPUT.replace("p/91234567", "p/   +65 9123-4567   "));

        assertEquals("+65 9123-4567", details.getPhone());
    }
}
