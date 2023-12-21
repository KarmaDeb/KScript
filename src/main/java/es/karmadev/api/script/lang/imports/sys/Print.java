package es.karmadev.api.script.lang.imports.sys;

import es.karmadev.api.script.body.func.Function;
import es.karmadev.api.script.lang.variables.VoidReturn;

import java.util.ArrayList;
import java.util.List;

/**
 * println function for {@link System}
 */
public class Print implements Function {

    /**
     * Get the function name
     *
     * @return the function name
     */
    @Override
    public String getName() {
        return "print";
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
        if (parameters.length == 1) {
            java.lang.System.out.print(parameters[0]);
        }

        if (parameters.length >= 2) {
            String formatted = String.valueOf(parameters[0]);
            int replacement = 0;
            List<Object> nonReplaced = new ArrayList<>();
            for (int i = 1; i < parameters.length; i++) {
                String placeholder = "$" + replacement;
                Object value = parameters[i];

                if (formatted.contains(placeholder)) {
                    formatted = formatted.replace(placeholder, String.valueOf(value));
                    replacement++;
                } else {
                    nonReplaced.add(value);
                }
            }

            StringBuilder formatBuilder = new StringBuilder(formatted);
            for (Object missing : nonReplaced) {
                formatBuilder.append(missing);
            }
            formatted = formatBuilder.toString();

            java.lang.System.out.print(formatted);
        }

        return VoidReturn.get();
    }
}
