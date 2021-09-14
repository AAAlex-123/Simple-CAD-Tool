package requirement.requirements;

import localisation.Languages;
import requirement.graphics.AbstractRequirementGraphic;
import requirement.graphics.NullRequirementGraphic;

/**
 * A Requirement for objects of any type. This Requirement doesn't impose any
 * restrictions on the objects that fulfil it and does not have a Graphic
 * associated with it. Constructing a Graphic returns a {@code NullGraphic}.
 *
 * @author Alex Mandelias
 *
 * @see NullRequirementGraphic
 */
public final class ObjectRequirement extends AbstractRequirement {

	/**
	 * Constructs an ObjectRequirement.
	 *
	 * @param key the new Requirement's key
	 */
	public ObjectRequirement(String key) {
		super(key);
	}

	@Override
	protected AbstractRequirementGraphic<?> constructGraphicOfSubclass() {
		return constructNullGraphic(Languages.getString("ObjectRequirement.0"), true); //$NON-NLS-1$
	}

	@Override
	protected boolean isValidValue(Object v) {
		return true;
	}

	@Override
	protected void resetValue() {
		value = defaultValue;
	}
}
