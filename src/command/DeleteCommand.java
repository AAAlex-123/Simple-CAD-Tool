package command;

import static myUtil.Utility.foreach;

import java.util.List;
import java.util.Vector;

import application.Application;
import application.Application.MissingComponentException;
import components.Component;
import components.ComponentFactory;
import requirement.StringType;

/** A Command that deletes a Component */
class DeleteCommand extends Command {

	private static final long serialVersionUID = 4L;

	private Component componentToDelete;

	private final List<Command> deleteCommands;

	/**
	 * Creates a Command that deletes a Component in the given {@code Application}.
	 *
	 * @param app the context of this Command.
	 */
	DeleteCommand(Application app) {
		super(app);
		deleteCommands = new Vector<>();
		requirements.add("id", StringType.NON_NEG_INTEGER);
	}

	@Override
	public Command clone() {
		Command newCommand = new DeleteCommand(context);
		newCommand.requirements = requirements;

		return newCommand;
	}

	@Override
	public void execute() throws MissingComponentException {
		int id = Integer.valueOf(requirements.getV("id"));
		componentToDelete = context.getComponent(id);

		ComponentFactory.destroyComponent(componentToDelete);
		context.removeComponent(componentToDelete);

		foreach(context.getDeletedComponents(), c -> {
			DeleteCommand dC = new DeleteCommand(context);
			deleteCommands.add(dC);

			// component is already deleted the command isn't executed
			// instead it is just set up so it can be undone successfully
			String compID = String.valueOf(c.UID());
			dC.requirements.fulfil("id", compID);
			try {
				// the Component for sure exists; this statement can't throw
				dC.componentToDelete = context.getComponent(Integer.valueOf(compID));
			} catch (MissingComponentException e) {
				throw new RuntimeException(e);
			}
			context.removeComponent(c);
		});
	}

	@Override
	public void unexecute() {
		ComponentFactory.restoreComponent(componentToDelete);
		context.addComponent(componentToDelete);
		foreach(deleteCommands, Command::unexecute);
	}

	@Override
	public String toString() {
		return "Delete";
	}
}
