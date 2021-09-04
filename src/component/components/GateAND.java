package component.components;

import component.ComponentType;

/**
 * Corresponds to the {@link ComponentType#GATEAND GATEAND} type.
 *
 * @author Alex Mandelias
 */
final class GateAND extends PrimitiveGate {

	private static final long serialVersionUID = 4L;

	/**
	 * Constructs the AND Gate with the given number of inputs and one output.
	 *
	 * @param inCount the number of input pins
	 */
	protected GateAND(int inCount) {
		super(inCount, 1);
	}

	@Override
	public ComponentType type() {
		return ComponentType.GATEAND;
	}

	@Override
	protected void calculateOutput() {
		if (checkBranches()) {
			boolean res = true;

			// perform logical `and` all of the input pins
			for (int i = 0; i < inputPins.length; ++i)
				res &= inputPins[i].getActiveOut(0);

			// and outputs the result
			outputPins[0].wake_up(res);
		}
	}
}
