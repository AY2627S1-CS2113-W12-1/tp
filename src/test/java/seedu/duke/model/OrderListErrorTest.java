package seedu.duke.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import seedu.duke.exception.CakeQueueException;

/**
 * Error-handling tests for {@link OrderList}: every lookup or mutation with a
 * bad index or unknown ID must throw {@link CakeQueueException} with a clear
 * message (instead of a JDK {@code IndexOutOfBoundsException} or
 * {@code NullPointerException}) and must not modify the list.
 */
public class OrderListErrorTest {

    /**
     * Creates a list containing one order, O001.
     */
    private OrderList createOrderList() {
        OrderList orders = new OrderList();
        orders.add(new Order("O001", new Customer("Alice Tan", "91234567"),
                new Cake(8, "Chocolate", "Strawberry"), LocalDate.of(2026, 10, 20)));
        return orders;
    }

    @Test
    public void get_indexOutOfRange_throwsCakeQueueException() {
        OrderList orders = createOrderList();

        for (int index : new int[] {0, -1, 2, 100}) {
            CakeQueueException exception = assertThrows(CakeQueueException.class,
                    () -> orders.get(index), "index: " + index);
            assertTrue(exception.getMessage().contains("between 1 and 1"));
        }
        assertThrows(CakeQueueException.class, () -> new OrderList().get(1));
    }

    @Test
    public void getById_unknownNullOrBlank_throwsCakeQueueException() {
        OrderList orders = createOrderList();

        assertEquals("Order not found: O999", assertThrows(CakeQueueException.class,
                () -> orders.getById("O999")).getMessage());
        assertThrows(CakeQueueException.class, () -> orders.getById(null));
        assertThrows(CakeQueueException.class, () -> orders.getById(""));
        assertThrows(CakeQueueException.class, () -> new OrderList().getById("O001"));
    }

    @Test
    public void getById_caseInsensitive_findsRecord() {
        assertEquals("O001", createOrderList().getById("o001").getOrderId());
    }

    @Test
    public void update_unknownOrder_throwsAndKeepsOriginal() {
        OrderList orders = createOrderList();
        Order replacement = new Order("O999", new Customer("Bob", "81111111"),
                new Cake(6, "Vanilla", "Cream"), LocalDate.of(2026, 10, 21));

        assertThrows(CakeQueueException.class, () -> orders.update(replacement));

        assertEquals(1, orders.size());
        assertEquals("Alice Tan", orders.getById("O001").getCustomer().getName());
    }

    @Test
    public void removeById_unknownOrNull_throwsAndKeepsList() {
        OrderList orders = createOrderList();

        assertThrows(CakeQueueException.class, () -> orders.removeById("O999"));
        assertThrows(CakeQueueException.class, () -> orders.removeById(null));
        assertEquals(1, orders.size());
    }

    @Test
    public void removeById_existing_removesAndReturnsIt() {
        OrderList orders = createOrderList();

        Order removed = orders.removeById("o001");

        assertEquals("O001", removed.getOrderId());
        assertEquals(0, orders.size());
    }

    @Test
    public void deleteOrder_indexOutOfRange_throwsCakeQueueExceptionNotIndexError() {
        OrderList orders = createOrderList();

        for (int index : new int[] {0, -1, 2}) {
            assertThrows(CakeQueueException.class, () -> orders.deleteOrder(index), "index: " + index);
        }
        // The failed deletions must not have removed the real order.
        assertEquals(1, orders.size());
        assertEquals("O001", orders.deleteOrder(1).getOrderId());
        assertEquals(0, orders.size());
    }

    @Test
    public void findByPhone_noMatches_returnsEmptyListNotError() {
        assertTrue(createOrderList().findByPhone("0000").isEmpty());
        assertEquals(1, createOrderList().findByPhone("91234567").size());
    }

    @Test
    public void generateIds_afterLoadingHighIds_continuesPastThem() {
        OrderList orders = new OrderList();
        orders.add(new Order("O010", new Customer("Alice", "1"),
                new Cake(6, "Vanilla", "Cream"), LocalDate.of(2026, 10, 20)));
        orders.add(new Order("E007", new Customer("Bob", "2"),
                new Cake(6, "Vanilla", "Cream"), LocalDate.of(2026, 10, 20), OrderStatus.ENQUIRY));

        assertEquals("O011", orders.generateOrderId());
        assertEquals("E008", orders.generateEnquiryId());
    }
}
