package es.karmadev.api.script.body;

import es.karmadev.api.script.ScriptParser;
import es.karmadev.api.script.body.func.Function;
import es.karmadev.api.script.exception.body.NoSuchDefException;
import es.karmadev.api.script.exception.body.NoSuchImportException;
import es.karmadev.api.script.exception.ScriptRuntimeException;
import es.karmadev.api.script.exception.body.NoSuchInvokeException;
import es.karmadev.api.script.exception.body.ScriptWorkException;
import es.karmadev.api.script.lang.NullReference;
import es.karmadev.api.script.lang.variables.ExitVoidReturn;
import es.karmadev.api.script.lang.variables.VoidReturn;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents a script method. A script method
 * (unlike functions) are methods which are defined
 * on and by the script itself.
 */
public class ScriptMethod {

    private final static Pattern methodCall = Pattern.compile("^(?<import>\\w+)#(?<function>.*)\\((?<parameters>.*)\\)$", Pattern.CASE_INSENSITIVE);

    private final String name;
    private final String content;
    private final String[] parameters;

    /**
     * Initialize the method
     *
     * @param name the method name
     * @param content the method content
     * @param parameters the method parameters
     */
    public ScriptMethod(final String name, final String content, final String... parameters) {
        this.name = name;
        this.content = content;
        this.parameters = parameters;
    }

    /**
     * Get the method name
     *
     * @return the method name
     */
    public String getName() {
        return name;
    }

    /**
     * Get the method raw content
     *
     * @return the method content
     */
    public String getContent() {
        return content;
    }

    /**
     * Get the number of parameters
     * for the method
     *
     * @return the method parameters amount
     */
    public int getParameters() {
        return parameters.length;
    }

    /**
     * Get the parameter of the method
     *
     * @param index the parameter index
     * @return the parameter name
     */
    public String getParameter(final int index) {
        if (index < 0 || index >= parameters.length) return null;
        return parameters[index];
    }

    /**
     * Invoke the method
     *
     * @param body the body
     * @param parameters the method parameters
     * @return the method return
     * @throws es.karmadev.api.script.exception.ScriptRuntimeException if there's an error
     * during the script execution
     */
    public Object invoke(final ScriptBody body, final Variable... parameters) throws ScriptRuntimeException {
        Map<String, Variable> param = new HashMap<>();
        for (int i = 0; i < this.parameters.length; i++) {
            String key = this.parameters[i];
            Variable value = NullReference.get();

            if (i < parameters.length) {
                value = parameters[i];
            }

            param.put(key, value);
        }

        String[] data = content.split("\n");
        for (String line : data) {
            Matcher methodCallMath = methodCall.matcher(line);
            if (methodCallMath.matches()) {
                String impName = methodCallMath.group("import");
                String funcName = methodCallMath.group("function");

                Import imp = body.getImport(impName);
                if (imp == null) throw new ScriptWorkException(new NoSuchImportException(impName));

                String rawContent = methodCallMath.group("parameters");
                String[] raw = ScriptParser.groupContent(rawContent);
                Object[] content = new Object[raw.length];
                System.arraycopy(raw, 0, content, 0, content.length);

                Function function = imp.getFunction(funcName, content.length);
                if (function == null) throw new ScriptWorkException(new NoSuchInvokeException(imp, funcName));

                for (int i = 0; i < content.length; i++) {
                    String word = (String) content[i];
                    if (word.startsWith("\"") || word.startsWith("'")) {
                        content[i] = word.substring(1, word.length() - 1);
                        continue;
                    }

                    if (word.contains(",") || word.contains(".")) {
                        try {
                            double dValue = Double.parseDouble(word);
                            content[i] = dValue;

                            continue;
                        } catch (NumberFormatException ignored) {
                        }
                    } else {
                        try {
                            long lValue = Long.parseLong(word);
                            content[i] = lValue;

                            continue;
                        } catch (NumberFormatException ignored) {}
                    }

                    if (word.equals("true") || word.equals("false")) {
                        content[i] = word.equals("true");
                        continue;
                    }

                    Variable variable = param.get(word);
                    if (variable == null) {
                        throw new ScriptWorkException(
                                new NoSuchDefException(imp, function, line, word)
                        );
                    }

                    content[i] = String.valueOf(variable.getValue());
                }

                Object response = function.execute(content);
                if (response instanceof ExitVoidReturn) {
                    return response;
                }

                continue;
            }

            if (line.startsWith("exit")) {
                if (line.contains(" ")) {
                    String v1 = line.split(" ")[1];
                    System.out.printf("Exiting program (%s).", v1);

                    return ExitVoidReturn.get();
                }

                System.out.print("Exiting program (0).");
                return ExitVoidReturn.get();
            }
        }

        return VoidReturn.get();
    }
}
