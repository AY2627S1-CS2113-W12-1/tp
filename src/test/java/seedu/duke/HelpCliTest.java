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

import seedu.duke.command.HelpCommand;
import seedu.duke.model.Cake;
import seedu.duke.model.Customer;
import seedu.duke.model.Order;
import seedu.duke.model.OrderList;
import seedu.duke.model.OrderStatus;
import seedu.duke.storage.Storage;

/**
 * Checks contextual help through the CLI and exercises the exact copyable examples it displays.
 */
public class HelpCliTest {
    @TempDir
    private Path tempDir;

    @Test
    public void run_helpTopicsAndErrors_preservesDataAndContinues() throws IOException {
        Path file = tempDir.resolve("cakequeue.txt");
        Storage storage = new Storage(file);
        OrderList orders = new OrderList();
        orders.add(new Order("O007", new Customer("Existing Customer", "81234567"),
                new Cake(6, "Vanilla", "Cream"), LocalDate.of(2030, 10, 30)));
        assertTrue(storage.save(orders));
        byte[] original = Files.readAllBytes(file);
        List<String> topics = List.of("add", "list", "view", "edit", "cancel", "find", "customer", "history",
                "capacity", "mark", "help", "bye");
        StringBuilder input = new StringBuilder("help\n");
        for (String topic : topics) {
            input.append("help ").append(topic).append('\n');
        }
        input.append("HELP\t ADD\nhelp search\nhelp add extra\nview O007\nbye\n");

        String output = runSession(file, input.toString());

        for (String topic : topics) {
            assertTrue(output.contains("Help: " + topic), topic);
        }
        assertEquals(2, output.lines().filter(line -> line.startsWith("Error:")).count());
        assertTrue(output.contains("Unknown help topic: search. Type help to see available commands."));
        assertTrue(output.contains("Error: Use: help [COMMAND]"));
        assertTrue(output.contains("Order ID: O007"));
        assertFalse(output.contains("Added order O"));
        assertFalse(output.contains("Cancelled O"));
        assertArrayEquals(original, Files.readAllBytes(file));
        assertEquals(1, storage.load().size());
    }

    @Test
    public void run_copyableHelpExamples_workWithTheExistingCommands() {
        Path file = tempDir.resolve("cakequeue.txt");
        StringBuilder input = new StringBuilder();
        for (String topic : List.of("add", "view", "edit", "find", "mark", "customer", "history", "capacity",
                "list", "cancel", "help", "bye")) {
            input.append(exampleFor(topic)).append('\n');
        }

        String output = runSession(file, input.toString());

        assertFalse(output.contains("Error:"));
        assertTrue(output.contains("Added order O001:"));
        assertTrue(output.contains("Help: add"));
        assertTrue(output.contains("Bye. Hope to see you again soon!"));
        OrderList restored = new Storage(file).load();
        assertEquals(1, restored.size());
        Order order = restored.getById("O001");
        assertEquals("Alice Tan", order.getCustomer().getName());
        assertEquals("91234567", order.getCustomer().getPhone());
        assertEquals(10, order.getCake().getSize());
        assertEquals("C001", order.getCustomerId());
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
    }

    /**
     * Takes the line after the example heading exactly as the user would copy it.
     */
    private String exampleFor(String topic) {
        String text = new HelpCommand().execute(topic);
        return text.split("Example:" + System.lineSeparator(), 2)[1].split("\\R", 2)[0];
    }

    /**
     * Runs a session against a temporary file and restores console streams afterwards.
     */
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
