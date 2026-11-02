package seedu.duke.parser;

//@@author Eethanzyj

import java.util.regex.Matcher;

import seedu.duke.exception.CakeQueueException;

/**
 * Finds field prefixes outside quoted values and decodes those values for add and edit.
 * Required fields, supported prefixes and domain validation remain in the command parsers.
 */
final class FieldTokenizer {
    private FieldTokenizer() {
    }

    /**
     * Masks quoted values without changing character positions, then matches visible prefixes.
     * Quotes delimit a value only when they appear at the start of that field's value.
     */
    static Matcher prefixesIn(String arguments) {
        StringBuilder searchable = new StringBuilder(arguments);
        Matcher candidates = Parser.FIELD_PREFIX.matcher(arguments);
        int quotedEnd = -1;
        while (candidates.find()) {
            if (candidates.start() <= quotedEnd) {
                continue;
            }
            int valueStart = candidates.end();
            while (valueStart < arguments.length() && Character.isWhitespace(arguments.charAt(valueStart))) {
                valueStart++;
            }
            if (valueStart < arguments.length() && arguments.charAt(valueStart) == '"') {
                quotedEnd = closingQuote(arguments, valueStart);
                // Non-whitespace masking also preserves the boundary after a closing quote.
                for (int i = valueStart; i <= quotedEnd; i++) {
                    searchable.setCharAt(i, '_');
                }
            }
        }
        return Parser.FIELD_PREFIX.matcher(searchable);
    }

    /**
     * Removes surrounding quotes and decodes escaped quotes and backslashes inside them.
     * Unquoted text retains its existing literal quotes and backslashes.
     */
    static String valueOf(String rawValue) {
        String value = rawValue.trim();
        if (!value.startsWith("\"")) {
            return value;
        }
        int end = closingQuote(value, 0);
        if (end != value.length() - 1) {
            throw new CakeQueueException("Unexpected text after a quoted value. Quote the entire value.");
        }
        StringBuilder decoded = new StringBuilder();
        for (int i = 1; i < end; i++) {
            char character = value.charAt(i);
            if (character == '\\' && i + 1 < end && isEscapable(value.charAt(i + 1))) {
                character = value.charAt(++i);
            }
            decoded.append(character);
        }
        return decoded.toString().trim();
    }

    /** Finds the closing unescaped quote or reports an unfinished quoted value. */
    private static int closingQuote(String text, int opening) {
        for (int i = opening + 1; i < text.length(); i++) {
            char character = text.charAt(i);
            if (character == '\\' && i + 1 < text.length() && isEscapable(text.charAt(i + 1))) {
                i++;
            } else if (character == '"') {
                return i;
            }
        }
        throw new CakeQueueException("Unclosed quoted value. Add a closing double quote.");
    }

    private static boolean isEscapable(char character) {
        return character == '"' || character == '\\';
    }
}
