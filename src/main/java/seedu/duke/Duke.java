package seedu.duke;

//@@author HammerHead-os

import java.nio.file.Path;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import seedu.duke.model.OrderList;
import seedu.duke.storage.Storage;

/**
 * Runs the CakeQueue command-line application.
 */
public class Duke {
    private static final Logger LOGGER = Logger.getLogger(Duke.class.getName());
    private static final Path DEFAULT_DATA_FILE = Path.of("data", "cakequeue.txt");

    private final Path dataFile;

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
     * Loads saved records, then runs the baker interface until exit.
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
        Runnable saveChanges = () -> {
            if (!storage.save(orders)) {
                System.out.println("Warning: Could not save your data. "
                        + "Your changes are kept for this session only.");
                LOGGER.log(Level.WARNING, "Save failed for {0}; continuing in-memory.", dataFile);
            }
        };
        System.out.println("Hello! I'm CakeQueue.");
        new BakerCli().run(scanner, orders, saveChanges);
    }

    /**
     * Launches CakeQueue.
     */
    public static void main(String[] args) {
        new Duke().run();
    }
}
