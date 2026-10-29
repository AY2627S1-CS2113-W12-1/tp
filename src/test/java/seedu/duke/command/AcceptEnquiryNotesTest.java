package seedu.duke.command;

//@@author Kaishhhh

import static org.junit.jupiter.api.Assertions.assertEquals;
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

import seedu.duke.Duke;
import seedu.duke.model.Cake;
import seedu.duke.model.Customer;
import seedu.duke.model.Order;
import seedu.duke.model.OrderList;
import seedu.duke.model.OrderStatus;
import seedu.duke.storage.Storage;

/**
 * Tests that notes on an enquiry are kept when the enquiry is accepted.
 */
public class AcceptEnquiryNotesTest {
    @TempDir
    private Path tempDir;

    @Test
    public void execute_enquiryWithNotes_copiesNotesToConfirmedOrder() {
        OrderList orders = new OrderList();
        Order enquiry = new Order("E001", new Customer("Cara", "92222222"),
                new Cake(8, "Mocha", "Cream"), LocalDate.of(2030, 12, 1), OrderStatus.ENQUIRY);
        enquiry.setNotes("No nuts");
        orders.add(enquiry);

        new AcceptEnquiryCommand("E001").execute(orders);

        assertEquals("No nuts", orders.getById("O001").getNotes());
    }

    @Test
    public void run_noteThenAccept_keepsNoteAfterRestart() {
        Path dataFile = tempDir.resolve("cakequeue.txt");
        String output = runSession(dataFile,
                "enquire n/Cara p/92222222 d/01-12-2030 s/8 f/Mocha fi/Cream\n"
                        + "note E001 No nuts\n"
                        + "accept E001\n"
                        + "view O001\n"
                        + "bye\n");

        assertTrue(output.contains("Notes: No nuts"));
        assertEquals("No nuts", new Storage(dataFile).load().getById("O001").getNotes());
    }

    private String runSession(Path dataFile, String commands) {
        InputStream originalInput = System.in;
        PrintStream originalOutput = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (PrintStream capturedOutput = new PrintStream(output, true, StandardCharsets.UTF_8)) {
            System.setIn(new ByteArrayInputStream(commands.getBytes(StandardCharsets.UTF_8)));
            System.setOut(capturedOutput);
            new Duke(dataFile).run();
        } finally {
            System.setIn(originalInput);
            System.setOut(originalOutput);
        }
        return output.toString(StandardCharsets.UTF_8);
    }
}
