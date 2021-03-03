package components;

import java.util.Vector;

import exceptions.ComponentNotFoundException;

final class InputPin extends Component {

	private final Vector<Branch> outputBranches;

	InputPin() {
		outputBranches = new Vector<>(1);
	}

	@Override
	void wake_up(boolean newActive, int index, boolean prevChangeable) {
		checkIndex(index, 1);
		changeable = prevChangeable;
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
		if (outputBranches.get(0) != b)
			throw new ComponentNotFoundException(b);

		outputBranches.remove(0);
	}

	void setActive(boolean newActive) {
		checkChangeable();
		wake_up(newActive, 0);
	}

	void setOuterGate(Gate g, int index) {
		changeable = false;
	}
}
