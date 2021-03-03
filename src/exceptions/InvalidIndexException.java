package exceptions;

import components.Component;

@SuppressWarnings("javadoc")
public final class InvalidIndexException extends RuntimeException {

	public InvalidIndexException(Component c, int index) {
		super(String.format("Invalid index %d for component of type %s.", index, c));
	}
}
