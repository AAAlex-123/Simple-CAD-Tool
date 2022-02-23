package requirement.exceptions;

import requirement.requirements.AbstractRequirement;

/**
 * Thrown when a finalised {@code Requirement} attempts to alter its
 * {@code value}.
 *
 * @author Alex Mandelias
 *
 * @see AbstractRequirement#finalised()
 */
public class LockedRequirementException extends RuntimeException {

	/**
	 * Constructs the Exception with a {@code Requirement}.
	 *
	 * @param requirement the finalised Requirement
	 */
	public LockedRequirementException(AbstractRequirement requirement) {
		super(String.format("Can't alter the value of the finalised Requirement with key:%n%s", //$NON-NLS-1$
		        requirement.key()));
	}
}
