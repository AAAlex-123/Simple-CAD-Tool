package exceptions;

import components.Component;

/**
 * Thrown when a hidden Component is accessed by a non-hidden one.
 * 
 * @see components.Component#hidden
 */
public class MalformedGateException extends RuntimeException {

	/**
	 * Constructs the exception using information about the {@code Component}.
	 *
	 * @param c hidden Component
	 */
	public MalformedGateException(Component c) {
		super(formatMessage(c));
	}

	private static String formatMessage(Component c) {
		return String.format("Component %s is hidden inside a Gate but was accessed by a non-hidden one", c); //$NON-NLS-1$
	}
}
