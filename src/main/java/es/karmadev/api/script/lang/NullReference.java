package es.karmadev.api.script.lang;

import es.karmadev.api.script.body.Variable;
import es.karmadev.api.script.exception.VariableDefinitionException;

/**
 * Represents a null variable
 */
public class NullReference extends Variable {

    private final static NullReference instance;

    static {
        try {
            instance = new NullReference();
        } catch (VariableDefinitionException e) {
            throw new RuntimeException(e);
        }
    }

    private NullReference() throws VariableDefinitionException {
        super("null", true, FLAG_PRIVATE & FLAG_READONLY & FLAG_FINAL, Object.class);
    }

    public static NullReference get() {
        return instance;
    }
}
