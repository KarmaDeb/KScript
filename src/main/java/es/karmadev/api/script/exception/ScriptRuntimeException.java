package es.karmadev.api.script.exception;

/**
 * Represents a generic script exception
 */
public abstract class ScriptRuntimeException extends RuntimeException {

    /**
     * Initialize the exception
     *
     * @param message the error message
     */
    public ScriptRuntimeException(final String message) {
        super(message);
    }

    /**
     * Initialize the exception
     *
     * @param cause the error which caused this
     *              exception
     */
    public ScriptRuntimeException(final Throwable cause) {
        super(cause);
    }
}
