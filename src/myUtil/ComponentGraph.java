package myUtil;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * A class monitoring the relations between all components to prevent major logical errors such as 
 * asynchronous circuits which infinitely go into a feedback loop.
 * 
 * @author dimits
 *
 */
public class ComponentGraph {
	private Map<String, Node> nodes = new HashMap<String, Node>();
	
	public static void main(String[] args) {
		ComponentGraph g = new ComponentGraph();
		g.componentAdded("in0");
		g.componentAdded("and0");
		g.componentAdded("not0");
		
		g.connectionAdded("in0", "and0");
		g.connectionAdded("and0", "not0");
		System.out.println(g.componentCanBeConnected("not0", "and0")); //should be false
		
	}
	
	/**
	 * Notify the graph that a component has been added to the UI.
	 * @param name the name of the new component
	 */
	public void componentAdded(String name) {
		nodes.put(name, new Node());
	}
	
	/**
	 * Notify the graph that a component has been deleted from the UI.
	 * 
	 * @param name the name of the now-deleted component
	 * @throws IllegalArgumentException if the component's name isn't registered in the graph
	 */
	public void componentDeleted(String name) throws IllegalArgumentException {
		final Node removed = nodes.remove(name);
		ComponentGraph.checkExists(removed);
	}
	
	/**
	 * Notify the graph that a branch connecting 2 nodes was deleted.
	 * 
	 * @param connector the <b>branch's</b> input component
	 * @param target the <b>branch's</b> output component
	 * @throws IllegalArgumentException if either of the components' names aren't registered in the graph.
	 */
	public void connectionRemoved(String connector, String target) throws IllegalArgumentException {
		Node conNode = nodes.get(connector);
		Node targetNode = nodes.get(target);
		
		ComponentGraph.checkExists(conNode, targetNode);

		conNode.neighbours.remove(targetNode);
	}
	
	/**
	 * Check whether or not a branch connecting 2 components can be safely added.
	 * Make sure to perform the check <i>before</i> the actual branch is added.
	 * The connection will be added to the graph if it's deemed safe to do so.
	 * 
	 * @param connector the <b>branch's</b> input component
	 * @param target the <b>branch's</b> output component
	 * @return true if the operation is safe, false if the operation will lead to an endless loop
	 * @throws IllegalArgumentException if either of the components' names aren't registered in the graph.
	 */
	public boolean componentCanBeConnected(String connector, String target) throws IllegalArgumentException {
		Node first = nodes.get(connector);
		Node last = nodes.get(target);

		ComponentGraph.checkExists(first, last);
		
		first.neighbours.add(last);
		
		//DFS on graph 
		final Clock clock = new Clock();
		for(String key_n : nodes.keySet()) 
			nodes.get(key_n).visited = false;

		for(String key_n : nodes.keySet()) {
			Node n = nodes.get(key_n);
			if(!n.visited)
				explore(n, clock);
		}
		
		/*
		 * For any edge (u,v): if post(u) < post(v) then the graph is not a DAG
		 * so there is a feedback look somewhere in the circuit. 
		 * 
		 * We have to check all nodes since the DFS algorithm starts from a non-determined 
		 * node and may detect a back-edge that is not the first-last pair from above.
		 */
		for(String outer_key : nodes.keySet()) {
			Node u = nodes.get(outer_key);
			
			for(Node v : nodes.get(outer_key).neighbours) {
				if(u.post < v.post) {
					u.neighbours.remove(v); //remove faulty connection
					return false;
				}
			}
			
		}
				
		return true;
	}
	
	
	private void explore(Node n, Clock clock) {
		n.prev = clock.value();
		clock.tick();
		n.visited = true;

		for (final Node v : n.neighbours)
			if (!v.visited)
				explore(v, clock);

		n.post = clock.value();
		clock.tick();
	}

	private static void checkExists(Node ... nodes) {
		for(Node n : nodes) 
			if (Objects.isNull(n))
				throw new IllegalArgumentException(
						String.format("There isn't any component named %s in the graph!", n));
	}

	private class Node {
		List<Node> neighbours = new LinkedList<Node>();
		
		//used by the DFS algorithm above
		int prev;
		int post;
		boolean visited;
	
	}
	
	private class Clock {

		private int value = 0;

		public Clock() {}

		public void tick() {
			++value;
		}

		public int value() {
			return value;
		}
	}
}
