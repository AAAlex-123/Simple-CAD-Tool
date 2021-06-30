package command;

import static components.ComponentType.INPUT_PIN;
import static components.ComponentType.OUTPUT_PIN;

import java.util.ArrayList;
import java.util.List;

import application.editor.Editor;
import components.Component;
import components.ComponentFactory;
import components.ComponentType;
import myUtil.Utility;
import requirement.Requirements;

/**
 * A Command that creates a composite {@code Component} (a {@code Gate}) and
 * subsequently adds it to the {@code context}.
 * <p>
 * <b>Note:</b> the Command does not check if the {@code requirements} are set.
 * If they are not, the Command neither prints an error message nor attempts to
 * recover and continue execution. It's up to the caller to ensure that the
 * {@code requirements} are set properly.
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
	 * @param editor the context of the Command
	 * @param cmds   the sub-commands that will be executed
	 * @param desc   the description of this Command
	 */
	CreateGateCommand(Editor editor, List<Command> cmds, String desc) {
		super(editor, ComponentType.GATE);
		commands = cmds;
		description = desc;
	}

	@Override
	public Command clone() {
		final CreateGateCommand newCommand = new CreateGateCommand(context, commands, description);
		newCommand.requirements = new Requirements<>(requirements);
		return newCommand;
	}

	@Override
	public void execute() {
		if (createdComponent != null) {
			// when re-executed, simply restore the already-created Component
			context.addComponent(createdComponent);
			ComponentFactory.restoreDeletedComponent(createdComponent);
		} else {
			// execute the sequence of commands to create the circuit in a temporary context
			final Editor tempContext = new Editor(null, "");

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
			createdComponent = ComponentFactory.createGate(in, out, description);
			createdComponent.setID(requirements.getV("name"));
			context.addComponent(createdComponent);
		}
	}

	@Override
	public void unexecute() {
		ComponentFactory.destroyComponent(createdComponent);
		context.removeComponent(createdComponent);
	}

	@Override
	public String toString() {
		return "Create " + description;
	}
}
