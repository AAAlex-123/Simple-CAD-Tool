package components;

@SuppressWarnings("javadoc")
public class GateAND extends Gate {

	//	public GateAND(Pin[] in, Pin[] out) {
	//		super(in, out);
	//	}

	public GateAND(int in, int out) {
		super(in, out);
	}

	@Override
	public void wake_up() {
		super.wake_up();
		boolean res = true;
		for (int i = 0; i < innerIn.length; ++i) {
			res &= innerIn[i].getActive();
		}
		// propagate to out
		innerOut[0].setActive(res);
		innerOut[0].out.wake_up();
	}
}
