package component.components;

/**
 * A Primitive Gate that maps the inputs to their logical {@code xor}.
 *
 * @author alexm
 */
final class GateXOR extends PrimitiveGate {

	private static final long serialVersionUID = 4L;

	private final ComponentGraphic g;

	/**
	 * Constructs the XOR Gate with the given number of inputs and one output.
	 *
	 * @param in the number of input pins
	 */
	GateXOR(int in) {
		super(in, 1);
		g = new GateXORGraphic(this);
	}

	@Override
	public ComponentType type() {
		return ComponentType.GATEXOR;
	}

	@Override
	protected void calculateOutput() {
		if (checkBranches()) {
			boolean res = false;

			// perform logical `xor` all of the input pins
			for (int i = 0; i < inputPins.length; ++i)
				res ^= inputPins[i].getActive(0);

			// and outputs the result
			outputPins[0].wake_up(res);
		}
	}

	@Override
	public ComponentGraphic getGraphics() {
		return g;
	}
}
