package command;

import static components.ComponentType.BRANCH;
import static components.ComponentType.GATEAND;
import static components.ComponentType.GATENOT;
import static components.ComponentType.GATEOR;
import static components.ComponentType.GATEXOR;
import static requirement.StringType.NON_NEG_INTEGER;
import static requirement.StringType.POS_INTEGER;

import java.util.ArrayList;
import java.util.List;

import application.editor.Editor;
import components.Component;
import components.ComponentFactory;
import components.ComponentType;
import exceptions.MalformedBranchException;
import myUtil.Utility;
import requirement.Requirements;

/**
 * A Command that creates a Component
 *
 * @author alexm
 */
class CreateCommand extends Command {

	private static final long serialVersionUID = 5L;
	private static final int  ID_NOT_SET       = -1;

	private final ComponentType componentType;

	private Component createdComponent;
	private int       componentID;     // used for composites

	private final List<Command> deleteCommands;

	/**
	 * Creates a Command that creates a specific ComponentType in the given context.
	 *
	 * @param editor the Application where this Command will act
	 * @param cType  the type of Components this Command creates
	 */
	CreateCommand(Editor editor, ComponentType cType) {
		super(editor);
		componentType = cType;
		componentID = CreateCommand.ID_NOT_SET;
		deleteCommands = new ArrayList<>();

		switch (cType) {
		case BRANCH:
			requirements.add("in id", NON_NEG_INTEGER);
			requirements.add("in index", NON_NEG_INTEGER);
			requirements.add("out id", NON_NEG_INTEGER);
			requirements.add("out index", NON_NEG_INTEGER);
			break;
		case GATEAND:
		case GATENOT:
		case GATEOR:
		case GATEXOR:
			requirements.add("in count", POS_INTEGER);
			break;
		case INPUT_PIN:
		case OUTPUT_PIN:
		default:
			break;
		}
	}

	@Override
	public Command clone() {
		final CreateCommand newCommand = new CreateCommand(context, componentType);
		if (createdComponent != null)
			newCommand.componentID = createdComponent.UID();

		newCommand.requirements = new Requirements<>(requirements);

		return newCommand;
	}

	@Override
	public void execute() throws Editor.MissingComponentException, MalformedBranchException {
		if (createdComponent != null) {
			context.addComponent(createdComponent);
			ComponentFactory.restoreDeletedComponent(createdComponent);
		} else {
			switch (componentType) {
			case INPUT_PIN:
				createdComponent = ComponentFactory.createInputPin();
				break;
			case OUTPUT_PIN:
				createdComponent = ComponentFactory.createOutputPin();
				break;
			case BRANCH:
				final Component in = context
				.getComponent_(Integer.parseInt(requirements.getV("in id")));
				final Component out = context
						.getComponent_(Integer.parseInt(requirements.getV("out id")));

				createdComponent = ComponentFactory.connectComponents(
						in,
						Integer.parseInt(requirements.getV("in index")),
						out,
						Integer.parseInt(requirements.getV("out index")));
				break;
			case GATEAND:
				createdComponent = ComponentFactory.createPrimitiveGate(GATEAND,
						Integer.parseInt(requirements.getV("in count")));
				break;
			case GATEOR:
				createdComponent = ComponentFactory.createPrimitiveGate(GATEOR,
						Integer.parseInt(requirements.getV("in count")));
				break;
			case GATENOT:
				createdComponent = ComponentFactory.createPrimitiveGate(GATENOT,
						Integer.parseInt(requirements.getV("in count")));
				break;
			case GATEXOR:
				createdComponent = ComponentFactory.createPrimitiveGate(GATEXOR,
						Integer.parseInt(requirements.getV("in count")));
				break;
			case GATE:
				throw new RuntimeException(String.format(
						"Cannot directly create Components of type %s", ComponentType.GATE));
			default:
				break;
			}

			if (componentID != CreateCommand.ID_NOT_SET)
				createdComponent.setID(componentID);

			context.addComponent(createdComponent);
		}

		// delete the branch that may have been deleted when creating this branch
		// there can't be more than two branches deleted when creating a branch
		if (createdComponent.type() == BRANCH) {
			final List<Component> ls = context.getDeletedComponents_();

			if (ls.size() == 0)
				return;

			if (ls.size() > 1)
				throw new RuntimeException(
						"There can't be more than 1 deleted Branches after creating a Branch");

			final Command d = new DeleteCommand(context);
			deleteCommands.add(d);
			d.requirements.fulfil("id", String.valueOf(ls.get(0).UID()));

			try {
				// the Component with that id for sure exists; this statement can't throw
				d.execute();
			} catch (final Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	@Override
	public void unexecute() {
		if (componentType == BRANCH) {
			Utility.foreach(deleteCommands, Command::unexecute);
			deleteCommands.clear();
		}

		ComponentFactory.destroyComponent(createdComponent);
		context.removeComponent(createdComponent);
	}

	@Override
	public String toString() {
		return "Create " + componentType.description();
	}
}
