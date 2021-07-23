package requirement.exceptions;

/**
 * Thrown when no Requirement with the {@code key} exists.
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
