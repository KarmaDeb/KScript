package es.karmadev.api.script.exception.body;

import es.karmadev.api.script.exception.ScriptException;

/**
 * This exception is thrown when an
 * import is defined but has not been
 * declared first.
 */
public class NoSuchImportException extends ScriptException {

    /**
     * Initialize the exception
     *
     * @param name the import name
     */
    public NoSuchImportException(final String name) {
        super("Failed to parse script: invalid import: " + name);
    }
}
