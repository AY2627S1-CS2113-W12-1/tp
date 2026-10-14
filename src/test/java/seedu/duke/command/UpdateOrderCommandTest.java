package seedu.duke.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import seedu.duke.model.Cake;
import seedu.duke.model.Customer;
import seedu.duke.model.Order;
import seedu.duke.model.OrderList;

/**
 * Tests updating existing orders while preserving unchanged details.
 */
public class UpdateOrderCommandTest {

    /**
     * Creates an order list containing one sample order.
     *
     * @return An order list containing O001.
     */
    private OrderList createOrderList() {
        OrderList orders = new OrderList();

        Order order = new Order(
                "O001",
                new Customer("Alice Tan", "91234567"),
                new Cake(8, "Chocolate", "Strawberry"),
                LocalDate.of(2026, 10, 20)
        );

        orders.add(order);
        return orders;
    }

    /**
     * Tests updating only the cake size and flavour.
     */
    @Test
    public void execute_updateCake_preservesOtherFields() {
        OrderList orders = createOrderList();

        // Null indicates that the corresponding field is unchanged.
        UpdateOrderCommand command = new UpdateOrderCommand(
                "O001", null, null, null, 10, "Vanilla", null
        );

        command.execute(orders);

        Order updated = orders.getById("O001");

        // Cake size and flavour should change.
        assertEquals(10, updated.getCake().getSize());
        assertEquals("Vanilla", updated.getCake().getFlavour());

        // Other details should remain unchanged.
        assertEquals("Strawberry", updated.getCake().getFilling());
        assertEquals("Alice Tan", updated.getCustomer().getName());
        assertEquals("91234567", updated.getCustomer().getPhone());
        assertEquals(LocalDate.of(2026, 10, 20), updated.getDueDate());
    }

    /**
     * Tests updating the customer name without changing the phone number.
     */
    @Test
    public void execute_updateName_preservesPhone() {
        OrderList orders = createOrderList();

        UpdateOrderCommand command = new UpdateOrderCommand(
                "O001", "Bob Tan", null, null, null, null, null
        );

        command.execute(orders);

        Order updated = orders.getById("O001");

        assertEquals("Bob Tan", updated.getCustomer().getName());
        assertEquals("91234567", updated.getCustomer().getPhone());
    }

    /**
     * Tests updating the due date without changing other details.
     */
    @Test
    public void execute_updateDate_preservesCake() {
        OrderList orders = createOrderList();

        UpdateOrderCommand command = new UpdateOrderCommand(
                "O001", null, null, LocalDate.of(2026, 10, 25),
                null, null, null
        );

        command.execute(orders);

        Order updated = orders.getById("O001");

        assertEquals(LocalDate.of(2026, 10, 25), updated.getDueDate());
        assertEquals(8, updated.getCake().getSize());
        assertEquals("Chocolate", updated.getCake().getFlavour());
    }

    /**
     * Tests that updating a nonexistent order produces an error.
     */
    @Test
    public void execute_unknownOrderId_throwsException() {
        OrderList orders = createOrderList();

        UpdateOrderCommand command = new UpdateOrderCommand(
                "O999", "Bob", null, null, null, null, null
        );

        assertThrows(IllegalArgumentException.class,
                () -> command.execute(orders));

        // The original order must still exist.
        assertEquals(1, orders.size());
        assertEquals("Alice Tan", orders.getById("O001").getCustomer().getName());
    }
}
