package component.exceptions;

import component.components.Component;

/**
 * Thrown when a {@code Component} that is connected to another one could not be
 * found.
 *
 * @author Alex Mandelias
 */
public final class ComponentNotFoundException extends RuntimeException {

	/**
	 * Constructs the Exception with the two {@code Components}.
	 *
	 * @param target the Component that was not found
	 * @param source the Component whose connected Component was not found
	 */
	public ComponentNotFoundException(Component target, Component source) {
		super(String.format("Component %s that connects to %s could not be found", target, source)); //$NON-NLS-1$
	}
}
