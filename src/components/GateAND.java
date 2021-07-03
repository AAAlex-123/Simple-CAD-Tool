package components;

/** A Primitive Gate that maps the inputs to their logical {@code and}. */
final class GateAND extends PrimitiveGate {

	/**
	 * Constructs the AND Gate with the given number of inputs and one output.
	 * 
	 * @param in the number of input pins
	 */
	GateAND(int in) {
		super(in, 1);
	}

	@Override
	public ComponentType type() {
		return ComponentType.GATEAND;
	}

	@Override
	void calculateOutput() {
		if (checkBranches()) {
			boolean res = true;

			// perform logical `and` all of the input pins
			for (int i = 0; i < inputPins.length; ++i)
				res &= inputPins[i].getActive(0);

			// and outputs the result
			outputPins[0].wake_up(res);
		}
	}
}
