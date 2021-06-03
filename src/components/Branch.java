package components;

import static components.ComponentType.BRANCH;
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

	private boolean active = false;

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
	 * 
	 * @throws MalformedBranchException in the case of an invalid connection
	 */
	Branch(Component in, int indexIn, Component out, int indexOut) throws MalformedBranchException {
		if ((in == null) || (out == null) || (in.type() == OUTPUT_PIN) || (out.type() == INPUT_PIN)
				|| (in.type() == BRANCH) || (out.type() == BRANCH))
			throw new MalformedBranchException(in, out);
		if (indexIn >= in.outCount())
			throw new MalformedBranchException(in, indexIn);
		if (indexOut >= out.inCount())
			throw new MalformedBranchException(out, indexOut);

		this.in = in;
		this.out = out;
		this.indexIn = indexIn;
		this.indexOut = indexOut;

		connect();
	}

	@Override
	public ComponentType type() {
		return ComponentType.BRANCH;
	}

	@Override
	protected void wake_up(boolean newActive, int index, boolean prevHidden) {
		checkIndex(index, inCount());

		// once hidden cannot be un-hidden
		if (hidden() && !prevHidden)
			throw new MalformedGateException(this);

		if (prevHidden)
			hideComponent();

		// repaint and propagate signal only if it's different
		if (active != newActive) {
			active = newActive;
			repaint();
			out.wake_up(active, indexOut, hidden());
		}
	}

	@Override
	protected void destroySelf() {
		checkChangeable();
		in.removeOut(this, indexIn);
		out.removeIn(this, indexOut);

		// inform `out` that there is no longer an input
		out.wake_up(false, indexOut);
	}

	@Override
	protected void restoreDeletedSelf() {
		connect();
	}

	@Override
	protected void restoreSerialisedSelf() {
	}

	@Override
	protected boolean getActive(int index) {
		checkIndex(index, inCount());
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
	protected void attachListeners() {
		attachListeners_((byte) 0);
	}

	@Override
	protected void draw(Graphics g) {
		g.setColor(active ? Color.green : Color.red);

		// draw with correct direction (as specified in the `direction` declaration)
		if (direction == 1)
			g.drawLine(5, 5, getWidth() - 5, getHeight() - 6);
		else if (direction == -1)
			g.drawLine(5, getHeight() - 6, getWidth() - 5, 5);
		else
			throw new RuntimeException("Invalid Branch direction");
	}

	/** @param g the Graphics object with which to draw the ID */
	@Override
	protected void drawID(Graphics g) {
		g.setColor(Color.BLACK);
		g.drawString(String.valueOf(UID()), (getWidth() / 2) - 4, (getHeight() / 2) + 5);
	}

	@Override
	protected void updateOnMovement() {
		// from the new coordinates calculate the Branch's start point, width and height
		// and also calculate its direction (as specified in the declaration).
		Point p1 = in.getBranchInputCoords(this, indexIn);
		Point p2 = out.getBranchOutputCoords(this, indexOut);
		direction = ((p2.x - p1.x) * (p2.y - p1.y)) > 0 ? 1 : -1;
		// components with a dimension = 0 aren't drawn and text can't be drawn on a
		// small space so add extra width/height here and remove it when drawing
		setBounds(min(p1.x, p2.x) - 5, min(p1.y, p2.y) - 5, abs(p2.x - p1.x) + 11, abs(p2.y - p1.y) + 11);
	}
}
