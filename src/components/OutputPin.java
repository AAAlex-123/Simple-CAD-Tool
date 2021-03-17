package components;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

import exceptions.ComponentNotFoundException;

// A Component with no output; only the client can get it.
final class OutputPin extends Component {

	private Branch inputBranch;
	private boolean active;

	private Gate outerGate;
	private int outerGateIndex;

	OutputPin() {
	}

	@Override
	void wake_up(boolean newActive, int index, boolean prevChangeable) {
		checkIndex(index, 1);
		changeable = prevChangeable;

		// propagate signal only if it's different
		if (active != newActive) {
			active = newActive;
			repaint();
			if (outerGate != null)
				outerGate.outputChanged(outerGateIndex);
		}
	}

	@Override
	void destroy() {
		if (inputBranch != null) {
			// the branch should be destroyed using the appropriate factory method
			inputBranch.toBeRemoved = true;
		}
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
			// the branch should be destroyed using the appropriate factory method
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

	// proper way for the client (the Factory) to get output
	boolean getActive() {
		checkChangeable();
		return active;
	}

	// sets the next Component to be woken up and
	// marks this pin as final because it's hidden inside another gate
	void setOuterGate(Gate g, int index) {
		changeable = false;
		outerGate = g;
		outerGateIndex = index;
	}

	@Override
	void draw(Graphics g) {
		g.setColor(active ? Color.yellow : Color.black);
		g.fillRect(0, 0, getWidth() - 1, getHeight() - 1);
	}

	@Override
	void updateOnMovement() {
		if (inputBranch != null)
			inputBranch.updateOnMovement();
	}

	@Override
	Point getBranchCoords(Branch b, int index) {
		checkIndex(index, 1);
		if (b == inputBranch)
			return new Point(getX(), getY() + (getHeight() / 2));
		throw new ComponentNotFoundException(b, this);
	}
}
