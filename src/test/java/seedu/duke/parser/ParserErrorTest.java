package seedu.duke.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import seedu.duke.UserMode;
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
            "add n/Alice Tan p/91234567 d/20-10-2026 s/8 f/Chocolate fi/Strawberry";
    private static final String VALID_ENQUIRE =
            "enquire n/Alice Tan p/91234567 d/20-10-2026 s/8 f/Chocolate fi/Strawberry";

    // ---------- mode parsing ----------

    @Test
    public void parseMode_validWordsAndAliases_returnsMode() {
        assertEquals(UserMode.BAKER, Parser.parseMode("baker"));
        assertEquals(UserMode.BAKER, Parser.parseMode("  BAKER "));
        assertEquals(UserMode.BAKER, Parser.parseMode("1"));
        assertEquals(UserMode.BAKER, Parser.parseMode("b"));
        assertEquals(UserMode.CUSTOMER, Parser.parseMode("customer"));
        assertEquals(UserMode.CUSTOMER, Parser.parseMode("2"));
        assertEquals(UserMode.CUSTOMER, Parser.parseMode("c"));
    }

    @Test
    public void parseMode_nullBlankOrUnknown_throwsCakeQueueException() {
        for (String input : new String[] {"", "   ", "owner", "bakery", "3", "baker customer"}) {
            CakeQueueException exception = assertThrows(CakeQueueException.class,
                    () -> Parser.parseMode(input), "input: " + input);
            assertEquals("Choose a mode: baker or customer", exception.getMessage());
        }
        assertThrows(CakeQueueException.class, () -> Parser.parseMode(null));
    }

    @Test
    public void parseModeCommand_valid_returnsMode() {
        assertEquals(UserMode.BAKER, Parser.parseModeCommand("mode baker"));
        assertEquals(UserMode.CUSTOMER, Parser.parseModeCommand("MODE customer"));
        assertEquals(UserMode.BAKER, Parser.parseModeCommand("mode 1"));
    }

    @Test
    public void parseModeCommand_invalid_throwsCakeQueueException() {
        for (String input : new String[] {"", "mode", "mode owner", "baker", "mode baker extra", "switch baker"}) {
            CakeQueueException exception = assertThrows(CakeQueueException.class,
                    () -> Parser.parseModeCommand(input), "input: " + input);
            assertEquals("Use: mode baker|customer", exception.getMessage());
        }
        assertThrows(CakeQueueException.class, () -> Parser.parseModeCommand(null));
    }

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

    // ---------- mine / track / customer cancel ----------

    @Test
    public void parseMine_valid_returnsPhone() {
        assertEquals("91234567", Parser.parseMine("mine p/91234567"));
        assertEquals("91234567", Parser.parseMine("MINE p/91234567"));
    }

    @Test
    public void parseMine_invalid_throwsCakeQueueException() {
        for (String input : new String[] {"", "mine", "mine 91234567", "mine n/Alice",
            "mine p/", "mine p/91234567 p/81111111", "mine x/1 p/91234567"}) {
            assertThrows(CakeQueueException.class, () -> Parser.parseMine(input), "input: " + input);
        }
        assertThrows(CakeQueueException.class, () -> Parser.parseMine(null));
    }

    @Test
    public void parseTrack_valid_returnsIdAndPhone() {
        Parser.TrackDetails details = Parser.parseTrack("track e001 p/91234567");

        assertEquals("E001", details.getRecordId());
        assertEquals("91234567", details.getPhone());
    }

    @Test
    public void parseTrack_invalid_throwsCakeQueueException() {
        for (String input : new String[] {"", "track", "track E001", "track E001 91234567",
            "track X001 p/91234567", "track E1 p/91234567", "track E001 n/Alice",
            "track E001 p/91234567 d/20-10-2026"}) {
            CakeQueueException exception = assertThrows(CakeQueueException.class,
                    () -> Parser.parseTrack(input), "input: " + input);
            assertEquals("Use: track ID p/PHONE", exception.getMessage());
        }
        // An empty phone value gets the more specific missing-value message.
        assertEquals("Missing value for p/", assertThrows(CakeQueueException.class,
                () -> Parser.parseTrack("track E001 p/")).getMessage());
        assertThrows(CakeQueueException.class, () -> Parser.parseTrack(null));
    }

    @Test
    public void parseCustomerCancel_valid_returnsIdAndPhone() {
        Parser.CustomerCancelDetails details = Parser.parseCustomerCancel("cancel e001 p/91234567");

        assertEquals("E001", details.getRecordId());
        assertEquals("91234567", details.getPhone());
    }

    @Test
    public void parseCustomerCancel_invalid_throwsCakeQueueException() {
        for (String input : new String[] {"", "cancel", "cancel E001", "cancel O001 p/91234567",
            "cancel E001 91234567", "cancel E001 n/Alice"}) {
            CakeQueueException exception = assertThrows(CakeQueueException.class,
                    () -> Parser.parseCustomerCancel(input), "input: " + input);
            assertEquals("Use: cancel ENQUIRY_ID p/PHONE", exception.getMessage());
        }
        assertEquals("Missing value for p/", assertThrows(CakeQueueException.class,
                () -> Parser.parseCustomerCancel("cancel E001 p/")).getMessage());
        assertThrows(CakeQueueException.class, () -> Parser.parseCustomerCancel(null));
    }

    // ---------- enquire (six-field) error paths not covered by parseAdd tests ----------

    @Test
    public void parseEnquire_valid_returnsAllDetails() {
        Parser.AddOrderDetails details = Parser.parseEnquire(VALID_ENQUIRE);

        assertEquals("Alice Tan", details.getCustomerName());
        assertEquals("91234567", details.getPhone());
        assertEquals(LocalDate.of(2026, 10, 20), details.getDueDate());
        assertEquals(8, details.getSize());
    }

    @Test
    public void parseEnquire_invalid_throwsCakeQueueExceptionWithEnquireUsage() {
        CakeQueueException exception = assertThrows(CakeQueueException.class,
                () -> Parser.parseEnquire("enquire"));
        assertEquals("Use: enquire n/NAME p/PHONE d/DATE s/SIZE f/FLAVOUR fi/FILLING",
                exception.getMessage());
        // A present-but-incomplete field list names the first missing field.
        assertEquals("Missing value for p/. Please provide a phone number using p/PHONE.",
                assertThrows(CakeQueueException.class,
                    () -> Parser.parseEnquire("enquire n/Alice")).getMessage());

        assertThrows(CakeQueueException.class, () -> Parser.parseEnquire(null));
        assertThrows(CakeQueueException.class, () -> Parser.parseEnquire(""));
        assertThrows(CakeQueueException.class,
                () -> Parser.parseEnquire(VALID_ENQUIRE.replace("20-10-2026", "29-02-2026")));
        assertThrows(CakeQueueException.class,
                () -> Parser.parseEnquire(VALID_ENQUIRE.replace("s/8", "s/9")));
        // The add command word must not be accepted by the enquire parser (and vice versa).
        assertThrows(CakeQueueException.class, () -> Parser.parseEnquire(VALID_ADD));
        assertThrows(CakeQueueException.class, () -> Parser.parseAdd(VALID_ENQUIRE));
    }

    @Test
    public void parseAdd_leapDayAndBoundarySizes_areAccepted() {
        assertEquals(LocalDate.of(2028, 2, 29),
                Parser.parseAdd(VALID_ADD.replace("20-10-2026", "29-02-2028")).getDueDate());
        assertEquals(6, Parser.parseAdd(VALID_ADD.replace("s/8", "s/6")).getSize());
        assertEquals(10, Parser.parseAdd(VALID_ADD.replace("s/8", "s/10")).getSize());
    }

    @Test
    public void parseAdd_errorMessages_identifyTheProblem() {
        CakeQueueException unknown = assertThrows(CakeQueueException.class,
                () -> Parser.parseAdd(VALID_ADD + " x/Extra"));
        assertEquals("Unknown field: x/. Supported prefixes: n/, p/, d/, s/, f/, fi/.", unknown.getMessage());

        CakeQueueException duplicate = assertThrows(CakeQueueException.class,
                () -> Parser.parseAdd(VALID_ADD + " n/Bob"));
        assertEquals("Duplicate field: n/. Supply each field once.", duplicate.getMessage());

        CakeQueueException missing = assertThrows(CakeQueueException.class,
                () -> Parser.parseAdd(VALID_ADD.replace("fi/Strawberry", "")));
        assertTrue(missing.getMessage().contains("fi/"));
    }

    // ---------- customer edit ----------

    @Test
    public void parseCustomerEdit_valid_returnsPhoneAndOnlySuppliedFields() {
        Parser.CustomerEditDetails details =
                Parser.parseCustomerEdit("edit e001 p/91234567 n/Alicia Tan s/10");

        assertEquals("E001", details.getEnquiryId());
        assertEquals("91234567", details.getPhone());
        assertEquals("Alicia Tan", details.getName());
        assertEquals(10, details.getSize());
        assertNull( details.getDueDate());
        assertNull( details.getFlavour());
    }

    @Test
    public void parseCustomerEdit_invalid_throwsCakeQueueException() {
        String usage = "Use: edit ENQUIRY_ID p/PHONE [n/NAME] [d/DATE] [s/SIZE] [f/FLAVOUR] [fi/FILLING]";
        for (String input : new String[] {"", "edit", "edit E001", "edit O001 p/91234567 n/Alicia",
            "edit E001 n/Alicia", "edit E001 p/91234567", "edit E1 p/91234567 n/Alicia",
            "edit E001 p/91234567 d/31-02-2026", "edit E001 p/91234567 s/7",
            "edit E001 p/91234567 s/large", "edit E001 p/91234567 n/",
            "edit E001 p/91234567 n/Alicia n/Bob", "edit E001 p/91234567 x/Extra"}) {
            CakeQueueException exception = assertThrows(CakeQueueException.class,
                    () -> Parser.parseCustomerEdit(input), "input: " + input);
            // Field-specific errors (bad date/size/unknown/duplicate) carry their own message.
            assertTrue(exception.getMessage().equals(usage)
                    || exception.getMessage().contains("Date must")
                    || exception.getMessage().contains("Cake size")
                    || exception.getMessage().contains("field")
                    || exception.getMessage().contains("Missing value"), "input: " + input);
        }
        assertThrows(CakeQueueException.class, () -> Parser.parseCustomerEdit(null));
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
