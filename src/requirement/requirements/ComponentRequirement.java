package requirement.requirements;

import java.util.LinkedList;
import java.util.List;

import components.Component;
import components.ComponentType;

/**
 * A {@link ListRequirement} that handles components. It holds their IDs and
 * can perform filtering by taking into account the type of different components.
 * 
 * @author dimits
 *
 */
public class ComponentRequirement extends ListRequirement<String> {
	
	/**
	 * Constructs a {@link ListRequirement} with a list of visible components' IDs 
	 * filtering out unnecessary components.
	 * 
	 * @param key the {@link AbstractRequirement#key}
	 * @param options a list with all visible components
	 * @param policy a {@link Policy} used to filter out unnecessary components.
	 * @return a list with the suitable components' IDs.
	 */
	public ComponentRequirement(String key, List<Component> options, Policy policy) {
		super(key, filterAndGetIDs(options, policy));
	}
	
	/**
	 * Constructs a {@link ListRequirement} with a list of visible components' IDs.<br>
	 * Consider {@link #ComponentRequirement(String, List, Policy)} for automatic 
	 * component filtering.
	 * 
	 * @param key the {@link AbstractRequirement#key}
	 * @param options a list with all visible components
	 * @return a list with the suitable components' IDs.
	 */
	public ComponentRequirement(String key, List<Component> options) {
		super(key, filterAndGetIDs(options, Policy.ANY));
	}
	
	/**
	 * Filters out unnecessary components and returns a list with the remaining components' IDs.
	 * 
	 * @param components a list with all visible components
	 * @param policy a {@link Policy} used to filter out unnecessary components.
	 * @return a list with the suitable components' IDs.
	 */
	private static List<String> filterAndGetIDs(List<Component> components, Policy policy){
		LinkedList<String> ids = new LinkedList<String>();
		for(Component comp : components) {
			if(policy == Policy.INPUT && comp.type() == ComponentType.OUTPUT_PIN)
				continue; 	//don't suggest connecting an output pin as input
				
			if(policy == Policy.OUTPUT && comp.type() == ComponentType.INPUT_PIN)
				continue;	//don't suggest connecting an input pin as output
			
			if(policy != Policy.ANY && comp.type() == ComponentType.BRANCH)
				continue;	//in the 2 above cases don't suggest directly connecting branches
			
			ids.add(comp.getID());
		}
			
		return ids;
	}
	
	/**
	 * Constants denoting whether or not the list's purpose is for a {@link components.ComponentType.Branch}'s
	 * input gate, output gate, or is for an entirely different use altogether.
	 * 
	 * Is used internally to filter available gates.
	 */
	public enum Policy {
		/**Get a list of components available for input.*/
		INPUT, 
		/**Get a list of components available for output.*/
		OUTPUT, 
		/**Get a list of all components.*/
		ANY
	}

}
