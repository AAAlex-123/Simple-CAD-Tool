package component.exceptions;

import component.components.Component;

/**
 * Thrown when attempting to access or modify a {@code Component} that is
 * hidden.
 *
 * @see component.components.Component#hidden
 *
 * @author Alex Mandelias
 */
public final class ComponentNotAccessibleException extends RuntimeException {

	/**
	 * Constructs the Exception with a {@code Component}.
	 *
	 * @param component the Component that is not accessible
	 */
	public ComponentNotAccessibleException(Component component) {
		super(String.format("Can't access or modify component %s hidden inside a gate", component)); //$NON-NLS-1$
	}
}
