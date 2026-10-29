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
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.duke.model.Order;
import seedu.duke.model.OrderList;
import seedu.duke.model.OrderStatus;
import seedu.duke.storage.Storage;

/**
 * Checks Add Order data integrity across rejected commands, edits, and restarts.
 */
public class AddOrderReliabilityTest {
    private static final String VALID_ADD =
            "add n/Alice Tan p/91234567 d/30-10-2026 s/8 f/Chocolate fi/Strawberry";

    @TempDir
    private Path tempDir;

    @Test
    public void run_invalidAdds_preservesSavedDataAndNextId() throws IOException {
        Path dataFile = tempDir.resolve("cakequeue.txt");
        runSession(dataFile, "baker\n" + VALID_ADD + "\nbye\n");
        byte[] originalData = Files.readAllBytes(dataFile);
        List<String> invalidAdds = List.of(
                VALID_ADD.replace("n/Alice Tan ", ""),
                VALID_ADD.replace("n/Alice Tan", "n/   "),
                VALID_ADD.replace("p/91234567", "p/   "),
                VALID_ADD.replace("30-10-2026", "31-02-2026"),
                VALID_ADD.replace("s/8", "s/7"),
                VALID_ADD + " n/Bob",
                VALID_ADD + " x/Extra");
        String rejectedCommands = String.join("\n", invalidAdds) + "\n";

        String rejectedOutput = runSession(dataFile, "baker\n" + rejectedCommands + "bye\n");

        assertEquals(invalidAdds.size(), rejectedOutput.lines()
                .filter(line -> line.startsWith("Error:")).count());
        assertFalse(rejectedOutput.contains("Added order"));
        assertArrayEquals(originalData, Files.readAllBytes(dataFile));
        assertEquals(1, new Storage(dataFile).load().size());

        // A valid add after the errors in the same session must still receive O002.
        String recoveredOutput = runSession(dataFile,
                "baker\n" + rejectedCommands + VALID_ADD + "\nbye\n");
        assertTrue(recoveredOutput.contains("Added order O002:"));
        OrderList restored = new Storage(dataFile).load();
        assertEquals(2, restored.size());
        assertEquals("Alice Tan", restored.getById("O001").getCustomer().getName());
        assertEquals("Alice Tan", restored.getById("O002").getCustomer().getName());
    }

    @Test
    public void run_nameAndFormattedPhone_survivesRestart() {
        Path dataFile = tempDir.resolve("cakequeue.txt");
        String input = VALID_ADD.replace("n/Alice Tan", "n/   Élodie O'Connor-Tan   ")
                .replace("p/91234567", "p/   +65 9123-4567   ");

        runSession(dataFile, "baker\n" + input + "\nbye\n");
        String restartedOutput = runSession(dataFile, "baker\nview O001\nbye\n");
        Order restored = new Storage(dataFile).load().getById("O001");

        assertTrue(restartedOutput.contains("Customer: Élodie O'Connor-Tan (+65 9123-4567)"));
        assertEquals("Élodie O'Connor-Tan", restored.getCustomer().getName());
        assertEquals("+65 9123-4567", restored.getCustomer().getPhone());
        assertEquals(LocalDate.of(2026, 10, 30), restored.getDueDate());
        assertEquals(8, restored.getCake().getSize());
        assertEquals("Chocolate", restored.getCake().getFlavour());
        assertEquals("Strawberry", restored.getCake().getFilling());
        assertEquals(OrderStatus.QUEUED, restored.getStatus());
    }

    @Test
    public void run_editOneCustomerName_leavesOtherOrderUnchanged() {
        Path dataFile = tempDir.resolve("cakequeue.txt");
        runSession(dataFile, "baker\n" + VALID_ADD + "\n" + VALID_ADD
                + "\nedit O001 n/Alicia Tan\nbye\n");
        OrderList restored = new Storage(dataFile).load();
        Order edited = restored.getById("O001");
        Order untouched = restored.getById("O002");

        assertEquals("Alicia Tan", edited.getCustomer().getName());
        assertEquals("Alice Tan", untouched.getCustomer().getName());
        assertEquals("91234567", edited.getCustomer().getPhone());
        assertEquals("91234567", untouched.getCustomer().getPhone());
        assertEquals(untouched.getDueDate(), edited.getDueDate());
        assertEquals(untouched.getCake().getSize(), edited.getCake().getSize());
        assertEquals(untouched.getCake().getFlavour(), edited.getCake().getFlavour());
        assertEquals(untouched.getCake().getFilling(), edited.getCake().getFilling());
        assertEquals(OrderStatus.QUEUED, edited.getStatus());
        assertEquals(OrderStatus.QUEUED, untouched.getStatus());
    }

    @Test
    public void run_saveFails_warnsAndKeepsOrderForSession() throws IOException {
        Path blockedParent = tempDir.resolve("blocked");
        Files.writeString(blockedParent, "A file cannot be used as a directory.");
        Path dataFile = blockedParent.resolve("cakequeue.txt");

        String output = runSession(dataFile, "baker\n" + VALID_ADD + "\nview O001\nbye\n");

        assertTrue(output.contains("Added order O001:"));
        assertTrue(output.contains("Warning: Could not save your data. "
                + "Your changes are kept for this session only."));
        assertTrue(output.contains("Order ID: O001"));
        assertTrue(output.contains("Customer: Alice Tan (91234567)"));
        assertFalse(Files.exists(dataFile));
        assertEquals("A file cannot be used as a directory.", Files.readString(blockedParent));
    }

    @Test
    public void run_enquiryAndAdd_preservesCustomerIdsAndCapacityRules() {
        Path dataFile = tempDir.resolve("cakequeue.txt");
        String enquiry = VALID_ADD.replace("add n/Alice Tan p/91234567", "enquire n/Bob Lim p/81234567");
        String invalidEnquiry = enquiry.replace("n/Bob Lim ", "");

        String output = runSession(dataFile, "customer\n" + invalidEnquiry + "\n" + enquiry
                + "\nmode baker\ncapacity 1\n" + VALID_ADD
                + "\naccept E001\ncustomer C002\nhistory C002\nbye\n");

        assertTrue(output.contains("Error: Missing value for n/. "
                + "Please provide a customer name using n/NAME."));
        assertTrue(output.contains("Customer ID: C001 (new customer)"));
        assertTrue(output.contains("Accepted enquiry E001 as order O002"));
        assertTrue(output.contains("Name: Bob Lim"));
        assertTrue(output.contains("Order history for Bob Lim (C002):"));
        // The pending enquiry does not fill capacity; only accepting it causes a warning.
        assertEquals(1, output.lines().filter(line -> line.startsWith("Warning: 30-10-2026")).count());

        OrderList loaded = new Storage(dataFile).load();
        assertEquals(2, loaded.size());
        assertEquals("C001", loaded.getById("O001").getCustomerId());
        assertEquals("C002", loaded.getById("O002").getCustomerId());
        assertEquals("Alice Tan", loaded.getById("O001").getCustomer().getName());
        assertEquals("Bob Lim", loaded.getById("O002").getCustomer().getName());

        String restarted = runSession(dataFile, "baker\ncapacity\ncustomer C002\nbye\n");
        assertTrue(restarted.contains("Daily capacity is 5 orders per day."));
        assertTrue(restarted.contains("Name: Bob Lim"));
    }

    /**
     * Runs a session against the specified temporary file and restores console streams.
     */
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
