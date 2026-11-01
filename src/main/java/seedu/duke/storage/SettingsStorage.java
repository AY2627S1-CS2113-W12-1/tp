package seedu.duke.storage;

//@@author Kaishhhh

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import seedu.duke.model.OrderList;

/**
 * Saves and loads the baker's settings in a small text file kept beside the order data.
 *
 * <p>The only setting so far is the daily capacity, stored as one line such as
 * {@code dailyCapacity=3}. Blank lines, lines starting with {@code #} and unknown keys are
 * ignored, so more settings can be added later without breaking older files. The settings
 * are kept out of the order file so that the order file format does not change.</p>
 */
public class SettingsStorage {
    /** Name of the settings file, placed in the same folder as the order data file. */
    public static final String FILE_NAME = "settings.txt";
    static final String CAPACITY_KEY = "dailyCapacity";

    private static final Logger LOGGER = Logger.getLogger(SettingsStorage.class.getName());
    private static final String HEADER = "# CakeQueue settings";

    private final Path settingsFile;

    /**
     * Creates a settings handler for the given file. The file need not exist yet.
     */
    public SettingsStorage(Path settingsFile) {
        assert settingsFile != null : "Settings file path must not be null";
        this.settingsFile = settingsFile;
    }

    /**
     * Returns the settings file that belongs beside the given order data file.
     *
     * @param dataFile The order data file, such as {@code data/cakequeue.txt}.
     * @return The settings file in the same folder, such as {@code data/settings.txt}.
     */
    public static Path besideDataFile(Path dataFile) {
        return dataFile.toAbsolutePath().resolveSibling(FILE_NAME);
    }

    /**
     * Loads the saved daily capacity.
     *
     * <p>A missing file is normal (the baker has never changed the capacity) and gives the
     * default without a warning. A file that cannot be read, or a capacity that is missing,
     * repeated, not a whole number, or outside 1 to {@value OrderList#MAX_DAILY_CAPACITY},
     * gives the default together with a warning. This method never changes any file.</p>
     *
     * @return The capacity to use, whether it came from the file, and a warning or null.
     */
    public CapacitySetting loadDailyCapacity() {
        if (!Files.exists(settingsFile)) {
            return CapacitySetting.defaultValue(null);
        }
        List<String> lines;
        try {
            lines = Files.readAllLines(settingsFile, StandardCharsets.UTF_8);
        } catch (IOException exception) {
            LOGGER.log(Level.WARNING, "Could not read settings file " + settingsFile, exception);
            return CapacitySetting.defaultValue("Could not read the saved daily capacity.");
        }
        String value = null;
        for (String line : lines) {
            String trimmed = line.trim();
            if (trimmed.isEmpty() || trimmed.startsWith("#")) {
                continue;
            }
            int separator = trimmed.indexOf('=');
            String key = separator < 0 ? trimmed : trimmed.substring(0, separator).trim();
            if (!key.equals(CAPACITY_KEY)) {
                continue;
            }
            if (value != null) {
                return CapacitySetting.defaultValue("The daily capacity is saved more than once.");
            }
            value = separator < 0 ? "" : trimmed.substring(separator + 1).trim();
        }
        if (value == null) {
            return CapacitySetting.defaultValue("No daily capacity was found in the settings file.");
        }
        if (!value.matches("[0-9]{1,2}")) {
            return CapacitySetting.defaultValue("The saved daily capacity \"" + value + "\" is not valid.");
        }
        int capacity = Integer.parseInt(value);
        if (capacity < 1 || capacity > OrderList.MAX_DAILY_CAPACITY) {
            return CapacitySetting.defaultValue("The saved daily capacity \"" + value + "\" is not valid.");
        }
        return new CapacitySetting(capacity, true, null);
    }

    /**
     * Saves the daily capacity, replacing the previous settings file.
     * The new contents are written to a temporary file first and then moved over the
     * settings file, so a failed save leaves the previous settings in place.
     *
     * @param capacity The capacity to save, from 1 to {@value OrderList#MAX_DAILY_CAPACITY}.
     * @return True if the save succeeded; false (after logging) otherwise.
     */
    public boolean saveDailyCapacity(int capacity) {
        assert capacity >= 1 && capacity <= OrderList.MAX_DAILY_CAPACITY : "Capacity must be in range";
        List<String> lines = List.of(HEADER, CAPACITY_KEY + "=" + capacity);
        Path parent = settingsFile.toAbsolutePath().getParent();
        Path temporary = parent.resolve(settingsFile.getFileName() + ".tmp");
        try {
            Files.createDirectories(parent);
            Files.write(temporary, lines, StandardCharsets.UTF_8);
            try {
                Files.move(temporary, settingsFile,
                        StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException exception) {
                Files.move(temporary, settingsFile, StandardCopyOption.REPLACE_EXISTING);
            }
            return true;
        } catch (IOException exception) {
            LOGGER.log(Level.WARNING, "Could not save settings file " + settingsFile, exception);
            deleteQuietly(temporary);
            return false;
        }
    }

    private void deleteQuietly(Path file) {
        try {
            Files.deleteIfExists(file);
        } catch (IOException exception) {
            LOGGER.log(Level.FINE, "Could not remove temporary settings file " + file, exception);
        }
    }

    /**
     * Holds the result of loading the daily capacity.
     *
     * @param capacity The capacity to use.
     * @param isFromFile True if the capacity was read from a valid settings file.
     * @param warning A message for the user if the saved setting could not be used, or null.
     */
    public record CapacitySetting(int capacity, boolean isFromFile, String warning) {
        static CapacitySetting defaultValue(String warning) {
            return new CapacitySetting(OrderList.DEFAULT_DAILY_CAPACITY, false, warning);
        }
    }
}
