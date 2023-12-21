package es.karmadev.api.script.exception;

/**
 * This exception is thrown when a
 * variable has flags which conflict each
 * other
 */
public class VariableDefinitionException extends ScriptException {

    /**
     * Initialize the exception
     *
     * @param name the variable name
     * @param flag1 the flag
     * @param flag2 the other flag
     */
    public VariableDefinitionException(final String name, final byte flag1, final byte flag2) {
        super("Failed to define variable " + name + " because flag " + flag1 + " conflicts with " + flag2);
    }
}
