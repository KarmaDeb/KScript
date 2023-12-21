package es.karmadev.api.script.exception;

/**
 * This exception is thrown when a script file
 * completely misses or has an unsupported schema
 * version. The schema version defines how the script
 * is parsed and interpreted
 */
public class InvalidSchemaVersion extends ScriptException {

    /**
     * Initialize the exception
     */
    public InvalidSchemaVersion() {
        super("Failed to parse script: missing schema version");
    }

    /**
     * Initialize the exception
     *
     * @param provided the provided schema version
     */
    public InvalidSchemaVersion(final String provided) {
        super("Failed to parse script: unsupported schema: " + provided);
    }
}
