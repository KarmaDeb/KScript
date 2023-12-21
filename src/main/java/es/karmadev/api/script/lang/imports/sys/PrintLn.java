package es.karmadev.api.script.lang.imports.sys;

import es.karmadev.api.script.body.Import;
import es.karmadev.api.script.body.Variable;
import es.karmadev.api.script.body.func.Function;

/**
 * println function for {@link System}
 */
public class PrintLn implements Function {

    private final System imp;

    /**
     * Initialize the function
     *
     * @param imp the function import
     */
    public PrintLn(final System imp) {
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
    public Object execute(final Variable... parameters) {
        if (parameters.length == 0) {
            java.lang.System.out.println();
        }

        Variable[] p = new Variable[parameters.length + 1];
        java.lang.System.arraycopy(parameters, 0, p, 0, parameters.length);
        p[p.length - 1] = Variable.wrap('\n', Character.class);

        return imp.print.execute(p);
    }
}
