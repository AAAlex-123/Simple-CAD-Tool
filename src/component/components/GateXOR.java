package component.components;

import component.ComponentType;

/**
 * Corresponds to the {@link ComponentType#GATEXOR GATEXOR} type.
 *
 * @author Alex Mandelias
 */
final class GateXOR extends PrimitiveGate {

	private static final long serialVersionUID = 4L;

	/**
	 * Constructs the XOR Gate with the given number of inputs and one output.
	 *
	 * @param inCount the number of input pins
	 */
	protected GateXOR(int inCount) {
		super(inCount, 1);
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
				res ^= inputPins[i].getActiveOut(0);

			// and outputs the result
			outputPins[0].wake_up(res);
		}
	}
}
