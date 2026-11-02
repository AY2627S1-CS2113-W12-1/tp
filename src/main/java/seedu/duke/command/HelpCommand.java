package seedu.duke.command;

//@@author Eethanzyj
import java.util.Locale;
import java.util.Map;

import seedu.duke.exception.CakeQueueException;
//@@author

/**
 * Shows general or command-specific help for baker commands.
 */
public class HelpCommand {
    //@@author Eethanzyj
    private static final String SEPARATOR = "------------------------------------------------------------";

    /** Keeps each topic's format, essential rules and copyable example together. */
    private static final Map<String, String> TOPICS = Map.ofEntries(
            Map.entry("add", formatHelp(
                    "add n/NAME p/PHONE d/DATE s/SIZE f/FLAVOUR fi/FILLING",
                    "add n/Alice Tan p/91234567 d/30-10-2030 s/8 f/Chocolate fi/Strawberry",
                    "All six fields are required and must be non-empty. Use lowercase prefixes in any order.",
                    "n/NAME: Customer name.",
                    "p/PHONE: At least 8 digits; spaces, + and - are allowed.",
                    "d/DATE: A real date in DD-MM-YYYY format, today or later.",
                    "s/SIZE: 6, 8 or 10 inches.",
                    "f/FLAVOUR: Cake flavour. fi/FILLING: Cake filling.")),
            Map.entry("list", formatHelp("list", "list",
                    "Shows active orders, earliest due date first, including overdue orders.",
                    "Cancelled and collected orders are excluded. Use Order IDs, not list positions.")),
            Map.entry("view", formatHelp("view ORDER_ID", "view O001",
                    "Shows one record's full details. Use an existing Order ID from list or find.")),
            Map.entry("edit", formatHelp(
                    "edit ORDER_ID [n/NAME] [p/PHONE] [d/DATE] [s/SIZE] [f/FLAVOUR] [fi/FILLING]",
                    "edit O001 s/10",
                    "Provide at least one field. Omitted fields stay unchanged.",
                    "The same field rules as add apply. Use help add for those rules.",
                    "Order ID, Customer ID and status stay unchanged.")),
            Map.entry("cancel", formatHelp("cancel ORDER_ID", "cancel O001",
                    "Cancels an order and keeps its record for history.",
                    "Collected, cancelled or rejected records cannot be cancelled.")),
            Map.entry("find", formatHelp("find KEYWORD", "find chocolate",
                    "Searches customer names, cake flavours and fillings, ignoring case.",
                    "Several words are searched as one phrase. Closed records are included.",
                    "Phone numbers, dates and IDs are not searched.")),
            Map.entry("customer", formatHelp("customer CUSTOMER_ID", "customer C001",
                    "Shows customer details, order count and whether they are a repeat customer.",
                    "Use the Customer ID printed by add or view. Cancelled orders count towards repeat status.")),
            Map.entry("history", formatHelp("history CUSTOMER_ID", "history C001",
                    "Shows the customer's orders in the order they were added, including cancelled and collected ones.",
                    "Use the Customer ID printed by add or view.")),
            Map.entry("capacity", formatHelp("capacity [NUMBER]", "capacity 3",
                    "Without NUMBER, shows the daily capacity and dates over the limit.",
                    "NUMBER must be a whole number from 1 to 50. The default limit is 5.",
                    "The limit triggers warnings; it does not block adding orders.")),
            Map.entry("mark", formatHelp("mark ORDER_ID preparing|ready|collected", "mark O001 preparing",
                    "Orders move one step at a time: QUEUED -> PREPARING -> READY -> COLLECTED.",
                    "Choose one status word; do not type the | separators.",
                    "Steps cannot be skipped, repeated or reversed. Closed records cannot be marked.")),
            Map.entry("help", formatHelp("help [COMMAND]", "help add",
                    "Use help for the command list, or help COMMAND for one command's rules and example.",
                    "Help only displays instructions; it does not run the example.")),
            Map.entry("bye", formatHelp("bye", "bye",
                    "Exits CakeQueue. Check for save-failure warnings before closing the session.")));
    //@@author

    /**
     * Returns baker command usage text.
     */
    public String execute() {
        return String.join(System.lineSeparator(),
                "Baker commands:",
                "  add n/NAME p/PHONE d/DATE s/SIZE f/FLAVOUR fi/FILLING",
                "  list",
                "  find KEYWORD",
                "  capacity [NUMBER]",
                "  view ORDER_ID",
                "  edit ORDER_ID [n/NAME] [p/PHONE] [d/DATE] [s/SIZE] [f/FLAVOUR] [fi/FILLING]",
                "  mark ORDER_ID preparing|ready|collected",
                "  cancel ORDER_ID",
                "  customer CUSTOMER_ID",
                "  history CUSTOMER_ID",
                //@@author Eethanzyj
                "  help [COMMAND]",
                //@@author
                "  bye",
                //@@author Eethanzyj
                "",
                "Add Order example:",
                "  add n/Alice Tan p/91234567 d/30-10-2030 s/8 f/Chocolate fi/Strawberry",
                "Use today or a future due date in DD-MM-YYYY format.",
                "",
                "For details and a copyable example, type help COMMAND, such as help add.");
        //@@author
    }

    //@@author Eethanzyj
    /**
     * Returns guidance for one command, or the overview when no topic is supplied.
     *
     * @param topic An optional, case-insensitive command word without the leading help word.
     * @return Help text containing a format, essential rules and one example.
     * @throws CakeQueueException If the topic is unknown or contains more than one word.
     */
    public String execute(String topic) {
        if (topic == null || topic.isBlank()) {
            return execute();
        }
        String command = topic.trim().toLowerCase(Locale.ROOT);
        if (command.split("\\s+").length != 1) {
            throw new CakeQueueException("Use: help [COMMAND]");
        }
        String details = TOPICS.get(command);
        if (details == null) {
            throw new CakeQueueException("Unknown help topic: " + command
                    + ". Type help to see available commands.");
        }
        return String.join(System.lineSeparator(), "Help: " + command, SEPARATOR, details, SEPARATOR);
    }

    /**
     * Builds a topic's text with an example on its own line for copying into the CLI.
     */
    private static String formatHelp(String format, String example, String... rules) {
        String nl = System.lineSeparator();
        return String.join(nl, "Format:", format, "", String.join(nl, rules), "", "Example:", example);
    }
}
