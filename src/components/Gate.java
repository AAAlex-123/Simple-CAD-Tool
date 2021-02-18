package components;

@SuppressWarnings({ "javadoc" })
public class Gate extends Component {

	// TODO: make clone-able

	protected final Pin[] in, out, innerIn, innerOut;

	public Gate(int inN, int outN) {
		in = new Pin[inN];
		out = new Pin[outN];

		innerIn = new Pin[inN];
		innerOut = new Pin[outN];

		for (int i = 0; i < in.length; ++i) {
			in[i] = new Pin();
			in[i].setOut(this);
			innerIn[i] = new Pin();
			innerIn[i].setIn(in[i]);
		}

		for (int i = 0; i < out.length; ++i) {
			out[i] = new Pin();
			innerOut[i] = new Pin();
			out[i].setIn(innerOut[i]);
			innerOut[i].setOut(out[i]);
		}
	}

	// TODO: lmao fix
	public void clear() {

		for (int i = 0; i < in.length; ++i) {
			in[i] = new Pin();
			in[i].setOut(this);
			innerIn[i].setIn(in[i]);
		}

		for (int i = 0; i < out.length; ++i) {
			out[i] = new Pin();
			out[i].setIn(innerOut[i]);
			innerOut[i].setOut(out[i]);
		}
	}

	public Gate(Pin[] in, Pin[] out) {
		this.in = new Pin[in.length];
		this.out = new Pin[out.length];
		innerIn = in;
		innerOut = out;

		for (int i = 0; i < in.length; ++i) {
			this.in[i] = new Pin();
			this.in[i].setOut(this);
			innerIn[i].setIn(this.in[i]);
		}

		for (int i = 0; i < out.length; ++i) {
			this.out[i] = new Pin();
			this.out[i].setIn(innerOut[i]);
			innerOut[i].setOut(this.out[i]);
		}
	}

	public Pin getAvailableIn() {
		for (int i = 0; i < in.length; ++i) {
			if (in[i].getIn() == null) {
				return in[i];
			}
		}
		return null;
	}

	public Pin getAvailableOut() {
		for (int i = 0; i < out.length; ++i) {
			if (out[i].getOut() == null) {
				return out[i];
			}
		}
		return null;
	}

	public Pin getIn(int index) {
		return in[index];
	}

	public void setIn(Pin c, int index) {
		in[index] = c;
		innerIn[index].setIn(in[index]);
		in[index].setOut(this);
	}

	public void setOut(Pin c, int index) {
		out[index] = c;
		innerOut[index].setIn(out[index].getIn());
		innerOut[index].setOut(out[index]);
		out[index].setIn(innerOut[index]);
	}

	@Override
	protected void wake_up() {
		for (int i = 0; i < in.length; ++i) {
			innerIn[i].wake_up();
		}
	}

	@Override
	public boolean getActive(int index) {
		return out[index].getActive();
	}

	/*
	 * private final String sprite; public int x, y, w, h; public Branch
	 * inputpins[], outputpins[];
	 *
	 * private Color color;
	 *
	 * public Gate(String sprite, int x, int y) { this.sprite = sprite;
	 * setVariablesLmao(x, y, 50, 50); setup(); }
	 *
	 * public Gate(String sprite, int x, int y, int w, int h) { this.sprite =
	 * sprite; setVariablesLmao(x, y, w, h); setup(); }
	 *
	 * private void setup() { setColor(Color.RED); addMouseListener(new
	 * myMouseAdapter()); addMouseMotionListener(new myMouseMotionAdapter());
	 * inputpins = new Branch[5]; outputpins = new Branch[2]; }
	 *
	 * public int[] getBranchCoords(Branch b) { for (int i = 0; i <
	 * inputpins.length; ++i) { if (inputpins[i] == b) { int dh = h /
	 * (inputpins.length + 1); return new int[] { getX() + 0, getY() + ((i + 1) *
	 * dh) }; } } for (int i = 0; i < outputpins.length; ++i) { if (outputpins[i] ==
	 * b) { int dh = h / (outputpins.length + 1); return new int[] { (getX() + w) -
	 * 5, getY() + ((i + 1) * dh) }; } } return null; }
	 *
	 * private void setVariablesLmao(int x, int y, int w, int h) { this.x = x;
	 * this.y = y; this.w = w; this.h = h; setBounds(x, y, w, h); }
	 *
	 * public void setCoordinates(int x, int y) { setVariablesLmao(x, y, w, h); for
	 * (Branch b : inputpins) if (b != null) b.move(); for (Branch b : outputpins)
	 * if (b != null) b.move(); }
	 *
	 * public void setDimensions(int w, int h) { setVariablesLmao(x, y, w, h); }
	 *
	 * public void setColor(Color c) { color = c; }
	 *
	 * @Override public void paintComponent(Graphics g) {
	 * System.out.printf("drawing gate:   %d-%d, %d-%d%n", x, y, w, h);
	 * super.paintComponent(g); g.drawImage(new ImageIcon(sprite).getImage(), 0, 0,
	 * w, h, color, this);
	 *
	 * int dh = h / (inputpins.length + 1); for (int i = 0; i < inputpins.length;
	 * ++i) { g.drawRect(0, (i + 1) * dh, 5, 5); } dh = h / (outputpins.length + 1);
	 * for (int i = 0; i < outputpins.length; ++i) { g.drawRect(w - 5, (i + 1) * dh,
	 * 5, 5); } }
	 *
	 * private class myMouseAdapter extends MouseAdapter {
	 *
	 * @Override public void mouseClicked(MouseEvent e) { } }
	 *
	 * private class myMouseMotionAdapter extends MouseMotionAdapter {
	 *
	 * @Override public void mouseDragged(MouseEvent e) { setCoordinates(getX() +
	 * (e.getX() - (w / 2)), getY() + (e.getY() - (h / 2))); } }
	 *
	 * // @Override // public void mouseClicked(MouseEvent e) { //
	 * System.out.println("owo"); // } // // @Override // public void
	 * mousePressed(MouseEvent e) { // System.out.println("owo"); // } //
	 * // @Override // public void mouseReleased(MouseEvent e) { //
	 * System.out.println("owo"); // } // // @Override // public void
	 * mouseEntered(MouseEvent e) { // System.out.println("owo"); // } //
	 * // @Override // public void mouseExited(MouseEvent e) { //
	 * System.out.println("owo"); // }
	 */
}
