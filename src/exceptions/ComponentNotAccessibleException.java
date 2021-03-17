package exceptions;

import components.Component;

/**
 * Thrown when trying to access or modify a Component that is "hidden" inside
 * another gate.
 */
public final class ComponentNotAccessibleException extends RuntimeException {

	/**
	 * Constructs the exception using the necessary information.
	 *
	 * @param c the Component that is not accessible
	 */
	public ComponentNotAccessibleException(Component c) {
		super(String.format("Can't access or modify component %s hidden inside a gate.", c));
	}
}
