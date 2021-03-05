package components;

import java.util.Vector;

import exceptions.ComponentNotFoundException;

// A Component with no input; only the client can set it.
final class InputPin extends Component {

	private final Vector<Branch> outputBranches;

	InputPin() {
		outputBranches = new Vector<>(1);
	}

	@Override
	void wake_up(boolean newActive, int index, boolean prevChangeable) {
		checkIndex(index, 1);
		changeable = prevChangeable;

		// propagate signal only if it's different
		if (active != newActive) {
			active = newActive;
			for (Branch b : outputBranches)
				b.wake_up(active);
		}
	}

	@Override
	void addOut(Branch b, int index) {
		checkIndex(index, 1);
		checkChangeable();
		outputBranches.add(b);
		b.wake_up(active);
	}

	@Override
	void removeOut(Branch b, int index) {
		checkIndex(index, 1);
		checkChangeable();
		for (Branch br : outputBranches) {
			if (br == b) {
				outputBranches.remove(b);
				return;
			}
		}
		throw new ComponentNotFoundException(b);
	}

	// proper way for the client to set input
	void setActive(boolean newActive) {
		checkChangeable();
		wake_up(newActive, 0);
	}

	// mark this pin as final because it's hidden inside another gate
	void setOuterGate(Gate g, int index) {
		checkChangeable();
		changeable = false;
	}
}
