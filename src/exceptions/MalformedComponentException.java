package exceptions;

@SuppressWarnings("javadoc")
public class MalformedComponentException extends RuntimeException {

	private static final long serialVersionUID = -7192710171970859109L;

	public MalformedComponentException() {
		super();
	}

	public MalformedComponentException(String message) {
		super(message);
	}

	public MalformedComponentException(Throwable cause) {
		super(cause);
	}

	public MalformedComponentException(String message, Throwable cause) {
		super(message, cause);
	}

	public MalformedComponentException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
