package components;

/** A Primitive Gate that maps the inputs to their logical {@code xor}. */
final class GateXOR extends PrimitiveGate {

	private static final long serialVersionUID = 1L;

	/**
	 * Constructs the XOR Gate with the given number of inputs and one output.
	 * 
	 * @param in the number of input pins
	 */
	GateXOR(int in) {
		super(in, 1);
	}

	@Override
	public ComponentType type() {
		return ComponentType.GATEXOR;
	}

	@Override
	void calculateOutput() {
		if (checkBranches()) {
			boolean res = false;

			// perform logical `xor` all of the input pins
			for (int i = 0; i < inputPins.length; ++i)
				res ^= inputPins[i].getActive(0);

			// and outputs the result
			outputPins[0].wake_up(res);
		}
	}
}
