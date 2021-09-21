package requirement.requirements;

import java.util.ArrayList;
import java.util.List;

import localisation.Languages;
import requirement.graphics.AbstractRequirementGraphic;
import requirement.graphics.ListRequirementGraphic;

/**
 * A Requirement demanding that its value belongs to a provided list of options.
 *
 * @param <T> the type of the list's options
 *
 * @author dimits
 * @author Alex Mandelias
 */
public class ListRequirement<T> extends AbstractRequirement {

	private List<T> options;
	private boolean error;
	private String cause;

	/**
	 * Constructs an empty ListRequirement, whose options will be given later, after
	 * construction
	 *
	 * @param key the new Requirement's key
	 */
	public ListRequirement(String key) {
		this(key, new ArrayList<>());
	}

	/**
	 * Constructs a ListRequirement with a provided list of options.
	 *
	 * @param key     the new Requirement's key
	 * @param options a list with all available options
	 */
	public ListRequirement(String key, List<T> options) {
		super(key);
		this.options = options;
		setCaseOfNullGraphic(true, Languages.getString("ListRequirement.0")); //$NON-NLS-1$
	}

	@Override
	protected AbstractRequirementGraphic<?> constructGraphicOfSubclass() {
		if (options.size() == 0)
			return constructNullGraphic(cause, error);

		return new ListRequirementGraphic<>(this);
	}

	@SuppressWarnings("unlikely-arg-type")
	@Override
	protected boolean isValidValue(Object v) {
		return options.contains(v);
	}

	@Override
	protected void resetValue() {
		value = defaultValue;
	}

	/**
	 * Provide a ListRequirement with a copy of the promised options list. Changes
	 * to the list passed as an argument will not be reflected in this Requirement.
	 *
	 * @param newOptions the list with the options
	 */
	public void setOptions(List<T> newOptions) {
		this.options = new ArrayList<>(newOptions);
	}

	/**
	 * Get a copy of the list with the options from the Requirement. Changes to the
	 * list returned from this method will not be reflected in this Requirement.
	 *
	 * @return the list with options
	 */
	public List<T> getOptions() {
		return new ArrayList<>(this.options);
	}

	/**
	 * Defines the cause and severity of the {@code NullGraphic} that will be
	 * constructed if this Requirement has no options and a Graphic is requested.
	 * The {@code error} and the formatted String passed as parameters will be used
	 * to call the {@link #constructNullGraphic(String, boolean)} method.
	 *
	 * @param error  {@code true} if the NullGraphic is caused by a programming
	 *               error, {@code false} otherwise
	 * @param format the format
	 * @param args   the format arguments
	 *
	 * @implNote if this method is never called, the NullGraphic will be the same as
	 *           if {@code setCaseOfNullGraphic(true, "No options")} was called.
	 */
	public void setCaseOfNullGraphic(boolean error, String format, Object... args) {
		this.error = error;
		cause = String.format(format, args);
	}
}
