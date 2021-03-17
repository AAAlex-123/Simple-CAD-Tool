package components;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.JComponent;

import exceptions.ComponentNotAccessibleException;
import exceptions.InvalidIndexException;

/**
 * A class representing a component that is connected to other Components and
 * carries a signal.
 * <p>
 * The Component's methods are package-private therefore the client may use the
 * {@link components.ComponentFactory ComponentFactory} to interact with them.
 */
public abstract class Component extends JComponent {

	boolean changeable = true;
	static int curr_id = 0;

	/** UID for this component */
	public final int UID;

	// ===== CIRCUITING =====

	// the main method: carries forward the signal is has just received
	abstract void wake_up(boolean newActive, int index, boolean prevChangeable);

	// "destroys" the component by deleting all it's branches, effectively bringing
	// its reference count down to 0
	abstract void destroy();

	// gets the active state of the specified pin
	// used for unified access inside this package
	abstract boolean getActive(int index);

	boolean toRemove() {
		return false;
	}

	void setIn(Branch b, int index) {
		throw new UnsupportedOperationException(String.format(
				"Components of type %s don't support setIn(Branch, int)",
				this.getClass().getSimpleName()));
	}

	void addOut(Branch b, int index) {
		throw new UnsupportedOperationException(String.format(
				"Components of type %s don't support addOut(Branch, int)",
				this.getClass().getSimpleName()));
	}

	void removeIn(Branch d, int index) {
		throw new UnsupportedOperationException(String.format(
				"Components of type %s don't support removeIn(Branch, int)",
				this.getClass().getSimpleName()));
	}

	void removeOut(Branch d, int index) {
		throw new UnsupportedOperationException(String.format(
				"Components of type %s don't support removeOut(Branch, int)",
				this.getClass().getSimpleName()));
	}

	// checks if the Component is not "hidden" inside another gate
	// if it is, it cannot be modified or accessed in any way.
	// This should should never throw.
	final void checkChangeable() {
		if (!changeable)
			throw new ComponentNotAccessibleException(this);
	}

	// checks the `index` (given by another object) to the `indexMax`
	// (specified by this component) for validity.
	// This should should never throw.
	final void checkIndex(int index, int indexMax) {
		if ((index < 0) || (index >= indexMax))
			throw new InvalidIndexException(this, index);
	}

	// more fancy ways to propagate a signal

	final void wake_up(boolean newActive, int index) {
		wake_up(newActive, index, changeable);
	}

	final void wake_up(boolean newActive, boolean prevChangeable) {
		wake_up(newActive, 0, prevChangeable);
	}

	final void wake_up(boolean newActive) {
		wake_up(newActive, 0, changeable);
	}

	@Override
	public String toString() {
		String str = String.format("%s", getClass().getSimpleName());
		return String.format("%s (UID: %d)", changeable ? str : "(" + str + ")", UID);
	}

	// ===== DRAWING =====

	Component() {
		this(50, 50, 50, 50, curr_id++);
	}

	Component(int x, int y) {
		this(x, y, 50, 50, curr_id++);
	}

	Component(int x, int y, int w, int h) {
		this(x, y, w, h, curr_id++);
	}

	Component(int x, int y, int w, int h, int UID) {
		this.UID = UID;
		setBounds(x, y, w, h);
		addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				setLocation(getX() + (e.getX() - (getWidth() / 2)), getY() + (e.getY() - (getHeight() / 2)));
			}
		});
	}

	@Override
	public final void paintComponent(Graphics g) {
		super.paintComponent(g);
		draw(g);
		g.setColor(Color.GREEN);
		g.drawString(getClass().getSimpleName(), 0, getHeight() / 2);
		g.drawString(String.valueOf(UID), 0, getHeight());
	}

	@Override
	public final void setLocation(int x, int y) {
		super.setLocation(x, y);
		updateOnMovement();
	}

	@Override
	public final void setSize(int w, int h) {
		super.setLocation(w, h);
		updateOnMovement();
	}

	// specifies how the Component should be drawn
	abstract void draw(Graphics g);

	// specifies how the Component should react when it's moved or resized
	abstract void updateOnMovement();

	// returns the coordinates of the branches so they know where to connect
	Point getBranchCoords(Branch b, int index) {
		throw new UnsupportedOperationException(String.format(
				"Components of type %s don't support getBranchCoords(Branch, int)", getClass().getSimpleName()));
	}
}
