package requirement.requirements;

import requirement.graphics.AbstractRequirementGraphic;
import requirement.graphics.StringRequirementGraphic;

/**
 * A Requirement specifically for Strings. This Requirement may impose
 * restrictions by only accepting strings of a specific
 * {@link requirement.requirements.StringType Type}.
 *
 * @author alexm
 */
public final class StringRequirement extends AbstractRequirement {

	private transient AbstractRequirementGraphic g;

	/** The {@code Type} of strings that will be accepted by this Requirement */
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
	public StringRequirement clone() {
		final StringRequirement cloned = (StringRequirement) super.clone();
		cloned.g = g;
		return cloned;
	}

	@Override
	public AbstractRequirementGraphic getGraphics() {
		if (g == null)
			g = new StringRequirementGraphic(this);
		return g;
	}

	@Override
	protected boolean isValidValue(Object s) {
		return stringType.isValid((String) s);
	}

	@Override
	protected void resetValue() {
		value = defaultValue == null ? "" : defaultValue; //$NON-NLS-1$
	}
}
