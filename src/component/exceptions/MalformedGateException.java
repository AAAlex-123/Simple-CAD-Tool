package component.exceptions;

import component.components.Component;

/**
 * Thrown when a hidden {@code Component} is accessed by a non-hidden one.
 *
 * @see component.components.Component#hidden hidden
 *
 * @author Alex Mandelias
 */
public class MalformedGateException extends RuntimeException {

	/**
	 * Constructs the Exception with the {@code Component}.
	 *
	 * @param component the Component that is hidden
	 */
	public MalformedGateException(Component component) {
		super(MalformedGateException.formatMessage(component));
	}

	private static String formatMessage(Component component) {
		return String.format(
		        "Component %s is hidden but was accessed by a non-hidden one", //$NON-NLS-1$
		        component);
	}
}
