package components;

abstract class PrimitiveGate extends Gate {

	PrimitiveGate(int in, int out) {
		super(in, out);
	}

	@Override
	protected void wake_up(boolean newActive, int indexIn, boolean prevChangeable) {
		changeable = prevChangeable;
		inputPins[indexIn].active = newActive;
		calculateOutput();
	}

	protected abstract void calculateOutput();
}
