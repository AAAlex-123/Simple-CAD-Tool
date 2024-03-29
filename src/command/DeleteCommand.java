package command;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import application.editor.Editor;
import application.editor.MissingComponentException;
import component.components.Component;
import component.components.ComponentFactory;
import localisation.CommandStrings;
import localisation.Languages;
import myUtil.Utility;
import requirement.requirements.ComponentRequirement;
import requirement.requirements.ComponentRequirement.Policy;

/**
 * The Command returned by {@link Command#delete()}.
 *
 * @author Alex Mandelias
 */
class DeleteCommand extends Command {

	private static final long serialVersionUID = 6L;

	private final List<Command> deleteCommands;

	/**
	 * Creates the Command constructing its {@code Requirements}.
	 *
	 * @param editor the {@code context} of this Command.
	 */
	protected DeleteCommand(Editor editor) {
		super(editor);
		deleteCommands = new Vector<>();
		constructRequirements();
	}

	@Override
	public void constructRequirements() {
		final ComponentRequirement req = new ComponentRequirement(CommandStrings.NAME,
		        new ArrayList<>(), Policy.ANY);
		req.setCaseOfNullGraphic(false, Languages.getString("DeleteCommand.0")); //$NON-NLS-1$
		requirements.add(req);
	}

	@Override
	public void adjustRequirements() {
		// provide options
		ComponentRequirement nameReq = (ComponentRequirement) requirements.get(CommandStrings.NAME);
		nameReq.setComponentOptions(context.getComponents_());
	}

	@Override
	public void execute() throws MissingComponentException {
		associatedComponent = context
		        .getComponent_(requirements.getValue(CommandStrings.NAME, String.class));

		ComponentFactory.destroyComponent(associatedComponent);
		context.removeComponent(associatedComponent);

		deleteCommands.clear();
		final List<Component> deletedComps = context.getDeletedComponents();

		Utility.foreach(deletedComps, component -> {
			// component is already deleted the command isn't executed
			// instead it is just set up so it can be undone successfully
			final DeleteCommand deleteCommand = new DeleteCommand(context);

			ComponentRequirement nameReq = (ComponentRequirement) deleteCommand.requirements.get(CommandStrings.NAME);
			nameReq.setComponentOptions(deletedComps);

			deleteCommand.requirements.fulfil(CommandStrings.NAME, component.getID());
			deleteCommand.associatedComponent = component;

			// store the command to be undone
			deleteCommands.add(deleteCommand);

			// remove the already deleted component
			context.removeComponent(component);
		});
	}

	@Override
	public void unexecute() {
		ComponentFactory.restoreDeletedComponent(associatedComponent);
		context.addComponent(associatedComponent);
		Utility.foreach(deleteCommands, Command::unexecute);
		deleteCommands.clear();
	}

	@Override
	public void context(Editor editor) {
		super.context(editor);
		Utility.foreach(deleteCommands, command -> command.context(editor));
	}

	@Override
	public String description() {
		return CommandStrings.DELETE_STR;
	}

	@Override
	public String toString() {
		return String.format("%s%ndelete commands: %s", super.toString(), deleteCommands); //$NON-NLS-1$
	}
}
