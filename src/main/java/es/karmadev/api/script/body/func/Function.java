package es.karmadev.api.script.body.func;

/**
 * Represents a script method
 */
public interface Function {

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
    Object execute(final Object... parameters);
}
