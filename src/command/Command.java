package command;

import java.awt.Frame;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.List;

import application.editor.Editor;
import application.editor.Undoable;
import components.Component;
import components.ComponentFactory;
import components.ComponentType;
import requirement.requirements.HasRequirements;
import requirement.requirements.Requirements;

/**
 * An implementation of the {@code Undoable} interface, specific to this
 * Application. {@link command.Command Commands} have certain
 * {@link requirement.requirements.AbstractRequirement requirements}, act on a
 * {@link application.editor.Editor context} and manipulate
 * {@link components.Component Components} by creating or deleting them.
 * <p>
 * <b>Note:</b> the Command does not check if the {@code requirements} are set.
 * If they are not, the Command neither prints an error message nor attempts to
 * recover and continue execution. It's up to the caller to ensure that the
 * {@code requirements} are set properly.
 *
 * @author alexm
 */
public abstract class Command implements HasRequirements, Undoable, Serializable, Cloneable {

	private static final long serialVersionUID = 4L;

	/**
	 * Creates a Command that creates a {@code Component} of the given
	 * {@link components.ComponentType Type}.
	 *
	 * @param componentType the type of the Component
	 *
	 * @return the Command
	 */
	public static Command create(ComponentType componentType) {
		return new CreateCommand(null, componentType);
	}

	/**
	 * Creates a Command that creates a composite {@code Gate}.
	 *
	 * @param commands    the instructions to create the Gate
	 * @param description the description
	 *
	 * @return the Command
	 *
	 * @see components.ComponentType#GATE
	 */
	public static Command create(List<Command> commands, String description) {
		return new CreateGateCommand(null, commands, description);
	}

	/**
	 * Creates a Command that deletes a {@code Component}.
	 *
	 * @return the Command
	 */
	public static Command delete() {
		return new DeleteCommand(null);
	}

	/**
	 * The {@code Component} that this {@code Command} manages. It is used to make
	 * sure that the {@code Command} can be properly undone.
	 */
	protected Component associatedComponent;

	/** What this Command needs to execute */
	protected Requirements requirements;

	/** Where this Command will act */
	protected transient Editor context;

	/**
	 * Constructs the Command with the given {@code context}.
	 *
	 * @param editor the context
	 */
	protected Command(Editor editor) {
		context = editor;
		requirements = new Requirements();
		constructRequirements();
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
	 * Fulfils the Command's {@code requirements} with a dialog while also
	 * specifying its {@code context}.
	 *
	 * @param parentFrame the parent of the dialog
	 * @param newContext  the Command's context
	 */
	public final void fillRequirements(Frame parentFrame, Editor newContext) {
		context(newContext);
		adjustRequirements();
		requirements.fulfillWithDialog(parentFrame, toString());
	}

	/**
	 * Returns whether or not this Command is ready to be executed.
	 *
	 * @return {@code true} if ready to be executed, {@code false} otherwise
	 */
	public final boolean canExecute() {
		return requirements.fulfilled();
	}

	/**
	 * Sets the Command's {@code context}.
	 *
	 * @param editor the context
	 */
	public void context(Editor editor) {
		context = editor;
	}

	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.defaultReadObject();
		ComponentFactory.restoreSerialisedComponent(associatedComponent);
	}

	@Override
	public abstract String toString();
}
