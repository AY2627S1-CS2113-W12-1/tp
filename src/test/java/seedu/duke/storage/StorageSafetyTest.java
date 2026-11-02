package seedu.duke.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import seedu.duke.exception.CakeQueueException;
import seedu.duke.model.Customer;
import seedu.duke.model.Order;
import seedu.duke.model.OrderList;

//@@author WillChin484848
public class StorageSafetyTest {
    @TempDir
    private Path tempDir;

    @Test
    public void capacity_roundTrips() {
        Storage storage = new Storage(tempDir.resolve("orders.txt"));
        OrderList orders = new OrderList();
        orders.setDailyCapacity(12);
        assertTrue(storage.save(orders));
        assertEquals(12, storage.load().getDailyCapacity());
    }

    @Test
    public void corruptedLoad_doesNotOverwriteOriginalFile() throws Exception {
        Path file = tempDir.resolve("orders.txt");
        Files.writeString(file, "invalid record\n");
        Storage storage = new Storage(file);
        OrderList orders = storage.load();
        assertFalse(storage.save(orders));
        assertEquals("invalid record\n", Files.readString(file));
    }

    @Test
    public void duplicateRecordId_isRejected() {
        OrderList orders = new OrderList();
        orders.add(new Order("O001", new Customer("Alice", "91234567"), null));
        assertThrows(CakeQueueException.class, () -> orders.add(
                new Order("O001", new Customer("Bob", "81234567"), null)));
        assertEquals(1, orders.size());
    }
    @Test
    public void countOrdersDueOn_nullDate_returnsZero() {
        assertEquals(0, new OrderList().countOrdersDueOn(null));
    }

    @Test
    public void update_nullOrder_isRejected() {
        assertThrows(CakeQueueException.class, () -> new OrderList().update(null));
    }

    @Test
    public void update_caseInsensitiveId_replacesRecord() {
        OrderList orders = new OrderList();
        orders.add(new Order("O001", new Customer("Alice", "91234567"), null));
        orders.update(new Order("o001", new Customer("Bob", "81234567"), null));
        assertEquals(1, orders.size());
        assertEquals("Bob", orders.getById("O001").getCustomer().getName());
    }

    @Test
    public void setStatus_null_isRejected() {
        Order order = new Order("O001", new Customer("Alice", "91234567"), null);
        assertThrows(IllegalArgumentException.class, () -> order.setStatus(null));
    }

}
