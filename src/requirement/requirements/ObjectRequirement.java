package requirement.requirements;

import localisation.Languages;
import requirement.graphics.AbstractRequirementGraphic;

/**
 * A {@code general}-purpose {@code Requirement} for objects of any type. This
 * Requirement doesn't impose any restrictions on the objects that fulfil it and
 * does not have a Graphic associated with it. Instead it uses a
 * {@link requirement.graphics.NullRequirementGraphic NullGraphic}.
 *
 * @author alexm
 */
public final class ObjectRequirement extends AbstractRequirement {

	/**
	 * Constructs this ObjectRequirement with a {@code key}.
	 *
	 * @param key the key
	 */
	public ObjectRequirement(String key) {
		super(key);
	}

	@Override
	protected AbstractRequirementGraphic<?> constructGraphicOfSubclass() {
		hasGraphic = false;
		return constructNullGraphic(Languages.getString("ObjectRequirement.0")); //$NON-NLS-1$
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
