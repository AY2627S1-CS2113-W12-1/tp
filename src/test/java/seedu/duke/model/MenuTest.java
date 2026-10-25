package seedu.duke.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import seedu.duke.exception.CakeQueueException;

/**
 * Tests {@link Menu}, including its index error handling: out-of-range
 * lookups must throw {@link CakeQueueException} rather than leaking an
 * {@code IndexOutOfBoundsException}.
 */
public class MenuTest {

    /**
     * Creates a menu with two cakes.
     */
    private Menu createMenu() {
        Menu menu = new Menu();
        menu.add(new Cake("Chocolate Cake", "Chocolate", 30.0));
        menu.add(new Cake("Vanilla Cake", "Vanilla", 25.0));
        return menu;
    }

    @Test
    public void get_validIndexes_returnsCakesInOrder() {
        Menu menu = createMenu();

        assertEquals("Chocolate Cake", menu.get(1).getName());
        assertEquals("Vanilla Cake", menu.get(2).getName());
        assertEquals(2, menu.size());
    }

    @Test
    public void get_indexOutOfRange_throwsCakeQueueException() {
        Menu menu = createMenu();

        for (int index : new int[] {0, -1, 3, 100}) {
            CakeQueueException exception = assertThrows(CakeQueueException.class,
                    () -> menu.get(index), "index: " + index);
            assertTrue(exception.getMessage().contains("between 1 and 2"));
        }
        assertThrows(CakeQueueException.class, () -> new Menu().get(1));
    }

    @Test
    public void toString_emptyAndPopulatedMenu_listsNumberedCakes() {
        assertEquals("", new Menu().toString());

        String listing = createMenu().toString();
        assertTrue(listing.contains("1. Chocolate Cake"));
        assertTrue(listing.contains("2. Vanilla Cake"));
    }

    @Test
    public void cake_bothConstructors_exposeExpectedFields() {
        Cake menuCake = new Cake("Chocolate Cake", "Chocolate", 30.0);
        assertEquals("Chocolate Cake", menuCake.getName());
        assertEquals(30.0, menuCake.getPrice());
        assertTrue(menuCake.toString().contains("$30.00"));

        Cake orderCake = new Cake(8, "Vanilla", "Cream");
        assertEquals(8, orderCake.getSize());
        assertEquals("Vanilla", orderCake.getFlavour());
        assertEquals("Cream", orderCake.getFilling());
    }

    @Test
    public void customer_nameOnlyConstructor_hasEmptyPhone() {
        Customer customer = new Customer("Alice Tan");

        assertEquals("Alice Tan", customer.getName());
        assertEquals("", customer.getPhone());
        assertEquals("Alice Tan (91234567)", new Customer("Alice Tan", "91234567").toString());
    }
}
