package requirement.requirements;

import requirement.graphics.AbstractRequirementGraphic;
import requirement.graphics.StringRequirementGraphic;

/**
 * A {@code Requirement} specifically for {@code Strings}. This Requirement may
 * impose restrictions by only accepting strings of a specific
 * {@link requirement.requirements.StringType Type}.
 *
 * @author alexm
 */
public final class StringRequirement extends AbstractRequirement {

	/** The {@code Type} of Strings that will be accepted by this Requirement */
	public final StringType stringType;

	/**
	 * Constructs this StringRequirement without any restrictions.
	 *
	 * @param key the Requirement's key
	 */
	public StringRequirement(String key) {
		this(key, StringType.ANY);
	}

	/**
	 * Constructs this StringRequirement with the restrictions of the {@code type}.
	 *
	 * @param key  the Requirement's key
	 * @param type the type of string that will be accepted
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
