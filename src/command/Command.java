package command;

import java.awt.Frame;
import java.io.Serializable;
import java.util.List;

import application.editor.Editor;
import application.editor.Undoable;
import components.ComponentType;
import requirement.Requirements;

/**
 * An implementation of the Undoable interface, specific to this Application
 *
 * @author alexm
 */
public abstract class Command implements Undoable, Serializable, Cloneable {

	private static final long serialVersionUID = 4L;

	/**
	 * Creates a Command that creates a Component of the given
	 * {@code ComponentType}.
	 *
	 * @param componentType the type of the Component
	 *
	 * @return the Command
	 */
	public static Command create(ComponentType componentType) {
		return new CreateCommand(null, componentType);
	}

	/**
	 * Creates a Command that creates a composite Gate.
	 *
	 * @param commands    the Command's instructions to create the composite
	 * @param description the Command's description
	 *
	 * @return the Command
	 */
	public static Command create(List<Command> commands, String description) {
		return new CreateGateCommand(null, commands, description);
	}

	/**
	 * Creates a Command that deletes a Component.
	 *
	 * @return the Command
	 */
	public static Command delete() {
		return new DeleteCommand(null);
	}

	/** The Command's requirements; what it needs to execute. */
	protected Requirements<String> requirements;

	/** The Command's context; where it will act. */
	protected transient Editor context;

	/**
	 * Constructs the Command with the given {@code editor} as its context.
	 *
	 * @param editor the context
	 */
	public Command(Editor editor) {
		context = editor;
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
	 * Fulfils the Command's requirements with a dialog while also specifying its
	 * context.
	 *
	 * @param parent     the parent of the dialog
	 * @param newContext the Command's context
	 */
	public final void fillRequirements(Frame parent, Editor newContext) {
		requirements.fulfillWithDialog(parent, toString());
		context(newContext);
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
	public final void context(Editor c) {
		context = c;
	}

	@Override
	public abstract String toString();
}
