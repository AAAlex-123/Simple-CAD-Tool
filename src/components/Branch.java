package components;

import static java.lang.Math.abs;
import static java.lang.Math.min;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

// A connection between two Components
final class Branch extends Component {

	private final Component in, out;
	private final int indexIn, indexOut;
	private boolean active;

	private int direction;

	Branch(Component in, int indexIn, Component out, int indexOut) {
		this.in = in;
		this.out = out;
		this.indexIn = indexIn;
		this.indexOut = indexOut;

		in.addOut(this, indexIn);
		out.setIn(this, indexOut);
		wake_up(in.getActive(indexIn), 0);
	}

	@Override
	void wake_up(boolean newActive, int index, boolean prevChangeable) {
		checkIndex(index, 1);
		changeable = prevChangeable;

		// propagate signal only if it's different
		if (active != newActive) {
			active = newActive;
			// repaint();
			out.wake_up(active, indexOut);
		}
	}

	@Override
	void destroy() {
		checkChangeable();
		in.removeOut(this, indexOut);
		out.removeIn(this, indexIn);

		// inform out that there is no longer an input
		out.wake_up(false, indexOut);
	}

	@Override
	boolean getActive(int index) {
		checkIndex(index, 1);
		return active;
	}

	@Override
	void draw(Graphics g) {
		g.setColor(active ? Color.green : Color.red);
		if (direction == 1)
			g.drawLine(0, 0, getWidth(), getHeight());
		else
			g.drawLine(0, getHeight(), getWidth(), 0);
	}

	@Override
	void updateOnMovement() {
		Point p1 = in.getBranchCoords(this, indexIn);
		Point p2 = out.getBranchCoords(this, indexOut);
		int w = abs(p2.x - p1.x);
		int h = abs(p2.y - p1.y);
		direction = ((p2.x - p1.x) * (p2.y - p1.y)) > 0 ? 1 : -1;
		setBounds(min(p1.x, p2.x), min(p1.y, p2.y), w, (h == 0 ? 3 : h));
	}
}
