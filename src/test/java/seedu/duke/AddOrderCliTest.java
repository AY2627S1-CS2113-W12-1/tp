package seedu.duke;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

/**
 * Tests the Add Order command through the application input and output.
 */
public class AddOrderCliTest {
    @Test
    public void run_invalidThenValidOrders_showsErrorAndSequentialIds() {
        String commands = "add n/Alice Tan d/20-10-2026\n"
                + "add n/Alice Tan p/91234567 d/20-10-2026 s/8 f/Chocolate fi/Strawberry\n"
                + "add fi/Cream f/Vanilla s/6 d/21-10-2026 p/81234567 n/Bob Lim\nbye\n";
        InputStream originalInput = System.in;
        PrintStream originalOutput = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        try (PrintStream capturedOutput = new PrintStream(output, true, StandardCharsets.UTF_8)) {
            System.setIn(new ByteArrayInputStream(commands.getBytes(StandardCharsets.UTF_8)));
            System.setOut(capturedOutput);
            new Duke().run();
        } finally {
            System.setIn(originalInput);
            System.setOut(originalOutput);
        }

        String displayed = output.toString(StandardCharsets.UTF_8);
        assertTrue(displayed.contains("Error: Missing value for p/"));
        assertTrue(displayed.contains("Added order O001: Alice Tan (91234567) | Due: 20-10-2026"
                + " | Size: 8 inches | Flavour: Chocolate | Filling: Strawberry [QUEUED]"));
        assertTrue(displayed.contains("Added order O002: Bob Lim (81234567) | Due: 21-10-2026"
                + " | Size: 6 inches | Flavour: Vanilla | Filling: Cream [QUEUED]"));
        assertTrue(displayed.contains("Bye. Hope to see you again soon!"));
    }
}
