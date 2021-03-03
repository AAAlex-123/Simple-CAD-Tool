package components;

import exceptions.ComponentNotFoundException;

final class OutputPin extends Component {

	private Branch inputBranch;

	private Gate outerGate;
	private int outerGateIndex;

	OutputPin() {
	}

	@Override
	void wake_up(boolean newActive, int index, boolean prevChangeable) {
		checkIndex(index, 1);
		changeable = prevChangeable;
		if (active != newActive) {
			active = newActive;
			if (outerGate != null)
				outerGate.outputChanged(outerGateIndex);
		}
	}

	@Override
	void setIn(Branch b, int index) {
		checkIndex(index, 1);
		checkChangeable();
		inputBranch = b;
		wake_up(inputBranch.active, index);
	}

	@Override
	void removeIn(Branch b, int index) {
		checkIndex(index, 1);
		checkChangeable();
		if (inputBranch != b)
			throw new ComponentNotFoundException(b);

		inputBranch = null;
	}

	boolean getActive() {
		checkChangeable();
		return active;
	}

	void setOuterGate(Gate g, int index) {
		changeable = false;
		outerGate = g;
		outerGateIndex = index;
	}
}
