package seedu.duke;

//@@author HammerHead-os

import java.nio.file.Path;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import seedu.duke.model.OrderList;
import seedu.duke.storage.SettingsStorage;
import seedu.duke.storage.Storage;

/**
 * Runs the CakeQueue command-line application.
 */
public class Duke {
    private static final Logger LOGGER = Logger.getLogger(Duke.class.getName());
    private static final Path DEFAULT_DATA_FILE = Path.of("data", "cakequeue.txt");

    private final Path dataFile;

    //@@author Kaishhhh
    /** The capacity the settings file holds (the default if there is no file), or null if it is invalid. */
    private Integer savedCapacity;
    //@@author HammerHead-os

    /**
     * Creates the application using the default data file ({@code data/cakequeue.txt}
     * relative to the folder the app is launched from).
     */
    public Duke() {
        this(DEFAULT_DATA_FILE);
    }

    /**
     * Creates the application with an explicit data file.
     * Tests use this to run against a temporary file instead of real data.
     */
    public Duke(Path dataFile) {
        assert dataFile != null : "Data file path must not be null";
        this.dataFile = dataFile;
    }

    /**
     * Loads saved records and settings, then runs the baker interface until exit.
     * Every successful change is saved immediately, so closing the app (or a
     * crash) after a change does not lose it.
     */
    public void run() {
        Scanner scanner = new Scanner(System.in);
        Storage storage = new Storage(dataFile);
        OrderList orders = storage.load();
        if (orders.size() > 0) {
            System.out.println("Loaded " + orders.size() + " saved record(s).");
        }
        //@@author Kaishhhh
        SettingsStorage settings = new SettingsStorage(SettingsStorage.besideDataFile(dataFile));
        restoreDailyCapacity(settings, orders);
        //@@author HammerHead-os
        Runnable saveChanges = () -> {
            if (!storage.save(orders)) {
                System.out.println("Warning: Could not save your data. "
                        + "Your changes are kept for this session only.");
                LOGGER.log(Level.WARNING, "Save failed for {0}; continuing in-memory.", dataFile);
            }
            //@@author Kaishhhh
            saveDailyCapacityIfChanged(settings, orders);
            //@@author HammerHead-os
        };
        System.out.println("Hello! I'm CakeQueue.");
        new BakerCli().run(scanner, orders, saveChanges);
    }

    //@@author Kaishhhh
    /**
     * Applies the saved daily capacity before any command is read.
     * A missing or invalid settings file leaves the default capacity in place.
     */
    private void restoreDailyCapacity(SettingsStorage settings, OrderList orders) {
        SettingsStorage.CapacitySetting setting = settings.loadDailyCapacity();
        if (setting.warning() != null) {
            System.out.println("Warning: " + setting.warning() + " Using the default daily capacity of "
                    + setting.capacity() + ".");
        }
        orders.setDailyCapacity(setting.capacity());
        savedCapacity = setting.warning() == null ? setting.capacity() : null;
    }

    /**
     * Saves the daily capacity if it differs from the saved one, or if the saved settings were
     * invalid. The settings are saved separately from the orders, so a settings failure cannot
     * damage the order data.
     */
    private void saveDailyCapacityIfChanged(SettingsStorage settings, OrderList orders) {
        int capacity = orders.getDailyCapacity();
        if (savedCapacity != null && savedCapacity == capacity) {
            return;
        }
        if (settings.saveDailyCapacity(capacity)) {
            savedCapacity = capacity;
        } else {
            System.out.println("Warning: Could not save the daily capacity. "
                    + "It is kept for this session only.");
        }
    }
    //@@author HammerHead-os

    /**
     * Launches CakeQueue.
     */
    public static void main(String[] args) {
        new Duke().run();
    }
}
