package command;

import static components.ComponentType.BRANCH;
import static components.ComponentType.GATEAND;
import static components.ComponentType.GATENOT;
import static components.ComponentType.GATEOR;
import static components.ComponentType.GATEXOR;
import static requirement.StringType.ANY;
import static requirement.StringType.CUSTOM;
import static requirement.StringType.NON_NEG_INTEGER;
import static requirement.StringType.POS_INTEGER;

import java.awt.Frame;
import java.util.ArrayList;
import java.util.List;

import application.editor.Editor;
import application.editor.MissingComponentException;
import components.Component;
import components.ComponentFactory;
import components.ComponentType;
import exceptions.MalformedBranchException;
import myUtil.Utility;
import requirement.Requirements;

/**
 * A Command that creates a {@code Component}. Assuming that all of the
 * {@code requirements} are set correctly, a {@code Component} of the correct
 * type is created and added to the {@code context}.
 *
 * @author alexm
 */
class CreateCommand extends Command {

	private static final long serialVersionUID = 6L;

	private final ComponentType componentType;

	/**
	 * The {@code Component} created by this {@code Command}. It is used to make
	 * sure that the {@code Command} can be properly undone.
	 */
	protected Component createdComponent;

	private final List<Command> deleteCommands;

	/**
	 * Creates the Command initialising its {@code requirements}.
	 *
	 * @param editor the Application where this Command will act
	 * @param type   the type of Components this Command creates
	 */
	CreateCommand(Editor editor, ComponentType type) {
		super(editor);
		componentType = type;
		deleteCommands = new ArrayList<>();

		switch (componentType) {
		case BRANCH:
			requirements.add("in id", ANY);
			requirements.add("in index", NON_NEG_INTEGER);
			requirements.add("out id", ANY);
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
		requirements.add("name", CUSTOM);
	}

	@Override
	public Command clone() {
		final CreateCommand newCommand = new CreateCommand(context, componentType);
		newCommand.requirements = new Requirements<>(requirements);
		return newCommand;
	}

	@Override
	public void fillRequirements(Frame parent, Editor newContext) {
		context(newContext);

		// alter the `CUSTOM` type for this specific use
		CUSTOM.alter(constructRegex(), "Name not already used");

		// provide preset
		requirements.get("name").offer(context.getNextID(componentType));
		super.fillRequirements(parent, newContext);
	}

	@Override
	public void execute() throws MissingComponentException, MalformedBranchException {
		if (createdComponent != null) {
			// when re-executed, simply restore the already-created Component
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
				final Component in = context.getComponent_(requirements.getV("in id"));
				final Component out = context.getComponent_(requirements.getV("out id"));
				final int inIndex = Integer.parseInt(requirements.getV("in index"));
				final int outIndex = Integer.parseInt(requirements.getV("out index"));

				createdComponent = ComponentFactory.connectComponents(in, inIndex, out, outIndex);
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

			createdComponent.setID(requirements.getV("name"));
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
			d.requirements.fulfil("id", String.valueOf(ls.get(0).getID()));

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
		// restore the previously deleted Branches
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

	private String constructRegex() {
		// Construct the following regex: ^(?!foo$|bar$|)[^\s]*$
		// to match IDs that don't contain blanks and are not in use
		final StringBuilder regex = new StringBuilder("^(?!");
		Utility.foreach(context.getComponents_(), c -> {
			regex.append(c.getID());
			regex.append("$|");
		});
		regex.append(")[^\\s]*$");
		return regex.toString();
	}
}
