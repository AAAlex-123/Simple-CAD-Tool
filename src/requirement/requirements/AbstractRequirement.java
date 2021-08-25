package requirement.requirements;

import static localisation.RequirementStrings.NO;
import static localisation.RequirementStrings.YES;

import java.io.Serializable;

import localisation.Languages;
import requirement.exceptions.LockedRequirementException;
import requirement.exceptions.UnsupportedGraphicException;
import requirement.graphics.AbstractRequirementGraphic;

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
 * defined, the {@link AbstractRequirement#getGraphics getGraphics} method
 * should throw an {@link requirement.exceptions.UnsupportedGraphicException}.
 *
 * @author alexm
 */
public abstract class AbstractRequirement implements Serializable, Cloneable {

	private static final long serialVersionUID = 6L;

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
	 * Indicates whether or not a valid value for this Requirement has been provided. If
	 * the Requirement is fulfilled, the object that created the Requirement may
	 * retrieve its value and use it.
	 */
	protected boolean fulfilled;

	/**
	 * Indicates whether or not the value for this Requirement is final, meaning it
	 * cannot be altered. A Graphic object may use this information to alter its
	 * appearance if the Requirement is finalised.
	 */
	protected boolean finalised;

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
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}

	/**
	 * Returns the {@code Graphic} for this Requirement. Since not all Requirements
	 * require a Graphic to function, this method may be defined to lazily create
	 * the Graphic object the first time it is needed.
	 *
	 * @return the Graphic
	 *
	 * @throws UnsupportedGraphicException if this Requirement doesn't support a
	 *                                     Graphic object
	 */
	public abstract AbstractRequirementGraphic getGraphics() throws UnsupportedGraphicException;

	/**
	 * Calls {@link AbstractRequirement#getGraphics() getGraphics()} while also
	 * {@link AbstractRequirementGraphic#update() updating} the {@code Graphic}.
	 *
	 * @return the Graphic
	 *
	 * @throws UnsupportedGraphicException if this Requirement doesn't support a
	 *                                     Graphic object
	 */
	public final AbstractRequirementGraphic getGraphicsAndUpdate()
	        throws UnsupportedGraphicException {
		final AbstractRequirementGraphic g = getGraphics();
		g.update();
		return g;
	}

	/**
	 * Returns the {@link AbstractRequirement#key key}
	 *
	 * @return the key
	 */
	public final String key() {
		return key;
	}

	/**
	 * Returns the {@link AbstractRequirement#value value}
	 *
	 * @return the value
	 */
	public final Object value() {
		return value;
	}

	/**
	 * Returns the {@link AbstractRequirement#defaultValue defaultValue}
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
	 * Calls {@link ObjectRequirement#offer(Object) offer(Object)} and additionally
	 * sets the {@code value} of this Requirement, marking is as {@code fulfilled}.
	 * <p>
	 * This method is intended for normal use, to specify that a value for this
	 * Requirement exists and may be retrieved.
	 *
	 * @param v the value of the required object
	 */
	public final void fulfil(Object v) {
		offer(v);
		value = v;
		fulfilled = isValidValue(v);
	}

	/**
	 * Calls {@link ObjectRequirement#fulfil(Object) fulfil(Object)} and
	 * additionally marks this Requirement as {@code finalised}.
	 * <p>
	 * This method is intended for providing a strict default or for ensuring the
	 * value cannot be altered in the future.
	 *
	 * @param v the value of the required object
	 */
	public final void finalise(Object v) {
		fulfil(v);
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
		getGraphics().reset();
	}

	/**
	 * Returns the value of {@link AbstractRequirement#fulfilled fulfilled}
	 *
	 * @return {@code fulfilled}
	 */
	public final boolean fulfilled() {
		return fulfilled;
	}

	/**
	 * Returns the value of {@link AbstractRequirement#finalised finalised}
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
		                key, defaultValue, value, fulfilled() ? YES : NO,
		                finalised() ? YES : NO));
		return sb.toString();
	}
}
