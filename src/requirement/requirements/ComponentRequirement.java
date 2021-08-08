package requirement.requirements;

import java.util.LinkedList;
import java.util.List;

import components.Component;

/**
 * A {@link ListRequirement} that handles components. Utilizes IDs and
 * can perform filtering taking into account the state and purpose
 * of different components.
 * 
 * @author dimits
 *
 */
public class ComponentRequirement extends ListRequirement<String> {
	
	public ComponentRequirement(String key, List<Component> options) {
		super(key, componentIDs(options));
	}
	
	//maybe add some methods to accept certain types or non-full components
	
	private static List<String> componentIDs(List<Component> components){
		LinkedList<String> ids = new LinkedList<String>();
		for(Component comp : components)
			ids.add(comp.getID());
		return ids;
	}

}
