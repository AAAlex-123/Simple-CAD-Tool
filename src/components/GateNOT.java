package components;

class GateNOT extends PrimitiveGate {

	public GateNOT() {
		super(1, 1);
	}

	@Override
	protected boolean calculateOutput() {
		return !getIn().getActive();
	}
}
