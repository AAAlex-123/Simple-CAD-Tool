package components;

abstract class PrimitiveGate extends Gate {

	public PrimitiveGate(int in, int out) {
		super(in, out);
	}

	@Override
	public void wake_up() {
		innerOut[0].active[0] = calculateOutput();
		getOut().wake_up();
	}

	protected abstract boolean calculateOutput();
}