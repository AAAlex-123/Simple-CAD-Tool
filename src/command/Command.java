package command;

import java.awt.Frame;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.List;

import application.editor.Editor;
import application.editor.Undoable;
import component.ComponentType;
import component.components.Component;
import component.components.ComponentFactory;
import localisation.Languages;
import requirement.requirements.AbstractRequirement;
import requirement.requirements.HasRequirements;
import requirement.requirements.Requirements;

/**
 * An implementation of the {@link Undoable} interface, specific to this
 * Application. In order to function, {@code Commands} have certain
 * {@link #requirements}, act on a {@link #context} and manipulate their
 * {@link #associatedComponent} by creating or deleting it.
 * <p>
 * The life cycle of a Command is as follows:
 * <ul>
 * <li>A Command is created by cloning another Command or by calling one of the
 * available static factory methods</li>
 * <li>Its {@code Requirements} are fulfilled and its context is set</li>
 * <li>It is executed</li>
 * </ul>
 * <p>
 * <b>Note:</b> the Command does not check if the {@code Requirements} are set.
 * If they are not, the Command neither prints an error message nor attempts to
 * recover and continue execution. It's up to the caller to ensure that the
 * {@code Requirements} are set properly.
 *
 * @author Alex Mandelias
 *
 * @see AbstractRequirement
 * @see Component
 * @see Editor
 *
 * @apiNote the methods of the {@link HasRequirements} interface are not part of
 *          the {@code Command's} public API and shouldn't be called externally:
 *          {@link #constructRequirements()} and {@link #adjustRequirements()}
 */
public abstract class Command implements HasRequirements, Undoable, Serializable, Cloneable {

	private static final long serialVersionUID = 4L;

	/**
	 * Creates a Command that creates a {@code Component} of the given {@code Type}.
	 * Cannot be used to create a composite Component, that is a Component of Type
	 * {@code GATE}.
	 *
	 * @param type the Type of the Component, cannot be {@code GATE}
	 *
	 * @return the Command
	 *
	 * @throws RuntimeException if {@code type == GATE}
	 *
	 * @see ComponentType
	 * @see ComponentType#GATE
	 */
	public static Command create(ComponentType type) {
		if (type == ComponentType.GATE)
			throw new RuntimeException(
			        String.format(Languages.getString("CreateCommand.1"), type)); //$NON-NLS-1$

		return new CreateCommand(null, type);
	}

	/**
	 * Creates a Command that creates a composite {@code Gate}.
	 *
	 * @param commands    the instructions to create the Gate
	 * @param description the description of the Gate
	 *
	 * @return the Command
	 *
	 * @see ComponentType#GATE
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
	 * The {@code Component} that this Command manages. It is stored to ensure that
	 * the Command can be properly undone.
	 */
	protected Component associatedComponent;

	/** What information this Command needs to execute */
	protected Requirements requirements;

	/** Where this Command will act */
	protected transient Editor context;

	/**
	 * Constructs the Command with the given {@code context}.
	 *
	 * @param editor the context
	 *
	 * @implSpec subclasses are responsible for calling the
	 *           {@link #constructRequirements()} method
	 */
	protected Command(Editor editor) {
		context = editor;
		requirements = new Requirements();
		// constructRequirements() is NOT called here
		// each subclass is responsible for calling it
	}

	/**
	 * Clones the Command. The cloned Command should be executed so that its
	 * execution doesn't alter the state of this Command.
	 *
	 * @return the cloned Command
	 */
	@Override
	public final Command clone() {
		Command cloned = null;
		try {
			cloned = (Command) super.clone();
		} catch (final CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
		cloned.requirements = new Requirements(requirements);
		return cloned;
	}

	/**
	 * Fulfils the Command's {@code Requirements} using a pop-up dialog while also
	 * specifying its {@code context}.
	 *
	 * @param parentFrame the parent frame of the dialog
	 * @param newContext  the Command's context
	 *
	 * @throws NullPointerException if the {@code parentFrame} is {@code null}
	 */
	public final void fillRequirements(Frame parentFrame, Editor newContext) {
		context(newContext);
		adjustRequirements();
		requirements.fulfillWithDialog(parentFrame, toString());
	}

	/**
	 * Returns whether or not this Command is ready to be executed. Commands can be
	 * executed if and only if their {@code Requirements} are fulfilled.
	 *
	 * @return {@code true} if ready to be executed, {@code false} otherwise
	 */
	public final boolean canExecute() {
		return requirements.fulfilled();
	}

	/**
	 * Sets this Command's {@code context}.
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
