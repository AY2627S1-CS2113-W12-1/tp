package seedu.duke;

//@@author HammerHead-os

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.duke.command.AcceptEnquiryCommand;
import seedu.duke.model.Cake;
import seedu.duke.model.Customer;
import seedu.duke.model.Order;
import seedu.duke.model.OrderList;
import seedu.duke.model.OrderStatus;

/**
 * Integration tests for the baker CLI and the enquiry/order workflow.
 * All due dates use 2028 to remain in the future.
 */
public class ModeCliTest {
    @TempDir
    private Path tempDir;

    /**
     * Seeds an enquiry directly into an OrderList (bypassing the parser, so date can be in
     * the past), then verifies AcceptEnquiryCommand promotes it to a confirmed order.
     */
    @Test
    public void acceptEnquiry_directOrderListSeed_createsOrder() {
        OrderList orders = new OrderList();
        orders.add(new Order(orders.generateEnquiryId(),
                new Customer("Alice", "91234567"),
                new Cake(8, "Chocolate", "Strawberry"),
                LocalDate.of(2028, 10, 20),
                OrderStatus.ENQUIRY));

        String accepted = new AcceptEnquiryCommand("E001").execute(orders);

        assertTrue(accepted.contains("Accepted enquiry E001 as order O001"));
        assertTrue(orders.getById("O001").getStatus() == OrderStatus.QUEUED);
    }

    @Test
    public void run_bakerCancelAndEnquiries_work() {
        String result = runSession(
                "add n/Direct p/81111111 d/21-10-2028 s/6 f/Vanilla fi/Cream\n"
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
    public void run_bakerHelpShowsExpectedCommands() {
        String result = runSession("help\nbye\n");

        assertTrue(result.contains("Baker commands:"));
        assertTrue(result.contains("reject ENQUIRY_ID"));
        assertTrue(result.contains("accept ENQUIRY_ID"));
        assertTrue(result.contains("mark ORDER_ID preparing|ready|collected"));
    }

    @Test
    public void run_ordersSavedInOneSession_areLoadedInTheNext() {
        String first = runSession(
                "add n/Alice Tan p/91234567 d/20-10-2028 s/8 f/Chocolate fi/Strawberry\n"
                        + "mark O001 preparing\nbye\n");
        String second = runSession("list\nview O001\n"
                + "add n/Bob Lim p/81234567 d/21-10-2028 s/6 f/Vanilla fi/Cream\nbye\n");

        assertTrue(first.contains("Updated order O001"));
        assertTrue(first.contains("[PREPARING]"));
        assertTrue(second.contains("Loaded 1 saved record(s)."));
        assertTrue(second.contains("1. O001: Alice Tan (91234567)"));
        assertTrue(second.contains("Status: PREPARING"));
        assertTrue(second.contains("Added order O002: Bob Lim"));
    }

    @Test
    public void run_markSkippingAStep_showsErrorAndKeepsStatus() {
        String result = runSession(
                "add n/Alice Tan p/91234567 d/20-10-2028 s/8 f/Chocolate fi/Strawberry\n"
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
