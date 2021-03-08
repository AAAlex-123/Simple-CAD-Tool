package exceptions;

import components.Component;

/**
 * Thrown when a Component is attempting to access another Component using an
 * invalid index
 */
public final class InvalidIndexException extends RuntimeException {

	/**
	 * Constructs the exception using the necessary information.
	 * 
	 * @param c     the Component that is accessed
	 * @param index the invalid index
	 */
	public InvalidIndexException(Component c, int index) {
		super(String.format("Invalid index %d for component of type %s.", index, c));
	}
}
