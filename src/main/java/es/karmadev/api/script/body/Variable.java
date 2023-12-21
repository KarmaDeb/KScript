package es.karmadev.api.script.body;

import es.karmadev.api.script.exception.VariableDefinitionException;
import es.karmadev.api.script.lang.NullReference;

/**
 * Represents a variable
 */
public abstract class Variable {

    private final String name;
    private final boolean nullable;
    private final int flags;

    private Object value;

    public final static byte FLAG_FINAL = 1;
    public final static byte FLAG_PUBLIC = 2;
    public final static byte FLAG_PRIVATE = 3;
    public final static byte FLAG_READONLY = 4;

    /**
     * Initialize the variable
     *
     * @param name the variable name
     * @throws VariableDefinitionException if the variable is invalid
     */
    public Variable(final String name, final boolean nullable, final int flags) throws VariableDefinitionException {
        this.name = name;
        this.nullable = nullable;
        this.flags = flags;

        if ((flags & FLAG_PUBLIC) == FLAG_PUBLIC) {
            //Variable is public
            if ((flags & FLAG_PRIVATE) == FLAG_PRIVATE) throw new VariableDefinitionException(name, FLAG_PUBLIC, FLAG_PRIVATE);
        }
        if ((flags & FLAG_PRIVATE) == FLAG_PRIVATE) {
            //Variable is private
            if ((flags & FLAG_PUBLIC) == FLAG_PUBLIC) throw new VariableDefinitionException(name, FLAG_PRIVATE, FLAG_PUBLIC);
        }
    }

    /**
     * Get if the variable is null
     *
     * @return if the variable is null
     */
    public boolean isNull() {
        return value instanceof NullReference;
    }

    /**
     * Get if the variable is nullable
     *
     * @return if the variable can
     * be set to a null value
     */
    public boolean isNullable() {
        return this.nullable;
    }

    /**
     * Get the variable value
     *
     * @return the value
     */
    public Object getValue() {
        return value;
    }

    /**
     * Set a variable value
     *
     * @param value the new value
     */
    public void setValue(final Object value) {

    }

    /**
     * Get if the variable has the
     * specified flag
     *
     * @param flag the flag
     * @return if the variable has the
     * flag
     */
    public boolean hasFlag(final byte flag) {
        if (flag <= 0 || flag >= FLAG_READONLY) return false;
        return (flags & flag) == flag;
    }
}
