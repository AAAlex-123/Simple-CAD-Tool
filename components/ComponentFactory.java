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

	public static void setActive(Drawable d, boolean active) {
		d.setActive(active);
	}

	public static boolean getActive(Drawable d) {
		return d.getActive();
	}

	// CONNECTIONS
	public static Branch connectGates(Drawable in, int indexIn, Drawable out, int indexOut) {
		return connectGates((Gate) in, indexIn, (Gate) out, indexOut);
	}

	public static Branch connectGates(Gate in, int indexIn, Gate out, int indexOut) {
		return new Branch(in, indexIn, out, indexOut);
	}

	public static Branch connectToGateInput(Drawable g, Drawable p, int index) {
		return connectToGateInput((Gate) g, (InputPin) p, index);
	}

	public static Branch connectToGateInput(Gate g, InputPin p, int index) {
		return new Branch(p, g, index);
	}

	public static Branch connectToGateOutput(Drawable g, Drawable p, int index) {
		return connectToGateOutput((Gate) g, (OutputPin) p, index);
	}

	public static Branch connectToGateOutput(Gate g, OutputPin p, int index) {
		return new Branch(g, p, index);
	}

	// GATES
	public static Gate createGate(InputPin[] in, OutputPin[] out) {
		return new Gate(in, out);
	}

	public static Gate createAND(int in) {
		return new GateAND(in);
	}

	public static Gate createNOT() {
		return new GateNOT();
	}


	// MISCELLANEOUS
	//	public static void clearGate(Gate g) {
	//		// g.clear();
	//	}
}
