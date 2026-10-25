package seedu.duke;

//@@author HammerHead-os

import java.util.Locale;
import java.util.Optional;
import java.util.Scanner;

import seedu.duke.command.CancelCommand;
import seedu.duke.command.CustomerEditCommand;
import seedu.duke.command.EnquireCommand;
import seedu.duke.command.HelpCommand;
import seedu.duke.command.MineCommand;
import seedu.duke.command.TrackCommand;
import seedu.duke.exception.CakeQueueException;
import seedu.duke.model.OrderList;
import seedu.duke.parser.Parser;

/**
 * Runs the customer-facing CakeQueue commands.
 */
public class CustomerCli {

    /**
     * Handles customer commands until the user exits or switches mode.
     * {@code afterChange} is run after every successful change so the caller
     * can persist the shared order list.
     *
     * @return The next mode to enter, or empty to exit the application.
     */
    public Optional<UserMode> run(Scanner scanner, OrderList orders, Runnable afterChange) {
        assert scanner != null && orders != null && afterChange != null
                : "Scanner, order list and save callback must not be null";
        System.out.println("Customer mode. Type help to see commands.");
        while (scanner.hasNextLine()) {
            String input = scanner.nextLine().trim();
            try {
                String commandWord = input.split("\\s+", 2)[0].toLowerCase(Locale.ROOT);
                switch (commandWord) {
                case "bye":
                    requireNoArguments(input, "bye");
                    System.out.println("Bye. Hope to see you again soon!");
                    return Optional.empty();
                case "mode":
                    UserMode nextMode = Parser.parseModeCommand(input);
                    System.out.println("Switching to " + nextMode.name().toLowerCase(Locale.ROOT)
                            + " mode.");
                    return Optional.of(nextMode);
                case "help":
                    requireNoArguments(input, "help");
                    System.out.println(new HelpCommand(UserMode.CUSTOMER).execute());
                    break;
                case "enquire":
                    Parser.AddOrderDetails details = Parser.parseEnquire(input);
                    System.out.println(new EnquireCommand(
                            details.getCustomerName(),
                            details.getPhone(),
                            details.getDueDate(),
                            details.getSize(),
                            details.getFlavour(),
                            details.getFilling()
                    ).execute(orders));
                    afterChange.run();
                    break;
                case "mine":
                    System.out.println(new MineCommand(Parser.parseMine(input)).execute(orders));
                    break;
                case "track":
                    Parser.TrackDetails track = Parser.parseTrack(input);
                    System.out.println(new TrackCommand(track.getRecordId(), track.getPhone())
                            .execute(orders));
                    break;
                case "edit":
                    Parser.CustomerEditDetails edit = Parser.parseCustomerEdit(input);
                    System.out.println(new CustomerEditCommand(
                            edit.getEnquiryId(),
                            edit.getPhone(),
                            edit.getName(),
                            edit.getDueDate(),
                            edit.getSize(),
                            edit.getFlavour(),
                            edit.getFilling()
                    ).execute(orders));
                    afterChange.run();
                    break;
                case "cancel":
                    Parser.CustomerCancelDetails cancel = Parser.parseCustomerCancel(input);
                    System.out.println(new CancelCommand(cancel.getRecordId(), cancel.getPhone())
                            .execute(orders));
                    afterChange.run();
                    break;
                case "":
                    throw new CakeQueueException("Please enter a command.");
                default:
                    throw new CakeQueueException("Unknown customer command: " + commandWord
                            + ". Type help to see customer commands.");
                }
            } catch (CakeQueueException exception) {
                System.out.println("Error: " + exception.getMessage());
            }
        }
        return Optional.empty();
    }

    private void requireNoArguments(String input, String commandWord) {
        if (!input.equalsIgnoreCase(commandWord)) {
            throw new CakeQueueException("Use: " + commandWord);
        }
    }
}
