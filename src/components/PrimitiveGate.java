package components;

/**
 * The most basic of gates; they can only be implemented with transistors, so
 * their output is artificially calculated (for now).
 */
abstract class PrimitiveGate extends Gate {

	private static final long serialVersionUID = 1L;

	/**
	 * Constructs the Primitive Gate with the given number of input and output pins.
	 * 
	 * @param in  the number of input pins
	 * @param out the number of output pins
	 */
	PrimitiveGate(int in, int out) {
		super(in, out);
	}

	@Override
	void wake_up(boolean newActive, int indexIn, boolean prevChangeable) {
		changeable = prevChangeable;

		inputPins[indexIn].wake_up(newActive); // this isn't propagated anywhere

		// each Gate subclass decides how to check if branches are connected
		// and whether or not to produce an output
		calculateOutput();
	}

	/** calculates the output and sets the output pins to their correct values */
	abstract void calculateOutput();
}
