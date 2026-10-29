package seedu.duke.parser;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import seedu.duke.exception.CakeQueueException;
import seedu.duke.model.OrderStatus;

/**
 * Parses and validates CakeQueue command input for baker mode.
 */
public final class Parser {
    private static final List<String> PREFIXES = List.of("n/", "p/", "d/", "s/", "f/", "fi/");
    private static final Pattern FIELD_PREFIX = Pattern.compile("(?<!\\S)([^\\s/]+/)");
    private static final String USAGE = "Use: add n/NAME p/PHONE d/DATE s/SIZE f/FLAVOUR fi/FILLING";
    private static final String ENQUIRE_USAGE =
            "Use: enquire n/NAME p/PHONE d/DATE s/SIZE f/FLAVOUR fi/FILLING";
    // The existing update command is also accepted as an alias for edit.
    private static final String UPDATE_USAGE =
            "Use: edit ORDER_ID [n/NAME] [p/PHONE] [d/DATE] "
                    + "[s/SIZE] [f/FLAVOUR] [fi/FILLING]";
    private static final String VIEW_USAGE = "Use: view ORDER_ID|ENQUIRY_ID";
    private static final String MARK_USAGE = "Use: mark ORDER_ID preparing|ready|collected";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter
            .ofPattern("dd-MM-uuuu")
            .withResolverStyle(ResolverStyle.STRICT);

    private Parser() {
    }

    /**
     * Parses and validates an enquire command (same fields as add).
     * The date must not be in the past, and the phone must be digits only (8+ digits).
     */
    public static AddOrderDetails parseEnquire(String input) {
        return parseSixFieldCommand(input, "enquire", ENQUIRE_USAGE);
    }

    /**
     * Parses {@code search QUERY} where QUERY is a name fragment or phone number.
     *
     * @return The trimmed search query string.
     */
    public static String parseSearch(String input) {
        if (input == null || input.isBlank()) {
            throw new CakeQueueException("Use: search NAME|PHONE");
        }
        String[] parts = input.trim().split("\\s+", 2);
        if (!parts[0].equalsIgnoreCase("search") || parts.length < 2 || parts[1].isBlank()) {
            throw new CakeQueueException("Use: search NAME|PHONE");
        }
        return parts[1].trim();
    }

    /**
     * Parses {@code delete ORDER_ID|ENQUIRY_ID}.
     */
    public static String parseDelete(String input) {
        return parseSingleIdCommand(input, "delete", "[EeOo][0-9]{3,}",
                "Use: delete ORDER_ID|ENQUIRY_ID");
    }

    /**
     * Parses {@code note ORDER_ID text...} or {@code note ORDER_ID -clear}.
     * Returns a {@link NoteDetails} carrying the record ID and the note text
     * (empty string when {@code -clear} was given).
     */
    public static NoteDetails parseNote(String input) {
        if (input == null || input.isBlank()) {
            throw new CakeQueueException("Use: note ORDER_ID TEXT  (or  note ORDER_ID -clear)");
        }
        String[] parts = input.trim().split("\\s+", 3);
        if (parts.length < 3 || !parts[0].equalsIgnoreCase("note")
                || !parts[1].matches("[EeOo][0-9]{3,}")) {
            throw new CakeQueueException("Use: note ORDER_ID TEXT  (or  note ORDER_ID -clear)");
        }
        String recordId = parts[1].toUpperCase(Locale.ROOT);
        String text = parts[2].trim().equals("-clear") ? "" : parts[2].trim();
        return new NoteDetails(recordId, text);
    }

    /**
     * Stores the record ID and note text for a note command.
     */
    public static final class NoteDetails {
        private final String recordId;
        private final String text;

        private NoteDetails(String recordId, String text) {
            this.recordId = recordId;
            this.text = text;
        }

        public String getRecordId() {
            return recordId;
        }

        public String getText() {
            return text;
        }
    }

    /**
     * Parses a baker mark command, e.g., {@code mark O001 preparing}.
     * Only the three production statuses may be requested; whether the step
     * is allowed from the order's current status is checked by the command.
     */
    public static MarkDetails parseMark(String input) {
        if (input == null || input.isBlank()) {
            throw new CakeQueueException(MARK_USAGE);
        }
        String[] parts = input.trim().split("\\s+");
        if (parts.length != 3 || !parts[0].equalsIgnoreCase("mark")
                || !parts[1].matches("[Oo][0-9]{3,}")) {
            throw new CakeQueueException(MARK_USAGE);
        }
        OrderStatus target;
        try {
            target = OrderStatus.valueOf(parts[2].toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            throw new CakeQueueException(MARK_USAGE, exception);
        }
        if (target != OrderStatus.PREPARING && target != OrderStatus.READY
                && target != OrderStatus.COLLECTED) {
            throw new CakeQueueException(MARK_USAGE);
        }
        return new MarkDetails(parts[1].toUpperCase(Locale.ROOT), target);
    }

    /**
     * Stores a mark command's order ID and requested status.
     */
    public static final class MarkDetails {
        private final String orderId;
        private final OrderStatus targetStatus;

        private MarkDetails(String orderId, OrderStatus targetStatus) {
            this.orderId = orderId;
            this.targetStatus = targetStatus;
        }

        public String getOrderId() {
            return orderId;
        }

        public OrderStatus getTargetStatus() {
            return targetStatus;
        }
    }

    /**
     * Parses a view command and returns the order or enquiry ID.
     *
     * @param input Command entered by the user.
     * @return The ID to view.
     * @throws CakeQueueException If the command is invalid.
     */
    public static String parseView(String input) {
        if (input == null || input.isBlank()) {
            throw new CakeQueueException(VIEW_USAGE);
        }
        String[] parts = input.trim().split("\\s+");
        if (parts.length != 2 || !parts[0].equalsIgnoreCase("view")
                || !parts[1].matches("[EeOo][0-9]{3,}")) {
            throw new CakeQueueException(VIEW_USAGE);
        }
        return parts[1].toUpperCase(Locale.ROOT);
    }

    /**
     * Parses and validates a baker add command.
     */
    public static AddOrderDetails parseAdd(String input) {
        return parseSixFieldCommand(input, "add", USAGE);
    }

    /**
     * Parses accept ENQUIRY_ID.
     */
    public static String parseAccept(String input) {
        return parseSingleIdCommand(input, "accept", "[Ee][0-9]{3,}",
                "Use: accept ENQUIRY_ID");
    }

    /**
     * Parses reject ENQUIRY_ID.
     */
    public static String parseReject(String input) {
        return parseSingleIdCommand(input, "reject", "[Ee][0-9]{3,}",
                "Use: reject ENQUIRY_ID");
    }

    /**
     * Parses cancel ORDER_ID|ENQUIRY_ID.
     */
    public static String parseBakerCancel(String input) {
        return parseSingleIdCommand(input, "cancel", "[EeOo][0-9]{3,}",
                "Use: cancel ORDER_ID|ENQUIRY_ID");
    }

    private static AddOrderDetails parseSixFieldCommand(String input, String commandWord,
                                                        String usage) {
        if (input == null || input.isBlank()) {
            throw new CakeQueueException(usage);
        }
        String[] command = input.trim().split("\\s+", 2);
        if (!command[0].equalsIgnoreCase(commandWord) || command.length != 2) {
            throw new CakeQueueException(usage);
        }
        Map<String, String> fields = parseFields(command[1], usage);
        LocalDate dueDate;
        try {
            dueDate = LocalDate.parse(fields.get("d/"), DATE_FORMATTER);
        } catch (DateTimeParseException exception) {
            throw new CakeQueueException("Date must use DD-MM-YYYY and be valid.", exception);
        }
        // Reject dates in the past — a baker cannot bake for yesterday.
        if (dueDate.isBefore(LocalDate.now())) {
            throw new CakeQueueException("Due date cannot be in the past.");
        }
        validatePhone(fields.get("p/"));
        int size = parseCakeSize(fields.get("s/"));
        return new AddOrderDetails(fields.get("n/"), fields.get("p/"), dueDate,
                size, fields.get("f/"), fields.get("fi/"));
    }

    /**
     * Validates that a phone number contains only digits and is at least 8 digits long.
     * This catches obvious typos (letters, spaces) without enforcing a specific country format.
     */
    private static void validatePhone(String phone) {
        if (!phone.matches("\\d{8,}")) {
            throw new CakeQueueException(
                    "Phone must be at least 8 digits and contain only numbers.");
        }
    }

    private static String parseSingleIdCommand(String input, String commandWord,
                                               String idPattern, String usage) {
        if (input == null || input.isBlank()) {
            throw new CakeQueueException(usage);
        }
        String[] parts = input.trim().split("\\s+");
        if (parts.length != 2 || !parts[0].equalsIgnoreCase(commandWord)
                || !parts[1].matches(idPattern)) {
            throw new CakeQueueException(usage);
        }
        return parts[1].toUpperCase(Locale.ROOT);
    }

    private static int parseCakeSize(String sizeText) {
        int size;
        try {
            size = Integer.parseInt(sizeText);
        } catch (NumberFormatException exception) {
            throw new CakeQueueException("Cake size must be 6, 8, or 10 inches.", exception);
        }
        if (size != 6 && size != 8 && size != 10) {
            throw new CakeQueueException("Cake size must be 6, 8, or 10 inches.");
        }
        return size;
    }

    /**
     * Extracts field values between prefixes and rejects missing or repeated fields.
     */
    private static Map<String, String> parseFields(String arguments, String usage) {
        Map<String, String> fields = new LinkedHashMap<>();
        Matcher matcher = FIELD_PREFIX.matcher(arguments);
        String previousPrefix = null;
        int valueStart = 0;
        while (matcher.find()) {
            if (previousPrefix == null && !arguments.substring(0, matcher.start()).isBlank()) {
                throw new CakeQueueException(usage);
            }
            if (previousPrefix != null) {
                fields.put(previousPrefix, arguments.substring(valueStart, matcher.start()).trim());
            }
            String prefix = matcher.group(1);
            if (!PREFIXES.contains(prefix)) {
                throw new CakeQueueException("Unknown field: " + prefix);
            }
            if (fields.containsKey(prefix)) {
                throw new CakeQueueException("Duplicate field: " + prefix);
            }
            previousPrefix = prefix;
            valueStart = matcher.end();
        }
        if (previousPrefix != null) {
            fields.put(previousPrefix, arguments.substring(valueStart).trim());
        }
        for (String prefix : PREFIXES) {
            if (!fields.containsKey(prefix) || fields.get(prefix).isBlank()) {
                throw new CakeQueueException("Missing value for " + prefix);
            }
        }
        return fields;
    }

    //@@author
    /**
     * Stores the values extracted from an Add Order command.
     */
    public static final class AddOrderDetails {
        private final String customerName;
        private final String phone;
        private final LocalDate dueDate;
        private final int size;
        private final String flavour;
        private final String filling;

        private AddOrderDetails(String customerName, String phone, LocalDate dueDate,
                                int size, String flavour, String filling) {
            this.customerName = customerName;
            this.phone = phone;
            this.dueDate = dueDate;
            this.size = size;
            this.flavour = flavour;
            this.filling = filling;
        }

        public String getCustomerName() {
            return customerName;
        }

        public LocalDate getDueDate() {
            return dueDate;
        }

        public String getPhone() {
            return phone;
        }

        public int getSize() {
            return size;
        }

        public String getFlavour() {
            return flavour;
        }

        public String getFilling() {
            return filling;
        }
    }

    //@@author WillChin484848
    /**
     * Parses an edit command or its update alias and validates the provided fields.
     * Only fields supplied by the user will be updated.
     *
     * @param input The full command entered by the user.
     * @return The validated update details.
     * @throws CakeQueueException If the command is invalid.
     */
    public static UpdateOrderDetails parseUpdate(String input) {

        // Reject null or empty input.
        if (input == null || input.isBlank()) {
            throw new CakeQueueException(UPDATE_USAGE);
        }

        // Split into 3 parts:
        // parts[0] = "edit" or "update"
        // parts[1] = "O001"
        // parts[2] = "n/Bob d/25-10-2026"
        String[] parts = input.trim().split("\\s+", 3);

        // Check that the command is "edit" or "update", the Order ID is valid,
        // and at least one field argument is present.
        if (parts.length != 3
                || (!parts[0].equalsIgnoreCase("edit") && !parts[0].equalsIgnoreCase("update"))
                || !parts[1].matches("[Oo][0-9]{3,}")) {
            throw new CakeQueueException(UPDATE_USAGE);
        }

        // Extract the fields the user wants to update.
        Map<String, String> fields = parseUpdateFields(parts[2], UPDATE_USAGE);

        // A null value means the user did not request a date change.
        LocalDate dueDate = null;

        // Validate the date only if the user supplied d/.
        if (fields.containsKey("d/")) {
            try {
                dueDate = LocalDate.parse(fields.get("d/"), DATE_FORMATTER);
            } catch (DateTimeParseException exception) {
                throw new CakeQueueException(
                        "Date must use DD-MM-YYYY and be valid.", exception);
            }
        }

        // A null value means the user did not request a size change.
        Integer size = null;

        // Validate the cake size only if the user supplied s/.
        if (fields.containsKey("s/")) {
            size = parseCakeSize(fields.get("s/"));
        }

        // Store the validated values in an UpdateOrderDetails object.
        // Fields that were not supplied remain null.
        return new UpdateOrderDetails(
                parts[1].toUpperCase(Locale.ROOT),
                fields.get("n/"),
                fields.get("p/"),
                dueDate,
                size,
                fields.get("f/"),
                fields.get("fi/")
        );
    }

    /**
     * Extracts the fields supplied in an update-style command.
     * Rejects unknown, duplicate, and empty fields.
     *
     * @param arguments The text after the command word / ID.
     * @param usage Usage message for malformed input.
     * @return A map containing only the supplied fields.
     */
    private static Map<String, String> parseUpdateFields(String arguments, String usage) {

        // Store each prefix and its corresponding value.
        // Example: "n/Bob s/8" becomes {"n/"="Bob", "s/"="8"}.
        Map<String, String> fields = new LinkedHashMap<>();

        // Find field prefixes such as n/, p/, d/, s/, f/, fi/.
        Matcher matcher = FIELD_PREFIX.matcher(arguments);

        // Track the previous prefix so we know where its value ends.
        String previousPrefix = null;
        int valueStart = 0;

        while (matcher.find()) {

            // Reject unexpected text before the first field prefix.
            if (previousPrefix == null
                    && !arguments.substring(0, matcher.start()).isBlank()) {
                throw new CakeQueueException(usage);
            }

            // Save the value belonging to the previous prefix.
            if (previousPrefix != null) {
                fields.put(previousPrefix,
                        arguments.substring(valueStart, matcher.start()).trim());
            }

            // Read the current prefix.
            String prefix = matcher.group(1);

            // Reject prefixes not supported by CakeQueue.
            if (!PREFIXES.contains(prefix)) {
                throw new CakeQueueException("Unknown field: " + prefix);
            }

            // Reject repeated prefixes, such as n/Bob n/Alice.
            if (fields.containsKey(prefix) || prefix.equals(previousPrefix)) {
                throw new CakeQueueException("Duplicate field: " + prefix);
            }

            // Remember this prefix and where its value starts.
            previousPrefix = prefix;
            valueStart = matcher.end();
        }

        // Save the value of the final field.
        if (previousPrefix != null) {
            fields.put(previousPrefix, arguments.substring(valueStart).trim());
        }

        // An update command must contain at least one field.
        if (fields.isEmpty()) {
            throw new CakeQueueException(usage);
        }

        // Reject fields without values, such as n/ or s/.
        for (Map.Entry<String, String> entry : fields.entrySet()) {
            if (entry.getValue().isBlank()) {
                throw new CakeQueueException(
                        "Missing value for " + entry.getKey());
            }
        }

        return fields;
    }

    /**
     * Stores the validated details of an update command.
     * A null field means that the original value should be preserved.
     */
    public static final class UpdateOrderDetails {

        // Identifies which order to update.
        private final String orderId;

        // Optional fields that the user may update.
        private final String name;
        private final String phone;
        private final LocalDate dueDate;
        private final Integer size;
        private final String flavour;
        private final String filling;

        /**
         * Creates an object containing the requested changes.
         */
        private UpdateOrderDetails(String orderId, String name, String phone,
                                   LocalDate dueDate, Integer size,
                                   String flavour, String filling) {
            this.orderId = orderId;
            this.name = name;
            this.phone = phone;
            this.dueDate = dueDate;
            this.size = size;
            this.flavour = flavour;
            this.filling = filling;
        }

        // Getters allow UpdateOrderCommand to access the parsed values.

        public String getOrderId() {
            return orderId;
        }

        public String getName() {
            return name;
        }

        public String getPhone() {
            return phone;
        }

        public LocalDate getDueDate() {
            return dueDate;
        }

        public Integer getSize() {
            return size;
        }

        public String getFlavour() {
            return flavour;
        }

        public String getFilling() {
            return filling;
        }
    }
}
