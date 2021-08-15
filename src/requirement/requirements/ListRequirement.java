package requirement.requirements;

import java.util.List;

import requirement.exceptions.UnsupportedGraphicException;
import requirement.graphics.AbstractRequirementGraphic;
import requirement.graphics.ListRequirementGraphic;

/**
 * A Requirement demanding that its value belongs to a provided list of options.
 * 
 * @author dimits
 */
public class ListRequirement<T> extends AbstractRequirement {
	
	private List<T> options;
	private transient AbstractRequirementGraphic g;
	private boolean listUpdated;
	
	/**
	 * Construct a list requirement with a provided list of options.
	 * 
	 * @param key the Requirement's key
	 * @param options a list with all available options
	 */
	public ListRequirement(String key, List<T> options) {
		super(key);
		this.options = options;
		listUpdated = true;
	}
	
	/**
	 * Construct an empty list requirement, whose options will be given later,
	 * after construction
	 * 
	 * @param key the Requirement's key
	 */
	public ListRequirement(String key) {
		super(key);
		listUpdated = false;
	}
	
	/**
	 * Provide an empty list requirement with the promised options list.
	 * 
	 * @param newOptions the list with the options
	 */
	public void setOptions(List<T> newOptions) {
		//could also throw exception if the list was provided in the constructor but is being overridden
		this.options = newOptions;
		listUpdated = true;
	}
	
	/**
	 * Get the list with the options from the requirement.
	 * 
	 * @return the list with options
	 * @throws IllegalStateException if a list wasn't provided during construction and {@link #setOptions(List)} 
	 * wasn't called before a new call to this method.
	 */
	public List<T> getOptions() throws IllegalStateException { 
		if(!listUpdated)
			throw new IllegalStateException("Provide a new list with setOptions() before calling this method again.");
		
		listUpdated = false;
		return this.options;
	}

	@Override
	public AbstractRequirementGraphic getGraphics() throws UnsupportedGraphicException {
		if (g == null)
			g = new ListRequirementGraphic<T>(this);
		return g;
	}

	@SuppressWarnings("unchecked") //no way this ever becomes anything other than T
	@Override
	protected boolean isValidValue(Object v) {
		return options.contains((T)v);
	}

	@Override
	protected void resetValue() {
		;//there's nothing to reset
	}

}
