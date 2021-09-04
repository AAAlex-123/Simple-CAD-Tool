package component.exceptions;

import component.ComponentType;
import component.components.Component;
import localisation.Languages;

/**
 * Thrown when a {@code Component} of a wrong {@code Type} is provided.
 *
 * @see component.ComponentType ComponentType
 *
 * @author Alex Mandelias
 */
public final class InvalidComponentException extends RuntimeException {

	/**
	 * Constructs the Exception with a {@code Component} and the expected
	 * {@code Type}.
	 *
	 * @param component    the Component with the wrong Type
	 * @param expectedType the expected Type
	 */
	public InvalidComponentException(Component component, ComponentType expectedType) {
		super(String.format(Languages.getString("InvalidComponentException.0"), //$NON-NLS-1$
		        expectedType.description(), component.type().description()));
	}
}
