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
 * End-to-end error-recovery tests: invalid commands must be reported as
 * {@code Error: ...} without crashing the app, and valid commands after errors
 * must still work. All dates use 2028 to stay in the future.
 */
public class ErrorRecoveryCliTest {
    @TempDir
    private Path tempDir;

    @Test
    public void run_invalidCommands_recoversAndKeepsValidOrder() {
        String result = runSession(
                "\n"                                                         // empty input
                + "nonsense\n"                                               // unknown command
                + "list extra\n"                                             // args on no-arg command
                + "add n/Alice p/91234567 d/31-02-2028 s/8 f/C fi/S\n"      // bad date
                + "add n/Alice p/91234567 d/20-10-2028 s/7 f/C fi/S\n"      // bad size
                + "add n/Alice p/91234567 d/20-10-2028 s/8 f/C\n"           // missing field
                + "view O999\n"                                              // unknown record
                + "edit O999 n/Bob\n"                                        // edit unknown
                + "mark O999 ready\n"                                        // mark unknown
                + "accept O001\n"                                            // accept an order (invalid)
                + "add n/Alice Tan p/91234567 d/20-10-2028 s/8 f/Chocolate fi/Strawberry\n"
                + "list\n"
                + "bye\n");

        assertTrue(result.contains("Error: Please enter a command."));
        assertTrue(result.contains("Error: Unknown baker command: nonsense"));
        assertTrue(result.contains("Error: Use: list"));
        assertTrue(result.contains("Error: Date must use DD-MM-YYYY and be valid."));
        assertTrue(result.contains("Error: Cake size must be 6, 8, or 10 inches."));
        assertTrue(result.contains("Error: Order not found: O999"));
        assertTrue(result.contains("Added order O001: Alice Tan"));
        assertTrue(result.contains("1. O001: Alice Tan (91234567)"));
        assertTrue(!result.contains("O002"));
        assertTrue(result.contains("Bye. Hope to see you again soon!"));
    }

    @Test
    public void run_inputEndsWithoutBye_exitsCleanly() {
        assertTrue(runSession("list\n").contains("You have no upcoming orders."));
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
