package es.karmadev.api.script.lang.imports.sys;

import es.karmadev.api.script.body.func.Function;
import es.karmadev.api.script.exception.body.ScriptWorkException;
import es.karmadev.api.script.lang.variables.ExitVoidReturn;
import es.karmadev.api.script.lang.variables.VoidReturn;

/**
 * println function for {@link System}
 */
public class Exit implements Function {

    private final static Print print = new Print();

    /**
     * Get the function name
     *
     * @return the function name
     */
    @Override
    public String getName() {
        return "exit";
    }

    /**
     * Get the amount of parameters this
     * function allows
     *
     * @return the amount of parameters the
     * function allows
     */
    @Override
    public int getParameters() {
        return 1;
    }

    /**
     * Get if the parameters value of
     * this function refers to the minimum
     * required parameters, and not the
     * exact amount
     *
     * @return if the function uses min parameters
     * instead of exact parameters
     */
    @Override
    public boolean isMinParameters() {
        return true;
    }

    /**
     * Execute a method
     *
     * @param parameters the method parameters
     * @return the method result
     */
    @Override
    public Object execute(final Object... parameters) {
        if (!(parameters[0] instanceof Number)) {
            throw new ScriptWorkException("Invalid parameter #1, expected number but got " + parameters[0]);
        }

        int code = ((Number) parameters[0]).intValue();
        parameters[0] = "Exiting program (" + code + ")." + (parameters.length > 1 ? " " : "");

        print.execute(parameters);

        return ExitVoidReturn.get();
    }
}
