package components;

import java.util.Vector;

final class InputPin extends Drawable {

	private final Vector<Branch> outs;

	InputPin() {
		outs = new Vector<>();
	}

	void wake_up(boolean newActive) {
		active = newActive;
		for (Branch b : outs)
			b.wake_up(active);
	}

	@Override
	void setActive(boolean newActive) {
		checkChangeable();
		wake_up(newActive);
	}

	@Override
	boolean getActive() {
		checkChangeable();
		return active;
	}

	void addOut(Branch b) {
		checkChangeable();
		outs.add(b);
	}
}
