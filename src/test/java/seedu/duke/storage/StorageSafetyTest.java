package seedu.duke.storage;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import seedu.duke.Duke;
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
    public void capacityOnly_firstRun_persistsWithoutAnyOrderChanges() {
        Path file = tempDir.resolve("first-capacity.txt");

        runSession(file, "capacity 3\nbye\n");

        OrderList loaded = new Storage(file).load();
        assertEquals(3, loaded.getDailyCapacity());
        assertEquals(0, loaded.size());
        assertTrue(runSession(file, "capacity\nbye\n").contains("Daily capacity is 3 orders per day."));
    }

    @Test
    public void capacityOnly_existingOrders_persistsAndPreservesRecords() throws Exception {
        Path file = tempDir.resolve("existing-capacity.txt");
        OrderList original = new OrderList();
        original.setDailyCapacity(1);
        original.add(new Order("O001", new Customer("Alice", "91234567"), null));
        assertTrue(new Storage(file).save(original));
        List<String> before = Files.readAllLines(file);

        runSession(file, "capacity 3\nbye\n");

        List<String> after = Files.readAllLines(file);
        assertEquals("# dailyCapacity=3", after.get(0));
        assertEquals(before.subList(1, before.size()), after.subList(1, after.size()));
        OrderList loaded = new Storage(file).load();
        assertEquals(3, loaded.getDailyCapacity());
        assertEquals(1, loaded.size());
        assertEquals("C001", loaded.getById("O001").getCustomerId());
        assertTrue(runSession(file, "capacity\nbye\n").contains("Daily capacity is 3 orders per day."));
    }

    @Test
    public void capacityQueryAndInvalidCommands_doNotSave() throws Exception {
        Path file = tempDir.resolve("read-only-capacity.txt");
        OrderList orders = new OrderList();
        orders.setDailyCapacity(3);
        assertTrue(new Storage(file).save(orders));
        // A valid legacy file without a header would gain one if a save were wrongly requested.
        Files.writeString(file, Files.readString(file).replace("# dailyCapacity=3", ""));
        byte[] original = Files.readAllBytes(file);

        runSession(file, "capacity\ncapacity 0\ncapacity 51\ncapacity nope\ncapacity 3 extra\nbye\n");

        assertArrayEquals(original, Files.readAllBytes(file));
    }

    @Test
    public void invalidCapacity_loadsOrdersAndUsesDefaultFive() throws Exception {
        Path file = tempDir.resolve("invalid-capacity.txt");
        OrderList orders = new OrderList();
        orders.add(new Order("O001", new Customer("Alice", "91234567"), null));
        assertTrue(new Storage(file).save(orders));
        String saved = Files.readString(file).replace("# dailyCapacity=5", "# dailyCapacity=abc");
        Files.writeString(file, saved);

        Storage storage = new Storage(file);
        OrderList loaded = storage.load();
        assertEquals(5, loaded.getDailyCapacity());
        assertEquals(1, loaded.size());
        assertEquals("Alice", loaded.getById("O001").getCustomer().getName());
        assertTrue(storage.save(loaded));
        assertTrue(Files.readString(file).startsWith("# dailyCapacity=5"));
    }

    @Test
    public void malformedCapacityHeader_preservesValidOrdersAndUsesDefault() throws Exception {
        Path file = tempDir.resolve("malformed-capacity.txt");
        OrderList orders = new OrderList();
        orders.add(new Order("O001", new Customer("Alice", "91234567"), null));
        assertTrue(new Storage(file).save(orders));
        String saved = Files.readString(file).replace("# dailyCapacity=5", "# dailyCapacity =abc");
        Files.writeString(file, saved);

        OrderList loaded = new Storage(file).load();
        assertEquals(OrderList.DEFAULT_DAILY_CAPACITY, loaded.getDailyCapacity());
        assertEquals(1, loaded.size());
        assertEquals("Alice", loaded.getById("O001").getCustomer().getName());
    }

    @Test
    public void malformedCapacityHeaderWithCorruptOrder_stillAborts() throws Exception {
        Path file = tempDir.resolve("malformed-and-corrupt.txt");
        Files.writeString(file, "# dailyCapacity =abc\ncorrupted order\n");
        Storage storage = new Storage(file);

        assertThrows(IllegalStateException.class, storage::load);
        assertFalse(storage.save(new OrderList()));
    }

    @Test
    public void invalidCapacityWithCorruptedOrder_stillBlocksSaving() throws Exception {
        Path file = tempDir.resolve("mixed-corruption.txt");
        String original = "# dailyCapacity=invalid\ncorrupted order\n";
        Files.writeString(file, original);
        Storage storage = new Storage(file);

        assertThrows(IllegalStateException.class, storage::load);
        assertFalse(storage.save(new OrderList()));
        assertEquals(original, Files.readString(file));
    }

    @Test
    public void corruptedLoad_doesNotOverwriteOriginalFile() throws Exception {
        Path file = tempDir.resolve("orders.txt");
        Files.writeString(file, "invalid record\n");
        Storage storage = new Storage(file);
        assertThrows(IllegalStateException.class, storage::load);
        assertFalse(storage.save(new OrderList()));
        assertEquals("invalid record\n", Files.readString(file));
    }

    @Test
    public void failedLoad_cannotBeClearedByRetryingOnSameStorage() throws Exception {
        Path file = tempDir.resolve("retry.txt");
        Files.writeString(file, "corrupted record\n");
        Storage storage = new Storage(file);
        assertThrows(IllegalStateException.class, storage::load);

        // Repairing the file does not silently unlock this storage instance.
        Files.delete(file);
        assertThrows(IllegalStateException.class, storage::load);
        assertFalse(storage.save(new OrderList()));
    }

    @Test
    public void corruptedRecord_abortsStartupBeforeAcceptingCommands() throws Exception {
        Path file = tempDir.resolve("partial.txt");
        Storage initial = new Storage(file);
        OrderList orders = new OrderList();
        orders.add(new Order("O001", new Customer("Alice", "91234567"), null));
        assertTrue(initial.save(orders));
        Files.writeString(file, Files.readString(file) + "corrupted record\n");
        byte[] original = Files.readAllBytes(file);

        InputStream previousInput = System.in;
        PrintStream previousOutput = System.out;
        ByteArrayOutputStream captured = new ByteArrayOutputStream();
        try (PrintStream output = new PrintStream(captured, true, StandardCharsets.UTF_8)) {
            System.setIn(new ByteArrayInputStream("delete 1\nbye\n".getBytes(StandardCharsets.UTF_8)));
            System.setOut(output);
            new Duke(file).run();
        } finally {
            System.setIn(previousInput);
            System.setOut(previousOutput);
        }
        assertTrue(captured.toString(StandardCharsets.UTF_8).contains("will exit"));
        org.junit.jupiter.api.Assertions.assertArrayEquals(original, Files.readAllBytes(file));
    }

    @Test
    public void add_nullRecordAndNullId_areRejectedWithoutMutatingList() {
        OrderList orders = new OrderList();
        assertThrows(CakeQueueException.class, () -> orders.add(null));
        assertThrows(CakeQueueException.class,
                () -> orders.add(new Order(null, new Customer("Alice", "91234567"), null)));
        assertEquals(0, orders.size());
    }

    @Test
    public void recordIdBoundary_rejectsOverflowWithoutChangingSavedData() throws Exception {
        Path file = tempDir.resolve("boundary.txt");
        Storage storage = new Storage(file);
        OrderList orders = new OrderList();
        orders.add(new Order("O2147483646", new Customer("Alice", "91234567"), null));
        assertTrue(storage.save(orders));
        byte[] original = Files.readAllBytes(file);
        OrderList reloaded = new Storage(file).load();
        assertEquals("O2147483646", reloaded.getById("O2147483646").getOrderId());
        assertThrows(CakeQueueException.class, () -> reloaded.add(
                new Order("O2147483647", new Customer("Bob", "81234567"), null)));
        assertEquals(1, reloaded.size());
        org.junit.jupiter.api.Assertions.assertArrayEquals(original, Files.readAllBytes(file));
    }

    @Test
    public void invalidUtf8_abortsWithoutOverwriting() throws Exception {
        Path file = tempDir.resolve("invalid-utf8.txt");
        byte[] original = {(byte) 0xC3, (byte) 0x28};
        Files.write(file, original);
        Storage storage = new Storage(file);
        assertThrows(IllegalStateException.class, storage::load);
        assertFalse(storage.save(new OrderList()));
        org.junit.jupiter.api.Assertions.assertArrayEquals(original, Files.readAllBytes(file));
    }

    @Test
    public void invalidUtf8_startupStopsAndPreservesOriginalBytes() throws Exception {
        Path file = tempDir.resolve("cakequeue.txt");
        byte[] original = {(byte) 0xC3, (byte) 0x28};
        Files.write(file, original);
        InputStream previousInput = System.in;
        PrintStream previousOutput = System.out;
        ByteArrayOutputStream captured = new ByteArrayOutputStream();
        try (PrintStream output = new PrintStream(captured, true, StandardCharsets.UTF_8)) {
            System.setIn(new ByteArrayInputStream("bye\n".getBytes(StandardCharsets.UTF_8)));
            System.setOut(output);
            new Duke(file).run();
        } finally {
            System.setIn(previousInput);
            System.setOut(previousOutput);
        }
        assertTrue(captured.toString(StandardCharsets.UTF_8).contains("will exit"));
        org.junit.jupiter.api.Assertions.assertArrayEquals(original, Files.readAllBytes(file));
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
        assertEquals("O001", orders.getById("O001").getOrderId());
        assertEquals("Bob", orders.getById("O001").getCustomer().getName());
        Storage storage = new Storage(tempDir.resolve("updated.txt"));
        assertTrue(storage.save(orders));
        assertEquals("O001", storage.load().getById("o001").getOrderId());
    }

    @Test
    public void setStatus_null_isRejected() {
        Order order = new Order("O001", new Customer("Alice", "91234567"), null);
        assertThrows(IllegalArgumentException.class, () -> order.setStatus(null));
    }

    /** Runs one CLI session and restores the process input and output after it ends. */
    private String runSession(Path file, String commands) {
        InputStream previousInput = System.in;
        PrintStream previousOutput = System.out;
        ByteArrayOutputStream captured = new ByteArrayOutputStream();
        try (PrintStream output = new PrintStream(captured, true, StandardCharsets.UTF_8)) {
            System.setIn(new ByteArrayInputStream(commands.getBytes(StandardCharsets.UTF_8)));
            System.setOut(output);
            new Duke(file).run();
        } finally {
            System.setIn(previousInput);
            System.setOut(previousOutput);
        }
        return captured.toString(StandardCharsets.UTF_8);
    }

}
