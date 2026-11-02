package seedu.duke;

//@@author Eethanzyj

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.duke.model.Order;
import seedu.duke.storage.Storage;

/**
 * Checks that quoted names and cake details survive editing, saving and restarting.
 */
public class QuotedOrderCliTest {
    private static final String ADD =
            "add n/Alice Tan p/91234567 d/30-10-2030 s/8 f/Chocolate fi/Cream";

    @TempDir
    private Path tempDir;

    @Test
    public void run_quotedAddAndEdit_preservesLiteralTextAfterRestart() {
        Path file = tempDir.resolve("cakequeue.txt");
        String output = runSession(file, ADD.replace("n/Alice Tan", "n/\"John s/o Doe\"")
                + "\nview O001\nedit O001 n/\"Mary d/o Jane | Ann\\\\Tan\" "
                + "f/\"Dark Chocolate/Vanilla\" fi/\"Cream fi/extra \\\"vanilla\\\"\"\nbye\n");

        assertFalse(output.contains("Error:"));
        assertTrue(output.contains("Customer: John s/o Doe (91234567)"));
        String restarted = runSession(file, "view O001\nhistory C001\nbye\n");
        assertFalse(restarted.contains("Error:"));
        assertTrue(restarted.contains("Customer: Mary d/o Jane | Ann\\Tan (91234567)"));
        Order restored = new Storage(file).load().getById("O001");
        assertEquals("Mary d/o Jane | Ann\\Tan", restored.getCustomer().getName());
        assertEquals("Dark Chocolate/Vanilla", restored.getCake().getFlavour());
        assertEquals("Cream fi/extra \"vanilla\"", restored.getCake().getFilling());
        assertEquals("C001", restored.getCustomerId());
        assertEquals(8, restored.getCake().getSize());
    }

    @Test
    public void run_badQuotedCommands_preservesRecordsAndNextId() throws IOException {
        Path file = tempDir.resolve("cakequeue.txt");
        runSession(file, ADD + "\nbye\n");
        byte[] original = Files.readAllBytes(file);
        List<String> invalid = List.of(
                ADD.replace("n/Alice Tan", "n/\"John"),
                ADD.replace("n/Alice Tan", "n/\"John\" Doe"),
                ADD.replace("n/Alice Tan", "n/\"\""),
                "edit O001 n/\"Mary",
                "edit O001 n/\"\"",
                "edit O001 f/\"Chocolate\"fi/Cream");
        String rejected = String.join("\n", invalid) + "\n";

        String output = runSession(file, rejected + "bye\n");
        assertEquals(invalid.size(), output.lines().filter(line -> line.startsWith("Error:")).count());
        assertArrayEquals(original, Files.readAllBytes(file));
        assertEquals(1, new Storage(file).load().size());
        String recovered = runSession(file, rejected + ADD.replace("n/Alice Tan", "n/\"John s/o Doe\"") + "\nbye\n");
        assertTrue(recovered.contains("Added order O002:"));
        assertEquals(2, new Storage(file).load().size());
    }

    /** Runs the application against a temporary file and restores the console streams. */
    private String runSession(Path file, String commands) {
        InputStream originalInput = System.in;
        PrintStream originalOutput = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (PrintStream captured = new PrintStream(output, true, StandardCharsets.UTF_8)) {
            System.setIn(new ByteArrayInputStream(commands.getBytes(StandardCharsets.UTF_8)));
            System.setOut(captured);
            new Duke(file).run();
        } finally {
            System.setIn(originalInput);
            System.setOut(originalOutput);
        }
        return output.toString(StandardCharsets.UTF_8);
    }
}
