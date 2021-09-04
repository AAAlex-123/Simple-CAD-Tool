package component.components;

import component.ComponentType;

/**
 * Corresponds to the {@link ComponentType#GATENOT GATENOT} type.
 * <p>
 * In this circuit there may be multiple input pins. Each of them is mapped to
 * its logical {@code not} and output at the output pin at the same index,
 * provided that a Branch is connected to that input pin.
 *
 * @author Alex Mandelias
 */
final class GateNOT extends PrimitiveGate {

	private static final long serialVersionUID = 4L;

	/**
	 * Constructs the NOT Gate with the given number of inputs and outputs.
	 *
	 * @param count the number of pairs of pins.
	 */
	protected GateNOT(int count) {
		super(count, count);
	}

	@Override
	public ComponentType type() {
		return ComponentType.GATENOT;
	}

	@Override
	protected void calculateOutput() {
		for (int i = 0; i < inputPins.length; ++i) {
			// for each individual NOT gate check if a Branch is connected
			// and if it is, produce the correct output
			if (checkBranch(i)) {
				boolean res = !inputPins[i].getActiveOut(0);
				outputPins[i].wake_up(res);
			}
		}
	}
}
