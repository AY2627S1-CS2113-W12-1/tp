package seedu.duke.storage;

//@@author Kaishhhh

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.duke.model.Cake;
import seedu.duke.model.Customer;
import seedu.duke.model.Order;
import seedu.duke.model.OrderList;
import seedu.duke.model.OrderStatus;

/**
 * Tests that notes and customer IDs are saved together, and that files from every earlier format still load.
 */
public class NotesAndCustomerIdStorageTest {
    @TempDir
    private Path tempDir;

    @Test
    public void saveAndLoad_keepsBothNotesAndCustomerId() {
        Path file = tempDir.resolve("cakequeue.txt");
        OrderList orders = new OrderList();
        Order order = new Order("O001", new Customer("Alice Tan", "91234567"),
                new Cake(8, "Chocolate", "Cream"), LocalDate.of(2030, 12, 1), OrderStatus.QUEUED);
        order.setNotes("No nuts | extra candles");
        orders.add(order);

        new Storage(file).save(orders);
        Order loaded = new Storage(file).load().getById("O001");

        assertEquals("C001", loaded.getCustomerId());
        assertEquals("No nuts | extra candles", loaded.getNotes());
    }

    @Test
    public void load_olderEightAndNineFieldLines_stillLoadWithoutNotes() throws IOException {
        Path file = tempDir.resolve("cakequeue.txt");
        Files.write(file, List.of(
                "O001 | QUEUED | Alice | 91234567 | 2030-12-01 | 8 | Chocolate | Cream",
                "O002 | QUEUED | Bob | 81234567 | 2030-12-02 | 6 | Vanilla | Cream | C007"));

        OrderList loaded = new Storage(file).load();

        assertEquals(2, loaded.size());
        assertEquals("", loaded.getById("O001").getNotes());
        assertEquals("C007", loaded.getById("O002").getCustomerId());
        assertEquals("", loaded.getById("O002").getNotes());
    }

    @Test
    public void save_writesNotesAfterCustomerId() throws IOException {
        Path file = tempDir.resolve("cakequeue.txt");
        OrderList orders = new OrderList();
        Order order = new Order("O001", new Customer("Alice", "91234567"),
                new Cake(8, "Chocolate", "Cream"), LocalDate.of(2030, 12, 1), OrderStatus.QUEUED);
        orders.add(order);

        new Storage(file).save(orders);
        String line = Files.readAllLines(file).get(0);

        assertTrue(line.endsWith("| C001 | -"), line);
    }
}
