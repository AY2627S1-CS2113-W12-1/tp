package seedu.duke.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import seedu.duke.model.Cake;
import seedu.duke.model.Customer;
import seedu.duke.model.Order;
import seedu.duke.model.OrderList;
import seedu.duke.model.OrderStatus;

/**
 * Tests customers editing their own pending enquiries through {@link CustomerEditCommand}.
 * Null edit fields mean "keep the existing value", mirroring {@link UpdateOrderCommandTest}.
 */
public class CustomerEditCommandTest {

    /**
     * Creates an order list with one enquiry (E001) in the given status.
     *
     * @param status Status to give the enquiry record.
     * @param withCake Whether the enquiry has cake details.
     * @return An order list containing E001 owned by Alice (phone 91234567).
     */
    private OrderList createOrderList(OrderStatus status, boolean withCake) {
        OrderList orders = new OrderList();
        Cake cake = withCake ? new Cake(8, "Chocolate", "Strawberry") : null;
        orders.add(new Order("E001",
                new Customer("Alice Tan", "91234567"),
                cake,
                LocalDate.of(2026, 10, 20),
                status));
        return orders;
    }

    /**
     * Tests editing only the name, which must preserve cake, date and phone.
     */
    @Test
    public void execute_nameOnly_preservesCakeDateAndPhone() {
        OrderList orders = createOrderList(OrderStatus.ENQUIRY, true);

        String result = new CustomerEditCommand(
                "E001", "91234567", "Alicia Tan", null, null, null, null).execute(orders);

        Order updated = orders.getById("E001");
        assertTrue(result.contains("Updated enquiry E001"));
        assertEquals("Alicia Tan", updated.getCustomer().getName());
        assertEquals("91234567", updated.getCustomer().getPhone());
        assertEquals(8, updated.getCake().getSize());
        assertEquals("Chocolate", updated.getCake().getFlavour());
        assertEquals(LocalDate.of(2026, 10, 20), updated.getDueDate());
        assertEquals(OrderStatus.ENQUIRY, updated.getStatus());
    }

    /**
     * Tests editing only the cake size, which must preserve flavour and filling.
     */
    @Test
    public void execute_sizeOnly_preservesFlavourAndFilling() {
        OrderList orders = createOrderList(OrderStatus.ENQUIRY, true);

        new CustomerEditCommand(
                "E001", "91234567", null, null, 10, null, null).execute(orders);

        Order updated = orders.getById("E001");
        assertEquals(10, updated.getCake().getSize());
        assertEquals("Chocolate", updated.getCake().getFlavour());
        assertEquals("Strawberry", updated.getCake().getFilling());
    }

    /**
     * Tests editing date, flavour and filling together.
     */
    @Test
    public void execute_dateFlavourAndFilling_updatesAllThree() {
        OrderList orders = createOrderList(OrderStatus.ENQUIRY, true);

        new CustomerEditCommand("E001", "91234567", null,
                LocalDate.of(2026, 11, 1), null, "Vanilla", "Cream").execute(orders);

        Order updated = orders.getById("E001");
        assertEquals(LocalDate.of(2026, 11, 1), updated.getDueDate());
        assertEquals("Vanilla", updated.getCake().getFlavour());
        assertEquals("Cream", updated.getCake().getFilling());
        assertEquals(8, updated.getCake().getSize());
    }

    /**
     * Tests that editing with a non-matching phone number is rejected (ownership check).
     */
    @Test
    public void execute_wrongPhone_throwsException() {
        OrderList orders = createOrderList(OrderStatus.ENQUIRY, true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new CustomerEditCommand(
                        "E001", "99999999", "Alicia Tan", null, null, null, null).execute(orders));

        assertTrue(exception.getMessage().contains("Phone number does not match"));
        assertEquals("Alice Tan", orders.getById("E001").getCustomer().getName());
    }

    /**
     * Tests that a customer cannot edit a record that is no longer a pending enquiry.
     */
    @Test
    public void execute_confirmedOrder_throwsException() {
        OrderList orders = createOrderList(OrderStatus.QUEUED, true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new CustomerEditCommand(
                        "E001", "91234567", "Alicia Tan", null, null, null, null).execute(orders));

        assertTrue(exception.getMessage().contains("only edit pending enquiries"));
    }

    /**
     * Tests that an edit supplying no fields to change is rejected with usage guidance.
     */
    @Test
    public void execute_noFieldsToChange_throwsException() {
        OrderList orders = createOrderList(OrderStatus.ENQUIRY, true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new CustomerEditCommand(
                        "E001", "91234567", null, null, null, null, null).execute(orders));

        assertTrue(exception.getMessage().contains("Use: edit ENQUIRY_ID"));
    }

    /**
     * Tests that cake fields cannot be edited when the enquiry has no cake details.
     */
    @Test
    public void execute_cakeFieldWithoutCakeDetails_throwsException() {
        OrderList orders = createOrderList(OrderStatus.ENQUIRY, false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new CustomerEditCommand(
                        "E001", "91234567", null, null, 10, null, null).execute(orders));

        assertTrue(exception.getMessage().contains("no cake details"));
    }

    /**
     * Tests that editing an unknown enquiry ID produces an error.
     */
    @Test
    public void execute_unknownId_throwsException() {
        OrderList orders = createOrderList(OrderStatus.ENQUIRY, true);

        assertThrows(IllegalArgumentException.class,
                () -> new CustomerEditCommand(
                        "E999", "91234567", "Alicia Tan", null, null, null, null).execute(orders));
    }
}
