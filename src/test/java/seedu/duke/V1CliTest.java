package seedu.duke;

//@@author Eethanzyj

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
 * Tests the integrated V1 commands through the application input and output.
 * All due dates use 2028 to remain in the future.
 */
public class V1CliTest {
    private static final String ADD_ALICE =
            "add n/Alice Tan p/91234567 d/22-10-2028 s/8 f/Chocolate fi/Strawberry\n";
    private static final String ADD_BOB =
            "add fi/Cream f/Vanilla s/6 d/20-10-2028 p/81234567 n/Bob Lim\n";

    @TempDir
    private Path tempDir;

    @Test
    public void run_addListViewAndEdit_workTogether() {
        String result = runSession("list\n" + ADD_ALICE + ADD_BOB
                + "list\nview o001\nEDIT\to001\td/19-10-2028 s/10\nview O001\n"
                + "update o002 p/81230000\nview o002\nlist\nbye\n");

        assertFalse(result.contains("Error:"));
        assertTrue(result.contains("You have no upcoming orders."));
        assertTrue(result.contains("1. O002: Bob Lim (81234567) | Due: 20-10-2028"));
        assertTrue(result.contains("2. O001: Alice Tan (91234567) | Due: 22-10-2028"));
        assertTrue(result.contains("Order ID: O001"));
        assertTrue(result.contains("Due date: 19-10-2028"));
        assertTrue(result.contains("Size: 10 inches"));
        assertTrue(result.contains("Customer: Bob Lim (81230000)"));
        assertTrue(result.contains("1. O001: Alice Tan (91234567) | Due: 19-10-2028"
                + " | Size: 10 inches | Flavour: Chocolate | Filling: Strawberry [QUEUED]"));
        assertTrue(result.contains("2. O002: Bob Lim (81230000) | Due: 20-10-2028"
                + " | Size: 6 inches | Flavour: Vanilla | Filling: Cream [QUEUED]"));
        assertTrue(result.contains("Bye. Hope to see you again soon!"));
    }

    @Test
    public void run_invalidCommands_recoversWithoutChangingOrders() {
        String result = runSession("" + ADD_ALICE
                + "\nlist extra\nview\nview O999\nviewpoint O001\nedit O001 s/7\n"
                + "edit O001 d/31-02-2028\nedit O999 s/10\nbye extra\nLIST\nbye\n");

        assertTrue(result.contains("Error: Please enter a command."));
        assertTrue(result.contains("Error: Use: list"));
        assertTrue(result.contains("Error: Use: view ORDER_ID|ENQUIRY_ID"));
        assertTrue(result.contains("Error: Order not found: O999"));
        assertTrue(result.contains("Error: Unknown baker command: viewpoint"));
        assertTrue(result.contains("Error: Cake size must be 6, 8, or 10 inches."));
        assertTrue(result.contains("Error: Date must use DD-MM-YYYY and be valid."));
        assertTrue(result.contains("Error: Use: bye"));
        assertTrue(result.contains("1. O001: Alice Tan (91234567) | Due: 22-10-2028"
                + " | Size: 8 inches | Flavour: Chocolate | Filling: Strawberry [QUEUED]"));
        assertTrue(result.contains("Bye. Hope to see you again soon!"));
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
