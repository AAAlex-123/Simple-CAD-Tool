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
	
	public ListRequirement(String key, List<T> options) {
		super(key);
		this.options = options;
	}
	
	public List<T> getOptions(){
		return this.options;
	}

	@Override
	public AbstractRequirementGraphic getGraphics() throws UnsupportedGraphicException {
		if (g == null)
			g = new ListRequirementGraphic<T>(this);
		return g;
	}

	@Override
	protected boolean isValidValue(Object v) {
		return options.contains((T)v);
	}

	@Override
	protected void resetValue() {
		;//there's nothing to reset
	}

}
