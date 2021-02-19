package components;

@SuppressWarnings("javadoc")
public class ComponentFactory {

	// PINS
	public static Pin createPin() {
		return new Pin();
	}

	public static InputPin createInputPin() {
		return new InputPin();
	}

	public static OutputPin createOutputPin() {
		return new OutputPin();
	}

	// CONNECTIONS
	public static Branch connectPins(Pin p1, Pin p2) {
		return new Branch(p1, p2);
	}

	public static Branch connectToGateInput(Pin p, Gate g) {
		return new Branch(p, g.getAvailableIn());
	}

	public static Branch connectToGateOutput(Gate g, Pin p) {
		return new Branch(g.getAvailableOut(), p);
	}

	// GATES
	public static Gate createGate(Pin[] in, Pin[] out) {
		return new Gate(in, out);
	}

	public static Gate createAND(int in) {
		return new GateAND(in);
	}

	public static Gate createOR(int in) {
		return new GateOR(in);
	}

	public static Gate createNOT() {
		return new GateNOT();
	}

	// MISCELLANEOUS
	public static void clearGate(Gate g) {
		g.clear();
	}
}
