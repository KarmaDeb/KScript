package es.karmadev.api.script.lang.imports.sys;

import es.karmadev.api.script.body.Import;
import es.karmadev.api.script.body.Variable;
import es.karmadev.api.script.body.func.Function;
import es.karmadev.api.script.lang.variables.VoidReturn;

import java.util.ArrayList;
import java.util.List;

/**
 * println function for {@link System}
 */
public class Print implements Function {

    private final System imp;

    /**
     * Initialize the function
     *
     * @param imp the function import
     */
    public Print(final System imp) {
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
    public Object execute(final Variable... parameters) {
        Variable first = parameters[0];
        Class<?> type = (Class<?>) first.getType();

        if (parameters.length == 1) {
            if (type.isArray()) {
                Object[] array = first.getValue(Object[].class);
                String raw = resolveArray(array);
                java.lang.System.out.print(raw);

                return VoidReturn.get();
            }

            java.lang.System.out.print(first.getValue(Object.class));
        }

        if (parameters.length >= 2) {
            String formatted = String.valueOf(first.getValue(Object.class));
            if (type.isArray()) {
                Object[] array = first.getValue(Object[].class);
                formatted = resolveArray(array);
            }

            int replacement = 0;
            List<Variable> nonReplaced = new ArrayList<>();
            for (int i = 1; i < parameters.length; i++) {
                String placeholder = "$" + replacement;
                Variable value = parameters[i];
                Class<?> valType = (Class<?>) value.getType();

                String raw = String.valueOf(value.getValue(Object.class));
                if (valType.isArray()) {
                    Object[] array = value.getValue(Object[].class);
                    raw = resolveArray(array);
                }

                if (formatted.contains(placeholder)) {
                    formatted = formatted.replace(placeholder, raw);
                    replacement++;
                } else {
                    nonReplaced.add(value);
                }
            }

            StringBuilder formatBuilder = new StringBuilder(formatted);
            for (Variable missing : nonReplaced) {
                Class<?> valType = (Class<?>) missing.getType();

                String raw = String.valueOf(missing.getValue(Object.class));
                if (valType.isArray()) {
                    Object[] array = missing.getValue(Object[].class);
                    raw = resolveArray(array);
                }

                formatBuilder.append(raw);
            }
            formatted = formatBuilder.toString();

            java.lang.System.out.print(formatted);
        }

        return VoidReturn.get();
    }

    private String resolveArray(final Object[] array) {
        StringBuilder builder = new StringBuilder();
        for (Object element : array) {
            if (element instanceof Variable) {
                Variable var = (Variable) element;
                if (var.isNull()) {
                    element = null;
                } else {
                    element = var.getValue(Object.class);
                }
            }

            if (element != null) {
                builder.append(element);
            }
        }

        return builder.toString();
    }
}
