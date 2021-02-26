package components;

final class OutputPin extends Drawable {

	Branch in;

	private Gate outerGate;
	private int outerGateIndex;

	OutputPin() {
	}

	@Override
	boolean getActive() {
		checkChangeable();
		return active;
	}

	void setOuterGate(Gate g, int index) {
		deactivate();
		outerGate = g;
		outerGateIndex = index;
	}

	void wake_up(boolean newActive) {
		if (active != newActive) {
			active = newActive;
			if (outerGate != null)
				outerGate.outputChanged(outerGateIndex);
		}
	}
}
