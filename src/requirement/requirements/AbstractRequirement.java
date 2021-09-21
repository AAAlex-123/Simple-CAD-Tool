package requirement.requirements;

import java.io.Serializable;

import localisation.RequirementStrings;
import requirement.exceptions.LockedRequirementException;
import requirement.graphics.AbstractRequirementGraphic;
import requirement.graphics.NullRequirementGraphic;

/**
 * A class which encapsulates the process where:
 * <ul>
 * <li>object A needs object B in order to carry out an operation
 * <li>object C has access to or can construct object B
 * <li>object C provides B to A thereby fulfilling A's requirement
 * <li>object A receives object B and can carry out its operation
 * </ul>
 * <p>
 * An {@code AbstractRequirement} (or simply {@code Requirement}) encapsulates
 * and extends this behaviour providing the means for object A to {@code create}
 * the Requirement that object C will {@code fulfil} using object B, which A
 * will then {@code retrieve}.
 * <p>
 * To manage a collection of Requirements a {@link Requirements} object may be
 * used. It can create, store, manage and give access to individual Requirements
 * and also perform most of the operations defined in this class on them.
 * <p>
 * Requirements are by design very loosely coupled with the objects that use
 * them and as a result writing structured code that can be easily read and
 * maintain can be rather difficult. To mitigate some of the problems, classes
 * that use Requirements may implement the {@link HasRequirements} interface
 * which acts as a marker interface for the methods which act on Requirements.
 * <p>
 * A Requirement may be accompanied by a {@link AbstractRequirementGraphic}
 * object which is responsible for providing a GUI with which the user fulfils
 * the Requirement. If such Graphic object isn't defined, requesting the Graphic
 * object returns a {@link NullRequirementGraphic} and subsequent calls to
 * {@link #hasGraphic()} return {@code false}.
 *
 * @author Alex Mandelias
 */
public abstract class AbstractRequirement implements Serializable, Cloneable {

	private static final long serialVersionUID = 7L;

	/**
	 * A short and descriptive name for the Requirement which may be displayed in a
	 * Graphic object and is used in a {@link Requirements} object to identify it.
	 */
	protected final String key;

	/**
	 * The required object, the object that will be set when the Requirement is
	 * fulfilled. This object will later be retrieved by another object in order to
	 * be used to carry out an operation. It has type {@code Object} since it is not
	 * possible to use generics to store the value and then retrieve it from a
	 * collection of Requirements in a type-safe manner.
	 */
	protected Object value;

	/**
	 * The (optional) default value of the Requirement. It is used to when resetting
	 * the Requirement and may also be the preset option in a Graphic object.
	 */
	protected Object defaultValue;

	/**
	 * Indicates whether or not a valid value for this Requirement has been
	 * provided. If the Requirement is {@code fulfilled}, the object that created
	 * the Requirement may retrieve its value and use it.
	 */
	protected boolean fulfilled;

	/**
	 * Indicates whether or not the value for this Requirement is final, meaning it
	 * cannot be altered. Attempting to fulfil a finalised Requirement throws a
	 * Runtime Exception.A Graphic object may use this information to alter its
	 * appearance if the Requirement is finalised.
	 */
	protected boolean finalised;

	/** The {@code Graphic} associated with this Requirement */
	private transient AbstractRequirementGraphic<?> g;

	/**
	 * {@code true} if and only if the Graphic is <i>not</i> a {@code NullGraphic},
	 * meaning that this Requirement does support a Graphic. This variable has the
	 * correct value only if a Graphic has previously been constructed.
	 */
	private transient boolean hasGraphic;

	/**
	 * {@code true} if and only if the Graphic is a {@code NullGraphic} and
	 * additionally the presence of the {@code NullGraphic} is the result of a
	 * programming error. This variable has the correct value if a Graphic has
	 * previously been constructed.
	 */
	private transient boolean graphicError;

	/**
	 * Constructs a new Requirement.
	 *
	 * @param key the key of the new Requirement
	 */
	protected AbstractRequirement(String key) {
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
	 * @return the cached Graphic
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
	 * @return the constructed Graphic
	 */
	public final AbstractRequirementGraphic<?> constructAndGetGraphic() {
		g = constructGraphic();
		g.reset();
		g.update();
		return g;
	}

	/**
	 * Returns whether or not this Requirement supports a Graphic. {@code true} is
	 * returned if and only if the Graphic is <b>not</b> a {@code NullGraphic}. For
	 * example, when the current state of this Requirement doesn't allow for a
	 * Graphic to fulfil it, a {@code NullGraphic} corresponding to an application
	 * error is returned when a Graphic is requested.
	 * <p>
	 * This method requires that a Graphic has been constructed in order to return
	 * the correct value.
	 *
	 * @return {@code true} if this Requirement supports a Graphic, meaning it can
	 *         be used in order to fulfil this Requirement, {@code false} otherwise
	 *
	 * @see NullRequirementGraphic
	 * @see #hasGraphic
	 */
	public final boolean hasGraphic() {
		return hasGraphic;
	}

	/**
	 * Returns whether or not the request for this Requirement's Graphic was the
	 * result of a programming error. {@code true} is returned if and only if the
	 * Graphic is a {@code NullGraphic} and it should have never been requested. For
	 * example, when no Graphic is not suitable to fulfil this Requirement, a
	 * {@code NullGraphic} corresponding to a programmer error is returned when a
	 * Graphic is requested.
	 * <p>
	 * This method requires that a Graphic has been constructed in order to return
	 * the correct value.
	 *
	 * @return {@code true} if this Requirement does not support a Graphic and the
	 *         request for it was a programming error, {@code false} otherwise
	 *
	 * @see NullRequirementGraphic
	 * @see #graphicError
	 */
	public final boolean graphicError() {
		return graphicError;
	}

	/**
	 * Constructs and returns a Graphic for this Requirement. The {@code hasGraphic}
	 * and {@code graphicError} fields are set to {@code true} and {@code false}
	 * respectively, but subclasses are free to construct a {@code NullGraphic} if
	 * they deem necessary to do so. The {@code NullGraphic} must be constructed by
	 * calling the {@link #constructNullGraphic(String, boolean)} method, otherwise
	 * the above fields will not be updated.
	 *
	 * @return the Graphic for this Requirement
	 *
	 * @see NullRequirementGraphic
	 * @see #hasGraphic
	 * @see #graphicError
	 */
	protected final AbstractRequirementGraphic<?> constructGraphic() {
		hasGraphic = true;
		graphicError = false;
		return constructGraphicOfSubclass();
	}

	/**
	 * Each subclass creates and returns its own Graphic. If a subclass deems that
	 * no Graphic is suitable it, the {@link #constructNullGraphic(String, boolean)}
	 * method must be called with the appropriate arguments in order to correctly
	 * update the {@code hasGraphic} and {@code graphicError} fields of this class.
	 *
	 * @return the Graphic for this Requirement
	 */
	protected abstract AbstractRequirementGraphic<?> constructGraphicOfSubclass();

	/**
	 * Creates and returns a {@code NullGraphic}. Subclasses should call this method
	 * when a Graphic is requested but no Graphic is suitable.
	 *
	 * @param cause the cause for the NullGrahpic, the reason that this Requirement
	 *              does not support a Graphic
	 * @param error {@code true} if this Requirement does not support a Graphic and
	 *              the request for it was programming error, {@code false} if it
	 *              this Requirement normally supports a Graphic but in this case no
	 *              Graphic was suitable for it
	 *
	 * @return the NullGraphic for this Requirement
	 *
	 * @see NullRequirementGraphic
	 */
	protected final AbstractRequirementGraphic<?> constructNullGraphic(String cause,
	        boolean error) {
		hasGraphic = false;
		graphicError = error;
		return new NullRequirementGraphic(this, cause, error);
	}

	/**
	 * Returns this Requirement's {@link #key}.
	 *
	 * @return the key
	 */
	public final String key() {
		return key;
	}

	/**
	 * Returns this Requirement's {@link #value}.
	 *
	 * @return the value
	 */
	public final Object value() {
		return value;
	}

	/**
	 * Returns this Requirement's {@link #defaultValue}.
	 *
	 * @return the defaultValue
	 */
	public final Object defaultValue() {
		return defaultValue;
	}

	/**
	 * Checks if {@code v} is a valid {@code value} for this Requirement. A
	 * Requirement may be fulfilled with a value that is valid according to whatever
	 * constraints each subclass defines.
	 *
	 * @param v the value to check
	 *
	 * @return {@code true} if {@code v} is valid, {@code false} otherwise
	 */
	protected abstract boolean isValidValue(Object v);

	/**
	 * Attempts to set the {@code defaultValue} of this Requirement <i>without</i>
	 * marking it as {@code fulfilled}. If {@code v} isn't valid, this method does
	 * nothing.
	 * <p>
	 * This method is intended for offering a default value which must then be
	 * explicitly set in order for the Requirement to be marked as fulfilled.
	 *
	 * @param v the default value of the required object
	 *
	 * @return {@code true} if {@code v} is valid and the method successfully set
	 *         the default, {@code false} otherwise
	 *
	 * @throws LockedRequirementException if this Requirement is finalised
	 */
	public final boolean offer(Object v) {
		if (finalised)
			throw new LockedRequirementException(this);

		if (!isValidValue(v))
			return false;

		defaultValue = v;
		return true;
	}

	/**
	 * Calls {@link #offer(Object)} and additionally sets the {@code value} of this
	 * Requirement, marking is as {@code fulfilled}. If {@code v} is not valid, this
	 * method does nothing.
	 * <p>
	 * This method is intended for normal use, to specify that a value for this
	 * Requirement exists and can later be retrieved to be used
	 *
	 * @param v the value of the required object
	 *
	 * @return {@code true} if {@code v} is valid and the method successfully set
	 *         the value, {@code false} otherwise
	 *
	 * @throws LockedRequirementException if this Requirement is finalised
	 */
	public final boolean fulfil(Object v) {
		if (!offer(v))
			return false;

		value = v;
		fulfilled = true;
		return true;
	}

	/**
	 * Calls {@link #fulfil(Object)} and additionally marks this Requirement as
	 * {@code finalised}. If {@code v} is not valid, this method does nothing.
	 * <p>
	 * This method is intended for providing a strict default or for ensuring the
	 * value cannot be altered in the future.
	 *
	 * @param v the value of the required object
	 *
	 * @return {@code true} if {@code v} is valid and the method successfully set
	 *         the value as final, {@code false} otherwise
	 *
	 * @throws LockedRequirementException if this Requirement is finalised
	 */
	public final boolean finalise(Object v) {
		if (!fulfil(v))
			return false;

		finalised = true;
		return true;
	}

	/**
	 * Clears this Requirement. After this call every field apart from its
	 * {@code defaultValue} have their original values.
	 */
	public final void clear() {
		value = null;
		fulfilled = false;
		finalised = false;
	}

	/**
	 * Clears this Requirement and additionally resets its {@code value} and its
	 * Graphic, if it exists.
	 */
	public final void reset() {
		clear();
		resetValue();
		if (g != null)
			g.reset();
	}

	/**
	 * Resets the value of this Requirement to its default. The default can be the
	 * {@code defaultValue} or some other value determined by the subclass.
	 */
	protected abstract void resetValue();

	/**
	 * Returns whether or not this Requirement is fulfilled.
	 *
	 * @return {@code true} if there exists a value for it, {@code false} otherwise
	 *
	 * @see #fulfilled
	 */
	public final boolean fulfilled() {
		return fulfilled;
	}

	/**
	 * Returns whether or not this Requirement is finalised.
	 *
	 * @return {@code true} if its value cannot be altered, {@code false} otherwise
	 *
	 * @see #finalised
	 */
	public final boolean finalised() {
		return finalised;
	}

	@Override
	public final String toString() {
		final StringBuilder sb = new StringBuilder(this.getClass().getSimpleName());

		sb.append(String.format(": %s%n", key)) //$NON-NLS-1$
		        .append(String.format("\tDefault    %s%n", defaultValue)) //$NON-NLS-1$
		        .append(String.format("\tValue:     %s%n", value)) //$NON-NLS-1$
		        .append(String.format("\tFulfilled: %s%n", //$NON-NLS-1$
		                AbstractRequirement.yesIfTrueNoOtherwise(fulfilled())))
		        .append(String.format("\tFinalised: %s%n", //$NON-NLS-1$
		                AbstractRequirement.yesIfTrueNoOtherwise(finalised())));

		return sb.toString();
	}

	private static String yesIfTrueNoOtherwise(boolean b) {
		return b ? RequirementStrings.YES : RequirementStrings.NO;
	}
}
