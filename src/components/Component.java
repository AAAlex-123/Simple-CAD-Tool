package components;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
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

	private static final long serialVersionUID = 1L;

	/**
	 * Indicates whether or not this Component is hidden inside another Component. A
	 * hidden Component cannot be altered in any way. Components become hidden when
	 * a Gate is constructed and they are part of its inner circuit. This is
	 * represented by a {@code false} value for the {@code changeable} field. This
	 * property of Components will be referred to as its "hiddenness".
	 */
	boolean changeable = true;

	/**
	 * Indicates whether or not this Component should be removed. When the method
	 * {@link Component#destroy() destroy()} is called, this flag is set to true.
	 * The application is responsible to check, using {@link Component#toRemove()
	 * toRemove()}, if a Component is destroyed and must therefore be removed from
	 * the application.
	 */
	boolean toBeRemoved = false;

	/**
	 * An ever increasing UID. During construction, each Component is assigned one.
	 * Note that because some Components consist internally of other Components the
	 * UIDs assigned may jump a few numbers.
	 */
	private static int curr_id = 0;

	/**
	 * Unique ID for this Component. It is automatically assigned during
	 * construction and can be set once afterwards.
	 */
	int UID;

	/**
	 * Allows for this Component's {@link Component#UID UID} to be changed once
	 * after construction.
	 *
	 * @param newID the new ID;
	 * @return this (used to chain method calls)
	 */
	public Component setID(int newID) {
		if (UIDset)
			throw new RuntimeException("id already set");

		UID = newID;
		UIDset = true;
		return this;
	}

	/** Resets the global UID to 0 */
	public static void resetGlobalID() {
		setGlobalID(0);
	}

	/** @param newID the new value for the global UID (use cautiously) */
	public static void setGlobalID(int newID) {
		curr_id = newID;
	}

	private boolean UIDset = false;

	/**
	 * Returns this Component's {@link Component#UID UID}.
	 *
	 * @return the UID
	 */
	public int UID() {
		return UID;
	}

	// ===== CIRCUITING =====

	/**
	 * Returns the Component's type as described by {@link ComponentType}.
	 *
	 * @return the type
	 */
	public abstract ComponentType type();

	/**
	 * Returns whether or not this Component is {@link Component#changeable hidden}
	 * inside a gate.
	 *
	 * @return the Component's hiddenness
	 */
	public final boolean hidden() {
		return !changeable;
	}

	/**
	 * The core of the library: all Components are able to propagate a received
	 * signal to other components.
	 * <p>
	 * Specifically, when the signal (a boolean value) changes to a
	 * {@code newActive} value in the Component's input at a specific {@code index},
	 * the Component may propagate it to all of the Components it is connected to.
	 * The {@code hiddenness} of the previous Component is also propagated so that a
	 * chain of Components can all update their hiddenness when one is altered.
	 *
	 * @param newActive      the new signal received
	 * @param index          the index at which it was received
	 * @param prevChangeable the hiddenness of the previous component
	 */
	abstract void wake_up(boolean newActive, int index, boolean prevChangeable);

	/**
	 * Specifies what this Component should do when it is destroyed. Subclasses
	 * specify how Components that are connected to this Component should react.
	 * <p>
	 * In principle, a destroyed Component isn't referenced by any other Component
	 * and when the application removes it, it should be garbage collected.
	 *
	 * @see Component#destroySelf()
	 * @see Component#toBeRemoved
	 */
	final void destroy() {
		toBeRemoved = true;
		destroySelf();
	}

	/** Specifies how each Component should destroy itself. */
	abstract void destroySelf();

	/**
	 * Returns whether or not the application should remove this Component.
	 *
	 * @return true if the Component should be removed, false otherwise.
	 */
	final boolean toRemove() {
		return toBeRemoved;
	}

	/** Restores the state of the destroyed Component so that it can function */
	abstract void restore();

	/**
	 * Returns the active state of the Component's specified pin. Used for unified
	 * access inside this package
	 *
	 * @param index the Component's Pin index
	 * @return true or false, active or not
	 */
	abstract boolean getActive(int index);

	/**
	 * Checks if this Component is not hidden inside another gate. If it is, it
	 * cannot be modified or accessed in any way and this method throws.
	 * <p>
	 * This method should be called in every method that changes a Component (e.g. a
	 * method that creates a connection). If everything is designed correctly, this
	 * method should never throw.
	 */
	final void checkChangeable() {
		if (!changeable)
			throw new ComponentNotAccessibleException(this);
	}

	/**
	 * Checks if the index given (by another component wishing to access this
	 * component) does not exceed its maximum value (specified by this component).
	 * <p>
	 * This method should be called in every method that is index-sensitive. If
	 * everything is designed correctly, this method should never throw.
	 *
	 * @param index    the index to check
	 * @param indexMax its maximum value
	 */
	final void checkIndex(int index, int indexMax) {
		if ((index < 0) || (index >= indexMax))
			throw new InvalidIndexException(this, index);
	}

	// NOTE: the following 4 methods are only called internally by the Branches:
	// - set/add are called by Branch.connect()
	// - remove in/out are called by Branch.destroy()

	/**
	 * Sets the Branch as the Component's Input.
	 *
	 * @param b     the Branch
	 * @param index the index the branch should connect to
	 */
	void setIn(Branch b, int index) {
		throw new UnsupportedOperationException(String.format(
				"Components of type %s don't support setIn(Branch, int)",
				this.getClass().getSimpleName()));
	}

	/**
	 * Adds the Branch to the Component's Outputs.
	 *
	 * @param b     the Branch
	 * @param index the index the branch should connect to
	 */
	void addOut(Branch b, int index) {
		throw new UnsupportedOperationException(String.format(
				"Components of type %s don't support addOut(Branch, int)",
				this.getClass().getSimpleName()));
	}

	/**
	 * Removes the Branch from the Component's Input.
	 *
	 * @param b     the Branch
	 * @param index the index the branch is connect to
	 */
	void removeIn(Branch b, int index) {
		throw new UnsupportedOperationException(String.format(
				"Components of type %s don't support removeIn(Branch, int)",
				this.getClass().getSimpleName()));
	}

	/**
	 * Removes the Branch from the Component's Output.
	 *
	 * @param b     the Branch
	 * @param index the index the branch is connect to
	 */
	void removeOut(Branch b, int index) {
		throw new UnsupportedOperationException(String.format(
				"Components of type %s don't support removeOut(Branch, int)",
				this.getClass().getSimpleName()));
	}

	/**
	 * Same as wake_up, but hiddenness is assumed to be the same (e.g. when
	 * connecting a Branch, hiddenness can't have changed)
	 *
	 * @param newActive the new signal received
	 * @param index     the index at which it was received
	 */
	final void wake_up(boolean newActive, int index) {
		wake_up(newActive, index, changeable);
	}

	/**
	 * Same as wake_up, but index is assumed to be 0 (e.g. Branch always 0)
	 *
	 * @param newActive      the new signal received
	 * @param prevChangeable the 'hiddenness' of the previous component
	 */
	final void wake_up(boolean newActive, boolean prevChangeable) {
		wake_up(newActive, 0, prevChangeable);
	}

	/**
	 * Same as wake_up, but both index and changeable are both assumed to be 0 and
	 * the same (e.g. see two above e.g.)
	 *
	 * @param newActive the new signal received
	 */
	final void wake_up(boolean newActive) {
		wake_up(newActive, 0, changeable);
	}

	@Override
	public String toString() {
		// [<component name>: (UID: <UID>)], enclosed in '()' if hidden
		String descr = type().description();
		return String.format("[%s: (UID: %d)]", changeable ? descr : "(" + descr + ")", UID);
	}

	// ===== DRAWING =====

	private boolean focused;

	/** bit to make component dragable */
	static final byte DRAG = 0x01;
	/** bit to make component moveable */
	static final byte KEYBOARD = 0x02;
	/** bit to make component activate on click */
	static final byte ACTIVATE = 0x04;
	/** bit to make component get keyboard focus when clicked */
	static final byte /* CLICK_TO_ */ FOCUS = 0x08;

	/** Default constructor (only constructor used at the moment lmao) */
	Component() {
		this(50, 50, 50, 50, curr_id++);
	}

	/**
	 * Constructs a Component with specific ID, probably will never be used.
	 *
	 * @param UID the component's ID
	 */
	Component(int UID) {
		this(50, 50, 50, 50, UID);
	}

	/**
	 * Master constructor specifying location, dimensions and ID,
	 *
	 * @param x   the Component's X position
	 * @param y   the Component's Y position
	 * @param w   the Component's width
	 * @param h   the Component's height
	 * @param UID the Component's ID
	 */
	Component(int x, int y, int w, int h, int UID) {
		this.UID = UID;
		focused = false;
		setBounds(x, y, w, h);
		attachListeners();
	}

	@Override
	public final void paintComponent(Graphics g) {
		super.paintComponent(g);

		// allow each component to draw itself
		draw(g);

		// do some other questionable drawing
		g.setColor(changeable ? Color.GREEN : Color.ORANGE);
		g.drawString(getClass().getSimpleName(), 0, getHeight() / 2);
		g.drawString(String.valueOf(UID), 0, getHeight());

		if (focused) {
			g.setColor(Color.CYAN);
			g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
		}
	}

	@Override
	public final void setLocation(int x, int y) {
		// use swing's setLocation and also update related components
		super.setLocation(x, y);
		updateOnMovement();
	}

	@Override
	public final void setSize(int w, int h) {
		// use swing's setSize and also update related components
		super.setSize(w, h);
		updateOnMovement();
	}

	private void move(KeyEvent e) {
		if (hasFocus()) {
			int dx = 0, dy = 0;
			switch (e.getKeyCode()) {
			case KeyEvent.VK_LEFT:
				dx = -7;
				break;
			case KeyEvent.VK_RIGHT:
				dx = 7;
				break;
			case KeyEvent.VK_UP:
				dy = -7;
				break;
			case KeyEvent.VK_DOWN:
				dy = 7;
				break;
			default:
				break;
			}

			if (e.isControlDown()) {
				dx *= 5;
				dy *= 5;
			}
			setLocation(getX() + dx, getY() + dy);
		}
	}

	/**
	 * Each Component specifies which listeners should be attached. This method may
	 * (and should) be defined to call the {@link Component#attachListeners_(byte)
	 * attachListeners_(byte)} method with the appropriate byte.
	 */
	abstract void attachListeners();

	/**
	 * Attaches listeners to this Component based on the {@code flags}.
	 * 
	 * @param flags a byte whose bits correspond to different listeners
	 */
	final void attachListeners_(byte flags) {
		if ((flags & DRAG) != 0)
			addDragListener();
		if ((flags & KEYBOARD) != 0)
			addKeyboardListener();
		if ((flags & ACTIVATE) != 0)
			addActivateListener();
		if ((flags & FOCUS) != 0)
			addFocusListener();
	}

	private void addDragListener() {
		addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				setLocation(getX() + (e.getX() - (getWidth() / 2)), getY() + (e.getY() - (getHeight() / 2)));
				e.consume();
			}
		});
	}

	private void addKeyboardListener() {
		addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				switch (e.getKeyCode()) {
				case KeyEvent.VK_LEFT:
				case KeyEvent.VK_RIGHT:
				case KeyEvent.VK_UP:
				case KeyEvent.VK_DOWN:
					move(e);
					break;
				case KeyEvent.VK_SPACE:
					if (type() == ComponentType.INPUT_PIN)
						((InputPin) Component.this).setActive(!getActive(0));
					break;
				default:
					break;

				}
			}
		});
	}

	private void addActivateListener() {
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				wake_up(!getActive(0));
				e.consume();
			}
		});
	}

	private void addFocusListener() {
		addFocusListener(new FocusListener() {
			@Override
			public void focusLost(FocusEvent e) {
				focused = false;
				repaint();
			}

			@Override
			public void focusGained(FocusEvent e) {
				focused = true;
				repaint();
			}
		});
	}

	/**
	 * Each component specifies how it's drawn.
	 *
	 * @param g the Graphics object necessary to draw
	 */
	abstract void draw(Graphics g);

	/** Specifies how the Component should react when it's moved or resized. */
	abstract void updateOnMovement();

	/**
	 * Returns information about the location of the, imaginary, pins on the
	 * component so the Branch knows precisely where to connect.
	 *
	 * @param b     the Branch (used for safety, only index is necessary)
	 * @param index the Branch's index (used for safety, only Branch is necessary)
	 * @return a Point with the coordinates of the Branch
	 */
	Point getBranchCoords(Branch b, int index) {
		throw new UnsupportedOperationException(String.format(
				"Components of type %s don't support getBranchCoords(Branch, int)", getClass().getSimpleName()));
	}
}
