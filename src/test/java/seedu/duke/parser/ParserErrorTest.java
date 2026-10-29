package seedu.duke.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import seedu.duke.exception.CakeQueueException;
import seedu.duke.model.OrderStatus;

/**
 * Comprehensive error-handling tests for {@link Parser}.
 *
 * <p>Every malformed-input path must throw {@link CakeQueueException} (never a
 * raw JDK exception such as {@code NullPointerException} or
 * {@code NumberFormatException}) with a message that tells the user the
 * correct format. Valid counterparts are asserted where a parser previously
 * had no positive test, so both sides of each validation branch are covered.</p>
 */
public class ParserErrorTest {
    private static final String VALID_ADD =
            "add n/Alice Tan p/91234567 d/20-10-2028 s/8 f/Chocolate fi/Strawberry";

    // ---------- single-ID commands ----------

    @Test
    public void parseAcceptRejectBakerCancel_valid_returnsCanonicalId() {
        assertEquals("E001", Parser.parseAccept("accept e001"));
        assertEquals("E012", Parser.parseReject("REJECT E012"));
        assertEquals("O001", Parser.parseBakerCancel("cancel o001"));
        assertEquals("E001", Parser.parseBakerCancel("cancel E001"));
    }

    @Test
    public void parseSingleIdCommands_wrongIdTypeOrShape_throwsCakeQueueException() {
        // accept/reject only take enquiry IDs; baker cancel takes either kind.
        assertThrows(CakeQueueException.class, () -> Parser.parseAccept("accept O001"));
        assertThrows(CakeQueueException.class, () -> Parser.parseReject("reject O001"));
        assertThrows(CakeQueueException.class, () -> Parser.parseAccept("accept E1"));
        assertThrows(CakeQueueException.class, () -> Parser.parseAccept("accept"));
        assertThrows(CakeQueueException.class, () -> Parser.parseAccept("accept E001 extra"));
        assertThrows(CakeQueueException.class, () -> Parser.parseAccept(""));
        assertThrows(CakeQueueException.class, () -> Parser.parseAccept(null));
        assertThrows(CakeQueueException.class, () -> Parser.parseReject(null));
        assertThrows(CakeQueueException.class, () -> Parser.parseBakerCancel("cancel X001"));
        assertThrows(CakeQueueException.class, () -> Parser.parseBakerCancel(null));

        CakeQueueException exception = assertThrows(CakeQueueException.class,
                () -> Parser.parseAccept("accept O001"));
        assertEquals("Use: accept ENQUIRY_ID", exception.getMessage());
    }

    // ---------- mark / view ----------

    @Test
    public void parseMark_allValidTargets_returnsDetails() {
        assertEquals(OrderStatus.PREPARING, Parser.parseMark("mark O001 preparing").getTargetStatus());
        assertEquals(OrderStatus.READY, Parser.parseMark("mark o002 READY").getTargetStatus());
        assertEquals(OrderStatus.COLLECTED, Parser.parseMark("MARK O003 collected").getTargetStatus());
        assertEquals("O002", Parser.parseMark("mark o002 READY").getOrderId());
    }

    @Test
    public void parseMark_nonProductionStatusesAndBadShapes_throwsCakeQueueException() {
        for (String input : new String[] {"mark O001 queued", "mark O001 enquiry", "mark O001 cancelled",
            "mark O001 rejected", "mark O001 banana", "mark E001 ready", "mark O1 ready",
            "mark O001", "mark O001 ready extra", "mark", ""}) {
            CakeQueueException exception = assertThrows(CakeQueueException.class,
                    () -> Parser.parseMark(input), "input: " + input);
            assertEquals("Use: mark ORDER_ID preparing|ready|collected", exception.getMessage());
        }
        assertThrows(CakeQueueException.class, () -> Parser.parseMark(null));
    }

    @Test
    public void parseView_enquiryId_returnsCanonicalId() {
        assertEquals("E001", Parser.parseView("view e001"));
    }

    // ---------- add ----------

    @Test
    public void parseAdd_leapDayAndBoundarySizes_areAccepted() {
        assertEquals(LocalDate.of(2028, 2, 29),
                Parser.parseAdd(VALID_ADD.replace("20-10-2028", "29-02-2028")).getDueDate());
        assertEquals(6, Parser.parseAdd(VALID_ADD.replace("s/8", "s/6")).getSize());
        assertEquals(10, Parser.parseAdd(VALID_ADD.replace("s/8", "s/10")).getSize());
    }

    @Test
    public void parseAdd_errorMessages_identifyTheProblem() {
        CakeQueueException unknown = assertThrows(CakeQueueException.class,
                () -> Parser.parseAdd(VALID_ADD + " x/Extra"));
        assertEquals("Unknown field: x/", unknown.getMessage());

        CakeQueueException duplicate = assertThrows(CakeQueueException.class,
                () -> Parser.parseAdd(VALID_ADD + " n/Bob"));
        assertEquals("Duplicate field: n/", duplicate.getMessage());

        CakeQueueException missing = assertThrows(CakeQueueException.class,
                () -> Parser.parseAdd(VALID_ADD.replace("fi/Strawberry", "")));
        assertTrue(missing.getMessage().contains("fi/"));
    }

    // ---------- baker edit/update ----------

    @Test
    public void parseUpdate_allFields_returnsEveryValue() {
        Parser.UpdateOrderDetails details = Parser.parseUpdate(
                "edit O001 n/Bob Tan p/81111111 d/25-10-2026 s/6 f/Vanilla fi/Cream");

        assertEquals("O001", details.getOrderId());
        assertEquals("Bob Tan", details.getName());
        assertEquals("81111111", details.getPhone());
        assertEquals(LocalDate.of(2026, 10, 25), details.getDueDate());
        assertEquals(6, details.getSize());
        assertEquals("Vanilla", details.getFlavour());
        assertEquals("Cream", details.getFilling());
    }

    @Test
    public void parseUpdate_nullAndBlank_throwsCakeQueueException() {
        assertThrows(CakeQueueException.class, () -> Parser.parseUpdate(null));
        assertThrows(CakeQueueException.class, () -> Parser.parseUpdate(""));
        assertThrows(CakeQueueException.class, () -> Parser.parseUpdate("   "));
    }
}
