package seedu.duke.command;

/**
 * Shows all commands available in baker mode.
 */
public class HelpCommand {

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
                "  help",
                "  bye");
    }
}
