package es.karmadev.api.script.lang.variables;

/**
 * Void return type for script
 */
public class ExitVoidReturn {

    private final static ExitVoidReturn instance = new ExitVoidReturn();

    private ExitVoidReturn() {}

    public static ExitVoidReturn get() {
        return instance;
    }
}
