package requirement.requirements;

import java.util.ArrayList;
import java.util.List;

import requirement.graphics.AbstractRequirementGraphic;
import requirement.graphics.ListRequirementGraphic;

/**
 * A Requirement demanding that its value belongs to a provided list of options.
 *
 * @param <T> the type of the list's options
 *
 * @author dimits
 */
public class ListRequirement<T> extends AbstractRequirement {

	private List<T> options;

	/**
	 * Construct an empty ListRequirement, whose options will be given later, after
	 * construction
	 *
	 * @param key the Requirement's key
	 */
	public ListRequirement(String key) {
		this(key, new ArrayList<>());
	}

	/**
	 * Construct a ListRequirement with a provided list of options.
	 *
	 * @param key     the Requirement's key
	 * @param options a list with all available options
	 */
	public ListRequirement(String key, List<T> options) {
		super(key);
		this.options = options;
	}

	@Override
	protected AbstractRequirementGraphic<?> constructGraphicOfSubclass() {
		if (options.size() == 0) {
			hasGraphic = false;
			return constructNullGraphic("No options");
		}

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
	 * Provide an empty ListRequirement with a copy of the promised options list.
	 * Changes to the list passed as an argument will not be reflected in this
	 * Requirement.
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
}
