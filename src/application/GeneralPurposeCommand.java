package application;

import java.util.function.Consumer;

import components.Component;

final class GeneralPurposeCommand extends Command {

	private final Consumer<Component> cons;

	public GeneralPurposeCommand(Application app, Requirements<String> reqs, Consumer<Component> consumer) {
		super(app);

		requirements = reqs;
		cons = consumer;
	}

	@Override
	public int execute() {
		if (!requirements.fulfilled())
			return 1;
		cons.accept(context.getComponent(Integer.valueOf(requirements.get("component").value())));
		return 0;
	}

	@Override
	public int unexecute() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	Command myclone() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	String desc() {
		// TODO Auto-generated method stub
		return null;
	}
}
