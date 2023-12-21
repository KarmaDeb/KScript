package es.karmadev.api.script.exception;

/**
 * Represents a generic script exception
 */
public abstract class ScriptException extends Exception {

    /**
     * Initialize the exception
     *
     * @param message the error message
     */
    public ScriptException(final String message) {
        super(message);
    }
}
