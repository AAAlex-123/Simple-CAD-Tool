package component.components;

import component.ComponentType;

/**
 * Corresponds to the {@link ComponentType#GATEOR GATEOR} type.
 *
 * @author Alex Mandelias
 */
final class GateOR extends PrimitiveGate {

	private static final long serialVersionUID = 4L;

	/**
	 * Constructs the OR Gate with the given number of inputs and one output.
	 *
	 * @param inCount the number of input pins
	 */
	protected GateOR(int inCount) {
		super(inCount, 1);
	}

	@Override
	public ComponentType type() {
		return ComponentType.GATEOR;
	}

	@Override
	protected void calculateOutput() {
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
