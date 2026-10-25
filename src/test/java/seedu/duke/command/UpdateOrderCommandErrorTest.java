package seedu.duke.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import seedu.duke.exception.CakeQueueException;
import seedu.duke.model.Cake;
import seedu.duke.model.Customer;
import seedu.duke.model.Order;
import seedu.duke.model.OrderList;
import seedu.duke.model.OrderStatus;

/**
 * Error-path tests for {@link UpdateOrderCommand} and the closed-record
 * branches of {@link MarkCommand} that the happy-path tests do not reach.
 * Failed operations must throw {@link CakeQueueException} and leave the
 * stored record exactly as it was.
 */
public class UpdateOrderCommandErrorTest {

    @Test
    public void execute_updatePhone_preservesNameAndCake() {
        OrderList orders = new OrderList();
        orders.add(new Order("O001", new Customer("Alice Tan", "91234567"),
                new Cake(8, "Chocolate", "Strawberry"), LocalDate.of(2026, 10, 20)));

        String result = new UpdateOrderCommand(
                "O001", null, "99998888", null, null, null, null).execute(orders);

        assertTrue(result.contains("Updated order O001"));
        assertEquals("99998888", orders.getById("O001").getCustomer().getPhone());
        assertEquals("Alice Tan", orders.getById("O001").getCustomer().getName());
        assertEquals(8, orders.getById("O001").getCake().getSize());
    }

    @Test
    public void execute_nullId_throwsCakeQueueException() {
        OrderList orders = new OrderList();
        orders.add(new Order("O001", new Customer("Alice Tan", "91234567"),
                new Cake(8, "Chocolate", "Strawberry"), LocalDate.of(2026, 10, 20)));

        assertThrows(CakeQueueException.class, () -> new UpdateOrderCommand(
                null, "Bob", null, null, null, null, null).execute(orders));
        assertEquals("Alice Tan", orders.getById("O001").getCustomer().getName());
    }

    @Test
    public void execute_cakeFieldOnOrderWithoutCake_throwsAndKeepsOrder() {
        OrderList orders = new OrderList();
        // Legacy-style order created without cake details.
        orders.add(new Order("O001", new Customer("Alice Tan", "91234567"),
                LocalDate.of(2026, 10, 20)));

        CakeQueueException exception = assertThrows(CakeQueueException.class,
                () -> new UpdateOrderCommand(
                        "O001", null, null, null, 10, null, null).execute(orders));
        assertEquals("This order has no cake details.", exception.getMessage());

        // A name-only edit of the same order must still work.
        new UpdateOrderCommand("O001", "Alicia Tan", null, null, null, null, null).execute(orders);
        assertEquals("Alicia Tan", orders.getById("O001").getCustomer().getName());
        assertNull( orders.getById("O001").getCake());
    }

    @Test
    public void execute_noChanges_preservesEveryField() {
        OrderList orders = new OrderList();
        orders.add(new Order("O001", new Customer("Alice Tan", "91234567"),
                new Cake(8, "Chocolate", "Strawberry"), LocalDate.of(2026, 10, 20)));

        new UpdateOrderCommand("O001", null, null, null, null, null, null).execute(orders);

        Order order = orders.getById("O001");
        assertEquals("Alice Tan", order.getCustomer().getName());
        assertEquals("91234567", order.getCustomer().getPhone());
        assertEquals(8, order.getCake().getSize());
        assertEquals(LocalDate.of(2026, 10, 20), order.getDueDate());
    }

    @Test
    public void mark_closedRecords_throwsWithStatusInMessage() {
        OrderList orders = new OrderList();
        orders.add(new Order("O001", new Customer("Alice Tan", "91234567"),
                new Cake(8, "Chocolate", "Strawberry"), LocalDate.of(2026, 10, 20),
                OrderStatus.COLLECTED));
        orders.add(new Order("O002", new Customer("Bob Tan", "81111111"),
                new Cake(6, "Vanilla", "Cream"), LocalDate.of(2026, 10, 21),
                OrderStatus.CANCELLED));

        CakeQueueException collected = assertThrows(CakeQueueException.class,
                () -> new MarkCommand("O001", OrderStatus.PREPARING).execute(orders));
        assertTrue(collected.getMessage().contains("already COLLECTED"));

        assertThrows(CakeQueueException.class,
                () -> new MarkCommand("O002", OrderStatus.PREPARING).execute(orders));
        // Backwards and sideways moves from READY are rejected too.
        orders.getById("O001").setStatus(OrderStatus.READY);
        assertThrows(CakeQueueException.class,
                () -> new MarkCommand("O001", OrderStatus.PREPARING).execute(orders));
        assertEquals(OrderStatus.READY, orders.getById("O001").getStatus());
    }
}
