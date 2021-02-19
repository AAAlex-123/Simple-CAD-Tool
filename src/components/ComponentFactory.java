package components;

@SuppressWarnings("javadoc")
public class ComponentFactory {

	public static InputPin createInputPin(boolean active) {
		return new InputPin(active);
	}

	public static Branch connectPins(Pin p1, Pin p2) {
		return new Branch(p1, p2);
	}

	public static Branch connectToGateInput(Pin p, Gate g) {
		return new Branch(p, g.getAvailableIn());
	}

	public static Branch connectToGateOutput(Gate g, Pin p) {
		return new Branch(g.getAvailableOut(), p);
	}

	public static void clearGate(Gate g) {
		g.clear();
	}
}
