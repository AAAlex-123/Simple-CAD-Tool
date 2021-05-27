package exceptions;

import components.Component;
import components.ComponentType;

/** Thrown when a Component of a wrong type is provided in a Factory method. */
public final class InvalidComponentException extends RuntimeException {

	/**
	 * Constructs the exception using information about the {@code Component} and
	 * the expected {@code correctType}.
	 * 
	 * @param c           the Component with the wrong type
	 * @param correctType the correct type
	 */
	public InvalidComponentException(Component c, ComponentType correctType) {
		super(String.format("Expected type %s but got Component of type %s", correctType.description(),
				c.type().description()));
	}

}
