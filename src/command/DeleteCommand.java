package command;

import static components.ComponentType.BRANCH;

import java.util.Collection;
import java.util.List;
import java.util.Vector;

import application.editor.CycleException;
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

		// first delete gracefully all of the component's branches
		if (associatedComponent.type() != BRANCH) {
			// get all branches
			final Collection<Component> otherDeletedComponents = associatedComponent.getOutputs()
			        .stream().flatMap(List::stream).toList();
			otherDeletedComponents.addAll(associatedComponent.getInputs());

			// create, store and execute a DeleteCommand for each of them
			Utility.foreach(otherDeletedComponents, component -> {
				final DeleteCommand deleteCommand = new DeleteCommand(context);
				deleteCommands.add(deleteCommand);

				deleteCommand.requirements.fulfil("id", component.getID());
				try {
					deleteCommand.execute();
				} catch (MissingComponentException e) {
					// the Component with that id for sure exists; this statement can't throw
					e.printStackTrace();
				}
			});
		}

		ComponentFactory.destroyComponent(associatedComponent);
		context.removeComponent(associatedComponent);
		context.graph.remove(associatedComponent);
	}

	@Override
	public void unexecute() {
		ComponentFactory.restoreDeletedComponent(associatedComponent);
		context.addComponent(associatedComponent);
		try {
			context.graph.add(associatedComponent);
		} catch (CycleException e) {
			// Component has been added before; this statement can't throw
			throw new RuntimeException(e);
		}
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
