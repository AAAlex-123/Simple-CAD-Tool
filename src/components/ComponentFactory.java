package components;

@SuppressWarnings("javadoc")
final public class ComponentFactory {

	// PINS
	public static InputPin createInputPin() {
		return new InputPin();
	}

	public static OutputPin createOutputPin() {
		return new OutputPin();
	}

	public static void setActive(Drawable inputPin, boolean active) {
		((InputPin) inputPin).setActive(active);
	}

	public static boolean getActive(Drawable outputPin) {
		return ((OutputPin) outputPin).getActive();
	}

	// CONNECTIONS
	public static Branch connectGates(Drawable gateIn, int indexIn, Drawable gateOut, int indexOut) {
		return new Branch((Gate) gateIn, indexIn, (Gate) gateOut, indexOut);
	}

	public static Branch connectToGateInput(Drawable gate, Drawable inputPin, int index) {
		return new Branch((InputPin) inputPin, (Gate) gate, index);
	}

	public static Branch connectToGateOutput(Drawable gate, Drawable outputPin, int index) {
		return new Branch((Gate) gate, (OutputPin) outputPin, index);
	}

	// GATES
	public static Gate createGate(Drawable[] inputPins, Drawable[] outputPins) {
		return new Gate((InputPin[]) inputPins, (OutputPin[]) outputPins);
	}

	public static Gate createAND(int inCount) {
		return new GateAND(inCount);
	}

	public static Gate createNOT() {
		return new GateNOT();
	}

	// MISCELLANEOUS
	public static void deleteBranch(Drawable branch) {
		((Branch) branch).disconnect();
	}
}
