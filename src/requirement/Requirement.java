package requirement;

import java.io.Serializable;

/**
 * A class representing a key-value pair where the key is a string and the value
 * is the required object. Can be used to pass around objects that are required
 * by other objects.
 * <p>
 * When the required object is of type {@code String}, restrictions may be
 * enforced using the appropriate {@link StringType Type}.
 * 
 * @param <V> the type of the Requirement's value
 */
public final class Requirement<V> implements Serializable {

	private static final long serialVersionUID = 3L;

	private final String key;
	private V value;

	private boolean fulfilled;

	/** The type of the String expected as value */
	public final StringType stringType;

	/**
	 * Constructs a Requirement with a given {@code key}.
	 * 
	 * @param key the key
	 */
	public Requirement(String key) {
		fulfilled = false;
		this.key = key;
		value = null;
		stringType = null;
	}

	/**
	 * Constructs this Requirement with a given {@code key} and {@code stringType}.
	 * Note that the {@code stringType} will have an effect only when the value is
	 * of type {@code String}.
	 * 
	 * @param key        the key
	 * @param stringType the type
	 */
	public Requirement(String key, StringType stringType) {
		fulfilled = false;
		this.key = key;
		value = null;
		this.stringType = stringType;
	}

	/**
	 * Copy constructor.
	 * 
	 * @param old the object to be copied
	 */
	public Requirement(Requirement<V> old) {
		this(old.key, old.stringType);
	}

	/** @return the key */
	public String key() {
		return key;
	}

	/** @return the value */
	public V value() {
		return value;
	}

	/**
	 * Attempts to fulfil this Requirement. If {@code v} is a {@code String}
	 * additional restrictions may be enforced according to the {@code stringType}.
	 * 
	 * @param v the value of the required object
	 */
	public void fulfil(V v) {
		if ((v instanceof String) && !stringType.isValid((String) v))
			return;

		value = v;
		fulfilled = true;
	}

	/** Clears this Requirement resetting its value */
	public void clear() {
		value = null;
		fulfilled = false;
	}

	/**
	 * Returns {@code true} if this Requirement is fulfilled.
	 * 
	 * @return {@code true} if this Requirement is fulfilled
	 */
	public boolean fulfilled() {
		return fulfilled;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(
				String.format("Requirement: %s%n\tValue:     %s%n\tFulfilled: %s%n", key, value,
						fulfilled ? "yes" : "no"));

		if (stringType != null)
			sb.append(String.format("\tType:      %s%n", stringType));

		return sb.toString();
	}
}
