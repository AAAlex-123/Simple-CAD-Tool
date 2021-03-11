package exceptions;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Thrown when a Component of a class other than the expected gets passed as an
 * argument in a factory method
 */
public final class InvalidComponentException extends RuntimeException {

	/**
	 * Constructs the exception with the information of a ClassCastException
	 *
	 * @param e      the underlying ClassCastException
	 */
	public InvalidComponentException(ClassCastException e) {
		super(formatMessage(e.getLocalizedMessage()));
	}

	private static String formatMessage(String exc) {
		Pattern p = Pattern.compile("class (.*?) cannot be cast to class (.*?) ");
		Matcher m = p.matcher(exc);
		m.find();
		return String.format("Expected %s but got %s", m.group(2), m.group(1));
	}
}
