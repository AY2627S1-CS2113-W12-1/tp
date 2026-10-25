package seedu.duke.exception;

/**
 * Represents a user-facing CakeQueue error, such as invalid command input,
 * a record that cannot be found, or an operation that is not allowed for
 * the record's current status.
 *
 * <p>This is the single exception type thrown for errors the user can fix
 * by changing their input. Command parsers and commands throw it, and the
 * command-line interfaces ({@code BakerCli}, {@code CustomerCli} and the
 * mode-selection loop in {@code Duke}) catch it to show
 * {@code Error: <message>} and continue running instead of crashing.</p>
 *
 * <p>It extends {@link IllegalArgumentException} deliberately: errors were
 * previously reported with that type, so existing handlers and tests that
 * expect {@code IllegalArgumentException} keep working, while new code can
 * catch or assert the more specific {@code CakeQueueException}.</p>
 */
public class CakeQueueException extends IllegalArgumentException {

    /**
     * Creates an exception with a message the user can act on.
     *
     * @param message Explanation of the problem and, where helpful, the
     *                correct command format.
     */
    public CakeQueueException(String message) {
        super(message);
    }

    /**
     * Creates an exception that keeps the underlying cause (for example a
     * {@code DateTimeParseException} for a bad date), so debugging keeps the
     * original stack trace while the user still sees a friendly message.
     *
     * @param message Explanation of the problem.
     * @param cause The underlying exception that caused this error.
     */
    public CakeQueueException(String message, Throwable cause) {
        super(message, cause);
    }
}
