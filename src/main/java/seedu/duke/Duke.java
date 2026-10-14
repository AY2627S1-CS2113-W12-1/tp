package seedu.duke;

import java.util.Locale;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import seedu.duke.command.ListCommand;
import seedu.duke.command.UpdateOrderCommand;
import seedu.duke.command.ViewOrderCommand;
import seedu.duke.model.Cake;
import seedu.duke.model.Customer;
import seedu.duke.model.Order;
import seedu.duke.model.OrderList;
import seedu.duke.parser.Parser;

/**
 * Runs the CakeQueue command-line application.
 */
public class Duke {
    private static final Logger LOGGER = Logger.getLogger(Duke.class.getName());

    /**
     * Runs CakeQueue until the user enters the bye command.
     */
    public void run() {
        Scanner scanner = new Scanner(System.in);
        OrderList orders = new OrderList();
        System.out.println("Hello! I'm CakeQueue.");
        System.out.println("Commands: add, list, view, edit (or update), bye.");

        while (scanner.hasNextLine()) {
            String input = scanner.nextLine().trim();
            try {
                String commandWord = input.split("\\s+", 2)[0].toLowerCase(Locale.ROOT);
                switch (commandWord) {
                case "bye":
                    requireNoArguments(input, "bye");
                    System.out.println("Bye. Hope to see you again soon!");
                    return;
                case "list":
                    requireNoArguments(input, "list");
                    System.out.println(new ListCommand().execute(orders));
                    break;
                case "view":
                    String orderId = Parser.parseView(input);
                    System.out.println(new ViewOrderCommand(orderId).execute(orders));
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
                    break;
                case "add":
                    System.out.println(addOrder(input, orders));
                    break;
                case "":
                    throw new IllegalArgumentException("Please enter a command.");
                default:
                    throw new IllegalArgumentException("Unknown command: " + commandWord
                            + ". Use add, list, view, edit, or bye.");
                }
            } catch (IllegalArgumentException exception) {
                // Display errors without terminating CakeQueue.
                System.out.println("Error: " + exception.getMessage());
            }
        }
    }

    /**
     * Adds an order only after its input has been validated.
     */
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

    /**
     * Rejects trailing arguments for commands that take no parameters.
     */
    private void requireNoArguments(String input, String commandWord) {
        if (!input.equalsIgnoreCase(commandWord)) {
            throw new IllegalArgumentException("Use: " + commandWord);
        }
    }

    /**
     * Launches CakeQueue.
     */
    public static void main(String[] args) {
        new Duke().run();
    }
}
