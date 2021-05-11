package components;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

import exceptions.ComponentNotFoundException;
import exceptions.MalformedGateException;

/** Corresponds to the {@link ComponentType#OUTPUT_PIN OUTPUT_PIN} type. */
final class OutputPin extends Component {

	private static final long serialVersionUID = 2L;

	private Branch inputBranch;
	private boolean active;

	// information about the enclosing Gate necessary for signal transmiion
	private Gate outerGate;
	private int outerGateIndex;

	/** Constructs the OuputPin */
	OutputPin() {
		active = false;
	}

	@Override
	public
	ComponentType type() {
		return ComponentType.OUTPUT_PIN;
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

			// inform the enclosing Gate that an output has changed
			if (outerGate != null)
				outerGate.outputChanged(outerGateIndex);
		}
	}

	@Override
	void destroySelf() {
		if (inputBranch != null) {
			inputBranch.destroy();
			inputBranch = null;
		}
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
	void setIn(Branch b, int index) {
		checkIndex(index, 1);
		checkChangeable();

		if (inputBranch != null) {
			// declare that the connected branches should be destroyed
			// the application should take care of destroying the Branch
			inputBranch.toBeRemoved = true;
		}

		inputBranch = b;
	}

	@Override
	void removeIn(Branch b, int index) {
		checkIndex(index, 1);
		checkChangeable();

		if ((inputBranch == b)) {
			inputBranch = null;
		} else {
			// throw new ComponentNotFoundException(b, this);

			// when a Branch is created, setIn is called
			// this component has in the new branch but
			// the old branch has out this component
			// therefore the old branch must be destroyed
			// but it will call removeIn on this component
			// but it isn't the in of this component :)
		}
	}

	/**
	 * Proper way for the client (the Factory) to get output.
	 *
	 * @return the active state of this OutputPin
	 */
	boolean getActive() {
		checkChangeable();
		return active;
	}

	/**
	 * Marks this Component as unchangeable because it's hidden in a {@code Gate}
	 * and sets the {@code gate} as the next component to be woken up. Normally
	 * should only be called during the construction of the {@code gate}.
	 *
	 * @param gate  the next component to be woken up
	 * @param index the pin's index in the gate
	 */
	void setOuterGate(Gate gate, int index) {
		if (outerGate != null)
			checkChangeable();

		changeable = false;

		outerGate = gate;
		outerGateIndex = index;
	}

	@Override
	void attachListeners() {
		attachListeners_(DRAG_KB_FOCUS);
	}

	@Override
	void draw(Graphics g) {
		// crappy drawing
		g.setColor(active ? Color.yellow : Color.black);
		g.fillRect(0, 0, getWidth() - 1, getHeight() - 1);
		g.setColor(Color.red);
		g.fillRect(0, (getHeight() / 2) - 5, 4, 4);
	}

	@Override
	void updateOnMovement() {
		// this component is moved by the user; tell branch to update
		if (inputBranch != null)
			inputBranch.updateOnMovement();
	}

	@Override
	Point getBranchCoords(Branch b, int index) {
		checkIndex(index, 1);

		if (b != inputBranch)
			throw new ComponentNotFoundException(b, this);

		return new Point(getX(), getY() + (getHeight() / 2));
	}
}
