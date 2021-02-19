package exceptions;

@SuppressWarnings("javadoc")
public class UnsupportedMethodException extends RuntimeException {

	private static final long serialVersionUID = -637886762295139230L;

	public UnsupportedMethodException() {
		super();
	}

	public UnsupportedMethodException(String message) {
		super(message);
	}

	public UnsupportedMethodException(Throwable cause) {
		super(cause);
	}

	public UnsupportedMethodException(String message, Throwable cause) {
		super(message, cause);
	}

	public UnsupportedMethodException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
