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
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter
            .ofPattern("dd-MM-uuuu")
            .withResolverStyle(ResolverStyle.STRICT);

    private Parser() {
    }

    /**
     * Parses a view command and returns the order ID.
     *
     * @param input Command entered by the user.
     * @return The order ID to view.
     * @throws IllegalArgumentException If the command is invalid.
     */
    public static String parseView(String input) {
        String[] parts = input.trim().split("\\s+");
        if (parts.length != 2 || !parts[0].equalsIgnoreCase("view")) {
            throw new IllegalArgumentException("Use: view ORDER_ID");
        }
        return parts[1];
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
}
