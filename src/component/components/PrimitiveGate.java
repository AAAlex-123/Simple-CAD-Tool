package component.components;

import component.exceptions.MalformedGateException;

/**
 * The most basic of gates; they can only be implemented with transistors (or by
 * NAND Gates), so their output is artificially calculated.
 *
 * @author Alex Mandelias
 */
abstract class PrimitiveGate extends Gate {

	private static final long serialVersionUID = 3L;

	/**
	 * Constructs the Primitive Gate with the given number of input and output pins.
	 *
	 * @param inCount  the number of input pins
	 * @param outCount the number of output pins
	 */
	protected PrimitiveGate(int inCount, int outCount) {
		super(inCount, outCount);
	}

	@Override
	protected final void wake_up(boolean newActive, int indexIn, boolean prevHidden) {

		// once hidden cannot be un-hidden
		if (hidden() && !prevHidden)
			throw new MalformedGateException(this);

		if (prevHidden)
			hideComponent();

		inputPins[indexIn].wake_up(newActive); // this isn't propagated anywhere

		// each Gate subclass decides how and whether or not to produce an output
		calculateOutput();
	}

	/** calculates the output and sets the output pins to their correct values */
	abstract protected void calculateOutput();
}
