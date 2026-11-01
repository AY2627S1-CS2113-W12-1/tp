package seedu.duke;

//@@author Kaishhhh

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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.duke.model.OrderList;
import seedu.duke.storage.SettingsStorage;
import seedu.duke.storage.Storage;

/**
 * Tests that the daily capacity is saved and restored across sessions.
 */
public class CapacityPersistenceCliTest {
    private static final String ADD_ALICE =
            "add n/Alice Tan p/91234567 d/21-12-2030 s/8 f/Chocolate fi/Strawberry\n";
    private static final String ADD_BOB =
            "add n/Bob Lim p/81234567 d/21-12-2030 s/6 f/Vanilla fi/Cream\n";

    @TempDir
    private Path tempDir;

    private Path dataFile() {
        return tempDir.resolve("cakequeue.txt");
    }

    private Path settingsFile() {
        return tempDir.resolve(SettingsStorage.FILE_NAME);
    }

    @Test
    public void run_firstStart_usesDefaultAndWritesNoSettings() {
        String result = runSession("capacity\nbye\n");

        assertTrue(result.contains("Daily capacity is 5 orders per day."));
        assertFalse(result.contains("Warning:"));
        assertFalse(Files.exists(settingsFile()));
    }

    @Test
    public void run_changedCapacity_survivesRestart() {
        runSession("capacity 3\nbye\n");

        String restarted = runSession("capacity\nbye\n");

        assertTrue(restarted.contains("Daily capacity is 3 orders per day."));
        assertFalse(restarted.contains("Warning:"));
    }

    @Test
    public void run_boundaryValues_surviveRestart() {
        runSession("capacity 1\nbye\n");
        assertTrue(runSession("capacity\nbye\n").contains("Daily capacity is 1 order per day."));

        runSession("capacity 50\nbye\n");
        assertTrue(runSession("capacity\nbye\n").contains("Daily capacity is 50 orders per day."));
    }

    @Test
    public void run_invalidCapacityCommands_leaveSettingAndFileUnchanged() throws IOException {
        runSession("capacity 3\nbye\n");
        byte[] savedSettings = Files.readAllBytes(settingsFile());

        String result = runSession("capacity 0\ncapacity 51\ncapacity abc\ncapacity 1 2\ncapacity\nbye\n");

        assertEquals(4, result.lines().filter(line -> line.startsWith("Error:")).count());
        assertTrue(result.contains("Daily capacity is 3 orders per day."));
        assertArrayEquals(savedSettings, Files.readAllBytes(settingsFile()));
        assertTrue(runSession("capacity\nbye\n").contains("Daily capacity is 3 orders per day."));
    }

    @Test
    public void run_bareCapacity_doesNotWriteSettings() throws IOException {
        runSession("capacity 4\nbye\n");
        Files.writeString(settingsFile(), "# hand-edited\ndailyCapacity=4\n");

        runSession("capacity\nbye\n");

        assertEquals("# hand-edited\ndailyCapacity=4\n", Files.readString(settingsFile()));
    }

    @Test
    public void run_malformedSettings_warnsUsesDefaultAndKeepsOrders() throws IOException {
        runSession(ADD_ALICE + "bye\n");
        byte[] savedOrders = Files.readAllBytes(dataFile());
        Files.writeString(settingsFile(), "dailyCapacity=lots\n");

        String result = runSession("capacity\nlist\nbye\n");

        assertTrue(result.contains("Warning: The saved daily capacity \"lots\" is not valid. "
                + "Using the default daily capacity of 5."));
        assertTrue(result.contains("Daily capacity is 5 orders per day."));
        assertTrue(result.contains("1. O001: Alice Tan"));
        assertArrayEquals(savedOrders, Files.readAllBytes(dataFile()));
    }

    @Test
    public void run_malformedSettingsThenNewCapacity_repairsFile() throws IOException {
        Files.writeString(settingsFile(), "dailyCapacity=lots\n");

        runSession("capacity 2\nbye\n");
        String restarted = runSession("capacity\nbye\n");

        assertFalse(restarted.contains("Warning:"));
        assertTrue(restarted.contains("Daily capacity is 2 orders per day."));
    }

    @Test
    public void run_unwritableSettings_keepsValueForSessionAndSavesOrders() throws IOException {
        Files.createDirectories(settingsFile());
        Files.writeString(settingsFile().resolve("keep.txt"), "blocks the settings file");

        String result = runSession("capacity 2\ncapacity\n" + ADD_ALICE + "bye\n");

        assertTrue(result.contains("Warning: Could not save the daily capacity. "
                + "It is kept for this session only."));
        assertTrue(result.contains("Daily capacity is 2 orders per day."));
        assertFalse(result.contains("Warning: Could not save your data."));
        assertEquals("O001", new Storage(dataFile()).load().getById("O001").getOrderId());
    }

    @Test
    public void run_capacityChange_keepsOrdersAndIdsAcrossRestart() throws IOException {
        runSession(ADD_ALICE + ADD_BOB + "capacity 1\nbye\n");

        String restarted = runSession(
                "add n/Alice Tan p/91234567 d/22-12-2030 s/8 f/Vanilla fi/Cream\ncustomer C001\nbye\n");

        assertTrue(restarted.contains("Loaded 2 saved record(s)."));
        assertTrue(restarted.contains("Added order O003"));
        assertTrue(restarted.contains("Customer ID: C001 (returning customer)"));
        OrderList loaded = new Storage(dataFile()).load();
        assertEquals(3, loaded.size());
        assertEquals("C002", loaded.getById("O002").getCustomerId());
        // The capacity lives in its own file, so the order file format is unchanged.
        assertFalse(Files.readString(dataFile()).contains("capacity"));
    }

    @Test
    public void run_restoredCapacity_stillWarnsWhenDateGoesOver() {
        runSession("capacity 1\n" + ADD_ALICE + "bye\n");

        String restarted = runSession(ADD_BOB + "bye\n");

        assertTrue(restarted.contains("Warning: 21-12-2030 now has 2 orders, over your daily capacity of 1."));
        assertTrue(restarted.contains("Added order O002"));
    }

    private String runSession(String commands) {
        InputStream originalInput = System.in;
        PrintStream originalOutput = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (PrintStream capturedOutput = new PrintStream(output, true, StandardCharsets.UTF_8)) {
            System.setIn(new ByteArrayInputStream(commands.getBytes(StandardCharsets.UTF_8)));
            System.setOut(capturedOutput);
            new Duke(dataFile()).run();
        } finally {
            System.setIn(originalInput);
            System.setOut(originalOutput);
        }
        return output.toString(StandardCharsets.UTF_8);
    }
}
