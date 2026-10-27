package seedu.duke.model;

/**
 * Stores the details of a menu cake or a cake requested in an order.
 */
public class Cake {
    private final String name;
    private final String flavour;
    private final double price;
    // Size and filling are specified for order cakes; the existing menu constructor leaves them unset.
    private final int size;
    private final String filling;

    public Cake(String name, String flavour, double price) {
        this.name = name;
        this.flavour = flavour;
        this.price = price;
        this.size = 0;
        this.filling = "";
    }


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

        this.name = flavour;
        this.flavour = flavour;
        this.price = 0;
        this.size = size;
        this.filling = filling;
    }


    public String getName() {
        return name;
    }

    public String getFlavour() {
        return flavour;
    }

    public double getPrice() {
        return price;
    }

    public int getSize() {
        return size;
    }

    public String getFilling() {
        return filling;
    }

    @Override
    public String toString() {
        return name + " (" + flavour + ") $" + String.format("%.2f", price);
    }
}
