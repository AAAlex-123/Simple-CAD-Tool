package requirement.exceptions;

/**
 * Thrown when no {@code Requirement} with a specific {@code key} can be found
 * within a collection of Requirements.
 *
 * @author Alex Mandelias
 *
 * @see requirement.util.Requirements Requirements
 */
public class MissingRequirementException extends RuntimeException {

	/**
	 * Constructs the Exception with a {@code key}.
	 *
	 * @param key the key for which no Requirement exists
	 */
	public MissingRequirementException(String key) {
		super(String.format("No Requirement with key '%s' exists", key)); //$NON-NLS-1$
	}
}
