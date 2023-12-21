package es.karmadev.api.script.body;

import es.karmadev.api.script.body.definition.DefType;
import es.karmadev.api.script.body.definition.Definition;
import es.karmadev.api.script.body.func.Function;

/**
 * Represents a script import
 */
public abstract class Import implements Definition {

    private final String name;

    /**
     * Initialize the import
     *
     * @param name the import name
     */
    public Import(final String name) {
        this.name = name;
    }

    /**
     * Get the definition type
     *
     * @return the definition type
     */
    @Override
    public DefType getType() {
        return DefType.IMPORT;
    }

    /**
     * Get the definition name
     *
     * @return the definition name
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Get all the import functions
     *
     * @return the import functions
     */
    public abstract Function[] getFunctions();

    /**
     * Get a function
     *
     * @param name the function name
     * @return the functions
     */
    public abstract Function[] getFunctions(final String name);

    /**
     * Get a function
     *
     * @param name the function name
     * @param parameters the function parameters amount
     * @return the function
     */
    public abstract Function getFunction(final String name, final int parameters);

    /**
     * Returns a string representation of the object.
     *
     * @return a string representation of the object.
     * @apiNote In general, the
     * {@code toString} method returns a string that
     * "textually represents" this object. The result should
     * be a concise but informative representation that is easy for a
     * person to read.
     * It is recommended that all subclasses override this method.
     * The string output is not necessarily stable over time or across
     * JVM invocations.
     * @implSpec The {@code toString} method for class {@code Object}
     * returns a string consisting of the name of the class of which the
     * object is an instance, the at-sign character `{@code @}', and
     * the unsigned hexadecimal representation of the hash code of the
     * object. In other words, this method returns a string equal to the
     * value of:
     * <blockquote>
     * <pre>
     * getClass().getName() + '@' + Integer.toHexString(hashCode())
     * </pre></blockquote>
     */
    @Override
    public String toString() {
        return "#" + name;
    }
}
