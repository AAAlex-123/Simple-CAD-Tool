package command;

import static localisation.CommandStrings.DELETE_STR;
import static localisation.CommandStrings.ID;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import application.editor.Editor;
import application.editor.MissingComponentException;
import components.Component;
import components.ComponentFactory;
import myUtil.Utility;
import requirement.requirements.ComponentRequirement;
import requirement.requirements.Requirements;

/**
 * A Command that deletes a {@code Component} and subsequently removes it from
 * the {@code context}.
 *
 * @author alexm
 */
class DeleteCommand extends Command {

	private static final long serialVersionUID = 6L;

	private final List<Command> deleteCommands;

	/**
	 * Creates a Command initialising its {@code requirements}.
	 *
	 * @param editor the {@code context} of this Command.
	 */
	protected DeleteCommand(Editor editor) {
		super(editor);
		deleteCommands = new Vector<>();
		constructRequirements();
	}

	@Override
	public Command clone() {
		final Command newCommand = new DeleteCommand(context);
		newCommand.requirements = new Requirements(requirements);
		return newCommand;
	}

	@Override
	public void constructRequirements() {
		requirements.add(ID, new ArrayList<>(), ComponentRequirement.Policy.ANY);
	}

	@Override
	public void adjustRequirements() {
		((ComponentRequirement)requirements.get(ID)).setComponentOptions(context.getComponents_());
	}

	@Override
	public void execute() throws MissingComponentException {
		deleteCommands.clear();
		associatedComponent = context.getComponent_((String) requirements.getValue(ID));

		ComponentFactory.destroyComponent(associatedComponent);
		context.removeComponent(associatedComponent);

		List<Component> deletedComps = context.getDeletedComponents();

		Utility.foreach(deletedComps, command -> {
			final DeleteCommand deleteCommand = new DeleteCommand(context);
			deleteCommands.add(deleteCommand);

			// component is already deleted the command isn't executed
			// instead it is just set up so it can be undone successfully
			((ComponentRequirement) deleteCommand.requirements.get(ID))
			        .setComponentOptions(deletedComps);
			deleteCommand.requirements.fulfil(ID, String.valueOf(command.getID()));
			deleteCommand.associatedComponent = command;

			context.removeComponent(command);
		});
	}

	@Override
	public void unexecute() {
		ComponentFactory.restoreDeletedComponent(associatedComponent);
		context.addComponent(associatedComponent);
		Utility.foreach(deleteCommands, Command::unexecute);
	}

	@Override
	public void context(Editor editor) {
		super.context(editor);
		Utility.foreach(deleteCommands, command -> command.context(editor));
	}

	@Override
	public String toString() {
		return DELETE_STR;
	}
}
