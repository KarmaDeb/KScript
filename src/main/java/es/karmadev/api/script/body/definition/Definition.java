package es.karmadev.api.script.body.definition;

/**
 * Represents a definition
 */
public interface Definition {

    /**
     * Get the definition type
     *
     * @return the definition type
     */
    DefType getType();

    /**
     * Get the definition name
     *
     * @return the definition name
     */
    String getName();
}
