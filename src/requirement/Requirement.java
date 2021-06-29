package requirement;

import java.io.Serializable;

/**
 * A class representing a key-value pair where the key is a string and the value
 * is the required object. Can be used to pass around objects that are required
 * by other objects.
 * <p>
 * When the required object is of type {@code String}, restrictions may
 * optionally be enforced using the appropriate {@link StringType Type}.
 *
 * @param <V> the type of the Requirement's value
 *
 * @author alexm
 */
public final class Requirement<V> implements Serializable {

	private static final long serialVersionUID = 5L;

	private final String key;
	private V            value;

	private boolean fulfilled, finalised;

	/** The (optional) type of String expected as value. Used when V == String. */
	public final StringType stringType;

	/**
	 * Constructs a Requirement with a given {@code key}.
	 *
	 * @param reqKey the key
	 */
	public Requirement(String reqKey) {
		fulfilled = finalised = false;
		key = reqKey;
		value = null;
		stringType = null;
	}

	/**
	 * Constructs this Requirement with a given {@code key} and {@code stringType}.
	 *
	 * @param reqKey  the key
	 * @param reqType the type
	 *
	 * @see Requirement#stringType
	 */
	public Requirement(String reqKey, StringType reqType) {
		fulfilled = finalised = false;
		key = reqKey;
		value = null;
		stringType = reqType;
	}

	/**
	 * Copy constructor.
	 *
	 * @param other the object to be copied
	 */
	public Requirement(Requirement<V> other) {
		this(other.key, other.stringType);
		fulfilled = other.fulfilled;
		finalised = other.finalised;
		value = other.value;
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
	 * Attempts set the value of this Requirement without marking it as fulfilled.
	 * If {@code v} is a {@code String} additional restrictions may be enforced
	 * according to the {@code stringType}.
	 * <p>
	 * This method is intended for offering a default value which must then be
	 * explicitly set in order for the Requirement to be marked as fulfilled.
	 *
	 * @param v the value of the required object
	 */
	public void offer(V v) {
		if (finalised)
			throw new LockedRequirementException(this);

		if ((v instanceof String) && (stringType != null) && !stringType.isValid((String) v))
			return;

		value = v;
	}

	/**
	 * Calls {@link Requirement#offer(Object) offer(V)} and additionally marks this
	 * Requirement as fulfilled, meaning that this value was not a default value.
	 * <p>
	 * This method is intended for normal use, to specify that a value for this
	 * Requirement exists.
	 *
	 * @param v the value of the required object
	 */
	public void fulfil(V v) {
		offer(v);
		fulfilled = !((v instanceof String) && (stringType != null)
		        && !stringType.isValid((String) v));
	}

	/**
	 * Calls {@link Requirement#fulfil(Object) fulfil(V)} and additionally marks
	 * this Requirement as locked, meaning that its value cannot be altered.
	 * <p>
	 * This method is intended for providing a strict default or for making sure the
	 * value is final.
	 *
	 * @param v the value of the required object
	 */
	public void finalise(V v) {
		fulfil(v);
		finalised = true;
	}

	/** Clears this Requirement resetting its value */
	public void clear() {
		value = null;
		fulfilled = false;
		finalised = false;
	}

	/** @return {@code true} if fulfilled, {@code false} otherwise */
	public boolean fulfilled() {
		return fulfilled;
	}

	/** @return {@code true} if locked, {@code false} otherwise */
	boolean finalised() {
		return finalised;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder(String.format(
		        "Requirement: %s%n\tValue:     %s%n\tFulfilled: %s%n\tFinalise:  %s%n", key, value,
		        fulfilled() ? "yes" : "no", finalised() ? "yes" : "no"));

		if (stringType != null)
			sb.append(String.format("\tType:      %s%n", stringType));

		return sb.toString();
	}
}
