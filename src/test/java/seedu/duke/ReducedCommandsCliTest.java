package seedu.duke;

//@@author Eethanzyj

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.duke.exception.CakeQueueException;
import seedu.duke.model.OrderList;
import seedu.duke.model.OrderStatus;
import seedu.duke.parser.Parser;
import seedu.duke.storage.Storage;

/**
 * Tests the reduced command surface and preservation of records from earlier versions.
 */
public class ReducedCommandsCliTest {
    @TempDir
    private Path tempDir;

    @Test
    public void run_removedCommands_areRejectedWithoutChangingOrders() {
        List<String> removed = List.of("enquire", "enquiries", "accept", "reject", "note",
                "stats", "delete", "update", "search", "mode");
        String date = LocalDate.now().plusDays(10).format(DateTimeFormatter.ofPattern("dd-MM-uuuu"));
        String result = runSession("help\nadd n/Alice p/91234567 d/" + date
                + " s/8 f/Chocolate fi/Cream\n" + String.join("\n", removed)
                + "\nedit O001 s/10\nfind chocolate\ncustomer C001\nhistory C001\n"
                + "capacity 1\nmark O001 preparing\nmark O001 ready\nmark O001 collected\nbye\n");

        for (String word : removed) {
            assertTrue(result.contains("Error: Unknown baker command: " + word), word);
            assertFalse(result.contains("  " + word + " "), word);
        }
        OrderList saved = new Storage(tempDir.resolve("cakequeue.txt")).load();
        assertEquals(1, saved.size());
        assertEquals(10, saved.getById("O001").getCake().getSize());
        assertEquals(OrderStatus.COLLECTED, saved.getById("O001").getStatus());
        assertEquals("C001", saved.getById("O001").getCustomerId());
        assertTrue(result.contains("Order history for Alice (C001):"));
    }

    @Test
    public void run_legacyRecordsAndNotes_surviveAnEditAndRestart() throws IOException {
        Path file = tempDir.resolve("cakequeue.txt");
        Files.write(file, List.of(
                "O001 | QUEUED | Alice | 91234567 | 2030-12-01 | 8 | Chocolate | Cream | C007 | No nuts",
                "E001 | ENQUIRY | Bob | 92222222 | 2030-12-02 | 6 | Mocha | Cream | - | Extra candles",
                "E002 | REJECTED | Cara | 93333333 | 2030-12-03 | 6 | Vanilla | Cream | - | Earlier request"));

        String output = runSession("view E001\nfind Mocha\ncancel E001\nedit O001 s/10\nbye\n");
        assertFalse(output.contains("Error:"));
        OrderList loaded = new Storage(file).load();
        assertEquals(3, loaded.size());
        assertEquals("C007", loaded.getById("O001").getCustomerId());
        assertEquals("No nuts", loaded.getById("O001").getNotes());
        assertEquals("Extra candles", loaded.getById("E001").getNotes());
        assertEquals(OrderStatus.CANCELLED, loaded.getById("E001").getStatus());
        assertEquals(OrderStatus.REJECTED, loaded.getById("E002").getStatus());
        String restarted = runSession("view O001\ncustomer C007\nhistory C007\nbye\n");
        assertTrue(restarted.contains("Notes: No nuts"));
        assertTrue(restarted.contains("Customer ID: C007"));
        assertTrue(restarted.contains("Order history for Alice (C007):"));
    }

    @Test
    public void parseUpdate_removedAlias_isRejectedWhileEditWorks() {
        assertThrows(CakeQueueException.class, () -> Parser.parseUpdate("update O001 s/10"));
        assertEquals(10, Parser.parseUpdate("edit O001 s/10").getSize());
    }

    /**
     * Runs the CLI against this test's temporary storage file and restores console streams.
     */
    private String runSession(String commands) {
        InputStream originalInput = System.in;
        PrintStream originalOutput = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (PrintStream captured = new PrintStream(output, true, StandardCharsets.UTF_8)) {
            System.setIn(new ByteArrayInputStream(commands.getBytes(StandardCharsets.UTF_8)));
            System.setOut(captured);
            new Duke(tempDir.resolve("cakequeue.txt")).run();
        } finally {
            System.setIn(originalInput);
            System.setOut(originalOutput);
        }
        return output.toString(StandardCharsets.UTF_8);
    }
}
