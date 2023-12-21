package es.karmadev.api.script.exception.body;

import es.karmadev.api.script.body.Import;
import es.karmadev.api.script.body.func.Function;
import es.karmadev.api.script.exception.ScriptException;

/**
 * This exception is thrown when a variable
 * name is used for an undefined variable
 */
public class NoSuchDefException extends ScriptException {

    /**
     * Initialize the exception
     *
     * @param line the line
     * @param varName the variable name
     */
    public NoSuchDefException(final String line, final String varName) {
        super("Undefined variable " + varName + " at " + line);
    }

    /**
     * Initialize the exception
     *
     * @param imp the import
     * @param func the function
     * @param line the line
     * @param varName the variable name
     */
    public NoSuchDefException(final Import imp, final Function func, String line, final String varName) {
        super("Cannot invoke " + imp.getName() + "#" + func.getName() + " at " + line + ". Undefined variable: " + varName);
    }
}
