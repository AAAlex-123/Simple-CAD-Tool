package application;

import static components.ComponentType.INPUT_PIN;
import static components.ComponentType.OUTPUT_PIN;
import static myUtil.Utility.foreach;

import java.util.ArrayList;
import java.util.List;

import components.Component;
import components.ComponentFactory;

/**
 * A Command that creates a composite Gate. That is, a Gate that has a user-made
 * circuit inside of it.
 */
class CreateGateCommand extends Command {

	private static final long serialVersionUID = 2L;

	// the sequence of Commands required to create the Gate
	private final List<Command> commands;
	// the description that will be displayed in the UI
	private final String description;

	private Component createdComponent;
	private int componentID;

	/**
	 * Creates the Command given an Application and a list of Commands
	 *
	 * @param app  the context of the Command
	 * @param cmds the sub-commands that will be executed
	 * @param desc the description of this Command
	 */
	public CreateGateCommand(Application app, List<Command> cmds, String desc) {
		super(app);
		commands = cmds;
		description = desc;
	}

	@Override
	Command myclone() {
		return myclone(false);
	}

	@Override
	Command myclone(boolean keepId) {
		CreateGateCommand cgc = new CreateGateCommand(context, commands, description);
		if (keepId)
			cgc.componentID = componentID;
		return cgc;
	}

	@Override
	public int execute() {
		if (createdComponent != null) {
			context.addComponent(createdComponent);
			ComponentFactory.restoreComponent(createdComponent);
		} else {

			// execute the sequence of commands to create the circuit in a temporary context
			Application tempContext = new Application();

			foreach(commands, c -> {
				Command cloned = ((CreateCommand) c).myclone(true);
				cloned.context = tempContext;
				cloned.execute();
			});

			// get arrays of the InputPins and the OutputPins from the temporary context
			List<Component> ins = new ArrayList<>(), outs = new ArrayList<>();
			foreach(tempContext.getComponents(), c -> {
				if (c.type() == INPUT_PIN)
					ins.add(c);
				else if (c.type() == OUTPUT_PIN)
					outs.add(c);
			});

			Component[] in = new Component[ins.size()], out = new Component[outs.size()];
			for (int i = 0; i < in.length; ++i)
				in[i] = ins.get(i);
			for (int i = 0; i < out.length; ++i)
				out[i] = outs.get(i);

			// create the composite Gate and add it to the real context
			createdComponent = ComponentFactory.createGate(in, out);
			componentID = createdComponent.UID();
			context.addComponent(createdComponent);
		}
		return 0;
	}

	@Override
	public int unexecute() {
		ComponentFactory.destroyComponent(createdComponent);
		context.removeComponent(createdComponent);
		return 0;
	}

	@Override
	String desc() {
		return description;
	}
}
