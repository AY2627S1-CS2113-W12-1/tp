package seedu.duke.command;

import seedu.duke.model.Order;
import seedu.duke.model.OrderList;

/**
 * Sets or clears the free-text notes on an existing order or enquiry.
 * Notes are useful for allergens, special decorations, or any extra detail
 * that doesn't fit the standard fields.
 *
 * <p>Usage: {@code note ORDER_ID text...} or {@code note ORDER_ID -clear}</p>
 */
public class NoteCommand {
    private final String recordId;
    /** The text to set, or empty string to clear the note. */
    private final String noteText;

    /**
     * Creates the command.
     *
     * @param recordId  The order or enquiry ID to annotate.
     * @param noteText  The note text, or an empty string to clear any existing note.
     */
    public NoteCommand(String recordId, String noteText) {
        this.recordId = recordId;
        this.noteText = noteText;
    }

    /**
     * Applies the note to the record and returns a confirmation message.
     */
    public String execute(OrderList orders) {
        Order record = orders.getById(recordId);
        record.setNotes(noteText);
        if (noteText.isEmpty()) {
            return "Cleared notes for " + recordId + ".";
        }
        return "Note set for " + recordId + ": " + noteText;
    }
}
