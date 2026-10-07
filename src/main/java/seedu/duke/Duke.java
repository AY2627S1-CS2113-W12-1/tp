package seedu.duke;

import java.util.Scanner;

import seedu.duke.model.Cake;
import seedu.duke.model.Customer;
import seedu.duke.model.Menu;
import seedu.duke.model.Order;
import seedu.duke.model.OrderList;

public class Duke {
    /**
     * Main entry-point for the java.duke.Duke application.
     */
    public static void main(String[] args) {
        String banner = " ____        _        \n"
                + "|  _ \\ _   _| | _____ \n"
                + "| | | | | | | |/ / _ \\\n"
                + "| |_| | |_| |   <  __/\n"
                + "|____/ \\__,_|_|\\_\\___|\n";
        System.out.println(banner);
        System.out.println("What is your name?");

        Scanner in = new Scanner(System.in);
        System.out.println("Hello " + in.nextLine());

        Menu menu = new Menu();
        menu.add(new Cake("Chocolate", "chocolate", 25.00));
        menu.add(new Cake("Strawberry", "strawberry", 28.00));

        OrderList orders = new OrderList();
        orders.add(new Order(new Customer("Alex", "91234567"), menu.get(1), 1));

        System.out.println("Menu:");
        System.out.println(menu);
        System.out.println("Orders:");
        System.out.println(orders);
    }
}
