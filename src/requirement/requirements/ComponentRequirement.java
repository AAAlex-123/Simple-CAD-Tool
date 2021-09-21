package requirement.requirements;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import component.ComponentType;
import component.components.Component;

/**
 * A {@link ListRequirement} for the {@link Component} class, utilises their
 * {@code IDs} and can perform filtering using a {@link Policy} taking into
 * account the state and purpose of different {@code Components}.
 *
 * @author dimits
 */
public final class ComponentRequirement extends ListRequirement<String> {

	private final Policy policy;

	/**
	 * Constructs a ComponentRequirement with a given {@code Policy}.
	 *
	 * @param key    the new Requirement's key
	 * @param policy the Policy used to filter out unnecessary components
	 */
	public ComponentRequirement(String key, Policy policy) {
		this(key, new ArrayList<>(), policy);
	}

	/**
	 * Constructs a ComponentRequirement that will filter a
	 * {@code list of components} according to the given {@code Policy}.
	 *
	 * @param key     the new Requirement's key
	 * @param options the list with the components
	 * @param policy  the Policy used to filter out unnecessary components
	 */
	public ComponentRequirement(String key, List<Component> options, Policy policy) {
		super(key, ComponentRequirement.filterAndGetIDs(options, policy));
		this.policy = policy;
	}

	/**
	 * Set the list with components at runtime.
	 *
	 * @param options the list with components
	 *
	 * @implNote internally calls {@link #setOptions(List)} after filtering
	 */
	public void setComponentOptions(List<Component> options) {
		super.setOptions((ComponentRequirement.filterAndGetIDs(options, policy)));
	}

	/**
	 * Filters out unnecessary components and returns a list with the remaining
	 * components' IDs.
	 *
	 * @param components the list with all visible components
	 * @param policy     the {@code Policy} used to filter out unnecessary
	 *                   components.
	 *
	 * @return the list with the suitable components' IDs.
	 */
	private static List<String> filterAndGetIDs(List<Component> components, Policy policy) {
		final LinkedList<String> ids = new LinkedList<>();
		for (final Component comp : components) {
			if ((policy == Policy.INPUT) && (comp.type() == ComponentType.OUTPUT_PIN))
				continue; //don't suggest connecting an output pin as input

			if ((policy == Policy.OUTPUT) && (comp.type() == ComponentType.INPUT_PIN))
				continue; //don't suggest connecting an input pin as output

			if ((policy == Policy.INPUT_PIN) && (comp.type() != ComponentType.INPUT_PIN))
				continue; //don't suggest anything other than input pin for the INPUT_PIN type

			if ((policy != Policy.ANY) && (comp.type() == ComponentType.BRANCH))
				continue; //in the 2 above cases don't suggest directly connecting branches

			ids.add(comp.getID());
		}

		return ids;
	}

	/**
	 * Constants denoting whether or not the list's purpose is for a
	 * {@link component.ComponentType#BRANCH}'s input gate, output gate, or is for
	 * an entirely different use altogether. Is used internally to filter available
	 * components.
	 */
	public enum Policy {
		/** Get a list of components available for input. */
		INPUT,
		/** Get a list of components that are Input Pins. */
		INPUT_PIN,
		/** Get a list of components available for output. */
		OUTPUT,
		/** Get a list of all non-branch components */
		NONBRANCH,
		/** Get a list of all components. */
		ANY
	}
}
