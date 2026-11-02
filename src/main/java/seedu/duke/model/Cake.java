package seedu.duke.model;

/**
 * Stores the size, flavour and filling of a cake requested in an order.
 */
public class Cake {
    private final String flavour;
    //@@author Eethanzyj
    private final int size;
    private final String filling;
    //@@author

    //@@author WillChin484848
    /**
     * Creates a cake with the specified size, flavour, and filling.
     *
     * @param size The cake size in inches.
     * @param flavour The cake flavour.
     * @param filling The cake filling.
     * @throws IllegalArgumentException If the cake details are invalid.
     */
    public Cake(int size, String flavour, String filling) {
        // Reject unsupported cake sizes to prevent invalid orders.
        if (size != 6 && size != 8 && size != 10) {
            throw new IllegalArgumentException(
                    "Cake size must be 6, 8, or 10 inches.");
        }

        // A cake must have a meaningful flavour and filling.
        if (flavour == null || flavour.isBlank()) {
            throw new IllegalArgumentException(
                    "Cake flavour cannot be empty.");
        }

        if (filling == null || filling.isBlank()) {
            throw new IllegalArgumentException(
                    "Cake filling cannot be empty.");
        }

        //@@author Eethanzyj
        this.flavour = flavour;
        this.size = size;
        this.filling = filling;
    }
    //@@author


    public String getFlavour() {
        return flavour;
    }

    //@@author Eethanzyj
    public int getSize() {
        return size;
    }

    public String getFilling() {
        return filling;
    }
    //@@author
}
