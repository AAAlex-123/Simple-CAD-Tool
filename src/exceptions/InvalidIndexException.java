package exceptions;

import components.Component;

/** Thrown when an invalid index is used to access parts of a Component. */
public final class InvalidIndexException extends RuntimeException {

	/**
	 * Constructs the exception using information about the {@code Component} and
	 * the invalid {@code index}.
	 * 
	 * @param c     the Component that is accessed
	 * @param index the invalid index
	 */
	public InvalidIndexException(Component c, int index) {
		super(String.format("Invalid index %d for component of type %s.", index, c));
	}
}
