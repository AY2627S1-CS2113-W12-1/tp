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
                "  enquire n/NAME p/PHONE d/DATE s/SIZE f/FLAVOUR fi/FILLING",
                "  list",
                "  enquiries",
                "  stats",
                "  find KEYWORD",
                "  capacity [NUMBER]",
                "  view ORDER_ID|ENQUIRY_ID",
                "  edit ORDER_ID [n/NAME] [p/PHONE] [d/DATE] [s/SIZE] [f/FLAVOUR] [fi/FILLING]",
                "  note ORDER_ID TEXT  (or  note ORDER_ID -clear)",
                "  accept ENQUIRY_ID",
                "  mark ORDER_ID preparing|ready|collected",
                "  reject ENQUIRY_ID",
                "  cancel ORDER_ID|ENQUIRY_ID",
                "  delete ORDER_ID|ENQUIRY_ID",
                "  customer CUSTOMER_ID",
                "  history CUSTOMER_ID",
                "  help",
                "  bye");
    }
}
