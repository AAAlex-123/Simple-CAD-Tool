package component.exceptions;

import component.components.Component;

/**
 * Thrown when an invalid index is used to access a pin of a {@code Component}.
 *
 * @author Alex Mandelias
 */
public final class InvalidIndexException extends RuntimeException {

	/**
	 * Constructs the Exception with a {@code Component} and the invalid
	 * {@code index}.
	 *
	 * @param component the Component that is accessed
	 * @param index     the invalid index
	 */
	public InvalidIndexException(Component component, int index) {
		super(String.format("Invalid index %d for Component of type %s", index, component)); //$NON-NLS-1$
	}
}
