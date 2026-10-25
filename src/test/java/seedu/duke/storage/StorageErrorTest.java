package seedu.duke.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
 * Error-handling tests for {@link Storage}: corrupted data must never crash
 * the application or lose valid records. Bad lines are skipped, unreadable
 * files load as empty, and failed saves report {@code false} instead of
 * throwing.
 */
public class StorageErrorTest {
    @TempDir
    private Path tempDir;

    private Path dataFile() {
        return tempDir.resolve("data").resolve("cakequeue.txt");
    }

    private String validLine() {
        return "O001 | QUEUED | Alice Tan | 91234567 | 2026-10-20 | 8 | Chocolate | Strawberry";
    }

    @Test
    public void load_eachKindOfCorruptedLine_skipsOnlyThatLine() throws Exception {
        Files.createDirectories(dataFile().getParent());
        Files.write(dataFile(), List.of(
                validLine(),
                "O002 | QUEUED | Too | Few",                                     // wrong field count
                "X003 | QUEUED | Bad Id | 1 | 2026-10-20 | 6 | Vanilla | Cream", // bad ID
                "O004 | NOPE | Bad Status | 1 | 2026-10-20 | 6 | Vanilla | Cream", // bad status
                "O005 | QUEUED | Bad Date | 1 | 20-10-2026 | 6 | Vanilla | Cream", // bad date
                "O006 | QUEUED | Bad Size | 1 | 2026-10-20 | six | Vanilla | Cream", // bad size
                "",                                                              // blank line
                "E002 | ENQUIRY | Bob Lim | 81111111 | 2026-10-21 | 6 | Vanilla | Cream"));

        OrderList loaded = new Storage(dataFile()).load();

        assertEquals(2, loaded.size());
        assertEquals("Alice Tan", loaded.getById("O001").getCustomer().getName());
        assertEquals(OrderStatus.ENQUIRY, loaded.getById("E002").getStatus());
    }

    @Test
    public void load_recordWithoutCakeOrDate_roundTripsAsNulls() {
        OrderList orders = new OrderList();
        orders.add(new Order("O001", new Customer("Alice Tan", "91234567"),
                LocalDate.of(2026, 10, 20)));
        Storage storage = new Storage(dataFile());

        assertTrue(storage.save(orders));
        Order loaded = storage.load().getById("O001");

        assertEquals(null, loaded.getCake());
        assertEquals(LocalDate.of(2026, 10, 20), loaded.getDueDate());
    }

    @Test
    public void load_pathIsDirectory_returnsEmptyListInsteadOfCrashing() {
        // readAllLines on a directory throws IOException, which load() must absorb.
        assertEquals(0, new Storage(tempDir).load().size());
    }

    @Test
    public void save_parentIsAFile_returnsFalseInsteadOfThrowing() throws Exception {
        Path blocker = tempDir.resolve("blocker");
        Files.writeString(blocker, "not a directory");
        Storage storage = new Storage(blocker.resolve("cakequeue.txt"));

        assertFalse(storage.save(new OrderList()));
    }

    @Test
    public void saveThenLoad_escapedBackslashAndPipe_surviveRoundTrip() {
        OrderList orders = new OrderList();
        orders.add(new Order("O001", new Customer("Ann \\ Marie | Tan", "91234567"),
                new Cake(8, "Choco|late", "Straw\\berry"), LocalDate.of(2026, 10, 20)));
        Storage storage = new Storage(dataFile());

        assertTrue(storage.save(orders));
        Order loaded = storage.load().getById("O001");

        assertEquals("Ann \\ Marie | Tan", loaded.getCustomer().getName());
        assertEquals("Choco|late", loaded.getCake().getFlavour());
        assertEquals("Straw\\berry", loaded.getCake().getFilling());
    }

    @Test
    public void save_emptyList_createsLoadableEmptyFile() {
        Storage storage = new Storage(dataFile());

        assertTrue(storage.save(new OrderList()));
        assertTrue(Files.exists(dataFile()));
        assertEquals(0, storage.load().size());
    }

    @Test
    public void save_recordWithoutId_isSkippedNotSaved() {
        OrderList orders = new OrderList();
        // Legacy constructor leaves the ID null; such records cannot be reloaded.
        orders.add(new Order(new Customer("Alice Tan", "91234567"),
                new Cake(8, "Chocolate", "Strawberry"), 1));
        Storage storage = new Storage(dataFile());

        assertTrue(storage.save(orders));
        assertEquals(0, storage.load().size());
    }
}
