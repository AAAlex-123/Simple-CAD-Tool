package application;

import static application.Requirement.StringType.NON_NEG_INTEGER;
import static application.Requirement.StringType.POS_INTEGER;
import static components.ComponentType.BRANCH;
import static components.ComponentType.GATEAND;
import static components.ComponentType.GATENOT;
import static components.ComponentType.GATEOR;
import static components.ComponentType.GATEXOR;
import static myUtil.Utility.foreach;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import components.Component;
import components.ComponentFactory;
import components.ComponentType;
import exceptions.InvalidIndexException;
import exceptions.MalformedBranchException;

/** A Command that creates a Component */
class CreateCommand extends Command {

	private static final long serialVersionUID = 2L;

	/** The type of the component that will be created */
	final ComponentType componentType;

	// save the created Component for undo
	private Component createdComponent;
	private int componentID;

	private final List<Command> deleteCommands;

	/**
	 * Creates a Command that creates a specific ComponentType in the given context.
	 *
	 * @param app           the Application where this Command will act
	 * @param ct the type of Components this Command creates
	 */
	CreateCommand(Application app, ComponentType ct) {
		super(app);
		componentType = ct;
		componentID = -1;
		deleteCommands = new ArrayList<>();

		switch (ct) {
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
	Command myclone() {
		return myclone(false);
	}

	@Override
	Command myclone(boolean keepIdAndReqs) {
		CreateCommand newCommand = new CreateCommand(context, componentType);
		if (keepIdAndReqs) {
			newCommand.componentID = createdComponent.UID();
			newCommand.requirements = requirements;
		}

		return newCommand;
	}

	@SuppressWarnings("unused")
	@Override
	public int execute() {
		if (createdComponent != null) {
			context.addComponent(createdComponent);
			ComponentFactory.restoreComponent(createdComponent);
		} else {
			switch (componentType) {
			case INPUT_PIN:
				createdComponent = ComponentFactory.createInputPin();
				break;
			case OUTPUT_PIN:
				createdComponent = ComponentFactory.createOutputPin();
				break;
			case BRANCH:
				// there are a lot of ways a Branch may fail to be created...
				// don't look at this case too much

				Component in = context.getComponent(Integer.valueOf(requirements.get("in id").value()));
				Component out = context.getComponent(Integer.valueOf(requirements.get("out id").value()));

				// catch null here for better error message
				if ((in == null) || (out == null)) {
					String ID1 = requirements.get("in id").value();
					String ID2 = requirements.get("out id").value();
					boolean same = ID1.equals(ID2);
					boolean both = ((in == null) && (out == null));

					String s = both && !same ? "s" : "";
					String firstID = (in == null) ? ID1 : ID2;
					String andSecondID = both && !same ? " and " + ID2 : "";

					context.error("Component%s with ID%s %s%s not found", s, s, firstID, andSecondID);
					return 1;
				}

				try {
					createdComponent = ComponentFactory.connectComponents(
							in,
							Integer.valueOf(requirements.get("in index").value()),
							out,
							Integer.valueOf(requirements.get("out index").value()));
				} catch (MalformedBranchException e) {
					// catch invalid Component type here
					context.error(e.getMessage());
					return 2;
				} catch (InvalidIndexException e) {
					// catch invalid index here. this can't be a MalformedBranchException because
					// index information would be lost.

					// also don't look at this too much

					// exception:
					// 	Invalid index `i` for component of type [`component-gate`: `in`-`out` (UID: `id`)].
					// 	Invalid index `i` for component of type [`component-pin`: (UID: `id`)].
					// user:
					//	Invalid index `i` for `component` (ID=`id`) with `in` input(s) and `out` output(s)
					// input pins have in=0, out=1, output pins have in=1, out=0
					Pattern p = Pattern.compile(
							"Invalid index (\\d+) for component of type \\(?\\[(.*?): (?:(\\d+)-(\\d+) )?\\(UID: (\\d+)\\)\\]\\)?");
					Matcher m = p.matcher(e.getMessage());
					m.find();
					int index = Integer.valueOf(m.group(1));
					String comp = m.group(2);
					int i1, i2;
					try {
						i1 = Integer.valueOf(m.group(3));
						i2 = Integer.valueOf(m.group(4));
					} catch (NumberFormatException e1) {
						if (comp.equals("Input Pin")) {
							i1 = 0;
							i2 = 1;
						} else if (comp.equals("Output Pin")) {
							i1 = 1;
							i2 = 0;
						} else {
							return 4;
						}
					}
					int id = Integer.valueOf(m.group(5));
					String s1 = i1 != 1 ? "s" : "";
					String s2 = i2 != 1 ? "s" : "";
					context.error("Invalid index %d for %s (ID=%d) with %d input%s and %d output%s", index, comp, id,
							i1, s1, i2, s2);
					return 3;
				}

				break;
			case GATEAND:
				createdComponent = ComponentFactory.createPrimitiveGate(GATEAND,
						Integer.valueOf(requirements.get("in count").value()));
				break;
			case GATEOR:
				createdComponent = ComponentFactory.createPrimitiveGate(GATEOR,
						Integer.valueOf(requirements.get("in count").value()));
				break;
			case GATENOT:
				createdComponent = ComponentFactory.createPrimitiveGate(GATENOT,
						Integer.valueOf(requirements.get("in count").value()));
				break;
			case GATEXOR:
				createdComponent = ComponentFactory.createPrimitiveGate(GATEXOR,
						Integer.valueOf(requirements.get("in count").value()));
				break;
			case GATE:
				return 6;
			default:
				break;
			}

			if (componentID != -1)
				createdComponent.setID(componentID);

			context.addComponent(createdComponent);
		}

		// delete the branch that may have been deleted when creating this branch
		// there can't be more than two branches deleted when creating a branch
		if (createdComponent.type() == BRANCH) {
			List<Component> ls = context.getDeletedComponents();
			if (ls.size() == 0)
				return 0;
			if (ls.size() > 1)
				return 5;
			Command d = new DeleteCommand(context);
			deleteCommands.add(d);
			d.requirements.get("id").fulfil(String.valueOf(ls.get(0).UID()));
			d.execute();
		}

		return 0;
	}

	@Override
	public int unexecute() {
		if (componentType == BRANCH) {
			foreach(deleteCommands, Command::unexecute);
			deleteCommands.clear();
		}
		ComponentFactory.destroyComponent(createdComponent);
		context.removeComponent(createdComponent);
		return 0;
	}

	@Override
	String desc() {
		return "Create " + componentType.description();
	}
}
