package exceptions;

import components.Component;

/** Thrown when a Component could not be found. */
public final class ComponentNotFoundException extends RuntimeException {

	/**
	 * Constructs the exception using the Component that couldn't be found.
	 * 
	 * @param c the Component
	 */
	public ComponentNotFoundException(Component c) {
		super(String.format("Attempted to remove a %s that couldn't be found", c));
	}
}
