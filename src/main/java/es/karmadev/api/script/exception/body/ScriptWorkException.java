package es.karmadev.api.script.exception.body;

import es.karmadev.api.script.exception.ScriptRuntimeException;

/**
 * This exception is thrown when
 * any problem occurs while executing
 * the script
 */
public class ScriptWorkException extends ScriptRuntimeException {

    /**
     * Initialize the exception
     *
     * @param reason the exception reason
     */
    public ScriptWorkException(final String reason) {
        super(reason);
    }

    /**
     * Initialize the exception
     *
     * @param cause the error which caused this
     *              exception
     */
    public ScriptWorkException(final Throwable cause) {
        super(cause);
    }
}
