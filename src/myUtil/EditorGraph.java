package myUtil;

import application.editor.CycleException;
import components.Component;
import components.ComponentFactory;
import components.ComponentType;
import exceptions.MalformedBranchException;

/**
 * A {@link ComponentGraph} wrapper translating components into graph nodes and edges.
 * Enables uniform access to the graph for every component.
 * 
 * @author dimits, alexm
 *
 */
public class EditorGraph {
	
	private final ComponentGraph g;

	public static void main(String[] args) throws MalformedBranchException, CycleException {
		EditorGraph eg = new EditorGraph();
		
		Component in0 = ComponentFactory.createInputPin();
		Component and0 = ComponentFactory.createPrimitiveGate(ComponentType.GATEAND, 2);
		Component not0 = ComponentFactory.createPrimitiveGate(ComponentType.GATENOT, 1);
		Component br0 = ComponentFactory.connectComponents(in0, 0, and0, 0);
		Component br1 = ComponentFactory.connectComponents(and0, 0, not0, 0);
		Component br2 = ComponentFactory.connectComponents(not0, 0, and0, 1);
		
		in0.setID("in0");
		and0.setID("and0");
		not0.setID("not0");
		br0.setID("br0");
		br1.setID("br1");
		br2.setID("br2");
		
		eg.add(in0);
		eg.add(and0);
		eg.add(not0);
		eg.add(br0);
		eg.add(br1);
		System.out.println(eg.canAdd(br2)); //should be false
	}
	
	/**
	 * Constructs a new graph.
	 */
	public EditorGraph() {
		this.g = new ComponentGraph();
	}
	
	/**
	 * Constructs a wrapper for the given existing graph .
	 * @param graph an existing ComponentGraph instance
	 */
	public EditorGraph(ComponentGraph graph) { //in case the graph has to be saved in a file
		this.g = graph;
	}
	
	/**
	 * Checks whether or not a component can be safely added to the graph.
	 * 
	 * @param c the component
	 * @return true if it's addition is safe, false otherwise. 
	 * Always returns true for non-branch components
	 */
	public boolean canAdd(Component c) {
		if (isBranch(c)) {
			PinGates<String> b = decomposeBranch(c);
			return g.componentCanBeConnected(b.inputID, b.outputID);
		} else
			return true;
	}
	
	/**
	 * Adds a component to the graph.
	 * 
	 * @param c the component to be added
	 * @throws CycleException if the component is a branch and its addition
	 * would create a cycle
	 */
	public void add(Component c) throws CycleException {
		if (canAdd(c)) {
			
			if (isBranch(c)) {
				PinGates<String> b = decomposeBranch(c);
				g.connectionAdded(b.inputID, b.outputID);
			} else 
				g.componentAdded(c.getID());
			
		} else {
			PinGates<String> b = decomposeBranch(c);
			throw new CycleException(b.inputID,b.outputID);
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
		 else
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
