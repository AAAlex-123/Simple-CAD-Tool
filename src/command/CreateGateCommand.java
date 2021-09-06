package command;

import static component.ComponentType.INPUT_PIN;
import static component.ComponentType.OUTPUT_PIN;

import java.util.ArrayList;
import java.util.List;

import application.editor.Editor;
import component.ComponentType;
import component.components.Component;
import component.components.ComponentFactory;
import localisation.CommandStrings;
import myUtil.Utility;

/**
 * The Command returned by {@link Command#create(List, String)}.
 *
 * @author Alex Mandelias
 */
class CreateGateCommand extends CreateCommand {

	private static final long serialVersionUID = 6L;

	private final List<Command> commands;    // sequence of Commands to create the Gate
	private final String        description; // displayed in the pop-up and in the Editor

	/**
	 * Creates the Command constructing its {@code Requirements}.
	 *
	 * @param editor      the {@code context} of this Command
	 * @param commands    the sequence of Commands that will be executed
	 * @param description the description of this Command
	 */
	protected CreateGateCommand(Editor editor, List<Command> commands, String description) {
		super(editor, ComponentType.GATE);
		this.commands = commands;
		this.description = description;
	}

	@Override
	public void execute() {
		if (associatedComponent != null) {
			// when re-executed, simply restore the already created Component
			context.addComponent(associatedComponent);
			ComponentFactory.restoreDeletedComponent(associatedComponent);
		} else {
			// execute the sequence of commands to create the circuit in a temporary context
			final Editor tempContext = new Editor(null, null);

			Utility.foreach(commands, command -> {
				final Command cloned = command.clone();
				cloned.context(tempContext);

				try {
					// this Command has executed successfully before; this statement can't throw
					cloned.execute();
				} catch (final Exception e) {
					throw new RuntimeException(e);
				}
			});

			// get arrays of the InputPins and the OutputPins from the temporary context
			final List<Component> inputPins = new ArrayList<>(), outputPins = new ArrayList<>();
			Utility.foreach(tempContext.getComponents_(), component -> {
				if (component.type() == INPUT_PIN)
					inputPins.add(component);
				else if (component.type() == OUTPUT_PIN)
					outputPins.add(component);
			});

			final Component[] inputPinArray  = inputPins.toArray(new Component[0]);
			final Component[] outputPinArray = outputPins.toArray(new Component[0]);

			// create the composite Gate and add it to the real context
			associatedComponent = ComponentFactory.createGate(inputPinArray, outputPinArray,
			        description);
			associatedComponent.setID((String) requirements.getValue(CommandStrings.NAME));
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
