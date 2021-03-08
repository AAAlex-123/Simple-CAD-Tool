package components;

// The most basic of gates; they can only be implemented with
// transistors, so their output is artificially simulated (for now).
abstract class PrimitiveGate extends Gate {

	PrimitiveGate(int in, int out) {
		super(in, out);
	}

	@Override
	void wake_up(boolean newActive, int indexIn, boolean prevChangeable) {
		changeable = prevChangeable;
		inputPins[indexIn].active = newActive;
		calculateOutput();
	}

	// calculates and sets the output pins to their correct values
	abstract void calculateOutput();
}
