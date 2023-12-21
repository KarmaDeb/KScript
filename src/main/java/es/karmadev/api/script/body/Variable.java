package es.karmadev.api.script.body;

import es.karmadev.api.script.exception.VariableDefinitionException;
import es.karmadev.api.script.exception.body.ScriptWorkException;
import es.karmadev.api.script.lang.NullReference;

import java.lang.reflect.Type;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Represents a variable
 */
public abstract class Variable {

    private final String name;
    private final boolean nullable;
    private final int flags;
    private final Class<?> type;

    protected Object value;

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
    public <T> Variable(final String name, final boolean nullable, final int flags, final Class<T> type) throws VariableDefinitionException {
        this.name = name;
        this.nullable = nullable;
        this.flags = flags;
        this.type = type;

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
    public <T> T getValue(final Class<T> type) {
        if (this.type.isAssignableFrom(type) || type.isAssignableFrom(this.type) ||
                this.type.equals(type)) {
            return type.cast(value);
        }

        return null;
    }

    public Type getType() {
        return type;
    }

    /**
     * Set a variable value
     *
     * @param value the new value
     */
    public <T> void setValue(final T value) {
        if (value != null) {
            Class<?> type = value.getClass();

            if (!this.type.isAssignableFrom(type) && !type.isAssignableFrom(this.type) &&
                    !this.type.equals(type)) {
                throw new ScriptWorkException("Cannot assign " + value + " to " + name + " because they are not the same type");
            }
        }

        if (value == null && !nullable) {
            throw new ScriptWorkException("Cannot assign null to " + name + " because it's not-nul");
        }
        if ((flags & FLAG_READONLY) == FLAG_READONLY) {
            throw new ScriptWorkException("Cannot assign " + value + " to readonly variable " + name);
        }

        if ((flags & FLAG_FINAL) == FLAG_FINAL && this.value != null) {
            throw new ScriptWorkException("Cannot assign " + value + " to already-defined final variable " + name);
        }

        this.value = value;
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

    /**
     * Wrap an object into a variable
     *
     * @param value the object
     * @return the variable
     */
    public static <T> Variable wrap(final T value, final Class<T> type) {
        if (value == null) return NullReference.get();

        int random = ThreadLocalRandom.current().nextInt();
        try {
            Variable v = new Variable("anon@" + random, false, FLAG_PRIVATE & FLAG_READONLY & FLAG_FINAL, type) {

                /**
                 * Get if the variable is null
                 *
                 * @return if the variable is null
                 */
                @Override
                public boolean isNull() {
                    return value == null;
                }
            };

            v.value = value;
            return v;
        } catch (VariableDefinitionException ex) {
            return NullReference.get();
        }
    }
}
