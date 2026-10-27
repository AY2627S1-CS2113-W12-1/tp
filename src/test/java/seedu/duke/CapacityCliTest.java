package seedu.duke;

//@@author Kaishhhh

import static org.junit.jupiter.api.Assertions.assertFalse;
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
 * Tests daily capacity warnings through the baker command-line interface.
 */
public class CapacityCliTest {
    @TempDir
    private Path tempDir;

    @Test
    public void run_addBeyondCapacity_warnsOnlyWhenOverLimit() {
        String result = runSession(
                "baker\n"
                        + "capacity 1\n"
                        + "add n/Alice p/91234567 d/21-10-2026 s/8 f/Chocolate fi/Strawberry\n"
                        + "add n/Bob p/81111111 d/21-10-2026 s/6 f/Vanilla fi/Cream\n"
                        + "bye\n");

        assertTrue(result.contains("Warning: 21-10-2026 now has 2 orders, over your daily capacity of 1."));
        assertFalse(result.contains("now has 1 orders"));
    }

    @Test
    public void run_editDateOrAcceptEnquiry_warnsWhenDateOverLimit() {
        String result = runSession(
                "customer\n"
                        + "enquire n/Cara p/92222222 d/21-10-2026 s/8 f/Mocha fi/Cream\n"
                        + "mode baker\n"
                        + "capacity 1\n"
                        + "add n/Alice p/91234567 d/21-10-2026 s/8 f/Chocolate fi/Strawberry\n"
                        + "add n/Bob p/81111111 d/22-10-2026 s/6 f/Vanilla fi/Cream\n"
                        + "edit O002 d/21-10-2026\n"
                        + "accept E001\n"
                        + "bye\n");

        assertTrue(result.contains("Updated order O002"));
        assertTrue(result.contains("now has 2 orders, over your daily capacity of 1."));
        assertTrue(result.contains("Accepted enquiry E001"));
        assertTrue(result.contains("now has 3 orders, over your daily capacity of 1."));
    }

    @Test
    public void run_collectedOrder_stopsCountingTowardsCapacity() {
        String result = runSession(
                "baker\n"
                        + "capacity 1\n"
                        + "add n/Alice p/91234567 d/21-10-2026 s/8 f/Chocolate fi/Strawberry\n"
                        + "mark O001 preparing\n"
                        + "mark O001 ready\n"
                        + "mark O001 collected\n"
                        + "add n/Bob p/81111111 d/21-10-2026 s/6 f/Vanilla fi/Cream\n"
                        + "bye\n");

        assertFalse(result.contains("Warning:"));
    }

    @Test
    public void run_invalidCapacityOrCustomerMode_recoversWithError() {
        String result = runSession(
                "baker\n"
                        + "capacity 0\n"
                        + "capacity 1 2\n"
                        + "capacity\n"
                        + "mode customer\n"
                        + "capacity 3\n"
                        + "bye\n");

        assertTrue(result.contains("Error: Daily capacity must be a whole number from 1 to 50."));
        assertTrue(result.contains("Error: Use: capacity [NUMBER]"));
        assertTrue(result.contains("Daily capacity is 5 orders per day."));
        assertTrue(result.contains("Error: Unknown customer command: capacity"));
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
