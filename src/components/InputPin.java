package components;

import exceptions.UnsupportedMethodException;

class InputPin extends Pin {

	public InputPin() {
		super();
	}

	@Override
	protected void wake_up() {
		getOut().wake_up();
	}

	public void setActive(boolean active) {
		this.active[0] = active;
		wake_up();
	}

	@Override
	protected void setIn(Component c) {
		throw new UnsupportedMethodException("Can't set input of InputPin");
	}

	@Override
	protected Component getIn() {
		throw new UnsupportedMethodException("Can't get input of InputPin");
	}
}
