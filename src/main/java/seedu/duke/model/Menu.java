package seedu.duke.model;

//@@author HammerHead-os

import java.util.ArrayList;

import seedu.duke.exception.CakeQueueException;

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
     *
     * @throws CakeQueueException If the index is out of range.
     */
    public Cake get(int index) {
        if (index < 1 || index > cakes.size()) {
            throw new CakeQueueException(
                    "Cake number must be between 1 and " + cakes.size());
        }
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
