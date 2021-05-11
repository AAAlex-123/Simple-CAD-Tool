package components;
import static components.ComponentType.INPUT_PIN;
import static components.ComponentType.OUTPUT_PIN;
import static java.lang.Math.abs;
import static java.lang.Math.min;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

import exceptions.InvalidIndexException;
import exceptions.MalformedBranchException;
import exceptions.MalformedGateException;

/** Corresponds to the {@link ComponentType#BRANCH BRANCH} type */
final class Branch extends Component {

	private static final long serialVersionUID = 3L;

	private final Component in, out;
	private final int indexIn, indexOut;

	private boolean active;

	// graphics, 1 or -1, slope of the line
	//  1 = draw top-left to bottom-right
	// -1 = draw bottom-left to top-right
	private int direction;

	/**
	 * Constructs a {@code Branch} between two Components at the specified indexes.
	 *
	 * @param in       the Branch's input
	 * @param indexIn  the index of the pin on the {@code in} component
	 * @param out      the Branch's output
	 * @param indexOut the index of the pin on the {@code out} component
	 */
	Branch(Component in, int indexIn, Component out, int indexOut) {
		if ((in == null) || (out == null) || (in.type() == OUTPUT_PIN) || (out.type() == INPUT_PIN))
			throw new MalformedBranchException(in, out);

		this.in = in;
		this.out = out;
		this.indexIn = indexIn;
		this.indexOut = indexOut;

		active = false;
		toBeRemoved = false;

		connect();
	}

	@Override
	public ComponentType type() {
		return ComponentType.BRANCH;
	}

	@Override
	void wake_up(boolean newActive, int index, boolean prevChangeable) {
		checkIndex(index, 1);

		// once hidden cannot be un-hidden
		if ((changeable == false) && (prevChangeable == true))
			throw new MalformedGateException(this);

		changeable = prevChangeable;

		// repaint and propagate signal only if it's different
		if (active != newActive) {
			active = newActive;
			repaint();
			out.wake_up(active, indexOut, changeable);
		}
	}

	@Override
	void destroySelf() {
		checkChangeable();
		in.removeOut(this, indexIn);
		out.removeIn(this, indexOut);

		// inform `out` that there is no longer an input
		out.wake_up(false, indexOut);
	}

	@Override
	void restore() {
		toBeRemoved = false;
		connect();
	}

	@Override
	boolean getActive(int index) {
		checkIndex(index, 1);
		return active;
	}

	/**
	 * Informs its {@code in} and {@code out} Components that this Branch connects
	 * to them. This method is called once during construction and can be called
	 * again after a branch has been destroyed to reconnect it.
	 */
	void connect() {
		try {
			in.addOut(this, indexIn);
			out.setIn(this, indexOut);
		} catch (UnsupportedOperationException | InvalidIndexException e) {
			// don't leave hanging connections
			destroy();
			throw e;
		}

		// set active to false so `wake_up` always propagates
		active = !in.getActive(indexIn);
		wake_up(in.getActive(indexIn));

		// update the Branch's graphics
		updateOnMovement();
	}

	@Override
	void attachListeners() {
		attachListeners_((byte) 0);
	}

	@Override
	void draw(Graphics g) {
		g.setColor(active ? Color.green : Color.red);

		// draw with correct direction (as specified in the `direction` declaration)
		if (direction == 1)
			g.drawLine(0, 0, getWidth(), getHeight());
		else if (direction == -1)
			g.drawLine(0, getHeight(), getWidth(), 0);
		else
			throw new RuntimeException("branch-draw-direction-ffs");
	}

	@Override
	void updateOnMovement() {
		// from the new coordinates calculate the Branch's start point, width and height
		// and also calculate its direction (as specified in the declaration).
		Point p1 = in.getBranchCoords(this, indexIn);
		Point p2 = out.getBranchCoords(this, indexOut);
		int w = abs(p2.x - p1.x);
		int h = abs(p2.y - p1.y);
		direction = ((p2.x - p1.x) * (p2.y - p1.y)) > 0 ? 1 : -1;
		setBounds(min(p1.x, p2.x), min(p1.y, p2.y), w, (h == 0 ? 3 : h));
	}
}
