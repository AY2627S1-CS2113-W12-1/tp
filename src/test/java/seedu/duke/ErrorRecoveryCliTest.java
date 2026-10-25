package seedu.duke;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * End-to-end error-recovery tests: invalid commands in either mode must be
 * reported as {@code Error: ...} (produced by catching
 * {@link seedu.duke.exception.CakeQueueException}) without crashing the app,
 * without changing stored records, and without saving anything to disk.
 * Valid commands entered after the errors must still work in the same session.
 */
public class ErrorRecoveryCliTest {
    @TempDir
    private Path tempDir;

    @Test
    public void run_bakerInvalidCommands_recoversAndKeepsValidOrder() {
        String result = runSession(
                "baker\n"
                        + "\n"                                                        // empty input
                        + "nonsense\n"                                                // unknown command
                        + "list extra\n"                                              // args to no-arg command
                        + "add n/Alice p/1 d/31-02-2026 s/8 f/C fi/S\n"               // bad date
                        + "add n/Alice p/1 d/20-10-2026 s/7 f/C fi/S\n"               // bad size
                        + "add n/Alice p/1 d/20-10-2026 s/8 f/C\n"                    // missing field
                        + "view O999\n"                                               // unknown record
                        + "edit O999 n/Bob\n"                                         // edit unknown
                        + "mark O999 ready\n"                                         // mark unknown
                        + "accept O001\n"                                             // accept an order ID
                        + "add n/Alice Tan p/91234567 d/20-10-2026 s/8 f/Chocolate fi/Strawberry\n"
                        + "list\n"
                        + "bye\n");

        assertTrue(result.contains("Error: Please enter a command."));
        assertTrue(result.contains("Error: Unknown baker command: nonsense"));
        assertTrue(result.contains("Error: Use: list"));
        assertTrue(result.contains("Error: Date must use DD-MM-YYYY and be valid."));
        assertTrue(result.contains("Error: Cake size must be 6, 8, or 10 inches."));
        assertTrue(result.contains("Error: Order not found: O999"));
        // Exactly one order survived: the single valid add at the end.
        assertTrue(result.contains("Added order O001: Alice Tan"));
        assertTrue(result.contains("1. O001: Alice Tan (91234567)"));
        assertTrue(!result.contains("O002"));
        assertTrue(result.contains("Bye. Hope to see you again soon!"));
    }

    @Test
    public void run_customerInvalidCommands_recoversAndKeepsEnquiry() {
        String result = runSession(
                "customer\n"
                        + "add n/Alice p/1 d/20-10-2026 s/8 f/C fi/S\n"               // baker-only command
                        + "track E001 p/0000\n"                                       // nothing to track yet
                        + "enquire n/Alice Tan p/91234567 d/20-10-2026 s/8 f/Chocolate fi/Strawberry\n"
                        + "track E001 p/0000\n"                                       // wrong phone
                        + "edit E001 p/0000 n/Alicia\n"                               // edit with wrong phone
                        + "cancel E001 p/0000\n"                                      // cancel with wrong phone
                        + "cancel O001 p/91234567\n"                                  // cancel an order ID
                        + "track E001 p/91234567\n"
                        + "mine p/91234567\n"
                        + "bye\n");

        assertTrue(result.contains("Error: Unknown customer command: add"));
        assertTrue(result.contains("Error: Order not found: E001"));
        assertTrue(result.contains("Error: Phone number does not match this record."));
        assertTrue(result.contains("Error: Use: cancel ENQUIRY_ID p/PHONE"));
        // The failed edit/cancel attempts left the enquiry untouched and trackable.
        assertTrue(result.contains("Enquiry ID: E001"));
        assertTrue(result.contains("Customer: Alice Tan (91234567)"));
        assertTrue(result.contains("Status: ENQUIRY"));
        assertTrue(result.contains("Bye. Hope to see you again soon!"));
    }

    @Test
    public void run_invalidModeThenValidMode_recovers() {
        String result = runSession("\nowner\n3\ncustomer\nhelp\nbye\n");

        assertTrue(result.contains("Error: Choose a mode: baker or customer"));
        assertTrue(result.contains("Customer mode."));
        assertTrue(result.contains("Customer commands:"));
    }

    @Test
    public void run_inputEndsWithoutBye_exitsCleanly() {
        // EOF without "bye" (and even without any mode) must end without an exception.
        assertTrue(runSession("baker\nlist\n").contains("You have no upcoming orders."));
        assertTrue(runSession("").contains("Choose a mode: baker or customer"));
    }

    /**
     * Runs one full app session against a fresh temporary data file.
     */
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
