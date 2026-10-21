package seedu.duke;

//@@author HammerHead-os

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.duke.command.AcceptEnquiryCommand;
import seedu.duke.command.EnquireCommand;
import seedu.duke.model.OrderList;
import seedu.duke.model.OrderStatus;

/**
 * Tests baker and customer mode selection and enquiry workflow.
 */
public class ModeCliTest {
    @TempDir
    private Path tempDir;


    @Test
    public void run_customerEnquireThenBakerAccept_createsOrder() {
        String result = runSession(
                "customer\n"
                        + "enquire n/Alice Tan p/91234567 d/20-10-2026 s/8 f/Chocolate fi/Strawberry\n"
                        + "mine p/91234567\n"
                        + "track E001 p/91234567\n"
                        + "edit E001 p/91234567 s/10\n"
                        + "mode baker\n"
                        + "enquiries\n"
                        + "accept E001\n"
                        + "list\n"
                        + "bye\n");

        assertTrue(result.contains("Customer mode."));
        assertTrue(result.contains("Submitted enquiry E001"));
        assertTrue(result.contains("Records for phone 91234567:"));
        assertTrue(result.contains("Enquiry ID: E001"));
        assertTrue(result.contains("Updated enquiry E001"));
        assertTrue(result.contains("Switching to baker mode."));
        assertTrue(result.contains("Accepted enquiry E001 as order O001"));
        assertTrue(result.contains("1. O001: Alice Tan (91234567) | Due: 20-10-2026"
                + " | Size: 10 inches | Flavour: Chocolate | Filling: Strawberry [QUEUED]"));
    }

    @Test
    public void run_bakerEnquiriesAcceptRejectCancel_work() {
        String result = runSession(
                "baker\n"
                        + "add n/Direct p/81111111 d/21-10-2026 s/6 f/Vanilla fi/Cream\n"
                        + "enquiries\n"
                        + "cancel O001\n"
                        + "list\n"
                        + "bye\n");

        assertTrue(result.contains("Baker mode."));
        assertTrue(result.contains("You have no pending enquiries."));
        assertTrue(result.contains("Cancelled O001"));
        assertTrue(result.contains("You have no upcoming orders."));
    }

    @Test
    public void run_bakerRejectEnquiryAndHelp_work() {
        String result = runSession(
                "customer\n"
                        + "enquire n/Alice Tan p/91234567 d/20-10-2026 s/8 f/Chocolate fi/Strawberry\n"
                        + "mode baker\n"
                        + "help\n"
                        + "reject E001\n"
                        + "enquiries\n"
                        + "bye\n");

        assertTrue(result.contains("Baker commands:"));
        assertTrue(result.contains("reject ENQUIRY_ID"));
        assertTrue(result.contains("Rejected enquiry E001"));
        assertTrue(result.contains("You have no pending enquiries."));
    }

    @Test
    public void run_customerCancelOwnEnquiryAndHelp_work() {
        String result = runSession(
                "customer\n"
                        + "help\n"
                        + "enquire n/Alice Tan p/91234567 d/20-10-2026 s/8 f/Chocolate fi/Strawberry\n"
                        + "cancel E001 p/91234567\n"
                        + "mine p/91234567\n"
                        + "bye\n");

        assertTrue(result.contains("Customer commands:"));
        assertTrue(result.contains("Cancelled E001"));
        // A cancelled enquiry is still listed for the owner, with its new status.
        assertTrue(result.contains("[CANCELLED]"));
    }

    @Test
    public void run_acceptEnquiry_convertsToOrder() {
        OrderList orders = new OrderList();
        String enquire = new EnquireCommand(
                "Alice", "91234567", java.time.LocalDate.of(2026, 10, 20),
                8, "Chocolate", "Strawberry").execute(orders);
        String accepted = new AcceptEnquiryCommand("E001").execute(orders);

        assertTrue(enquire.contains("E001"));
        assertTrue(accepted.contains("Accepted enquiry E001 as order O001"));
        assertTrue(orders.getById("O001").getStatus() == OrderStatus.QUEUED);
    }

    @Test
    public void run_customerCannotUseBakerCommands() {
        String result = runSession("customer\nadd n/Alice p/1 d/20-10-2026 s/8 f/C fi/S\nbye\n");
        assertTrue(result.contains("Error: Unknown customer command: add"));
    }

    @Test
    public void run_invalidMode_promptsAgain() {
        String result = runSession("owner\nbaker\nbye\n");
        assertTrue(result.contains("Error: Choose a mode: baker or customer"));
        assertTrue(result.contains("Baker mode."));
    }

    @Test
    public void run_ordersSavedInOneSession_areLoadedInTheNext() {
        String first = runSession("baker\n"
                + "add n/Alice Tan p/91234567 d/20-10-2026 s/8 f/Chocolate fi/Strawberry\n"
                + "mark O001 preparing\nbye\n");
        // A second launch against the same data file must see the saved order.
        String second = runSession("baker\nlist\nview O001\n"
                + "add n/Bob Lim p/81234567 d/21-10-2026 s/6 f/Vanilla fi/Cream\nbye\n");

        assertTrue(first.contains("Updated order O001"));
        assertTrue(first.contains("[PREPARING]"));
        assertTrue(second.contains("Loaded 1 saved record(s)."));
        assertTrue(second.contains("1. O001: Alice Tan (91234567)"));
        assertTrue(second.contains("Status: PREPARING"));
        // The ID counter must continue past the loaded record, not reuse O001.
        assertTrue(second.contains("Added order O002: Bob Lim"));
    }

    @Test
    public void run_markSkippingAStep_showsErrorAndKeepsStatus() {
        String result = runSession("baker\n"
                + "add n/Alice Tan p/91234567 d/20-10-2026 s/8 f/Chocolate fi/Strawberry\n"
                + "mark O001 collected\nview O001\nbye\n");

        assertTrue(result.contains("Error: Cannot change order O001 from QUEUED to COLLECTED."));
        assertTrue(result.contains("Status: QUEUED"));
    }

    private String runSession(String commands) {
        InputStream originalInput = System.in;
        PrintStream originalOutput = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (PrintStream capturedOutput = new PrintStream(output, true, StandardCharsets.UTF_8)) {
            System.setIn(new ByteArrayInputStream(commands.getBytes(StandardCharsets.UTF_8)));
            System.setOut(capturedOutput);
            new Duke(tempDir.resolve("cakequeue.txt")).run();
        } finally {
            System.setIn(originalInput);
            System.setOut(originalOutput);
        }
        return output.toString(StandardCharsets.UTF_8);
    }
}
