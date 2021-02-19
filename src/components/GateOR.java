package components;

class GateOR extends PrimitiveGate {

	public GateOR(int in) {
		super(in, 1);
	}

	@Override
	protected boolean calculateOutput() {
		boolean res = false;

		for (int i = 0; i < innerIn.length; ++i) {
			res |= in[i].getActive();
		}

		return res;
	}
}
