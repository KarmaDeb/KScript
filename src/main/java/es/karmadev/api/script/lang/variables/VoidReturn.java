package es.karmadev.api.script.lang.variables;

/**
 * Void return type for script
 */
public class VoidReturn {

    private final static VoidReturn instance = new VoidReturn();

    private VoidReturn() {}

    public static VoidReturn get() {
        return instance;
    }
}
