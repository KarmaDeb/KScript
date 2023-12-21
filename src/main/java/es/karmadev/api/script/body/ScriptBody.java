package es.karmadev.api.script.body;

import es.karmadev.api.script.body.func.Function;
import es.karmadev.api.script.exception.ScriptRuntimeException;
import es.karmadev.api.script.exception.body.ScriptWorkException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Represents a script body
 */
public class ScriptBody {

    private final int version;
    private final List<Import> imports = new ArrayList<>();
    private final List<ScriptMethod> methods = new ArrayList<>();

    /**
     * Initialize the script body
     *
     * @param version the script schema version
     * @param imports the script imports
     * @param methods the script methods
     */
    public ScriptBody(final int version, final Collection<Import> imports, final Collection<ScriptMethod> methods) {
        this.version = version;
        this.imports.addAll(imports);
        this.methods.addAll(methods);
    }

    /**
     * Find a function through all the
     * imports. This method will return the first
     * match
     *
     * @param funcName the function to call
     * @param parameters the number of parameters
     * @return the matching function
     */
    public Function findFunction(final String funcName, final int parameters) {
        Function match = null;
        for (Import imp : imports) {
            for (Function func : imp.getFunctions()) {
                if (func.getName().equals(funcName)) {
                    int params = func.getParameters();
                    if (func.isMinParameters()) {
                        if (params <= parameters) {
                            match = func;
                        }
                    } else {
                        if (params == parameters) return func;
                    }
                }
            }
        }

        return match;
    }

    /**
     * Get an import
     *
     * @param name the import name
     * @return the import
     */
    public Import getImport(final String name) {
        return imports.stream().filter((imp) -> imp.getName().equals(name))
                .findAny().orElse(null);
    }

    /**
     * Get a method
     *
     * @param name the method name
     * @return the method
     */
    public ScriptMethod getMethod(final String name, final Object... parameters) {
        return methods.stream()
                .filter((method) -> {
                    if (method.getName().equals(name)) {
                        if (method.getParameters() == parameters.length) return true;

                        int count = 0;
                        for (int i = 0; i < method.getParameters(); i++) {
                            String nm = method.getParameter(i);
                            if (nm.endsWith("::")) {
                                if (count >= parameters.length) return true;
                            }

                            count++;
                        }
                    }

                    return false;
                })
                .findAny().orElse(null);
    }

    /**
     * Initialize the script. This always
     * means a call to main() script method
     *
     * @throws ScriptRuntimeException if there's any problem while
     * running the script
     */
    public void init() throws ScriptRuntimeException {
        ScriptMethod main = methods.stream()
                .filter((method) -> method.getName().equals("main") && method.getParameters() == 0)
                .findAny().orElse(null);

        if (main == null) throw new ScriptWorkException("Cannot run script. Missing main() method");
        main.invoke(this);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("ScriptBody@")
                .append(hashCode()).append("{\n\t")
                .append("schema=").append(version).append("\n\t")
                .append("imports=[\n");
        for (Import imp : imports) {
            builder.append("\t\t").append(imp.getName());
        }
        builder.append("\n\t],\n\t")
                .append("methods=[\n");

        for (ScriptMethod method : methods) {
            builder.append("\t\t").append(method.getName()).append("(");

            for (int i = 0; i < method.getParameters(); i++) {
                builder.append("<").append((i + 1))
                        .append(":").append(method.getParameter(i)).append(">");
                if (i < method.getParameters() - 1) {
                    builder.append(", ");
                }
            }
            builder.append(")").append(" {\n");

            for (String raw : method.getContent().split("\n")) {
                builder.append("\t\t\t").append(raw).append("\n");
            }
            builder.append("\t\t}\n");
        }
        builder.append("\t]\n}");

        return builder.toString();
    }
}
