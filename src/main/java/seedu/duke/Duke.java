package seedu.duke;

import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

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

        while (scanner.hasNextLine()) {
            String input = scanner.nextLine().trim();
            if (input.equalsIgnoreCase("bye")) {
                System.out.println("Bye. Hope to see you again soon!");
                return;
            }

            try {
                // Check whether the user wants to view an order.
                if (input.toLowerCase(java.util.Locale.ROOT).startsWith("view")) {
                    String orderId = Parser.parseView(input);
                    System.out.println(new ViewOrderCommand(orderId).execute(orders));
                    continue;
                }

                // Check whether the user wants to update an existing order.
                if (input.equalsIgnoreCase("update")
                        || input.toLowerCase(java.util.Locale.ROOT).startsWith("update ")) {

                    // Parse the Order ID and fields that need updating.
                    Parser.UpdateOrderDetails details = Parser.parseUpdate(input);

                    // Create the command using the validated update details.
                    UpdateOrderCommand command = new UpdateOrderCommand(
                            details.getOrderId(),
                            details.getName(),
                            details.getPhone(),
                            details.getDueDate(),
                            details.getSize(),
                            details.getFlavour(),
                            details.getFilling()
                    );

                    // Execute the update and display the updated order.
                    System.out.println(command.execute(orders));

                } else {
                    // Existing Add Order implementation by Ethan.
                    Parser.AddOrderDetails details = Parser.parseAdd(input);

                    Order order = new Order(orders.generateOrderId(),
                            new Customer(details.getCustomerName(), details.getPhone()),
                            new Cake(details.getSize(), details.getFlavour(), details.getFilling()),
                            details.getDueDate());

                    orders.add(order);

                    LOGGER.log(Level.INFO, "Added order {0}.", order.getOrderId());
                    System.out.println("Added order " + order);
                }

            } catch (IllegalArgumentException exception) {
                // Display errors without terminating CakeQueue.
                System.out.println("Error: " + exception.getMessage());
            }
        }
    }

    /**
     * Launches CakeQueue.
     */
    public static void main(String[] args) {
        new Duke().run();
    }
}
