package requirement.requirements;

import java.util.regex.Pattern;

import localisation.Languages;

/**
 * A wrapper for a regular expression that a String must match.
 *
 * @author Alex Mandelias
 */
public enum StringType {

	/** Type for non-negative integers */
	NON_NEG_INTEGER("[0-9]+", Languages.getString("StringType.1")), //$NON-NLS-1$ //$NON-NLS-2$

	/** Type for positive integers */
	POS_INTEGER("[1-9][0-9]*", Languages.getString("StringType.3")), //$NON-NLS-1$ //$NON-NLS-2$

	/** Type for valid file names */
	FILENAME("^[^\\\\/:*?\"<>|]*$", Languages.getString("StringType.5")), //$NON-NLS-1$ //$NON-NLS-2$

	/** Type for any non-empty string */
	NON_EMPTY(".+", Languages.getString("StringType.7")), //$NON-NLS-1$ //$NON-NLS-2$

	/** Type for any non-empty string */
	ANY(".*", Languages.getString("StringType.9")), //$NON-NLS-1$ //$NON-NLS-2$

	/** Type for custom regex. Defaults to {@link #ANY}. */
	CUSTOM(".*", Languages.getString("StringType.11")) { //$NON-NLS-1$ //$NON-NLS-2$
		@Override
		public StringType alter(String regex, String desc) {
			p = Pattern.compile(regex);
			description = desc;
			return this;
		}
	};

	/** A Pattern for the regex of this Type */
	protected Pattern p;

	/** A human-readable description for the regex of this Type */
	protected String description;

	StringType(String regex, String description) {
		p = Pattern.compile(regex);
		this.description = description;
	}

	/**
	 * Allows Types to alter their {@code regex} and {@code description} at runtime.
	 * <p>
	 * Currently only the {@link #CUSTOM} Type may call this method.
	 *
	 * @param regex the new regex for this Type
	 * @param desc  the new description for this Type
	 *
	 * @return this (used for chaining)
	 */
	@SuppressWarnings("unused")
	public StringType alter(String regex, String desc) {
		throw new UnsupportedOperationException(
		        String.format("Type %s does not support custom regex and description", this)); //$NON-NLS-1$
	}

	/**
	 * Checks whether or not a String matches the regex of this Type.
	 *
	 * @param s the String to check
	 *
	 * @return {@code true} if it matches, {@code false} otherwise
	 */
	public final boolean isValid(String s) {
		return p.matcher(s).matches();
	}

	/**
	 * Returns this Type's {@code description}, a human-readable String that
	 * describes the restrictions of the regex of this Type.
	 *
	 * @return the description
	 */
	public final String getDescription() {
		return description;
	}
}
