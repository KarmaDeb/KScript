package es.karmadev.api.script.exception;

/**
 * This exception is thrown when a script
 * body is tried to be parsed but has unexpected
 * syntax. This error should contain information
 * about what the error is, and the line that caused
 * this error
 */
public class InvalidSyntaxException extends ScriptException {

    /**
     * Initialize the exception
     *
     * @param fullLine the full line
     * @param line the line number
     * @param info the possible fix
     */
    public InvalidSyntaxException(final String fullLine, final int line, final String info) {
        super("Failed to parse " + fullLine + " at line " + line + ". " + info);
    }
}
