package es.karmadev.api.script.exception.body;

import es.karmadev.api.script.body.Import;
import es.karmadev.api.script.exception.ScriptException;

/**
 * This exception is thrown when an import
 * is requested for a function which is not
 * defined on that import
 */
public class NoSuchInvokeException extends ScriptException {

    /**
     * Initialize the exception
     *
     * @param imp the import
     * @param func the function
     */
    public NoSuchInvokeException(final Import imp, final String func) {
        super("Failed to invoke function " + func + " on import " + imp.getName() + ". (Function does not exist)");
    }
}
