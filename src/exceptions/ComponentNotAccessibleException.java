package exceptions;

/**
 * Thrown when trying to access or modify a Component that is "hidden" inside
 * another gate.
 */
public final class ComponentNotAccessibleException extends RuntimeException {

	/** Constructs the exception with the default message */
	public ComponentNotAccessibleException() {
		super("Can't access or modify component hidden inside a gate.");
	}
}
