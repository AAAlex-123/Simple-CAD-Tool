package components;

final class GateNOT extends PrimitiveGate {

	public GateNOT() {
		super(1, 1);
	}

	@Override
	protected void calculateOutput() {
		outputPins[0].wake_up(!inputPins[0].active);
	}
}
