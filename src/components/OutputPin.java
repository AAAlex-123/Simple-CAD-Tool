package components;

import exceptions.ComponentNotFoundException;

// A Component with no output; only the client can get it.
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

		// propagate signal only if it's different
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

	// proper way for the client to get output
	boolean getActive() {
		checkChangeable();
		return active;
	}

	// sets the next Component to be woken up
	// and marks this pin as final because it's hidden inside another gate
	void setOuterGate(Gate g, int index) {
		changeable = false;
		outerGate = g;
		outerGateIndex = index;
	}
}
