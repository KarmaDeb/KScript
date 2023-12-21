package es.karmadev.api.script.body;

import es.karmadev.api.script.ScriptParser;
import es.karmadev.api.script.body.func.Function;
import es.karmadev.api.script.exception.body.NoSuchDefException;
import es.karmadev.api.script.exception.body.NoSuchImportException;
import es.karmadev.api.script.exception.ScriptRuntimeException;
import es.karmadev.api.script.exception.body.NoSuchInvokeException;
import es.karmadev.api.script.exception.body.NoSuchMethodException;
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
    private final static Pattern selfMethodCall = Pattern.compile("^(this#)?(?<function>.*)\\((?<parameters>.*)\\)$", Pattern.CASE_INSENSITIVE);

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
            if (key.endsWith("::")) {
                if (i < parameters.length) {
                    Variable[] vars = new Variable[parameters.length - i];
                    int wIndex = 0;
                    for (int j = i; j < parameters.length; j++) {
                        vars[wIndex++] = parameters[j];
                    }

                    param.put(key.substring(0, key.length() - 2), Variable.wrap(vars, Variable[].class));
                }

                i = this.parameters.length;
            }

            Variable value = NullReference.get();

            if (i < parameters.length) {
                value = parameters[i];
            }

            param.put(key, value);
        }

        param.put("this", Variable.wrap(body, ScriptBody.class));

        String[] data = content.split("\n");
        for (String line : data) {
            Matcher methodCallMath = methodCall.matcher(line);
            Matcher selfMethodCallMath = selfMethodCall.matcher(line);

            if (methodCallMath.matches() || selfMethodCallMath.matches()) {
                String impName;
                String funcName;
                String rawContent;
                if (methodCallMath.matches()) {
                    impName = methodCallMath.group("import");
                    funcName = methodCallMath.group("function");
                    rawContent = methodCallMath.group("parameters");
                } else {
                    impName = null;
                    funcName = selfMethodCallMath.group("function");
                    rawContent = selfMethodCallMath.group("parameters");
                }

                String[] raw = ScriptParser.groupContent(rawContent);
                Variable[] content = new Variable[raw.length];
                for (int i = 0; i < raw.length; i++) {
                    String str = raw[i];
                    if (str.endsWith("::")) {
                        str = str.substring(0, str.length() - 2);
                    }
                    content[i] = Variable.wrap(str, String.class);
                }

                Import imp = null;
                Function func = null;
                if (impName != null) {
                    if (!impName.equals("this")) {
                    imp = body.getImport(impName);
                    if (imp == null) {
                        throw new ScriptWorkException(
                                new NoSuchImportException(impName)
                        );
                    }

                    func = imp.getFunction(funcName, content.length);
                    if (func == null) throw new ScriptWorkException(new NoSuchInvokeException(imp, funcName));
                    }
                } else {
                    func = body.findFunction(funcName, content.length);
                    if (func == null) {
                        impName = "this";
                    } else {
                        imp = func.getImport();
                        impName = func.getImport().getName();
                    }
                }

                mapVariables(content, param, imp, func, line);

                if (imp == null) {
                    if (impName.equals("this")) {
                        ScriptMethod method = body.getMethod(funcName, content.length);
                        if (method == null) throw new ScriptWorkException(
                                new NoSuchMethodException(funcName, content.length)
                        );

                        method.invoke(body, content);
                        continue;
                    }

                    throw new ScriptWorkException(new NoSuchImportException(impName));
                }

                Object response = func.execute(content);
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

            if (line.startsWith("echo")) {
                if (!line.contains(" ")) {
                    System.out.print("");
                    continue;
                }

                line = ScriptParser.removeFirstSpaces(line.substring(4));
                if (!line.startsWith("[") && !line.endsWith("]")) {
                    System.out.println(line);
                } else {
                    es.karmadev.api.script.lang.imports.sys.System
                            sys = es.karmadev.api.script.lang.imports.sys.System.getImport();
                    Function print = sys.getFunction("println", 0);

                    String[] raw = ScriptParser.groupContent(line.substring(1, line.length() - 1));
                    Variable[] content = new Variable[raw.length];
                    for (int i = 0; i < raw.length; i++) {
                        String str = raw[i];
                        if (str.endsWith("::")) {
                            str = str.substring(0, str.length() - 2);
                        }
                        content[i] = Variable.wrap(str, String.class);
                    }

                    mapVariables(content, param, null, null, line);
                    print.execute(content);
                }
            }
        }

        return VoidReturn.get();
    }

    private void mapVariables(final Variable[] content, final Map<String, Variable> param,
                              final Import imp, final Function func, final String line) {

        for (int i = 0; i < content.length; i++) {
            Variable var = content[i];
            if (var.isNull()) {
                content[i] = var;
                continue;
            }

            String word = var.getValue(String.class);

            if (word.startsWith("\"") || word.startsWith("'")) {
                content[i] = Variable.wrap(word.substring(1, word.length() - 1), String.class);
                continue;
            }

            if (word.contains(",") || word.contains(".")) {
                try {
                    double dValue = Double.parseDouble(word);
                    content[i] = Variable.wrap(dValue, Number.class);

                    continue;
                } catch (NumberFormatException ignored) {
                }
            } else {
                try {
                    long lValue = Long.parseLong(word);
                    content[i] = Variable.wrap(lValue, Number.class);

                    continue;
                } catch (NumberFormatException ignored) {}
            }

            if (word.equals("true") || word.equals("false")) {
                content[i] = Variable.wrap(word.equals("true"), Boolean.class);
                continue;
            }

            Variable variable = param.get(word);
            if (variable == null) {
                if (imp == null) {
                    throw new ScriptWorkException(
                            new NoSuchDefException(line, word)
                    );
                } else {
                    throw new ScriptWorkException(
                            new NoSuchDefException(imp, func, line, word)
                    );
                }
            }

            content[i] = variable;
        }
    }
}
