package seedu.duke.storage;

//@@author anannya-mk

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
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
 * Tests that customer IDs are saved with orders and that older save files still load.
 */
public class CustomerIdStorageTest {
    @TempDir
    private Path tempDir;

    private Order order(String id, String name, String phone) {
        return new Order(id, new Customer(name, phone), new Cake(8, "Chocolate", "Cream"),
                LocalDate.of(2026, 12, 1), OrderStatus.QUEUED);
    }

    @Test
    public void saveAndLoad_keepsCustomerIdsEvenAfterPhoneEdit() {
        Path file = tempDir.resolve("cakequeue.txt");
        OrderList orders = new OrderList();
        orders.add(order("O001", "Alice", "91111111"));
        orders.add(order("O002", "Bob", "92222222"));
        orders.update(orders.getById("O001").updatedCopy(new Customer("Alice", "98888888"), null, null));
        new Storage(file).save(orders);

        OrderList loaded = new Storage(file).load();
        assertEquals("C001", loaded.getById("O001").getCustomerId());
        assertEquals("C002", loaded.getById("O002").getCustomerId());
        loaded.add(order("O003", "Carl", "93333333"));
        assertEquals("C003", loaded.getById("O003").getCustomerId());
    }

    @Test
    public void load_oldFileWithoutCustomerIds_assignsIdsByPhone() throws IOException {
        Path file = tempDir.resolve("cakequeue.txt");
        Files.write(file, List.of(
                "O001 | QUEUED | Alice | 91111111 | 2026-12-01 | 8 | Chocolate | Cream",
                "O002 | QUEUED | Bob | 92222222 | 2026-12-01 | 8 | Chocolate | Cream",
                "O003 | QUEUED | Alice | 91111111 | 2026-12-02 | 6 | Vanilla | Cream",
                "E001 | ENQUIRY | Carl | 93333333 | 2026-12-03 | 6 | Vanilla | Cream"));

        OrderList loaded = new Storage(file).load();
        assertEquals("C001", loaded.getById("O001").getCustomerId());
        assertEquals("C002", loaded.getById("O002").getCustomerId());
        assertEquals("C001", loaded.getById("O003").getCustomerId());
        assertNull(loaded.getById("E001").getCustomerId());
    }

    @Test
    public void load_badCustomerId_skipsLine() throws IOException {
        Path file = tempDir.resolve("cakequeue.txt");
        Files.write(file, List.of(
                "O001 | QUEUED | Alice | 91111111 | 2026-12-01 | 8 | Chocolate | Cream | X1"));

        assertTrue(new Storage(file).load().getAll().isEmpty());
    }
}
