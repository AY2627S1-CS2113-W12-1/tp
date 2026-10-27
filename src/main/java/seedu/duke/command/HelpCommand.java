package seedu.duke.command;

//@@author HammerHead-os

import seedu.duke.UserMode;

/**
 * Shows the commands available in the current user mode.
 */
public class HelpCommand {
    private final UserMode mode;

    public HelpCommand(UserMode mode) {
        this.mode = mode;
    }

    /**
     * Returns mode-specific usage text.
     */
    public String execute() {
        if (mode == UserMode.BAKER) {
            return String.join(System.lineSeparator(),
                    "Baker commands:",
                    "  add n/NAME p/PHONE d/DATE s/SIZE f/FLAVOUR fi/FILLING",
                    "  list",
                    "  enquiries",
                    "  view ORDER_ID|ENQUIRY_ID",
                    "  edit ORDER_ID [n/NAME] [p/PHONE] [d/DATE] [s/SIZE] [f/FLAVOUR] [fi/FILLING]",
                    "  accept ENQUIRY_ID",
                    "  mark ORDER_ID preparing|ready|collected",
                    "  reject ENQUIRY_ID",
                    "  cancel ORDER_ID|ENQUIRY_ID",
                    "  customer CUSTOMER_ID",
                    "  history CUSTOMER_ID",
                    "  mode customer",
                    "  help",
                    "  bye");
        }
        return String.join(System.lineSeparator(),
                "Customer commands:",
                "  enquire n/NAME p/PHONE d/DATE s/SIZE f/FLAVOUR fi/FILLING",
                "  mine p/PHONE",
                "  track ID p/PHONE",
                "  edit ENQUIRY_ID p/PHONE [n/NAME] [d/DATE] [s/SIZE] [f/FLAVOUR] [fi/FILLING]",
                "  cancel ENQUIRY_ID p/PHONE",
                "  mode baker",
                "  help",
                "  bye");
    }
}
