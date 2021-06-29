package command;

import static components.ComponentType.INPUT_PIN;
import static components.ComponentType.OUTPUT_PIN;

import java.util.ArrayList;
import java.util.List;

import application.editor.Editor;
import components.Component;
import components.ComponentFactory;
import myUtil.Utility;

/**
 * A Command that creates a composite Gate, a Gate with a user-made circuit
 *
 * @author alexm
 */
class CreateGateCommand extends Command {

	private static final long serialVersionUID = 6L;

	private final List<Command> commands;    // sequence of Commands to create the Gate
	private final String        description; // displayed in the UI

	private Component createdComponent;
	private int       componentID;

	/**
	 * Creates the Command given an Application and a list of Commands
	 *
	 * @param editor the context of the Command
	 * @param cmds   the sub-commands that will be executed
	 * @param desc   the description of this Command
	 */
	CreateGateCommand(Editor editor, List<Command> cmds, String desc) {
		super(editor);
		commands = cmds;
		description = desc;
		componentID = -1;
	}

	@Override
	public Command clone() {
		final CreateGateCommand cgc = new CreateGateCommand(context, commands,
				description);

		if (createdComponent != null)
			cgc.componentID = createdComponent.UID();

		return cgc;
	}

	@Override
	public void execute() {
		if (createdComponent != null) {
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
			if (componentID != -1)
				createdComponent.setID(componentID);

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
		return description;
	}
}
