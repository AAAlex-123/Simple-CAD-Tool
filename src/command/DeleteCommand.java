package command;

import java.util.List;
import java.util.Vector;

import application.editor.Editor;
import application.editor.MissingComponentException;
import components.Component;
import components.ComponentFactory;
import myUtil.Utility;
import requirement.Requirements;
import requirement.StringType;

/**
 * A Command that deletes a {@code Component} and subsequently removes it from
 * the {@code context}.
 *
 * @author alexm
 */
class DeleteCommand extends Command {

	private static final long serialVersionUID = 6L;

	private Component componentToDelete;

	private final List<Command> deleteCommands;

	/**
	 * Creates a Command initialising its {@code requirements}.
	 *
	 * @param editor the {@code context} of this Command.
	 */
	DeleteCommand(Editor editor) {
		super(editor);
		deleteCommands = new Vector<>();
		requirements.add("id", StringType.ANY);
	}

	@Override
	public Command clone() {
		final Command newCommand = new DeleteCommand(context);
		newCommand.requirements = new Requirements<>(requirements);
		return newCommand;
	}

	@Override
	public void execute() throws MissingComponentException {
		componentToDelete = context.getComponent_(requirements.getV("id"));

		ComponentFactory.destroyComponent(componentToDelete);
		context.removeComponent(componentToDelete);

		Utility.foreach(context.getDeletedComponents(), c -> {
			final DeleteCommand deleteCommand = new DeleteCommand(context);
			deleteCommands.add(deleteCommand);

			// component is already deleted the command isn't executed
			// instead it is just set up so it can be undone successfully
			deleteCommand.requirements.fulfil("id", String.valueOf(c.getID()));
			deleteCommand.componentToDelete = c;

			context.removeComponent(c);
		});
	}

	@Override
	public void unexecute() {
		ComponentFactory.restoreDeletedComponent(componentToDelete);
		context.addComponent(componentToDelete);
		Utility.foreach(deleteCommands, Command::unexecute);
	}

	@Override
	public String toString() {
		return "Delete";
	}
}
