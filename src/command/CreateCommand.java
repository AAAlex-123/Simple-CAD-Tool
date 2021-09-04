package command;

import static component.ComponentType.BRANCH;
import static localisation.CommandStrings.CREATE_STR;
import static localisation.CommandStrings.ID;
import static localisation.CommandStrings.IN_COUNT;
import static localisation.CommandStrings.IN_ID;
import static localisation.CommandStrings.IN_INDEX;
import static localisation.CommandStrings.NAME;
import static localisation.CommandStrings.OUT_ID;
import static localisation.CommandStrings.OUT_INDEX;
import static requirement.requirements.StringType.ANY;
import static requirement.requirements.StringType.CUSTOM;
import static requirement.requirements.StringType.NON_NEG_INTEGER;
import static requirement.requirements.StringType.POS_INTEGER;

import java.util.ArrayList;
import java.util.List;

import application.editor.Editor;
import application.editor.MissingComponentException;
import component.ComponentType;
import component.components.Component;
import component.components.ComponentFactory;
import component.exceptions.MalformedBranchException;
import localisation.Languages;
import myUtil.Utility;
import requirement.requirements.Requirements;

/**
 * A Command that creates a basic {@code Component} and subsequently adds it to
 * the {@code context}.
 *
 * @author alexm
 */
class CreateCommand extends Command {

	private static final long serialVersionUID = 6L;

	private final ComponentType componentType;

	private final List<Command> deleteCommands;

	/**
	 * Creates the Command initialising its {@code requirements}.
	 *
	 * @param editor the {@code context} of this Command
	 * @param type   the type of Components this Command creates
	 */
	protected CreateCommand(Editor editor, ComponentType type) {
		super(editor);
		componentType = type;
		deleteCommands = new ArrayList<>();
		constructRequirements();
	}

	@Override
	public Command clone() {
		final CreateCommand newCommand = new CreateCommand(context, componentType);
		newCommand.requirements = new Requirements(requirements);
		return newCommand;
	}

	@Override
	public void constructRequirements() {
		switch (componentType) {
		case BRANCH:
			requirements.add(IN_ID, ANY);
			requirements.add(IN_INDEX, NON_NEG_INTEGER);
			requirements.add(OUT_ID, ANY);
			requirements.add(OUT_INDEX, NON_NEG_INTEGER);
			break;
		case GATEAND:
		case GATENOT:
		case GATEOR:
		case GATEXOR:
			requirements.add(IN_COUNT, POS_INTEGER);
			break;
		case INPUT_PIN:
		case OUTPUT_PIN:
		default:
			break;
		}
		requirements.add(NAME, CUSTOM);
	}

	@Override
	public void adjustRequirements() {
		// alter the `CUSTOM` type for this specific use
		CUSTOM.alter(constructRegex(), Languages.getString("CreateCommand.0")); //$NON-NLS-1$

		// provide preset
		requirements.offer(NAME, context.getNextID(componentType));
	}

	@Override
	public void execute() throws MissingComponentException, MalformedBranchException {
		if (associatedComponent != null) {
			// when re-executed, simply restore the already-created Component
			context.addComponent(associatedComponent);
			ComponentFactory.restoreDeletedComponent(associatedComponent);
		} else {
			switch (componentType) {
			case INPUT_PIN:
				associatedComponent = ComponentFactory.createInputPin();
				break;
			case OUTPUT_PIN:
				associatedComponent = ComponentFactory.createOutputPin();
				break;
			case BRANCH:
				final Component in = context.getComponent_((String) requirements.getValue(IN_ID));
				final Component out = context.getComponent_((String) requirements.getValue(OUT_ID));
				final int inIndex = Integer.parseInt((String) requirements.getValue(IN_INDEX));
				final int outIndex = Integer.parseInt((String) requirements.getValue(OUT_INDEX));

				associatedComponent = ComponentFactory.connectComponents(in, inIndex, out, outIndex);
				break;
			case GATEAND:
			case GATEOR:
			case GATENOT:
			case GATEXOR:
				associatedComponent = ComponentFactory.createPrimitiveGate(componentType,
				        Integer.parseInt((String) requirements.getValue(IN_COUNT)));
				break;
			case GATE:
				throw new RuntimeException(String.format(Languages.getString("CreateCommand.1"), componentType)); //$NON-NLS-1$
			default:
				break;
			}

			associatedComponent.setID((String) requirements.getValue(NAME));
			context.addComponent(associatedComponent);
		}

		// delete the branch that may have been deleted when creating this branch
		// there can't be more than two branches deleted when creating a branch
		if (associatedComponent.type() == BRANCH) {
			final List<Component> ls = context.getDeletedComponents();

			if (ls.size() == 0)
				return;

			if (ls.size() > 1)
				throw new RuntimeException("There can't be more than 1 deleted Branches after creating a Branch"); //$NON-NLS-1$

			final Command d = new DeleteCommand(context);
			deleteCommands.add(d);
			d.requirements.fulfil(ID, String.valueOf(ls.get(0).getID()));

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

		ComponentFactory.destroyComponent(associatedComponent);
		context.removeComponent(associatedComponent);
	}

	@Override
	public String toString() {
		return String.format("%s %s", CREATE_STR, componentType.description()); //$NON-NLS-1$
	}

	private String constructRegex() {
		// Construct the following regex: ^(?!foo$|bar$|)[^\s]*$
		// to match IDs that don't contain blanks and are not in use
		final StringBuilder regex = new StringBuilder(Languages.getString("CreateCommand.4")); //$NON-NLS-1$
		Utility.foreach(context.getComponents_(), c -> {
			regex.append("|"); //$NON-NLS-1$
			regex.append(c.getID());
			regex.append("$"); //$NON-NLS-1$
		});
		regex.append(")[^\\s]*$"); //$NON-NLS-1$
		return regex.toString();
	}
}
