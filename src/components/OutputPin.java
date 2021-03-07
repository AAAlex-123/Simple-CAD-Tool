package components;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

import exceptions.ComponentNotFoundException;

// A Component with no output; only the client can get it.
final class OutputPin extends Component {

	private Branch inputBranch;

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
	void setIn(Branch b, int index) {
		checkIndex(index, 1);
		checkChangeable();
		inputBranch = b;
		wake_up(inputBranch.active, index);
	}

	@Override
	void removeIn(Branch b, int index) {
		checkIndex(index, 1);
		checkChangeable();
		if (inputBranch != b)
			throw new ComponentNotFoundException(b);

		inputBranch = null;
	}

	// proper way for the client to get output
	boolean getActive() {
		checkChangeable();
		return active;
	}

	// sets the next Component to be woken up
	// and marks this pin as final because it's hidden inside another gate
	void setOuterGate(Gate g, int index) {
		changeable = false;
		outerGate = g;
		outerGateIndex = index;
	}

	@Override
	void draw(Graphics g) {
		g.setColor(active ? Color.yellow : Color.black);
		g.fillRect(0, 0, getWidth() - 1, getHeight() - 1);
		g.setColor(Color.red);
		g.drawString("OUT", getWidth() / 2, getHeight() / 2);
	}

	@Override
	void updateOnMovement() {
		inputBranch.updateOnMovement();
	}

	@Override
	Point getBranchCoords(Branch b, int index) {
		checkIndex(index, 1);
		if (b != inputBranch)
			throw new RuntimeException("output aaaaaaaaa");
		return new Point(getX(), getY() + (getHeight() / 2));
	}
}
