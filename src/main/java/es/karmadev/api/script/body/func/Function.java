package es.karmadev.api.script.body.func;

import es.karmadev.api.script.body.Import;
import es.karmadev.api.script.body.Variable;

/**
 * Represents a script method
 */
public interface Function {

    /**
     * Get the import the function
     * pertains to
     *
     * @return the function import
     */
    Import getImport();

    /**
     * Get the function name
     *
     * @return the function name
     */
    String getName();

    /**
     * Get the amount of parameters this
     * function allows
     *
     * @return the amount of parameters the
     * function allows
     */
    int getParameters();

    /**
     * Get if the parameters value of
     * this function refers to the minimum
     * required parameters, and not the
     * exact amount
     *
     * @return if the function uses min parameters
     * instead of exact parameters
     */
    boolean isMinParameters();

    /**
     * Execute a method
     *
     * @param parameters the method parameters
     * @return the method result
     */
    Object execute(final Variable... parameters);
}
