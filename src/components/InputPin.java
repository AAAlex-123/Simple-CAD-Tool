package components;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;

import exceptions.ComponentNotFoundException;

// A Component with no input; only the client can set it
final class InputPin extends Component {

	private final Vector<Branch> outputBranches;
	private boolean active;

	InputPin() {
		outputBranches = new Vector<>(1);
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				wake_up(!active);
			}
		});
	}

	@Override
	void wake_up(boolean newActive, int index, boolean prevChangeable) {
		checkIndex(index, 1);
		changeable = prevChangeable;

		// propagate signal only if it's different
		if (active != newActive) {
			active = newActive;
			repaint();
			for (Branch b : outputBranches)
				b.wake_up(active);
		}
	}

	@Override
	void destroy() {
		for (Branch b : outputBranches)
			if (b != null) {
				// the branch should be destroyed using the appropriate factory method
				b.toBeRemoved = true;
			}
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
		b.wake_up(active);
	}

	@Override
	void removeOut(Branch b, int index) {
		checkIndex(index, 1);
		checkChangeable();
		for (Branch br : outputBranches) {
			if (br == b) {
				// the branch should be destroyed using the appropriate factory method
				outputBranches.remove(b);
				return;
			}
		}
		throw new ComponentNotFoundException(b, this);
	}

	// proper way for the client to set input
	void setActive(boolean newActive) {
		checkChangeable();
		wake_up(newActive, 0);
	}

	// marks this pin as final because it's hidden inside another gate
	void setOuterGate(Gate g, int index) {
		checkChangeable();
		changeable = false;
	}

	@Override
	public void draw(Graphics g) {
		g.setColor(active ? Color.yellow : Color.black);
		g.fillRect(0, 0, getWidth() - 1, getHeight() - 1);
	}

	@Override
	public void updateOnMovement() {
		for (Branch b : outputBranches) {
			b.updateOnMovement();
		}
	}

	@Override
	public Point getBranchCoords(Branch b, int index) {
		checkIndex(index, 1);
		for (Branch br : outputBranches) {
			if (br == b)
				return new Point(getX() + getWidth(), getY() + (getHeight() / 2));
		}
		throw new ComponentNotFoundException(b, this);
	}
}
