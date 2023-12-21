package es.karmadev.api.script.exception;

/**
 * This exception is thrown as a
 * generic exception for when a script
 * fails to load for any reason
 */
public class ScriptInitializationException extends ScriptException {

    /**
     * Initialize the exception
     *
     * @param reason the error exception
     */
    public ScriptInitializationException(final String reason) {
        super("Failed to parse script. (" + reason + ")");
    }
}
