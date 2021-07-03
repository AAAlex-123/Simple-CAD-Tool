package components;

/** A Primitive Gate that maps the inputs to their logical {@code or}. */
final class GateOR extends PrimitiveGate {

	private static final long serialVersionUID = 1L;

	/**
	 * Constructs the OR Gate with the given number of inputs and one output.
	 * 
	 * @param in the number of input pins
	 */
	GateOR(int in) {
		super(in, 1);
	}

	@Override
	public ComponentType type() {
		return ComponentType.GATEOR;
	}

	@Override
	void calculateOutput() {
		if (checkBranches()) {
			boolean res = false;

			// perform logical `or` all of the input pins
			for (int i = 0; i < inputPins.length; ++i)
				res |= inputPins[i].getActive(0);

			// and outputs the result
			outputPins[0].wake_up(res);
		}
	}
}
