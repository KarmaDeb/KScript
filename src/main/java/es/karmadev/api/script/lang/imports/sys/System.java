package es.karmadev.api.script.lang.imports.sys;

import es.karmadev.api.script.body.Import;
import es.karmadev.api.script.body.func.Function;

import java.util.ArrayList;
import java.util.List;

/**
 * System import
 */
public class System extends Import {

    public final static System system = new System();

    final Print print = new Print(this);
    final PrintLn printLn = new PrintLn(this);
    final Exit exit = new Exit(this);

    private final Function[] functions = {
        print,
        printLn,
        exit
    };

    /**
     * Initialize the import
     */
    System() {
        super("system");
    }

    /**
     * Get the import instance
     *
     * @return the import instance
     */
    public static System getImport() {
        return system;
    }

    /**
     * Get all the import functions
     *
     * @return the import functions
     */
    @Override
    public Function[] getFunctions() {
        return functions.clone();
    }

    /**
     * Get a function
     *
     * @param name the function name
     * @return the functions
     */
    @Override
    public Function[] getFunctions(final String name) {
        List<Function> match = new ArrayList<>();
        for (Function func : functions) {
            if (func.getName().equals(name)) {
                match.add(func);
            }
        }

        return match.toArray(new Function[0]);
    }

    /**
     * Get a function
     *
     * @param name       the function name
     * @param parameters the function parameters amount
     * @return the function
     */
    @Override
    public Function getFunction(final String name, final int parameters) {
        Function match = null;
        for (Function function : functions) {
            String funcName = function.getName();
            if (!funcName.equals(name)) continue;

            int funcParams = function.getParameters();
            if (function.isMinParameters()) {
                if (funcParams <= parameters) {
                    if (match == null || parameters == funcParams) {
                        match = function;
                    }

                    continue;
                }
            }

            if (funcParams == parameters) {
                match = function;
            }
        }

        return match;
    }
}
