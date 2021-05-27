package requirement;

import java.util.regex.Pattern;

/** A wrapper for a regular expression that a String value must match */
public enum StringType {

	/** Type for non-negative integers */
	NON_NEG_INTEGER("[0-9]+", "Non-negative Integer"),

	/** Type for positive integers */
	POS_INTEGER("[1-9][0-9]*", "Positive Integer"),

	/** Type for valid file names */
	FILENAME("^[^\\\\/:*?\"<>|]*$", "None of: \\, , :, *, ?, \", <, >, |"),

	/** Type for valid file types */
	FILETYPE("component|circuit", "'component' or 'circuit'"),

	/** Type for positive integers */
	ON_OFF("on|off", "'on' or 'off'"),

	/** Type for on or off */
	ANY(".+", "Non-empty string");

	/** A human-readable description for the regex of this Type */
	final String description;

	private final Pattern p;

	StringType(String regex, String description) {
		p = Pattern.compile(regex);
		this.description = description;
	}

	/**
	 * Checks whether or not the String provided matches the regular expression of
	 * this type.
	 * 
	 * @param s the String to check
	 * @return true if it matches, false otherwise
	 */
	public final boolean isValid(String s) {
		return p.matcher(s).matches();
	}

	@Override
	public String toString() {
		return String.format("%s: %s", super.toString(), description);
	}
}
