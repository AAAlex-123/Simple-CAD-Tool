package application;

import static myUtil.Utility.foreach;

import java.util.List;
import java.util.Vector;

import components.Component;
import components.ComponentFactory;

/** A Command that deletes a Component */
class DeleteCommand extends Command {

	private static final long serialVersionUID = 3L;

	private Component componentToDelete;

	private final List<Command> deleteCommands;

	/**
	 * Creates a Command that deletes a Component in the given {@code Application}.
	 *
	 * @param app the context of this Command.
	 */
	public DeleteCommand(Application app) {
		super(app);
		deleteCommands = new Vector<>();
		requirements.add("id", Requirement.StringType.NON_NEG_INTEGER);
	}

	@Override
	Command myclone() {
		return new DeleteCommand(context);
	}

	@Override
	Command myclone(boolean keepIdAndReqs) {
		Command newCommand = new DeleteCommand(context);
		if (keepIdAndReqs) {
			newCommand.requirements = requirements;
		}

		return newCommand;
	}

	@Override
	public int execute() {
		componentToDelete = context.getComponent(Integer.valueOf(requirements.get("id").value()));
		if (componentToDelete == null)
			return 1;

		ComponentFactory.destroyComponent(componentToDelete);
		context.removeComponent(componentToDelete);

		foreach(context.getDeletedComponents(), c -> {
			DeleteCommand dC = new DeleteCommand(context);
			deleteCommands.add(dC);

			// component is already deleted the command isn't executed
			// instead it is just set up so it can be undone successfully
			String compID = String.valueOf(c.UID());
			dC.requirements.get("id").fulfil(compID);
			dC.componentToDelete = context.getComponent(Integer.valueOf(compID));
			context.removeComponent(c);
		});
		return 0;
	}

	@Override
	public int unexecute() {
		ComponentFactory.restoreComponent(componentToDelete);
		context.addComponent(componentToDelete);
		foreach(deleteCommands, Command::unexecute);
		return 0;
	}

	@Override
	String desc() {
		return "Delete";
	}
}
