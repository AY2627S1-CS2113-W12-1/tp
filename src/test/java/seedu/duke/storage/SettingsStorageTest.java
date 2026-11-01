package seedu.duke.storage;

//@@author Kaishhhh

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests {@link SettingsStorage}.
 */
public class SettingsStorageTest {
    @TempDir
    private Path tempDir;

    private SettingsStorage.CapacitySetting loadFrom(String... lines) throws IOException {
        Path file = tempDir.resolve(SettingsStorage.FILE_NAME);
        Files.write(file, List.of(lines));
        return new SettingsStorage(file).loadDailyCapacity();
    }

    @Test
    public void loadDailyCapacity_missingFile_returnsDefaultWithoutWarning() {
        SettingsStorage.CapacitySetting setting =
                new SettingsStorage(tempDir.resolve(SettingsStorage.FILE_NAME)).loadDailyCapacity();

        assertEquals(5, setting.capacity());
        assertFalse(setting.isFromFile());
        assertNull(setting.warning());
    }

    @Test
    public void saveThenLoad_boundaryAndMiddleValues_roundTrip() {
        SettingsStorage settings = new SettingsStorage(tempDir.resolve(SettingsStorage.FILE_NAME));
        for (int capacity : new int[] {1, 3, 50}) {
            assertTrue(settings.saveDailyCapacity(capacity));

            SettingsStorage.CapacitySetting setting = settings.loadDailyCapacity();

            assertEquals(capacity, setting.capacity());
            assertTrue(setting.isFromFile());
            assertNull(setting.warning());
        }
    }

    @Test
    public void saveDailyCapacity_missingFolder_createsFolder() {
        Path file = tempDir.resolve("new-folder").resolve(SettingsStorage.FILE_NAME);

        assertTrue(new SettingsStorage(file).saveDailyCapacity(7));
        assertEquals(7, new SettingsStorage(file).loadDailyCapacity().capacity());
    }

    @Test
    public void loadDailyCapacity_commentsBlankLinesAndUnknownKeys_areIgnored() throws IOException {
        SettingsStorage.CapacitySetting setting = loadFrom("# note", "", "theme=dark", "  dailyCapacity = 12  ");

        assertEquals(12, setting.capacity());
        assertNull(setting.warning());
    }

    @Test
    public void loadDailyCapacity_invalidValues_returnDefaultWithWarning() throws IOException {
        List<List<String>> invalidFiles = List.of(
                List.of("dailyCapacity=0"),
                List.of("dailyCapacity=51"),
                List.of("dailyCapacity=-3"),
                List.of("dailyCapacity=abc"),
                List.of("dailyCapacity=2.5"),
                List.of("dailyCapacity=999999999999"),
                List.of("dailyCapacity="),
                List.of("dailyCapacity"),
                List.of("dailyCapacity=3", "dailyCapacity=4"),
                List.of("theme=dark"),
                List.of(""));
        for (List<String> lines : invalidFiles) {
            SettingsStorage.CapacitySetting setting = loadFrom(lines.toArray(new String[0]));

            assertEquals(5, setting.capacity(), lines.toString());
            assertFalse(setting.isFromFile(), lines.toString());
            assertNotNull(setting.warning(), lines.toString());
        }
    }

    @Test
    public void unreadableAndUnwritableSettings_reportFailureAndKeepOtherFiles() throws IOException {
        Path blocked = tempDir.resolve(SettingsStorage.FILE_NAME);
        Files.createDirectories(blocked);
        Files.writeString(blocked.resolve("keep.txt"), "not a settings file");
        SettingsStorage settings = new SettingsStorage(blocked);

        assertFalse(settings.saveDailyCapacity(3));
        SettingsStorage.CapacitySetting setting = settings.loadDailyCapacity();

        assertEquals(5, setting.capacity());
        assertNotNull(setting.warning());
        assertEquals("not a settings file", Files.readString(blocked.resolve("keep.txt")));
        assertFalse(Files.exists(tempDir.resolve(SettingsStorage.FILE_NAME + ".tmp")));
    }

    @Test
    public void besideDataFile_usesSameFolderAsOrders() {
        Path dataFile = tempDir.resolve("data").resolve("cakequeue.txt");

        assertEquals(dataFile.toAbsolutePath().getParent().resolve("settings.txt"),
                SettingsStorage.besideDataFile(dataFile));
    }
}
