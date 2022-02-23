package requirement.requirements;

import requirement.graphics.AbstractRequirementGraphic;
import requirement.graphics.StringRequirementGraphic;

/**
 * A Requirement specifically for {@code Strings}. This Requirement may impose
 * restrictions on which values are considered valid by only accepting strings
 * of a specific {@code Type}.
 *
 * @author Alex Mandelias
 *
 * @see StringType
 */
public final class StringRequirement extends AbstractRequirement {

	/** The {@code Type} of Strings that will be accepted by this Requirement */
	public final StringType stringType;

	/**
	 * Constructs a StringRequirement without any restrictions.
	 *
	 * @param key the new Requirement's key
	 */
	public StringRequirement(String key) {
		this(key, StringType.ANY);
	}

	/**
	 * Constructs a StringRequirement with the restrictions of a {@code Type}.
	 *
	 * @param key  the new Requirement's key
	 * @param type the type against which the values will be validated
	 *
	 * @see StringType
	 */
	public StringRequirement(String key, StringType type) {
		super(key);
		stringType = type;
	}

	@Override
	protected AbstractRequirementGraphic<?> constructGraphicOfSubclass() {
		return new StringRequirementGraphic(this);
	}

	@Override
	protected boolean isValidValue(Object s) {
		if (!(s instanceof String))
			return false;

		return stringType.isValid((String) s);
	}

	@Override
	protected void resetValue() {
		value = defaultValue == null ? "" : defaultValue; //$NON-NLS-1$
	}
}
