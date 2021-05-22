package command;

import java.awt.Frame;
import java.io.Serializable;
import java.util.List;

import application.Application;
import application.Undoable;
import components.ComponentType;
import requirement.Requirements;

/** An implementation of the Undoable interface, specific to this Application */
public abstract class Command implements Undoable, Serializable {

	private static final long serialVersionUID = 3L;

	/**
	 * Creates a Command that creates a Component of the given
	 * {@code ComponentType}.
	 * 
	 * @param app           the Command's context
	 * @param componentType the type of the Component
	 * @return the Command
	 */
	public static Command create(Application app, ComponentType componentType) {
		return new CreateCommand(app, componentType);
	}

	/**
	 * Creates a Command that creates a composite Gate.
	 * 
	 * @param app         the Command's context
	 * @param commands    the Command's instructions to create the composite
	 * @param description the Command's description
	 * @return the Command
	 */
	public static Command create(Application app, List<Command> commands, String description) {
		return new CreateGateCommand(app, commands, description);
	}

	/**
	 * Creates a Command that deletes a Component.
	 * 
	 * @param app the Command's context
	 * @return the Command
	 */
	public static Command delete(Application app) {
		return new DeleteCommand(app);
	}

	/** The Command's requirements; what it needs to execute. */
	protected Requirements<String> requirements;

	/** The Command's context; where it will act. */
	protected transient Application context;

	/**
	 * Constructs the Command with the given {@code Application}.
	 *
	 * @param app the Command's context
	 */
	public Command(Application app) {
		context = app;
		requirements = new Requirements<>();
	}

	/**
	 * Clones the Command so that the execution of this Command doesn't affect
	 * future executions.
	 *
	 * @return the cloned Command
	 */
	@Override
	public abstract Command clone();

	/**
	 * Fulfils the Command's requirements with a dialog.
	 * 
	 * @param parent the parent of the dialog
	 */
	public final void fillRequirements(Frame parent) {
		requirements.fulfillWithDialog(parent, toString());
	}

	/**
	 * Returns whether or not this Command is ready to execute.
	 * 
	 * @return true if ready to execute, false otherwise
	 */
	public final boolean canExecute() {
		return requirements.fulfilled();
	}

	/**
	 * Sets the Command's context.
	 * 
	 * @param c the context
	 */
	public final void context(Application c) {
		context = c;
	}

	@Override
	public abstract String toString();
}
