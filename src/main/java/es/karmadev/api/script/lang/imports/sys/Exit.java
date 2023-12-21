package es.karmadev.api.script.lang.imports.sys;

import es.karmadev.api.script.body.Import;
import es.karmadev.api.script.body.Variable;
import es.karmadev.api.script.body.func.Function;
import es.karmadev.api.script.exception.body.ScriptWorkException;
import es.karmadev.api.script.lang.variables.ExitVoidReturn;

/**
 * println function for {@link System}
 */
public class Exit implements Function {

    private final System imp;

    /**
     * Initialize the function
     *
     * @param imp the function import
     */
    public Exit(final System imp) {
        this.imp = imp;
    }

    /**
     * Get the import the function
     * pertains to
     *
     * @return the function import
     */
    @Override
    public Import getImport() {
        return imp;
    }

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
    public Object execute(final Variable... parameters) {
        Variable first = parameters[0];
        if (!first.getType().equals(Number.class)) {
            throw new ScriptWorkException("Invalid parameter #1, expected number but got " + parameters[0]);
        }

        int code = first.getValue(Number.class).intValue();
        parameters[0] = Variable.wrap("Exiting program (" + code + ")." + (parameters.length > 1 ? " " : ""), String.class);

        imp.exit.execute(parameters);

        return ExitVoidReturn.get();
    }
}
