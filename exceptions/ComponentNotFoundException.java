package exceptions;

import components.Component;

/** Thrown when a Component that is connected to another could not be found. */
public final class ComponentNotFoundException extends RuntimeException {

	/**
	 * Constructs the exception using information about the two {@code Component}s.
	 *
	 * @param target the Component that was not found
	 * @param source the Component whose Component was not found
	 */
	public ComponentNotFoundException(Component target, Component source) {
		super(String.format("Component %s of %s couldn't be found", target, source));
	}
}
