package myUtil;

import application.editor.CycleException;
import components.Component;
import components.ComponentType;

/**
 * A {@link ComponentGraph} wrapper translating components into graph nodes and
 * edges. Enables uniform access to the graph for every component.
 *
 * @author dimits
 * @author alexm
 */
public class EditorGraph {

	private final ComponentGraph g;

	/** Constructs a new graph */
	public EditorGraph() {
		this.g = new ComponentGraph();
	}

	/**
	 * Checks whether or not a component can be safely added to the graph.
	 *
	 * @param c the component
	 *
	 * @return true if its addition is safe, false otherwise. For non-branch
	 *         components always returns true
	 */
	public boolean canAdd(Component c) {
		if (isBranch(c)) {
			PinGates<String> b = decomposeBranch(c);
			return g.componentCanBeConnected(b.inputID, b.outputID);
		}

		return true;
	}

	/**
	 * Adds a component to the graph.
	 *
	 * @param c the component to be added
	 *
	 * @throws CycleException if the component is a branch and its addition would
	 *                        create a cycle
	 */
	public void add(Component c) throws CycleException {
		if (isBranch(c)) {
			final PinGates<String> b = decomposeBranch(c);
			if (canAdd(c))
				g.connectionAdded(b.inputID, b.outputID);
			else
				throw new CycleException(b.inputID, b.outputID);
		} else {
			g.componentAdded(c.getID());
		}
	}

	/**
	 * Removes a component from the graph.
	 *
	 * @param c the component to be removed
	 */
	public void remove(Component c) {
		if (isBranch(c)) {
			PinGates<String> b = decomposeBranch(c);
			g.connectionRemoved(b.inputID, b.outputID);
		} else
			g.componentDeleted(c.getID());
	}

	private static PinGates<String> decomposeBranch(Component c) {
		if (isBranch(c))
			return new PinGates<>(c.getInputs().get(0).getID(), c.getOutputs().get(0).get(0).getID());
		throw new IllegalArgumentException("Component " + c + "must be a branch.");
	}

	private static boolean isBranch(Component c) {
		return c.type() == ComponentType.BRANCH;
	}

	private static class PinGates<T> {
		public T inputID, outputID;

		public PinGates(T first, T second) {
			this.inputID = first;
			this.outputID = second;
		}
	}
}
