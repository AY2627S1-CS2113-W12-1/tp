package seedu.duke.storage;

//@@author HammerHead-os

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
 * Tests saving to and loading from the plain-text data file.
 */
public class StorageTest {
    @TempDir
    private Path tempDir;

    private Path dataFile() {
        return tempDir.resolve("data").resolve("cakequeue.txt");
    }

    private OrderList sampleOrders() {
        OrderList orders = new OrderList();
        orders.add(new Order(orders.generateOrderId(), new Customer("Alice | Tan", "91234567"),
                new Cake(8, "Chocolate", "Strawberry"), LocalDate.of(2026, 10, 20)));
        Order enquiry = new Order(orders.generateEnquiryId(), new Customer("Bob Lim", "81234567"),
                new Cake(6, "Vanilla", "Cream"), LocalDate.of(2026, 10, 21), OrderStatus.ENQUIRY);
        orders.add(enquiry);
        return orders;
    }

    @Test
    public void saveThenLoad_roundTrip_preservesEveryField() {
        Storage storage = new Storage(dataFile());

        assertTrue(storage.save(sampleOrders()));
        OrderList loaded = storage.load();

        assertEquals(2, loaded.size());
        Order order = loaded.getById("O001");
        // The pipe in the name must survive the round trip via escaping.
        assertEquals("Alice | Tan", order.getCustomer().getName());
        assertEquals("91234567", order.getCustomer().getPhone());
        assertEquals(LocalDate.of(2026, 10, 20), order.getDueDate());
        assertEquals(8, order.getCake().getSize());
        assertEquals("Chocolate", order.getCake().getFlavour());
        assertEquals("Strawberry", order.getCake().getFilling());
        assertEquals(OrderStatus.QUEUED, order.getStatus());
        assertEquals(OrderStatus.ENQUIRY, loaded.getById("E001").getStatus());
    }

    @Test
    public void load_afterSaving_idsContinueInsteadOfBeingReused() {
        Storage storage = new Storage(dataFile());
        assertTrue(storage.save(sampleOrders()));

        OrderList loaded = storage.load();

        assertEquals("O002", loaded.generateOrderId());
        assertEquals("E002", loaded.generateEnquiryId());
    }

    @Test
    public void load_missingFile_returnsEmptyList() {
        OrderList loaded = new Storage(dataFile()).load();

        assertEquals(0, loaded.size());
    }

    @Test
    public void load_corruptedLines_skipsThemAndKeepsValidRecords() throws Exception {
        Storage storage = new Storage(dataFile());
        assertTrue(storage.save(sampleOrders()));
        Files.write(dataFile(),
                List.of("this is not a record", "O999 | NOPE | X | 1 | - | - | - | -"),
                java.nio.charset.StandardCharsets.UTF_8,
                java.nio.file.StandardOpenOption.APPEND);

        OrderList loaded = storage.load();

        assertEquals(2, loaded.size());
    }
}
