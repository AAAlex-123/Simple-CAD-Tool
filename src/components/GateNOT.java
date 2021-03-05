package components;

// A Primitive Gate that maps the input to its logical not.
final class GateNOT extends PrimitiveGate {

	public GateNOT() {
		super(1, 1);
	}

	@Override
	protected void calculateOutput() {
		boolean res = !inputPins[0].active;
		outputPins[0].wake_up(res);
	}
}
