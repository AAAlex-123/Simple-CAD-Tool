package application;

import java.io.Serializable;
import java.util.regex.Pattern;

/**
 * A class representing a key-value pair where the key is a string and the value
 * is the required object. Can be used to pass around objects that are required
 * by other objects.
 * <p>
 * When the required object is a String, restrictions may be enforced using the
 * {@link StringType Type} needed.
 * 
 * @param <V> the type of the Requirement's value
 */
final class Requirement<V> implements Serializable {

	private static final long serialVersionUID = 1L;

	private final String key;
	private V value;

	private boolean fulfilled;

	/** The type of the String expected as value */
	final StringType stringType;

	/** A wrapper for a regular expression that a String value must match */
	enum StringType {

		/** Type for non-negative integers */
		NON_NEG_INTEGER("[0-9]+"),

		/** Type for positive integers */
		POS_INTEGER("[1-9][0-9]*"),

		/** Type for valid file names */
		FILENAME("^[^\\\\/:*?\"<>|]*$"),

		/** Type for valid file types */
		FILETYPE("component|circuit"),

		/** Type for positive integers */
		ON_OFF("on|off"),

		/** Type for any string */
		ANY(".*");

		StringType(String regex) {
			this.regex = regex;
			p = Pattern.compile(regex);
		}

		/** The regular expression that validates a string */
		final String regex;

		private final Pattern p;
	}

	/**
	 * Constructs a Requirement with a given {@code key}.
	 * 
	 * @param key the key
	 */
	Requirement(String key) {
		fulfilled = false;
		this.key = key;
		value = null;
		stringType = StringType.ANY;
	}

	/**
	 * Constructs this Requirement with a given {@code key} and {@code stringType}.
	 * Note that the {@code stringType} will have an effect only when the value is
	 * of type String.
	 * 
	 * @param key        the key
	 * @param stringType the type
	 */
	Requirement(String key, StringType stringType) {
		fulfilled = false;
		this.key = key;
		value = null;
		this.stringType = stringType;
	}

	/**
	 * Attempts to fulfil this Requirement. If {@code v} is a {@code String}
	 * additional restrictions may be enforced according to the {@code stringType}.
	 * 
	 * @param v the value of the required object
	 */
	void fulfil(V v) {
		if ((v instanceof String) && !stringType.p.matcher((String) v).matches())
			return;

		value = v;
		fulfilled = true;
	}

	/**
	 * Returns {@code true} if this Requirement is fulfilled.
	 * 
	 * @return {@code true} if this Requirement is fulfilled
	 */
	boolean fulfilled() {
		return fulfilled;
	}

	/** Clears this Requirement resetting its value */
	void clear() {
		value = null;
		fulfilled = false;
	}

	/** @return the key */
	String key() {
		return key;
	}

	/** @return the value */
	V value() {
		return value;
	}

	@Override
	public String toString() {
		return String.format("Requirement: %s, Value %d, %sfulfilled", key, value, fulfilled ? "" : "not ");
	}
}