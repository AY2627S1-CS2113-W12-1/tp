package seedu.duke.model;

/**
 * Represents a cake on the menu.
 */
public class Cake {
    private final String name;
    private final String flavour;
    private final double price;

    public Cake(String name, String flavour, double price) {
        this.name = name;
        this.flavour = flavour;
        this.price = price;
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

    @Override
    public String toString() {
        return name + " (" + flavour + ") $" + String.format("%.2f", price);
    }
}
