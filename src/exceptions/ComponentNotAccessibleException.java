package exceptions;

@SuppressWarnings("javadoc")
public final class ComponentNotAccessibleException extends RuntimeException {

	public ComponentNotAccessibleException() {
		super("Can't access or modify component hidden inside a gate.");
	}
}
