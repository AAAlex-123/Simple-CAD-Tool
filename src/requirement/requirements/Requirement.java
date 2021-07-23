package requirement.requirements;

import static localisation.RequirementStrings.NO;
import static localisation.RequirementStrings.YES;

import java.io.Serializable;

import localisation.Languages;

/**
 * A class representing a key-value pair where the key is a string and the value
 * is the required object. Can be used to pass around objects that are required
 * by other objects.
 * <p>
 * When the required object is of type {@code String}, restrictions may
 * optionally be enforced using the appropriate {@link StringType}.
 *
 * @param <V> the type of the Requirement's value
 *
 * @author alexm
 */
public final class Requirement<V> implements Serializable {

	private static final long serialVersionUID = 6L;

	private final String key;
	private V            value;
	private V            defaultValue;

	private boolean fulfilled, finalised;

	/** The (optional) type of String expected as value. Used when V == String. */
	final StringType stringType;

	/**
	 * Constructs this Requirement with a {@code key}.
	 *
	 * @param key the key
	 */
	public Requirement(String key) {
		this.key = key;
		value = defaultValue = null;
		fulfilled = finalised = false;
		stringType = null;
	}

	/**
	 * Constructs this Requirement with a {@code key} and a {@code type}.
	 *
	 * @param key  the key
	 * @param type the type
	 *
	 * @see Requirement#stringType
	 */
	public Requirement(String key, StringType type) {
		this.key = key;
		value = defaultValue = null;
		fulfilled = finalised = false;
		stringType = type;
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
		defaultValue = other.defaultValue;
	}

	/** @return the key */
	public String key() {
		return key;
	}

	/** @return the value */
	public V value() {
		return value;
	}

	/** @return the default value */
	V defaultValue() {
		return defaultValue;
	}

	/**
	 * Attempts to set the {@code default value} of this Requirement without marking
	 * it as {@code fulfilled}. If {@code v} is a {@code String} additional
	 * restrictions may be enforced according to the {@code stringType}.
	 * <p>
	 * This method is intended for offering a default value which must then be
	 * explicitly set in order for the Requirement to be marked as fulfilled.
	 *
	 * @param v the default value of the required object
	 */
	public void offer(V v) {
		if (finalised)
			throw new LockedRequirementException(this);

		if ((v instanceof String) && (stringType != null) && !stringType.isValid((String) v))
			return;

		defaultValue = v;
	}

	/**
	 * Calls {@link Requirement#offer(Object) offer(V)} and additionally sets the
	 * {@code value} of this Requirement, marking is as {@code fulfilled}.
	 * <p>
	 * This method is intended for normal use, to specify that a value for this
	 * Requirement exists.
	 *
	 * @param v the value of the required object
	 */
	public void fulfil(V v) {
		offer(v);
		value = v;
		fulfilled = !((v instanceof String) && (stringType != null)
				&& !stringType.isValid((String) v));
	}

	/**
	 * Calls {@link Requirement#fulfil(Object) fulfil(V)} and additionally marks
	 * this Requirement as finalised, meaning that its value cannot be altered.
	 * <p>
	 * This method is intended for providing a strict default or for ensuring the
	 * value cannot be altered in the future.
	 *
	 * @param v the value of the required object
	 */
	public void finalise(V v) {
		fulfil(v);
		finalised = true;
	}

	/** Clears this Requirement and its {@code value} resetting its state */
	public void clear() {
		value = null;
		fulfilled = false;
		finalised = false;
	}

	/** Clears this Requirement and resets the {@code value} to its default */
	@SuppressWarnings("unchecked")
	public void reset() {
		clear();
		if ((value instanceof String) && (defaultValue == null))
			value = (V) ""; //$NON-NLS-1$
		else
			value = defaultValue;
	}

	/** @return {@code true} if fulfilled, {@code false} otherwise */
	public boolean fulfilled() {
		return fulfilled;
	}

	/** @return {@code true} if finalised, {@code false} otherwise */
	public boolean finalised() {
		return finalised;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder(String.format(
				Languages.getString("Requirement.1"), //$NON-NLS-1$
				key, defaultValue, value, fulfilled() ? YES : NO, finalised() ? YES : NO));

		if (stringType != null)
			sb.append(String.format(Languages.getString("Requirement.2"), stringType)); //$NON-NLS-1$

		return sb.toString();
	}
}
