package seedu.duke;

//@@author HammerHead-os

import java.util.Locale;
import java.util.Optional;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import seedu.duke.command.AcceptEnquiryCommand;
import seedu.duke.command.CancelCommand;
import seedu.duke.command.CustomerCommand;
import seedu.duke.command.FindCommand;
import seedu.duke.command.HelpCommand;
import seedu.duke.command.HistoryCommand;
import seedu.duke.command.ListCommand;
import seedu.duke.command.ListEnquiriesCommand;
import seedu.duke.command.MarkCommand;
import seedu.duke.command.RejectEnquiryCommand;
import seedu.duke.command.UpdateOrderCommand;
import seedu.duke.command.ViewOrderCommand;
import seedu.duke.exception.CakeQueueException;
import seedu.duke.model.Cake;
import seedu.duke.model.Customer;
import seedu.duke.model.CustomerList;
import seedu.duke.model.CustomerProfile;
import seedu.duke.model.Order;
import seedu.duke.model.OrderList;
import seedu.duke.parser.Parser;

/**
 * Runs the baker-facing CakeQueue commands.
 */
public class BakerCli {
    private static final Logger LOGGER = Logger.getLogger(BakerCli.class.getName());

    /**
     * Handles baker commands until the user exits or switches mode.
     * {@code afterChange} is run after every successful change so the caller
     * can persist the shared order list.
     *
     * @return The next mode to enter, or empty to exit the application.
     */
    public Optional<UserMode> run(Scanner scanner, OrderList orders, Runnable afterChange) {
        assert scanner != null && orders != null && afterChange != null
                : "Scanner, order list and save callback must not be null";
        System.out.println("Baker mode. Type help to see commands.");
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
                    System.out.println(new HelpCommand(UserMode.BAKER).execute());
                    break;
                case "list":
                    requireNoArguments(input, "list");
                    System.out.println(new ListCommand().execute(orders));
                    break;
                case "find":
                    System.out.println(new FindCommand(Parser.parseFind(input)).execute(orders));
                    break;
                case "enquiries":
                    requireNoArguments(input, "enquiries");
                    System.out.println(new ListEnquiriesCommand().execute(orders));
                    break;
                case "view":
                    String recordId = Parser.parseView(input);
                    System.out.println(new ViewOrderCommand(recordId).execute(orders));
                    break;
                case "edit":
                case "update":
                    Parser.UpdateOrderDetails details = Parser.parseUpdate(input);
                    UpdateOrderCommand command = new UpdateOrderCommand(
                            details.getOrderId(),
                            details.getName(),
                            details.getPhone(),
                            details.getDueDate(),
                            details.getSize(),
                            details.getFlavour(),
                            details.getFilling()
                    );
                    System.out.println(command.execute(orders));
                    afterChange.run();
                    break;
                case "add":
                    System.out.println(addOrderAndReportCustomer(input, orders));
                    afterChange.run();
                    break;
                case "mark":
                    Parser.MarkDetails mark = Parser.parseMark(input);
                    System.out.println(new MarkCommand(mark.getOrderId(), mark.getTargetStatus())
                            .execute(orders));
                    afterChange.run();
                    break;
                case "accept":
                    System.out.println(new AcceptEnquiryCommand(Parser.parseAccept(input)).execute(orders));
                    afterChange.run();
                    break;
                case "reject":
                    System.out.println(new RejectEnquiryCommand(Parser.parseReject(input)).execute(orders));
                    afterChange.run();
                    break;
                case "cancel":
                    System.out.println(new CancelCommand(Parser.parseBakerCancel(input)).execute(orders));
                    afterChange.run();
                    break;
                //@@author anannya-mk
                case "customer":
                    System.out.println(new CustomerCommand(Parser.parseCustomer(input)).execute(orders));
                    break;
                case "history":
                    System.out.println(new HistoryCommand(Parser.parseHistory(input)).execute(orders));
                    break;
                //@@author HammerHead-os
                case "":
                    throw new CakeQueueException("Please enter a command.");
                default:
                    throw new CakeQueueException("Unknown baker command: " + commandWord
                            + ". Type help to see baker commands.");
                }
            } catch (CakeQueueException exception) {
                System.out.println("Error: " + exception.getMessage());
            }
        }
        return Optional.empty();
    }

    //@@author anannya-mk
    /**
     * Adds an order, then tells the baker which customer it belongs to and whether they are new.
     */
    private String addOrderAndReportCustomer(String input, OrderList orders) {
        String phone = Parser.parseAdd(input).getPhone();
        boolean isNewCustomer = CustomerList.fromOrders(orders).findByPhone(phone).isEmpty();
        String added = addOrder(input, orders);
        CustomerProfile customer = CustomerList.fromOrders(orders).findByPhone(phone).orElseThrow();
        return added + System.lineSeparator() + "Customer ID: " + customer.getCustomerId()
                + (isNewCustomer ? " (new customer)" : " (returning customer)");
    }

    //@@author HammerHead-os
    private String addOrder(String input, OrderList orders) {
        Parser.AddOrderDetails details = Parser.parseAdd(input);
        Order order = new Order(orders.generateOrderId(),
                new Customer(details.getCustomerName(), details.getPhone()),
                new Cake(details.getSize(), details.getFlavour(), details.getFilling()),
                details.getDueDate());
        orders.add(order);
        LOGGER.log(Level.INFO, "Added order {0}.", order.getOrderId());
        return "Added order " + order;
    }

    private void requireNoArguments(String input, String commandWord) {
        if (!input.equalsIgnoreCase(commandWord)) {
            throw new CakeQueueException("Use: " + commandWord);
        }
    }
}
