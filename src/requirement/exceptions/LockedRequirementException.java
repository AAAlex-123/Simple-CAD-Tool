package requirement.exceptions;

import requirement.requirements.AbstractRequirement;

/**
 * Thrown when a finalised {@code Requirement} attempts to alter its
 * {@code value}.
 *
 * @author alexm
 */
public class LockedRequirementException extends RuntimeException {

	/**
	 * Constructs the Exception with information about the {@code Requirement}.
	 *
	 * @param req the finalised Requirement
	 */
	public LockedRequirementException(AbstractRequirement req) {
		super(String.format("Can't alter the value of the finalised Requirement:%n%s", req)); //$NON-NLS-1$
	}
}
