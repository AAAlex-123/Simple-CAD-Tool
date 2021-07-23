package requirement.exceptions;

import requirement.requirements.AbstractRequirement;

/**
 * Thrown when a {@code Requirement} attempts to access its {@code Graphic} the
 * its concrete subclass does not support it.
 *
 * @author alexm
 */
public final class UnsupportedGraphicException extends RuntimeException {

	/**
	 * Constructs the Exception with information about the {@code Requirement}.
	 *
	 * @param req the Requirement that doesn't have a Graphic object
	 */
	public UnsupportedGraphicException(AbstractRequirement req) {
		super(String.format("Requirements of type %s don't have Graphic support", //$NON-NLS-1$
		        req.getClass().getSimpleName()));
	}
}
