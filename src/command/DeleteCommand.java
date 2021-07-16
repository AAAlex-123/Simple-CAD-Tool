package command;

import java.util.List;
import java.util.Vector;

import application.editor.Editor;
import application.editor.MissingComponentException;
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
		deleteCommands.clear();
		associatedComponent = context.getComponent_(requirements.getV("id"));

		ComponentFactory.destroyComponent(associatedComponent);
		context.removeComponent(associatedComponent);
		
		if(associatedComponent.type() == ComponentType.BRANCH) {
			String input = associatedComponent.getInputs().get(0).getID();
			String output = associatedComponent.getOutputs().get(0).get(0).getID();
			context.graph.connectionRemoved(input, output);
		} else {
			context.graph.componentDeleted(associatedComponent.getID());
		}

		Utility.foreach(context.getDeletedComponents(), component -> {
			final DeleteCommand deleteCommand = new DeleteCommand(context);
			deleteCommands.add(deleteCommand);

			// component is already deleted the command isn't executed
			// instead it is just set up so it can be undone successfully
			deleteCommand.requirements.fulfil("id", String.valueOf(component.getID()));
			deleteCommand.associatedComponent = component;

			context.removeComponent(component);
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
		return "Delete";
	}
}
