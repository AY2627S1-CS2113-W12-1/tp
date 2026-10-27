
package seedu.duke.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/**
 * Tests the validation and storage of cake details.
 */
public class CakeTest {

    /**
     * Verifies that all supported cake sizes are accepted.
     */
    @Test
    public void constructor_validSizes_createsCake() {
        for (int size : new int[] {6, 8, 10}) {
            Cake cake = new Cake(size, "Chocolate", "Vanilla");

            assertEquals(size, cake.getSize());
            assertEquals("Chocolate", cake.getFlavour());
            assertEquals("Vanilla", cake.getFilling());
        }
    }

    /**
     * Verifies that unsupported cake sizes are rejected.
     */
    @Test
    public void constructor_invalidSizes_throwsIllegalArgumentException() {
        for (int size : new int[] {-1, 0, 5, 7, 9, 11, 100}) {
            // Each unsupported size must trigger the constructor's validation.
            assertThrows(IllegalArgumentException.class,
                    () -> new Cake(size, "Chocolate", "Vanilla"));
        }
    }

    /**
     * Verifies that the existing menu cake constructor still works.
     */
    @Test
    public void constructor_menuCake_preservesDetails() {
        Cake cake = new Cake("Chocolate Cake", "Chocolate", 30.0);

        assertEquals("Chocolate Cake", cake.getName());
        assertEquals("Chocolate", cake.getFlavour());
        assertEquals(30.0, cake.getPrice());
    }

    /**
     * Verifies that null, empty, and whitespace-only flavours are rejected.
     */
    @Test
    public void constructor_invalidFlavour_throwsIllegalArgumentException() {
        // Flavours must contain at least one non-whitespace character.
        assertThrows(IllegalArgumentException.class,
                () -> new Cake(6, null, "Cream"));
        assertThrows(IllegalArgumentException.class,
                () -> new Cake(6, "", "Cream"));
        assertThrows(IllegalArgumentException.class,
                () -> new Cake(6, "   ", "Cream"));
    }

    /**
     * Verifies that null, empty, and whitespace-only fillings are rejected.
     */
    @Test
    public void constructor_invalidFilling_throwsIllegalArgumentException() {
        // Fillings must contain at least one non-whitespace character.
        assertThrows(IllegalArgumentException.class,
                () -> new Cake(8, "Chocolate", null));
        assertThrows(IllegalArgumentException.class,
                () -> new Cake(8, "Chocolate", ""));
        assertThrows(IllegalArgumentException.class,
                () -> new Cake(8, "Chocolate", "   "));
    }

    /**
     * Verifies that different valid flavours and fillings are accepted.
     */
    @Test
    public void constructor_validFlavourAndFilling_preservesDetails() {
        // Custom cake orders can use any non-empty flavour and filling.
        Cake cake = new Cake(10, "Matcha", "Red Bean");

        assertEquals("Matcha", cake.getFlavour());
        assertEquals("Red Bean", cake.getFilling());
    }
}
