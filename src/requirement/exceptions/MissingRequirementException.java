package requirement.exceptions;

/**
 * Thrown when no {@code Requirement} with the {@code key} can be found within a
 * collection of Requirements.
 *
 * @author alexm
 */
public class MissingRequirementException extends RuntimeException {

	/**
	 * Constructs the Exception with information about the {@code key}.
	 *
	 * @param key the key for which there is no Requirement
	 */
	public MissingRequirementException(String key) {
		super(String.format("No Requirement with key %s exists", key)); //$NON-NLS-1$
	}
}
