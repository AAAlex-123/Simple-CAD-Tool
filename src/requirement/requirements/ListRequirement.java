package requirement.requirements;

import java.util.List;

import requirement.exceptions.UnsupportedGraphicException;
import requirement.graphics.AbstractRequirementGraphic;
import requirement.graphics.ListRequirementGraphic;

/**
 * A Requirement demanding that its value belongs to a provided list of options.
 * .
 * @author dimits
 */
public class ListRequirement extends AbstractRequirement {
	
	private List<Object> options;
	private transient AbstractRequirementGraphic g;
	
	public ListRequirement(String key, List<Object> options) {
		super(key);
		this.options = options;
	}
	
	public List<Object> getOptions(){
		return this.options;
	}

	@Override
	public AbstractRequirementGraphic getGraphics() throws UnsupportedGraphicException {
		if (g == null)
			g = new ListRequirementGraphic(this);
		return g;
	}

	@Override
	protected boolean isValidValue(Object v) {
		return options.contains((String)v);
	}

	@Override
	protected void resetValue() {
		;//there's nothing to reset
	}

}
