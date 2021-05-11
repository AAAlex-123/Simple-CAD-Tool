package components;

import static myUtil.Utility.foreach;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Vector;

import exceptions.ComponentNotFoundException;
import exceptions.MalformedGateException;

/** Corresponds to the {@link ComponentType#INPUT_PIN INPUT_PIN} type. */
final class InputPin extends Component {

	private static final long serialVersionUID = 2L;

	private final Vector<Branch> outputBranches;
	private boolean active;

	/** Constructs an {@code InputPin} */
	InputPin() {
		outputBranches = new Vector<>(1, 1);
		active = false;
	}

	@Override
	public ComponentType type() {
		return ComponentType.INPUT_PIN;
	}

	@Override
	void wake_up(boolean newActive, int index, boolean prevChangeable) {
		checkIndex(index, 1);

		// once hidden cannot be un-hidden
		if ((changeable == false) && (prevChangeable == true))
			throw new MalformedGateException(this);

		changeable = prevChangeable;

		// propagate signal only if it's different
		if (active != newActive) {
			active = newActive;
			repaint();
			foreach(outputBranches, b -> b.wake_up(active, changeable));
		}
	}

	@Override
	void destroySelf() {
		foreach(new ArrayList<>(outputBranches), Branch::destroy);
		outputBranches.clear();
	}

	@Override
	void restore() {
		toBeRemoved = false;
	}

	@Override
	boolean getActive(int index) {
		checkIndex(index, 1);
		return active;
	}

	@Override
	void addOut(Branch b, int index) {
		checkIndex(index, 1);
		checkChangeable();
		outputBranches.add(b);
	}

	@Override
	void removeOut(Branch b, int index) {
		checkIndex(index, 1);
		checkChangeable();
		if (!outputBranches.remove(b))
			throw new ComponentNotFoundException(b, this);
	}

	/**
	 * Proper way for the client (the Factory) to set input. This method, unlike
	 * wake_up, will throw when it is called on a hidden {@code InputPin}.
	 *
	 * @param newActive the new value for the active state of this InputPin
	 */
	void setActive(boolean newActive) {
		checkChangeable();
		wake_up(newActive);
	}

	/**
	 * Marks this Component as unchangeable because it's hidden in a {@code Gate}.
	 * Normally should only be called during the construction of a {@code Gate}.
	 */
	void setOuterGate() {
		checkChangeable();
		changeable = false;
	}

	@Override
	void attachListeners() {
		attachListeners_((byte) (DRAG_KB_FOCUS | ACTIVATE));
	}

	@Override
	public void draw(Graphics g) {
		// crappy drawing
		g.setColor(active ? Color.yellow : Color.black);
		g.fillRect(0, 0, getWidth() - 1, getHeight() - 1);
		g.setColor(Color.RED);
		g.fillRect(getWidth() - 5, (getHeight() / 2) - 5, 4, 4);
	}

	@Override
	public void updateOnMovement() {
		// this component is moved by the user; tell branches to update
		foreach(outputBranches, Branch::updateOnMovement);
	}

	@Override
	public Point getBranchCoords(Branch b, int index) {
		checkIndex(index, 1);

		if (!outputBranches.contains(b))
			throw new ComponentNotFoundException(b, this);

		return new Point(getX() + getWidth(), getY() + (getHeight() / 2));
	}
}
