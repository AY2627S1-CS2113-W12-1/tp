package seedu.duke.model;

/**
 * Represents the status of an enquiry or confirmed order.
 */
public enum OrderStatus {
    ENQUIRY,
    QUEUED,
    PREPARING,
    READY,
    COLLECTED,
    CANCELLED,
    REJECTED
}
