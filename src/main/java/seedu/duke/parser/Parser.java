package seedu.duke.parser;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses the six required fields of the Add Order command.
 */
public final class Parser {
    private static final List<String> PREFIXES = List.of("n/", "p/", "d/", "s/", "f/", "fi/");
    private static final Pattern FIELD_PREFIX = Pattern.compile("(?<!\\S)([^\\s/]+/)");
    private static final String USAGE = "Use: add n/NAME p/PHONE d/DATE s/SIZE f/FLAVOUR fi/FILLING";
    // Shows the correct format when an update command is invalid.
    // Square brackets indicate optional fields.
    private static final String UPDATE_USAGE =
            "Use: update ORDER_ID [n/NAME] [p/PHONE] [d/DATE] "
                    + "[s/SIZE] [f/FLAVOUR] [fi/FILLING]";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter
            .ofPattern("dd-MM-uuuu")
            .withResolverStyle(ResolverStyle.STRICT);

    private Parser() {
    }

    /**
     * Parses and validates a command containing all six order fields in any order.
     *
     * @param input Command entered by the user.
     * @return Parsed order details.
     * @throws IllegalArgumentException If the command is invalid.
     */
    public static AddOrderDetails parseAdd(String input) {
        if (input == null || input.isBlank()) {
            throw new IllegalArgumentException(USAGE);
        }
        String[] command = input.trim().split("\\s+", 2);
        if (!command[0].equalsIgnoreCase("add") || command.length != 2) {
            throw new IllegalArgumentException(USAGE);
        }
        Map<String, String> fields = parseFields(command[1]);
        LocalDate dueDate;
        try {
            dueDate = LocalDate.parse(fields.get("d/"), DATE_FORMATTER);
        } catch (DateTimeParseException exception) {
            throw new IllegalArgumentException("Date must use DD-MM-YYYY and be valid.");
        }
        int size;
        try {
            size = Integer.parseInt(fields.get("s/"));
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Cake size must be 6, 8, or 10 inches.");
        }
        if (size != 6 && size != 8 && size != 10) {
            throw new IllegalArgumentException("Cake size must be 6, 8, or 10 inches.");
        }
        return new AddOrderDetails(fields.get("n/"), fields.get("p/"), dueDate,
                size, fields.get("f/"), fields.get("fi/"));
    }

    /**
     * Extracts field values between prefixes and rejects missing or repeated fields.
     */
    private static Map<String, String> parseFields(String arguments) {
        Map<String, String> fields = new LinkedHashMap<>();
        Matcher matcher = FIELD_PREFIX.matcher(arguments);
        String previousPrefix = null;
        int valueStart = 0;
        while (matcher.find()) {
            if (previousPrefix == null && !arguments.substring(0, matcher.start()).isBlank()) {
                throw new IllegalArgumentException(USAGE);
            }
            if (previousPrefix != null) {
                fields.put(previousPrefix, arguments.substring(valueStart, matcher.start()).trim());
            }
            String prefix = matcher.group(1);
            if (!PREFIXES.contains(prefix)) {
                throw new IllegalArgumentException("Unknown field: " + prefix);
            }
            if (fields.containsKey(prefix)) {
                throw new IllegalArgumentException("Duplicate field: " + prefix);
            }
            previousPrefix = prefix;
            valueStart = matcher.end();
        }
        if (previousPrefix != null) {
            fields.put(previousPrefix, arguments.substring(valueStart).trim());
        }
        for (String prefix : PREFIXES) {
            if (!fields.containsKey(prefix) || fields.get(prefix).isBlank()) {
                throw new IllegalArgumentException("Missing value for " + prefix);
            }
        }
        return fields;
    }

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
    /**
     * Parses an update command and validates the provided fields.
     * Only fields supplied by the user will be updated.
     *
     * @param input The full command entered by the user.
     * @return The validated update details.
     * @throws IllegalArgumentException If the command is invalid.
     */
    public static UpdateOrderDetails parseUpdate(String input) {

        // Reject null or empty input.
        if (input == null || input.isBlank()) {
            throw new IllegalArgumentException(UPDATE_USAGE);
        }

        // Split into 3 parts:
        // parts[0] = "update"
        // parts[1] = "O001"
        // parts[2] = "n/Bob d/25-10-2026"
        String[] parts = input.trim().split("\\s+", 3);

        // Check that the command is "update", the Order ID is valid,
        // and at least one field argument is present.
        if (parts.length != 3 || !parts[0].equalsIgnoreCase("update")
                || !parts[1].matches("O[0-9]{3,}")) {
            throw new IllegalArgumentException(UPDATE_USAGE);
        }

        // Extract the fields the user wants to update.
        Map<String, String> fields = parseUpdateFields(parts[2]);

        // A null value means the user did not request a date change.
        LocalDate dueDate = null;

        // Validate the date only if the user supplied d/.
        if (fields.containsKey("d/")) {
            try {
                dueDate = LocalDate.parse(fields.get("d/"), DATE_FORMATTER);
            } catch (DateTimeParseException exception) {
                throw new IllegalArgumentException(
                        "Date must use DD-MM-YYYY and be valid.");
            }
        }

        // A null value means the user did not request a size change.
        Integer size = null;

        // Validate the cake size only if the user supplied s/.
        if (fields.containsKey("s/")) {
            try {
                size = Integer.parseInt(fields.get("s/"));
            } catch (NumberFormatException exception) {
                throw new IllegalArgumentException(
                        "Cake size must be 6, 8, or 10 inches.");
            }

            // Only 6, 8 and 10 inch cakes are supported.
            if (size != 6 && size != 8 && size != 10) {
                throw new IllegalArgumentException(
                        "Cake size must be 6, 8, or 10 inches.");
            }
        }

        // Store the validated values in an UpdateOrderDetails object.
        // Fields that were not supplied remain null.
        return new UpdateOrderDetails(
                parts[1],
                fields.get("n/"),
                fields.get("p/"),
                dueDate,
                size,
                fields.get("f/"),
                fields.get("fi/")
        );
    }
    /**
     * Extracts the fields supplied in an update command.
     * Rejects unknown, duplicate, and empty fields.
     *
     * @param arguments The text after the Order ID.
     * @return A map containing only the supplied fields.
     */
    private static Map<String, String> parseUpdateFields(String arguments) {

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
                throw new IllegalArgumentException(UPDATE_USAGE);
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
                throw new IllegalArgumentException("Unknown field: " + prefix);
            }

            // Reject repeated prefixes, such as n/Bob n/Alice.
            if (fields.containsKey(prefix) || prefix.equals(previousPrefix)) {
                throw new IllegalArgumentException("Duplicate field: " + prefix);
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
            throw new IllegalArgumentException(UPDATE_USAGE);
        }

        // Reject fields without values, such as n/ or s/.
        for (Map.Entry<String, String> entry : fields.entrySet()) {
            if (entry.getValue().isBlank()) {
                throw new IllegalArgumentException(
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
