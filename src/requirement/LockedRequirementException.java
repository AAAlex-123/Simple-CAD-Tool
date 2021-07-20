package requirement;

/**
 * Thrown when a finalised Requirement attempts to alter its value.
 *
 * @author alexm
 */
public class LockedRequirementException extends RuntimeException {

	/**
	 * Constructs the Exception with information about the {@code requirement}.
	 *
	 * @param requirement the finalised Requirement
	 */
	public LockedRequirementException(Requirement<?> requirement) {
		super(String.format("Can't alter the value of the finalised Requirement:%n%s", //$NON-NLS-1$
		        requirement));
	}
}
