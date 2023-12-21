package es.karmadev.api.script;

import es.karmadev.api.array.ArrayUtils;
import es.karmadev.api.script.body.Import;
import es.karmadev.api.script.body.ScriptBody;
import es.karmadev.api.script.body.ScriptMethod;
import es.karmadev.api.script.exception.*;
import es.karmadev.api.script.exception.body.NoSuchImportException;
import es.karmadev.api.script.exception.body.ScriptWorkException;
import es.karmadev.api.script.lang.imports.sys.System;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Script parser. The script parser allows
 * to read a script file
 */
public final class ScriptParser {

    private final static String[] PROTECTED_WORDS = {
            "while",
            "if",
            "or",
            "switch",
            "exit",
            "return",
            "void",
            "string",
            "byte",
            "short",
            "int",
            "long",
            "float",
            "double",
            "bool",
            "is",
            "type",
            "this",
            "echo"
    };

    private final static Pattern schemaPattern = Pattern.compile("^(this)?:schema(\\s)+(?<version>[0-9]+)$", Pattern.CASE_INSENSITIVE);
    private final static Pattern importPattern = Pattern.compile("^(this)?#def \\[(?<import>\\w+)]$", Pattern.CASE_INSENSITIVE);

    private final static Pattern methodStartPattern = Pattern.compile("^((?<method>\\w+)\\((?<parameters>.*)\\)\\s*\\{.*)$");

    private final Path script;
    private final Set<Import> imports = ConcurrentHashMap.newKeySet();

    public ScriptParser(final Path script) {
        this.script = script;
        this.imports.add(System.getImport());
    }

    /**
     * Get the script body
     *
     * @return the script body
     * @throws ScriptException if there's a problem while
     * parsing the script
     * @throws ScriptRuntimeException if there's a problem while reading
     * the script file
     */
    public ScriptBody getBody() throws ScriptException, ScriptRuntimeException {
        try {
            List<String> lines = Files.readAllLines(script);
            if (lines.isEmpty()) {
                throw new InvalidSchemaVersion();
            }

            int line = 0;
            while ((lines.get(line)).trim().isEmpty()) {
                line++;
            }

            String schema = lines.remove(line++);
            Matcher schemaMatcher = schemaPattern.matcher(schema);

            if (!schemaMatcher.matches()) {
                if (schema.startsWith(":schema")) {
                    throw new InvalidSchemaVersion(schema.replaceFirst("schema", "").trim());
                }

                throw new InvalidSchemaVersion();
            }

            int schemaVersion = Integer.parseInt(schemaMatcher.group("version"));

            boolean importing = true;
            boolean commenting = false;
            boolean parsing = true;

            Set<Import> usedImports = new HashSet<>();
            List<ScriptMethod> methods = new ArrayList<>();

            int currentLevel = 0;

            String currentMethod = "";
            String[] currentParameters = new String[0];

            StringBuilder methodBuilder = new StringBuilder();
            for (String str : lines) {
                str = removeFirstSpaces(str);
                line++;

                if (str.trim().isEmpty()) {
                    continue;
                }
                if (commenting) {
                    if (str.endsWith("*/")) {
                        commenting = false;
                    }

                    continue;
                }
                if (str.startsWith("/*")) {
                    commenting = true;
                    continue;
                }

                Matcher importMatcher = importPattern.matcher(str);
                boolean isImport = false;
                if (importMatcher.matches()) {
                    isImport = true;
                    if (!importing) {
                        throw new InvalidSyntaxException(str, line, "Cannot import out of import area");
                    }
                }

                if (isImport) {
                    String importName = importMatcher.group("import");
                    Import imp = imports.stream().filter((i) -> i.getName().equals(importName)).findAny().orElse(null);

                    if (ArrayUtils.containsAny(PROTECTED_WORDS, importName)) {
                        throw new InvalidSyntaxException(str, line, "Protected import name (\"" + importName + "\")");
                    }

                    if (imp == null) {
                        throw new NoSuchImportException(importName);
                    }

                    if (!usedImports.add(imp)) {
                        java.lang.System.out.println("[WARN] Duplicated import for " + importName + " at script " + script + " (" + str + ":" + line + ")");
                    }
                }

                if (str.startsWith("exit")) {
                    if (currentLevel == 0) {
                        throw new InvalidSyntaxException(str, line, "Statement out of method body");
                    }

                    if (!str.contains(" ")) {
                        java.lang.System.out.println("[WARNING] exit statement without exit code at line " + line);
                    } else {
                        String[] data = str.split(" ");
                        String v1 = data[1];

                        try {
                            Long.parseLong(v1);
                        } catch (NumberFormatException ex) {
                            try {
                                Double.parseDouble(v1);
                            } catch (NumberFormatException ex1) {
                                throw new InvalidSyntaxException(str, line, "exit statement with invalid exit code");
                            }
                        }
                    }

                    //parsing = false;
                }

                Matcher methodMatcher = methodStartPattern.matcher(str);
                if (methodMatcher.matches()) {
                    importing = false;
                    currentMethod = methodMatcher.group("method");

                    String rawParams = methodMatcher.group("parameters");
                    if (rawParams.isEmpty()) {
                        currentParameters = new String[0];
                    } else {
                        currentParameters = groupContent(rawParams);
                    }

                    boolean multi = false;
                    for (String p : currentParameters) {
                        if (multi) {
                            throw new InvalidSyntaxException(str, line, "Multiple parameters modifier is only supported for one parameter");
                        }

                        if (p.endsWith("::")) {
                            multi = true;
                        }
                    }

                    currentLevel++;
                } else {
                    if (str.trim().endsWith("{")) {
                        currentLevel++;
                    }
                    if (str.trim().equals("}")) {
                        currentLevel--;
                        if (currentLevel == 0) {
                            parsing = true;
                            String rawMethod = methodBuilder.substring(0,
                                    methodBuilder.length() - 1);

                            ScriptMethod method = new ScriptMethod(currentMethod, rawMethod, currentParameters);
                            methods.add(method);

                            methodBuilder = new StringBuilder();
                        }
                    }

                    if (parsing && currentLevel != 0) {
                        methodBuilder.append(str).append("\n");
                        if (str.startsWith("exit")) {
                            parsing = false;
                        }
                    }
                }
            }

            if (methods.stream().noneMatch((method) -> method.getName().equals("main") && method.getParameters() == 0)) {
                throw new ScriptInitializationException("Missing main method on script body");
            }

            return new ScriptBody(schemaVersion, usedImports, methods);
        } catch (IOException ex){
            throw new ScriptWorkException(ex);
        }
    }

    public static String removeFirstSpaces(final String line) {
        int lastIndex = 0;

        if (line.startsWith(" ")) {
            while ((lastIndex++) < line.length()) {
                char character = line.charAt(lastIndex);
                if (character != ' ') break;
            }
        }

        return line.substring(lastIndex);
    }

    public static String[] groupContent(final String content) {
        List<String> contents = new ArrayList<>();
        char[] chars = content.toCharArray();

        boolean escape = false;
        char string = '\0';

        StringBuilder builder = new StringBuilder();
        for (char character : chars) {
            if (character == ' ') {
                if (string == '\0') continue;
            }

            if (character == ',') {
                if (string == '\0') {
                    escape = false;
                    contents.add(builder.toString());
                    builder = new StringBuilder();
                }

                continue;
            }

            builder.append(character);
            if (character == '\'' || character == '\"') {
                if (!escape) {
                    if (string == character) {
                        string = '\0';
                    } else {
                        string = character;
                    }
                }

                escape = false;
            }
            if (character == '\\') {
                escape = !escape;
            } else {
                escape = false;
            }
        }

        if (builder.length() > 0) {
            contents.add(builder.toString());
        }

        return contents.toArray(new String[0]);
    }
}
