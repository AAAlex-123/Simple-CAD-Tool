package exceptions;

import components.Component;

/**
 * A checked exception indicating a cycle has been created within the current circuit configuration.
 * Acts as a notification to the UI classes to inform the user. 
 * Any action to break the cycle will have been dealt with before this exception is thrown.
 * 
 * For more information why cycles are catastrophical to the application refer to {@link myUtil.ComponentGraph}
 * 
 * @author dimits
 */
public class CycleException extends Exception {

	private static final long serialVersionUID = -4668906301492855812L;

	public CycleException(Component comp1, Component comp2) {
		this(String.format("Cycle formed between components %s and %s. The connection between them has been deleted.", comp1.getID(),comp2.getID()));
	}
	
	public CycleException(String message) {
		super(message);
	}

}
