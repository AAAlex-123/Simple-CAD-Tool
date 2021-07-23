package requirement.requirements;

import requirement.exceptions.UnsupportedGraphicException;
import requirement.graphics.AbstractRequirementGraphic;

/**
 * A general-purpose Requirement for objects of any type. This Requirement
 * doesn't impose any restrictions on the objects that fulfil it.
 *
 * @author alexm
 */
public final class ObjectRequirement extends AbstractRequirement {

	/**
	 * Constructs this Requirement with a {@code key}.
	 *
	 * @param key the key
	 */
	public ObjectRequirement(String key) {
		super(key);
	}

	@Override
	public AbstractRequirementGraphic getGraphics() {
		throw new UnsupportedGraphicException(this);
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
