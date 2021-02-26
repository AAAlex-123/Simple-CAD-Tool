package components;

final class Branch extends Drawable {

	private final Gate gateOut;
	private final OutputPin pinOut;
	private final int indexOut;

	private boolean hasGateAsOutput;

	Branch(Gate in, int indexIn, Gate out, int gateIndexOut) {
		gateOut = out;
		pinOut = null;
		indexOut = gateIndexOut;
		hasGateAsOutput = true;

		in.setOut(this, indexIn);
		out.setIn(this, gateIndexOut);
	}

	Branch(InputPin in, Gate out, int gateIndex) {
		gateOut = out;
		pinOut = null;
		indexOut = gateIndex;
		hasGateAsOutput = true;

		in.addOut(this);
		out.setIn(this, gateIndex);
	}

	Branch(Gate in, OutputPin out, int gateIndex) {
		gateOut = null;
		pinOut = out;
		indexOut = gateIndex;
		hasGateAsOutput = false;

		in.setOut(this, gateIndex);
		out.in = this;
	}

	void wake_up(boolean newActive) {
		if ((active != newActive) && hasGateAsOutput)
			gateOut.wake_up(active = newActive, indexOut);

		else if ((active != newActive) && !hasGateAsOutput)
			pinOut.wake_up(active = newActive);
	}
}
