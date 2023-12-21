package es.karmadev.api.script.exception.body;

import es.karmadev.api.script.exception.ScriptException;

/**
 * This exception is thrown when a
 * script references a method which is
 * not defined in the script
 */
public class NoSuchMethodException extends ScriptException {

    /**
     * Initialize the exception
     *
     * @param methodName the method name
     * @param parameters the number of method parameters
     */
    public NoSuchMethodException(final String methodName, final int parameters) {
        super("Method " + methodName + " with " + parameters + " parameters not found");
    }
}