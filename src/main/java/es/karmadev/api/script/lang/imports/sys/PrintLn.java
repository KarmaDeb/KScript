package es.karmadev.api.script.lang.imports.sys;

import es.karmadev.api.script.body.func.Function;

/**
 * println function for {@link System}
 */
public class PrintLn implements Function {

    private final static Print print = new Print();

    /**
     * Get the function name
     *
     * @return the function name
     */
    @Override
    public String getName() {
        return "println";
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
        return 0;
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
        if (parameters.length == 0) {
            java.lang.System.out.println();
        }

        Object[] p = new Object[parameters.length + 1];
        java.lang.System.arraycopy(parameters, 0, p, 0, parameters.length);
        p[p.length - 1] = '\n';

        return print.execute(p);
    }
}
