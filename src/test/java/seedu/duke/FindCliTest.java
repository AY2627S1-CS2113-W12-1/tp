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
 * Tests the baker {@code find} command through the command-line interface.
 */
public class FindCliTest {
    @TempDir
    private Path tempDir;

    @Test
    public void run_findAfterAdding_showsOnlyMatchingOrders() {
        String result = runSession(
                ""
                        + "add n/Alice Tan p/91234567 d/20-10-2030 s/8 f/Chocolate fi/Strawberry\n"
                        + "add n/Bob Lim p/81111111 d/21-10-2030 s/6 f/Vanilla fi/Cream\n"
                        + "find alice\n"
                        + "bye\n");

        assertTrue(result.contains("Orders matching \"alice\":"));
        assertTrue(result.contains("1. O001: Alice Tan"));
        assertFalse(result.contains("1. O002"));
    }

    @Test
    public void run_findNoMatchOrMissingKeyword_recoversAndKeepsRunning() {
        String result = runSession(
                ""
                        + "add n/Alice Tan p/91234567 d/20-10-2030 s/8 f/Chocolate fi/Strawberry\n"
                        + "find Zed\n"
                        + "find\n"
                        + "list\n"
                        + "bye\n");

        assertTrue(result.contains("No orders found matching \"Zed\"."));
        assertTrue(result.contains("Error: Use: find KEYWORD"));
        assertTrue(result.contains("Here are your upcoming orders:"));
    }

    @Test
    public void run_findIsListedInBakerHelp() {
        String result = runSession("help\nbye\n");

        assertTrue(result.contains("find KEYWORD"));
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
