package command;

import static component.ComponentType.INPUT_PIN;
import static component.ComponentType.OUTPUT_PIN;
import static localisation.CommandStrings.NAME;

import java.util.ArrayList;
import java.util.List;

import application.editor.Editor;
import component.ComponentType;
import component.components.Component;
import component.components.ComponentFactory;
import myUtil.Utility;
import requirement.requirements.Requirements;

/**
 * A Command that creates a composite {@code Component} (a {@code Gate}) and
 * subsequently adds it to the {@code context}.
 *
 * @author alexm
 */
class CreateGateCommand extends CreateCommand {

	private static final long serialVersionUID = 6L;

	private final List<Command> commands;    // sequence of Commands to create the Gate
	private final String        description; // displayed in the pop-up and the Editor

	/**
	 * Creates the Command.
	 *
	 * @param editor the {@code context} of the Command
	 * @param cmds   the sequence of Commands that will be executed
	 * @param desc   the description of this Command
	 */
	protected CreateGateCommand(Editor editor, List<Command> cmds, String desc) {
		super(editor, ComponentType.GATE);
		commands = cmds;
		description = desc;
	}

	@Override
	public Command clone() {
		final CreateGateCommand newCommand = new CreateGateCommand(context, commands, description);
		newCommand.requirements = new Requirements(requirements);
		return newCommand;
	}

	@Override
	public void execute() {
		if (associatedComponent != null) {
			// when re-executed, simply restore the already-created Component
			context.addComponent(associatedComponent);
			ComponentFactory.restoreDeletedComponent(associatedComponent);
		} else {
			// execute the sequence of commands to create the circuit in a temporary context
			final Editor tempContext = new Editor(null, null);

			Utility.foreach(commands, c -> {
				final Command cloned = c.clone();
				cloned.context(tempContext);

				try {
					cloned.execute();
				} catch (final Exception e) {
					// this Command has executed successfully before; this statement can't throw
					throw new RuntimeException(e);
				}
			});

			// get arrays of the InputPins and the OutputPins from the temporary context
			final List<Component> ins = new ArrayList<>(), outs = new ArrayList<>();
			Utility.foreach(tempContext.getComponents_(), c -> {
				if (c.type() == INPUT_PIN)
					ins.add(c);
				else if (c.type() == OUTPUT_PIN)
					outs.add(c);
			});

			final Component[] in = new Component[ins.size()], out = new Component[outs.size()];
			for (int i = 0; i < in.length; ++i)
				in[i] = ins.get(i);

			for (int i = 0; i < out.length; ++i)
				out[i] = outs.get(i);

			// create the composite Gate and add it to the real context
			associatedComponent = ComponentFactory.createGate(in, out, description);
			associatedComponent.setID((String) requirements.getValue(NAME));
			context.addComponent(associatedComponent);
		}
	}

	@Override
	public void unexecute() {
		ComponentFactory.destroyComponent(associatedComponent);
		context.removeComponent(associatedComponent);
	}

	@Override
	public String toString() {
		return description;
	}
}
