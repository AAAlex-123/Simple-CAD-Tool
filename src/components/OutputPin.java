package components;

import exceptions.UnsupportedMethodException;

class OutputPin extends Pin {

	public OutputPin() {
		super();
	}

	@Override
	protected void wake_up() {
		active[0] = getIn().getActive();
	}

	@Override
	protected void setOut(Component c) {
		throw new UnsupportedMethodException("Can't set output of OutputPin");
	}

	@Override
	protected Component getOut() {
		throw new UnsupportedMethodException("Can't set output of OutputPin");
	}
}
