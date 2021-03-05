package components;

// A Primitive Gate that maps the inputs to their logical and.
final class GateAND extends PrimitiveGate {

	GateAND(int in) {
		super(in, 1);
	}

	@Override
	protected void calculateOutput() {
		boolean res = true;

		for (int i = 0; i < inputPins.length; ++i) {
			res &= inputPins[i].active;
		}
		outputPins[0].wake_up(res);
	}
}
