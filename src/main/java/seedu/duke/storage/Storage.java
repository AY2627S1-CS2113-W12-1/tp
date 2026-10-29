package seedu.duke.storage;

//@@author HammerHead-os

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import seedu.duke.exception.CakeQueueException;
import seedu.duke.model.Cake;
import seedu.duke.model.Customer;
import seedu.duke.model.Order;
import seedu.duke.model.OrderList;
import seedu.duke.model.OrderStatus;

/**
 * Saves and loads CakeQueue records in a plain-text file.
 *
 * <p>Format: one record per line, fields separated by {@code |}, in the order
 * ID, status, customer name, phone, due date ({@code yyyy-MM-dd} or {@code -}),
 * cake size (or {@code -}), flavour, filling, customer ID (or {@code -}) and notes (or {@code -}).
 * Older files without the customer ID or the notes are still read, including 9-field lines whose
 * last field holds notes instead of a customer ID. Backslash and {@code |} inside a
 * value are escaped as {@code \\} and {@code \|}, so names such as
 * {@code Mary | Ann} survive a save/load round trip. Plain text was chosen over
 * Java serialisation so the file stays human-readable, diffable in Git, and
 * editable by hand if it ever gets corrupted.</p>
 */
public class Storage {
    private static final Logger LOGGER = Logger.getLogger(Storage.class.getName());
    private static final String NO_VALUE = "-";
    private static final int FIELD_COUNT = 8;
    //@@author anannya-mk
    private static final int FIELD_COUNT_WITH_CUSTOMER_ID = 9;
    //@@author HammerHead-os
    private static final int FIELD_COUNT_WITH_NOTES = 10;

    private final Path dataFile;

    /**
     * Creates a storage handler for the given file. The file (and its parent
     * folder) need not exist yet; they are created on the first save.
     */
    public Storage(Path dataFile) {
        assert dataFile != null : "Data file path must not be null";
        this.dataFile = dataFile;
    }

    /**
     * Loads all records from the data file.
     *
     * <p>Defensive choices: a missing file is not an error (first run of the
     * app), and a corrupted line is skipped with a warning instead of crashing
     * the whole application, so one bad line cannot destroy every order.</p>
     *
     * @return The loaded records, or an empty list if the file does not exist.
     */
    public OrderList load() {
        OrderList orders = new OrderList();
        if (!Files.exists(dataFile)) {
            LOGGER.log(Level.INFO, "No data file at {0}; starting with no records.", dataFile);
            return orders;
        }
        List<String> lines;
        try {
            lines = Files.readAllLines(dataFile, StandardCharsets.UTF_8);
        } catch (IOException exception) {
            LOGGER.log(Level.SEVERE, "Could not read data file " + dataFile, exception);
            return orders;
        }
        int lineNumber = 0;
        for (String line : lines) {
            lineNumber++;
            if (line.isBlank()) {
                continue;
            }
            try {
                orders.add(parseLine(line));
            } catch (IllegalArgumentException | DateTimeParseException exception) {
                LOGGER.log(Level.WARNING, "Skipping corrupted line " + lineNumber
                        + " in " + dataFile + ": " + exception.getMessage());
            }
        }
        return orders;
    }

    /**
     * Saves all records to the data file, replacing its previous contents.
     * The new contents are first written to a temporary file in the same
     * folder and then moved over the data file, so a crash mid-save cannot
     * leave a half-written data file behind.
     *
     * @return True if the save succeeded; false (after logging) otherwise.
     */
    public boolean save(OrderList orders) {
        assert orders != null : "Order list to save must not be null";
        ArrayList<String> lines = new ArrayList<>();
        for (Order order : orders.getAll()) {
            if (order.getOrderId() == null) {
                // Legacy in-memory orders without an ID cannot be reloaded, so they are not persisted.
                LOGGER.log(Level.WARNING, "Skipping record without an ID while saving.");
                continue;
            }
            lines.add(toLine(order));
        }
        try {
            Path parent = dataFile.toAbsolutePath().getParent();
            Files.createDirectories(parent);
            Path temporary = parent.resolve(dataFile.getFileName() + ".tmp");
            Files.write(temporary, lines, StandardCharsets.UTF_8);
            try {
                Files.move(temporary, dataFile,
                        StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException exception) {
                Files.move(temporary, dataFile, StandardCopyOption.REPLACE_EXISTING);
            }
            return true;
        } catch (IOException exception) {
            LOGGER.log(Level.SEVERE, "Could not save data file " + dataFile, exception);
            return false;
        }
    }

    private String toLine(Order order) {
        Cake cake = order.getCake();
        String dueDate = order.getDueDate() == null ? NO_VALUE : order.getDueDate().toString();
        String size = cake == null ? NO_VALUE : String.valueOf(cake.getSize());
        String flavour = cake == null ? NO_VALUE : cake.getFlavour();
        String filling = cake == null ? NO_VALUE : cake.getFilling();
        String notes = order.getNotes().isEmpty() ? NO_VALUE : order.getNotes();
        return String.join(" | ",
                escape(order.getOrderId()),
                escape(order.getStatus().name()),
                escape(order.getCustomer().getName()),
                escape(order.getCustomer().getPhone()),
                escape(dueDate),
                escape(size),
                escape(flavour),
                escape(filling),
                escape(order.getCustomerId() == null ? NO_VALUE : order.getCustomerId()),
                escape(notes));
    }

    private Order parseLine(String line) {
        List<String> fields = splitEscaped(line);
        if (fields.size() < FIELD_COUNT || fields.size() > FIELD_COUNT_WITH_NOTES) {
            throw new CakeQueueException("Expected " + FIELD_COUNT + " to " + FIELD_COUNT_WITH_NOTES
                    + " fields but found " + fields.size());
        }
        String orderId = fields.get(0);
        if (!orderId.matches("[OE][0-9]{3,}")) {
            throw new CakeQueueException("Invalid record ID: " + orderId);
        }
        OrderStatus status = OrderStatus.valueOf(fields.get(1));
        Customer customer = new Customer(fields.get(2), fields.get(3));
        LocalDate dueDate = fields.get(4).equals(NO_VALUE) ? null : LocalDate.parse(fields.get(4));
        Cake cake = null;
        if (!fields.get(5).equals(NO_VALUE)) {
            cake = new Cake(Integer.parseInt(fields.get(5)), fields.get(6), fields.get(7));
        }
        Order order = new Order(orderId, customer, cake, dueDate, status);
        //@@author Kaishhhh
        if (fields.size() == FIELD_COUNT_WITH_CUSTOMER_ID && isLegacyNotes(fields.get(8))) {
            // A 9-field line from the short-lived "notes without customer ID" format.
            order.setNotes(fields.get(8));
            return order;
        }
        //@@author anannya-mk
        if (fields.size() >= FIELD_COUNT_WITH_CUSTOMER_ID && !fields.get(8).equals(NO_VALUE)) {
            if (!fields.get(8).matches("C[0-9]{3,}")) {
                throw new CakeQueueException("Invalid customer ID: " + fields.get(8));
            }
            order.setCustomerId(fields.get(8));
        }
        //@@author HammerHead-os
        // Load notes if present (10-field format); older files without a notes field get empty notes.
        if (fields.size() == FIELD_COUNT_WITH_NOTES) {
            String notes = fields.get(9).equals(NO_VALUE) ? "" : fields.get(9);
            order.setNotes(notes);
        }
        return order;
    }

    //@@author Kaishhhh
    /**
     * Returns true if the 9th field of a 9-field line holds notes rather than a customer ID.
     * Some data files were saved with notes in that position and no customer ID. A value that
     * looks like an ID (one letter followed by digits, such as {@code C001} or {@code X1}) is
     * still treated as a customer ID, so a damaged ID is reported instead of being read as notes.
     */
    private boolean isLegacyNotes(String value) {
        return !value.equals(NO_VALUE) && !value.matches("[A-Za-z][0-9]+");
    }
    //@@author HammerHead-os

    private String escape(String value) {
        return value.replace("\\", "\\\\").replace("|", "\\|");
    }

    /**
     * Splits a saved line on unescaped {@code |} separators and unescapes each field.
     */
    private List<String> splitEscaped(String line) {
        ArrayList<String> fields = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean isEscaped = false;
        for (int i = 0; i < line.length(); i++) {
            char character = line.charAt(i);
            if (isEscaped) {
                current.append(character);
                isEscaped = false;
            } else if (character == '\\') {
                isEscaped = true;
            } else if (character == '|') {
                fields.add(current.toString().trim());
                current.setLength(0);
            } else {
                current.append(character);
            }
        }
        if (isEscaped) {
            current.append('\\');
        }
        fields.add(current.toString().trim());
        return fields;
    }
}
