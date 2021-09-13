package command;

import static component.ComponentType.BRANCH;

import java.util.ArrayList;
import java.util.List;

import application.editor.Editor;
import application.editor.MissingComponentException;
import component.ComponentType;
import component.components.Component;
import component.components.ComponentFactory;
import component.exceptions.MalformedBranchException;
import localisation.CommandStrings;
import localisation.Languages;
import myUtil.Utility;
import requirement.requirements.ComponentRequirement;
import requirement.requirements.ComponentRequirement.Policy;
import requirement.requirements.StringType;

/**
 * The Command returned by {@link Command#create(ComponentType)}.
 *
 * @author Alex Mandelias
 */
class CreateCommand extends Command {

	private static final long serialVersionUID = 6L;

	private final ComponentType componentType;

	/* Deletes the Branch that may have been deleted when creating another Branch */
	private DeleteCommand deleteCommand;

	/**
	 * Creates the Command constructing its {@code Requirements}.
	 *
	 * @param editor the {@code context} of this Command
	 * @param type   the {@code Type} of {@code Components} this Command creates
	 */
	protected CreateCommand(Editor editor, ComponentType type) {
		super(editor);
		componentType = type;
		constructRequirements();
	}

	@Override
	public void constructRequirements() {
		switch (componentType) {
		case BRANCH:
			final ComponentRequirement inName = new ComponentRequirement(CommandStrings.IN_NAME,
			        new ArrayList<>(), Policy.INPUT);
			final ComponentRequirement outName = new ComponentRequirement(CommandStrings.OUT_NAME,
			        new ArrayList<>(), Policy.OUTPUT);
			inName.setCaseOfNullGraphic(false, "There are no Components to act as Input");
			outName.setCaseOfNullGraphic(false, "There are no Components to act as Output");

			requirements.add(inName);
			requirements.add(CommandStrings.IN_INDEX, StringType.NON_NEG_INTEGER);
			requirements.add(outName);
			requirements.add(CommandStrings.OUT_INDEX, StringType.NON_NEG_INTEGER);
			break;
		case GATE:
			// can never be GATE
			break;
		case GATEAND:
		case GATENOT:
		case GATEOR:
		case GATEXOR:
			requirements.add(CommandStrings.IN_COUNT, StringType.POS_INTEGER);
			break;
		case INPUT_PIN:
		case OUTPUT_PIN:
		default:
			break;
		}
		requirements.add(CommandStrings.NAME, StringType.CUSTOM);
	}

	@Override
	public void adjustRequirements() {
		// alter the `CUSTOM` type for this specific use
		StringType.CUSTOM.alter(constructRegex(), Languages.getString("CreateCommand.0")); //$NON-NLS-1$

		// provide options
		if (componentType == BRANCH) {
			final List<Component> components = context.getComponents_();
			((ComponentRequirement) requirements.get(CommandStrings.IN_NAME))
			        .setComponentOptions(new ArrayList<>(components));
			((ComponentRequirement) requirements.get(CommandStrings.OUT_NAME))
			        .setComponentOptions(new ArrayList<>(components));
		}

		// provide preset
		requirements.offer(CommandStrings.NAME, context.getNextID(componentType));
	}

	@Override
	public void execute() throws MissingComponentException, MalformedBranchException {
		if (associatedComponent != null) {
			// when re-executed, simply restore the already created Component
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
				final Component in = context
				        .getComponent_((String) requirements.getValue(CommandStrings.IN_NAME));
				final int inIndex = Integer
				        .parseInt((String) requirements.getValue(CommandStrings.IN_INDEX));

				final Component out = context
				        .getComponent_((String) requirements.getValue(CommandStrings.OUT_NAME));
				final int outIndex = Integer
				        .parseInt((String) requirements.getValue(CommandStrings.OUT_INDEX));

				associatedComponent = ComponentFactory.connectComponents(in, inIndex, out,
				        outIndex);
				break;
			case GATEAND:
			case GATEOR:
			case GATENOT:
			case GATEXOR:
				associatedComponent = ComponentFactory.createPrimitiveGate(componentType,
				        Integer.parseInt((String) requirements.getValue(CommandStrings.IN_COUNT)));
				break;
			case GATE:
			default:
				break;
			}

			associatedComponent.setID((String) requirements.getValue(CommandStrings.NAME));
			context.addComponent(associatedComponent);
		}

		// delete the branch that may have been deleted when creating this branch
		// for example, existing connection A->C is deleted by new connection B->C
		if (componentType == BRANCH) {
			final List<Component> deletedComps = context.getDeletedComponents();

			final int deletedComponentCount = deletedComps.size();

			if (deletedComponentCount == 0)
				return;

			// there can't be more than two branches deleted when creating a branch
			if (deletedComponentCount > 1)
				throw new RuntimeException(
				        "More than 1 deleted Branches found after creating a Branch"); //$NON-NLS-1$

			deleteCommand = new DeleteCommand(context);
			((ComponentRequirement) deleteCommand.requirements.get(CommandStrings.NAME))
			        .setComponentOptions(deletedComps);
			deleteCommand.requirements.fulfil(CommandStrings.NAME, deletedComps.get(0).getID());

			try {
				// the Component with that id for sure exists; this statement can't throw
				deleteCommand.execute();
			} catch (final Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	@Override
	public void unexecute() {
		// restore the previously deleted Branches
		if (componentType == BRANCH) {
			deleteCommand.unexecute();
			deleteCommand = null;
		}

		ComponentFactory.destroyComponent(associatedComponent);
		context.removeComponent(associatedComponent);
	}

	@Override
	public String toString() {
		return String.format("%s %s", CommandStrings.CREATE_STR, componentType.description()); //$NON-NLS-1$
	}

	private String constructRegex() {
		// Constructs the following regex: ^(?!foo$|bar$|)[^\s]*$
		// to match IDs that don't contain blanks and are not in use
		final StringBuilder regex = new StringBuilder("^(?!$"); //$NON-NLS-1$

		Utility.foreach(context.getComponents_(), component -> {
			regex.append("|"); //$NON-NLS-1$
			regex.append(component.getID());
			regex.append("$"); //$NON-NLS-1$
		});

		regex.append(")[^\\s]*$"); //$NON-NLS-1$
		return regex.toString();
	}
}
