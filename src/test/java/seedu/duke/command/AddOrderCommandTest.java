package seedu.duke.command;

//@@author Eethanzyj

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.junit.jupiter.api.Test;

import seedu.duke.model.Cake;
import seedu.duke.model.Customer;
import seedu.duke.model.Order;
import seedu.duke.model.OrderList;
import seedu.duke.model.OrderStatus;
import seedu.duke.parser.Parser;

/**
 * Checks order creation, customer identity and confirmation formatting without running the CLI.
 */
public class AddOrderCommandTest {
    private static final LocalDate DUE_DATE = LocalDate.now().plusDays(30);
    private static final String INPUT = "add n/Alice Tan p/91234567 d/"
            + DUE_DATE.format(DateTimeFormatter.ofPattern("dd-MM-uuuu"))
            + " s/8 f/Chocolate fi/Strawberry";

    @Test
    public void execute_newCustomer_storesCompleteOrderAndReturnsConfirmation() {
        OrderList orders = new OrderList();
        orders.setDailyCapacity(1);

        String result = new AddOrderCommand(Parser.parseAdd(INPUT)).execute(orders);

        assertEquals(1, orders.size());
        Order added = orders.getById("O001");
        assertEquals("Alice Tan", added.getCustomer().getName());
        assertEquals("91234567", added.getCustomer().getPhone());
        assertEquals(DUE_DATE, added.getDueDate());
        assertEquals(8, added.getCake().getSize());
        assertEquals("Chocolate", added.getCake().getFlavour());
        assertEquals("Strawberry", added.getCake().getFilling());
        assertEquals(OrderStatus.QUEUED, added.getStatus());
        assertEquals("C001", added.getCustomerId());
        assertEquals("Added order " + added + System.lineSeparator()
                + "Customer ID: C001 (new customer)", result);
    }

    @Test
    public void execute_returningCustomer_reusesIdFromCancelledOrder() {
        OrderList orders = new OrderList();
        Order earlier = new Order("O007", new Customer("Alice", "91234567"),
                new Cake(6, "Vanilla", "Cream"), DUE_DATE, OrderStatus.CANCELLED);
        orders.add(earlier);

        String result = new AddOrderCommand(Parser.parseAdd(INPUT)).execute(orders);

        assertEquals(2, orders.size());
        Order added = orders.getById("O008");
        assertEquals(earlier.getCustomerId(), added.getCustomerId());
        assertEquals("Alice", earlier.getCustomer().getName());
        assertEquals(OrderStatus.CANCELLED, earlier.getStatus());
        assertTrue(result.endsWith("Customer ID: C001 (returning customer)"));
    }

    @Test
    public void execute_overCapacity_addsOrderAndPlacesWarningBeforeCustomerConfirmation() {
        OrderList orders = new OrderList();
        orders.setDailyCapacity(1);
        orders.add(new Order("O001", new Customer("Bob", "81234567"),
                new Cake(6, "Vanilla", "Cream"), DUE_DATE));

        String result = new AddOrderCommand(Parser.parseAdd(INPUT)).execute(orders);

        assertEquals(2, orders.size());
        String[] lines = result.split(System.lineSeparator());
        assertEquals(3, lines.length);
        assertTrue(lines[0].startsWith("Added order O002:"));
        assertTrue(lines[1].startsWith("Warning:"));
        assertTrue(lines[1].endsWith("now has 2 orders, over your daily capacity of 1."));
        assertEquals("Customer ID: C002 (new customer)", lines[2]);
    }
}
