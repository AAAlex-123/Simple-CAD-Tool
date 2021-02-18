package components;

@SuppressWarnings("javadoc")
public class InputPin extends Pin {

	public InputPin() {
		super();
	}

	public InputPin(boolean active) {
		super(active);
	}

	@Override
	public void wake_up() {
		out.wake_up();
	}

	@Override
	public void setActive(boolean active) {
		this.active = active;
	}
}
