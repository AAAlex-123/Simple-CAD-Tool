package exceptions;

import components.Component;

/**
 * Thrown when attempting to access or modify a Component that is hidden.
 * 
 * @see components.Component#hidden
 */
public final class ComponentNotAccessibleException extends RuntimeException {

	/**
	 * Constructs the exception using information about the {@code Component}.
	 *
	 * @param c the Component that is not accessible
	 */
	public ComponentNotAccessibleException(Component c) {
		super(String.format("Can't access or modify component %s hidden inside a gate.", c)); //$NON-NLS-1$
	}
}
