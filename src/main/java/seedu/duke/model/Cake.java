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
     * Creates an order cake with a size, flavour, and filling; pricing is not recorded yet.
     */
    public Cake(int size, String flavour, String filling) {
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
