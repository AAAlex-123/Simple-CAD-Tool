package components;

abstract class Component /* extends Drawable */ {

	protected final Component[] in, out;
	protected final boolean active[];

	public Component(Component[] in, Component[] out) {
		this.in = in;
		this.out = out;
		active = new boolean[out == null ? 1 : out.length];
	}

	protected abstract void wake_up();

	protected boolean getActive() {
		return active[0];
	}

	protected void setIn(Component c) {
		in[0] = c;
	}

	protected void setOut(Component c) {
		out[0] = c;
	}

	protected Component getIn() {
		return in[0];
	}

	protected Component getOut() {
		return out[0];
	}
}
