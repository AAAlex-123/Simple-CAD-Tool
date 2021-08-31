package requirement.requirements;

import java.io.Serializable;

import localisation.Languages;
import localisation.RequirementStrings;
import requirement.exceptions.LockedRequirementException;
import requirement.graphics.AbstractRequirementGraphic;
import requirement.graphics.NullRequirementGraphic;

/**
 * A class encapsulating the process where:
 * <ul>
 * <li>object A needs object B in order to carry out an operation
 * <li>object C has access to / can construct object B
 * <li>object C provides B to A thereby fulfilling A's requirement
 * <li>object A receives object B and can carry out its operation
 * </ul>
 * <p>
 * An {@code AbstractRequirement} ({@code Requirement}) encapsulates this
 * behaviour (and extends it to some extent) providing the means for object A to
 * create the Requirement that object C will fulfil with object B, which A will
 * then retrieve.
 * <p>
 * A Requirement may optionally be accompanied by a
 * {@link requirement.graphics.AbstractRequirementGraphic
 * AbstractRequirementGraphic} object which is responsible for providing a GUI
 * with which the user fulfils the Requirement. If such Graphic object isn't
 * defined, requesting the Graphic object returns a
 * {@link requirement.graphics.NullRequirementGraphic NullGraphic} and
 * subsequent calls to {@link #hasGraphic()} return {@code false}.
 *
 * @author alexm
 */
public abstract class AbstractRequirement implements Serializable, Cloneable {

	private static final long serialVersionUID = 7L;

	/**
	 * A short and descriptive name for the Requirement which may be displayed in a
	 * Graphic object and is used in a {@link requirement.requirements.Requirements
	 * Requirements} object to identify it.
	 */
	protected final String key;

	/**
	 * The required object, the object that will be set when the Requirement is
	 * fulfilled and will later be retrieved by another object to be used to carry
	 * out another operation.
	 */
	protected Object value;

	/**
	 * The default value of the Requirement. This (optional) value is used to when
	 * resetting the Requirement and may also be the preset option in a Graphic
	 * object.
	 */
	protected Object defaultValue;

	/**
	 * Indicates whether or not a valid value for this Requirement has been
	 * provided. If the Requirement is fulfilled, the object that created the
	 * Requirement may retrieve its value and use it.
	 */
	protected boolean fulfilled;

	/**
	 * Indicates whether or not the value for this Requirement is final, meaning it
	 * cannot be altered. A Graphic object may use this information to alter its
	 * appearance if the Requirement is finalised.
	 */
	protected boolean finalised;

	/** The Graphic associated with this Requirement */
	private transient AbstractRequirementGraphic<?> g;

	/**
	 * {@code true} if and only if the Graphic is not a {@code NullGraphic}, meaning
	 * that this Requirement supports a Graphic. This variable can only be expected
	 * to have the correct value if a Graphic has previously been constructed.
	 */
	protected transient boolean hasGraphic;

	/**
	 * Constructs this Requirement with the given {@code key}.
	 *
	 * @param key the key of this Requirement
	 */
	public AbstractRequirement(String key) {
		this.key = key;
		value = defaultValue = null;
		fulfilled = finalised = false;
	}

	@Override
	public AbstractRequirement clone() {
		try {
			return (AbstractRequirement) super.clone();
		} catch (final CloneNotSupportedException e) {
			return null;
		}
	}

	/**
	 * Returns the cached {@code Graphic} for this Requirement, if it exists.
	 * Otherwise creates and then returns it. Since the Graphic is <b>not</b>
	 * constructed, this method may return an outdated type of Graphic.
	 * <p>
	 * This method should be used when no changes have been made to the Requirement
	 * that would require a different Graphic object.
	 *
	 * @return the Graphic
	 */
	public final AbstractRequirementGraphic<?> getCachedGraphic() {
		if (g == null)
			g = constructGraphic();
		return g;
	}

	/**
	 * Constructs a new {@code Graphic} for this Requirement and returns it. Since
	 * the Graphic is constructed, it will always be the correct type of Graphic,
	 * regardless of the state of the Requirement.
	 *
	 * @return the Graphic
	 */
	public final AbstractRequirementGraphic<?> constructAndGetGraphic() {
		g = constructGraphic();
		g.reset();
		g.update();
		return g;
	}

	/**
	 * Returns whether or not this Requirement supports a Graphic. This method
	 * requires that a Graphic has been constructed in order to return the correct
	 * value. {@code true} is returned if and only if the Graphic is <b>not</b> a
	 * {@code NullGraphic}.
	 *
	 * @return {@code true} if this Requirement supports a Graphic
	 */
	public final boolean hasGraphic() {
		return hasGraphic;
	}

	/**
	 * Returns the Graphic for this Requirement. {@link #hasGraphic} is set to
	 * {@code true} and the subclass-specific creation of the Graphic
	 * ({@link #constructGraphicOfSubclass()}) may set it to {@code false} if a
	 * {@code NullGraphic} is created.
	 *
	 * @return the Graphic
	 */
	protected final AbstractRequirementGraphic<?> constructGraphic() {
		hasGraphic = true;
		return constructGraphicOfSubclass();
	}

	/**
	 * Each subclass returns its own Graphic. Implementations are responsible for
	 * setting {@link #hasGraphic} to {@code false} if the Graphic constructed is a
	 * {@code NullGrahpic}.
	 *
	 * @return the Graphic
	 */
	protected abstract AbstractRequirementGraphic<?> constructGraphicOfSubclass();

	/**
	 * Returns a {@code NullGraphic}.
	 *
	 * @param cause the cause for the NullGrahpic
	 *
	 * @return the NullGraphic
	 */
	protected final AbstractRequirementGraphic<?> constructNullGraphic(String cause) {
		hasGraphic = false;
		return new NullRequirementGraphic(this, cause);
	}

	/**
	 * Returns the {@link #key}
	 *
	 * @return the key
	 */
	public final String key() {
		return key;
	}

	/**
	 * Returns the {@link #value}
	 *
	 * @return the value
	 */
	public final Object value() {
		return value;
	}

	/**
	 * Returns the {@link #defaultValue}
	 *
	 * @return the defaultValue
	 */
	public final Object defaultValue() {
		return defaultValue;
	}

	/**
	 * Checks if {@code v} is a valid {@code value} for this Requirement.
	 *
	 * @param v the value
	 *
	 * @return {@code true} if {@code v} is valid, {@code false} otherwise
	 */
	protected abstract boolean isValidValue(Object v);

	/**
	 * Attempts to set the {@code defaultValue} of this Requirement <i>without</i>
	 * marking it as {@code fulfilled}. If the {@code v} isn't valid, this method
	 * does nothing.
	 * <p>
	 * This method is intended for offering a default value which must then be
	 * explicitly set in order for the Requirement to be marked as fulfilled.
	 *
	 * @param v the default value of the required object
	 */
	public final void offer(Object v) {
		if (finalised)
			throw new LockedRequirementException(this);

		if (!isValidValue(v))
			return;

		defaultValue = v;
	}

	/**
	 * Calls {@link #offer(Object) offer(Object)} and additionally sets the
	 * {@code value} of this Requirement, marking is as {@code fulfilled}.
	 * <p>
	 * This method is intended for normal use, to specify that a value for this
	 * Requirement exists and may be retrieved.
	 *
	 * @param v the value of the required object
	 */
	public final void fulfil(Object v) {
		offer(v);

		if (!isValidValue(v))
			return;

		value = v;
		fulfilled = isValidValue(v);
	}

	/**
	 * Calls {@link #fulfil(Object) fulfil(Object)} and additionally marks this
	 * Requirement as {@code finalised}.
	 * <p>
	 * This method is intended for providing a strict default or for ensuring the
	 * value cannot be altered in the future.
	 *
	 * @param v the value of the required object
	 */
	public final void finalise(Object v) {
		fulfil(v);

		if (!isValidValue(v))
			return;

		finalised = true;
	}

	/** Clears this Requirement and its {@code value}, resetting its state */
	public void clear() {
		value = null;
		fulfilled = false;
		finalised = false;
	}

	/** Resets the value of the Requirement */
	protected abstract void resetValue();

	/** Clears this Requirement and resets its {@code value} to the default */
	public final void reset() {
		clear();
		resetValue();
		g.reset();
	}

	/**
	 * Returns the value of {@link #fulfilled fulfilled}
	 *
	 * @return {@code fulfilled}
	 */
	public final boolean fulfilled() {
		return fulfilled;
	}

	/**
	 * Returns the value of {@link #finalised finalised}
	 *
	 * @return {@code finalised}
	 */
	public final boolean finalised() {
		return finalised;
	}

	@Override
	public final String toString() {
		final StringBuilder sb = new StringBuilder(
		        String.format(Languages.getString("Requirement.1"), this.getClass().getSimpleName(), //$NON-NLS-1$
		                key, defaultValue, value,
		                fulfilled() ? RequirementStrings.YES : RequirementStrings.NO,
		                finalised() ? RequirementStrings.YES : RequirementStrings.NO));
		return sb.toString();
	}
}
