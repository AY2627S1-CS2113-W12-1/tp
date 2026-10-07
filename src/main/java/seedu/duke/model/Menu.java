package seedu.duke.model;

import java.util.ArrayList;

/**
 * Represents the menu of cakes.
 */
public class Menu {
    private final ArrayList<Cake> cakes;

    public Menu() {
        cakes = new ArrayList<>();
    }

    /**
     * Adds a cake to the menu.
     */
    public void add(Cake cake) {
        cakes.add(cake);
    }

    /**
     * Gets the cake at the given index. Index starts from 1.
     */
    public Cake get(int index) {
        return cakes.get(index - 1); // arraylist starts from 0
    }

    /**
     * Returns the number of cakes in the menu.
     */
    public int size() {
        return cakes.size();
    }

    @Override
    public String toString() {
        StringBuilder listing = new StringBuilder();
        for (int i = 0; i < cakes.size(); i++) {
            listing.append(i + 1)
                    .append(". ")
                    .append(cakes.get(i))
                    .append(System.lineSeparator());
        }
        return listing.toString();
    }
}
