package seedu.duke.parser;

//@@author Eethanzyj

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;

import seedu.duke.exception.CakeQueueException;

/**
 * Parses the six required Add Order fields without creating orders or changing stored data.
 * Shared field rules remain in Parser so Add Order and Edit Order use the same validation.
 */
public final class AddOrderParser {
    private static final String USAGE = "Use: add n/NAME p/PHONE d/DATE s/SIZE f/FLAVOUR fi/FILLING";

    /** Gives a field's purpose and syntax when a required value is missing. */
    private static final Map<String, String> FIELD_GUIDANCE = Map.of(
            "n/", "a customer name using n/NAME",
            "p/", "a phone number using p/PHONE",
            "d/", "a due date using d/DD-MM-YYYY",
            "s/", "a cake size using s/6, s/8, or s/10",
            "f/", "a cake flavour using f/FLAVOUR",
            "fi/", "a cake filling using fi/FILLING");

    private AddOrderParser() {
    }

    /**
     * Validates an Add Order command and returns its customer and cake details.
     *
     * @param input The complete command entered by the baker.
     * @return Validated details, ready for AddOrderCommand to execute.
     * @throws CakeQueueException If the command or any required field is invalid.
     */
    public static Parser.AddOrderDetails parse(String input) {
        //@@author HammerHead-os
        if (input == null || input.isBlank()) {
            throw new CakeQueueException(USAGE);
        }
        //@@author Eethanzyj
        String[] command = input.trim().split("\\s+", 2);
        //@@author HammerHead-os
        if (!command[0].equalsIgnoreCase("add") || command.length != 2) {
            throw new CakeQueueException(USAGE);
        }
        Map<String, String> fields = parseFields(command[1], USAGE);
        //@@author Eethanzyj
        LocalDate dueDate;
        try {
            dueDate = LocalDate.parse(fields.get("d/"), Parser.DATE_FORMATTER);
        } catch (DateTimeParseException exception) {
            //@@author HammerHead-os
            throw new CakeQueueException("Date must use DD-MM-YYYY and be valid.", exception);
        }
        // Reject dates in the past - a baker cannot bake for yesterday.
        if (dueDate.isBefore(LocalDate.now())) {
            throw new CakeQueueException("Due date cannot be in the past.");
        }
        Parser.validatePhone(fields.get("p/"));
        int size = Parser.parseCakeSize(fields.get("s/"));
        return new Parser.AddOrderDetails(fields.get("n/"), fields.get("p/"), dueDate,
                size, fields.get("f/"), fields.get("fi/"));
    }

    //@@author Eethanzyj
    /**
     * Extracts field values between prefixes and rejects missing or repeated fields.
     */
    //@@author HammerHead-os
    private static Map<String, String> parseFields(String arguments, String usage) {
        //@@author Eethanzyj
        Map<String, String> fields = new LinkedHashMap<>();
        Matcher matcher = Parser.FIELD_PREFIX.matcher(arguments);
        String previousPrefix = null;
        int valueStart = 0;
        while (matcher.find()) {
            if (previousPrefix == null && !arguments.substring(0, matcher.start()).isBlank()) {
                //@@author HammerHead-os
                throw new CakeQueueException(usage);
                //@@author Eethanzyj
            }
            if (previousPrefix != null) {
                fields.put(previousPrefix, arguments.substring(valueStart, matcher.start()).trim());
            }
            String prefix = matcher.group(1);
            if (!Parser.PREFIXES.contains(prefix)) {
                throw new CakeQueueException("Unknown field: " + prefix
                        + ". Supported prefixes: " + String.join(", ", Parser.PREFIXES) + ".");
            }
            if (fields.containsKey(prefix)) {
                throw new CakeQueueException("Duplicate field: " + prefix + ". Supply each field once.");
            }
            previousPrefix = prefix;
            valueStart = matcher.end();
        }
        if (previousPrefix != null) {
            fields.put(previousPrefix, arguments.substring(valueStart).trim());
        }
        for (String prefix : Parser.PREFIXES) {
            if (!fields.containsKey(prefix) || fields.get(prefix).isBlank()) {
                throw new CakeQueueException("Missing value for " + prefix
                        + ". Please provide " + FIELD_GUIDANCE.get(prefix) + ".");
            }
        }
        return fields;
    }
}
