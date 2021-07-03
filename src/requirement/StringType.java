package requirement;

import java.util.regex.Pattern;

/**
 * A wrapper for a regular expression that a String must match.
 *
 * @author alexm
 */
public enum StringType {

	/** Type for non-negative integers */
	NON_NEG_INTEGER("[0-9]+", "Non-negative int"),

	/** Type for positive integers */
	POS_INTEGER("[1-9][0-9]*", "Positive integer"),

	/** Type for valid file names */
	FILENAME("^[^\\\\/:*?\"<>|]*$", "Valid file name"),

	/** Type for valid file types */
	FILETYPE("component|circuit", "'component' or 'circuit'"),

	/** Type for positive integers */
	ON_OFF("on|off", "'on' or 'off'"),

	/** Type for any string */
	ANY(".+", "Non-empty string"),

	/** Type for custom regex. Defaults to ANY */
	CUSTOM(".*", "Custom") {
		@Override
		public StringType alter(String regex, String description) {
			p = Pattern.compile(regex);
			desc = description;
			return this;
		}
	};

	/** The regex of this Type */
	Pattern p;
	/** A human-readable description for the regex of this Type */
	String  desc;

	StringType(String regex, String description) {
		p = Pattern.compile(regex);
		desc = description;
	}

	/**
	 * Allows some StringTypes to define custom regex at runtime.
	 *
	 * @param regex       the new regex for this StringType
	 * @param description the new description for this StringType
	 *
	 * @return this (used for chaining)
	 */
	public StringType alter(String regex, String description) {
		throw new UnsupportedOperationException(
				"Type " + this + " does not support custom regex and description");
	}

	/**
	 * Checks whether or not the String provided matches the regular expression of
	 * this type.
	 *
	 * @param s the String to check
	 *
	 * @return true if it matches, false otherwise
	 */
	public final boolean isValid(String s) {
		return p.matcher(s).matches();
	}

	@Override
	public final String toString() {
		return String.format("%s: %s", super.toString(), desc);
	}
}
