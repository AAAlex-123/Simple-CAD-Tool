package command;

import java.util.List;
import java.util.Vector;

import application.editor.Editor;
import components.Component;
import components.ComponentFactory;
import myUtil.Utility;
import requirement.StringType;

/**
 * A Command that deletes a Component
 *
 * @author alexm
 */
class DeleteCommand extends Command {

	private static final long serialVersionUID = 5L;

	private Component componentToDelete;

	private final List<Command> deleteCommands;

	/**
	 * Creates a Command that deletes a Component in the given {@code Application}.
	 *
	 * @param editor the context of this Command.
	 */
	DeleteCommand(Editor editor) {
		super(editor);
		deleteCommands = new Vector<>();
		requirements.add("id", StringType.NON_NEG_INTEGER);
	}

	@Override
	public Command clone() {
		final Command newCommand = new DeleteCommand(context);
		newCommand.requirements = requirements;

		return newCommand;
	}

	@Override
	public void execute() throws Editor.MissingComponentException {
		final int id = Integer.parseInt(requirements.getV("id"));
		componentToDelete = context.getComponent_(id);

		ComponentFactory.destroyComponent(componentToDelete);
		context.removeComponent(componentToDelete);

		Utility.foreach(context.getDeletedComponents_(), c -> {
			final DeleteCommand dC = new DeleteCommand(context);
			deleteCommands.add(dC);

			// component is already deleted the command isn't executed
			// instead it is just set up so it can be undone successfully
			final String compID = String.valueOf(c.UID());
			dC.requirements.fulfil("id", compID);

			try {
				// the Component for sure exists; this statement can't throw
				dC.componentToDelete = context.getComponent_(Integer.parseInt(compID));
			} catch (final Editor.MissingComponentException e) {
				throw new RuntimeException(e);
			}

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
