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
 * A class representing a component that is connected to other Components,
 * carries a signal and can be drawn.
 * <p>
 * The Component's methods are package-private therefore the client may use the
 * {@link components.ComponentFactory ComponentFactory} to interact with them.
 * <p>
 * Components may be used in the context of an Application which manages when
 * they are created and deleted. This Application will be referenced throughout
 * the documentation.
 */
public abstract class Component extends JComponent implements Identifiable<String> {

	private static final long serialVersionUID = 5L;

	private String componentID;

	@Override
	public final String getID() {
		return componentID;
	}

	@Override
	public final void setID(String id) {
		componentID = id;
	}

	// ===== CIRCUITING =====

	/**
	 * Indicates whether or not this Component is hidden inside another Component. A
	 * hidden Component cannot be altered in any way, including making it not
	 * hidden. Components become hidden when a Gate is constructed and they are part
	 * of its inner circuit. This property of Components will be referred to as its
	 * "hiddenness".
	 */
	private boolean hidden = false;

	/**
	 * Indicates whether or not this Component should be removed. When the method
	 * {@link Component#destroy() destroy()} is called, this flag is set to true.
	 * The application is responsible to check, using {@link Component#toRemove()
	 * toRemove()}, if a Component is destroyed and must therefore be removed.
	 */
	protected boolean toBeRemoved = false;

	/** @return the number of incoming connections */
	protected int inCount() {
		return 1;
	}

	/** @return the number of outgoing connections */
	protected int outCount() {
		return 1;
	}

	/**
	 * Returns the Component's type, as described by {@link ComponentType}.
	 *
	 * @return the type
	 */
	public abstract ComponentType type();

	/**
	 * Returns whether or not this Component is {@link Component#hidden hidden}
	 * inside another Component.
	 *
	 * @return true if it is hidden, false otherwise
	 */
	protected final boolean hidden() {
		return hidden;
	}

	/** Marks this Component as hidden */
	protected final void hideComponent() {
		hidden = true;
	}

	/**
	 * The core of the library: all Components are able to propagate a received
	 * signal to other components.
	 * <p>
	 * Specifically, when the signal (a boolean value) changes to the
	 * {@code newActive} value in the Component's input at a specific {@code index},
	 * the Component may propagate it to all of the Components it is connected to.
	 * The {@code "hiddenness"} of the previous Component is also propagated so that
	 * a chain of Components can all update their "hiddenness" when one is altered.
	 *
	 * @param newActive  the new signal received
	 * @param index      the index at which it was received
	 * @param prevHidden the "hiddenness" of the previous component
	 */
	protected abstract void wake_up(boolean newActive, int index, boolean prevHidden);

	/**
	 * Specifies what this Component should do when it is destroyed. Subclasses
	 * specify how Components that are connected to this Component should react
	 * using the template method {@link Component#destroySelf destroySelf}.
	 * <p>
	 * In principle, a destroyed Component isn't referenced by any other Component
	 * and when the application removes it, it should be garbage collected.
	 *
	 * @see Component#toBeRemoved
	 */
	protected final void destroy() {
		toBeRemoved = true;
		destroySelf();
	}

	/** Each Component specifies how it should destroy itself. */
	protected abstract void destroySelf();

	/**
	 * Returns whether or not the application should remove this Component.
	 *
	 * @return true if the Component should be removed, false otherwise.
	 *
	 * @see Component#toBeRemoved
	 */
	final boolean toRemove() {
		return toBeRemoved;
	}

	/** Restores the state of the Component after it was destroyed */
	final void restoreDeleted() {
		restoreDeletedSelf();
		toBeRemoved = false;
		focused = false;
		requestFocus();
	}

	/** Each Component specifies how it is restored after destruction */
	protected abstract void restoreDeletedSelf();

	/** Restores the state of the Component after it was serialised */
	final void restoreSerialised() {
		restoreSerialisedSelf();
		attachListeners();
		focused = false;
		requestFocus();
	}

	/** Each Component specifies how it is restored after serialisation */
	protected abstract void restoreSerialisedSelf();

	/**
	 * Returns the active state of the Component's pin at the specified index.
	 *
	 * @param index the Component's Pin index
	 *
	 * @return true if active, false otherwise
	 */
	protected abstract boolean getActive(int index);

	/**
	 * Checks if this Component is not hidden inside another gate. If it is, it
	 * cannot be modified or accessed in any way, and this method throws.
	 * <p>
	 * This method should be called in every method that changes a Component (e.g. a
	 * method that creates a connection). If everything is designed correctly, this
	 * method should never throw.
	 */
	protected final void checkChangeable() {
		if (hidden())
			throw new ComponentNotAccessibleException(this);
	}

	/**
	 * Checks if the {@code index} given by another component wishing to access this
	 * component does not exceed {@code indexMax} (specified by this component).
	 * <p>
	 * This method should be called in every method that is index-sensitive. If
	 * everything is designed correctly, this method should never throw.
	 *
	 * @param index    the index to check
	 * @param indexMax its maximum value
	 */
	protected final void checkIndex(int index, int indexMax) {
		if ((index < 0) || (index >= indexMax))
			throw new InvalidIndexException(this, index);
	}

	// NOTE: the following 4 methods are only called internally by the Branches:
	// - set/add are called by Branch.connect()
	// - remove in/out are called by Branch.destroy()

	/**
	 * Sets the {@code branch} as the Component's Input at {@code index}.
	 *
	 * @param branch the Branch
	 * @param index  the index the Branch should connect to
	 */
	protected void setIn(Branch branch, int index) {
		throw new UnsupportedOperationException(String.format(
				"Components of type %s don't support setIn(Branch, int)", type().description()));
	}

	/**
	 * Adds the {@code branch} to the Component's Outputs at {@code index}.
	 *
	 * @param branch the Branch
	 * @param index  the index the Branch should connect to
	 */
	protected void addOut(Branch branch, int index) {
		throw new UnsupportedOperationException(String.format(
				"Components of type %s don't support addOut(Branch, int)", type().description()));
	}

	/**
	 * Removes the {@code branch} from the Component's Input at {@code index}.
	 *
	 * @param branch the Branch
	 * @param index  the index the Branch is connected to
	 */
	protected void removeIn(Branch branch, int index) {
		throw new UnsupportedOperationException(String.format(
				"Components of type %s don't support removeIn(Branch, int)", type().description()));
	}

	/**
	 * Removes the {@code branch} from the Component's Output at {@code index}.
	 *
	 * @param branch the Branch
	 * @param index  the index the Branch is connected to
	 */
	protected void removeOut(Branch branch, int index) {
		throw new UnsupportedOperationException(String.format(
				"Components of type %s don't support removeOut(Branch, int)",
				type().description()));
	}

	/**
	 * Same as wake_up, but "hiddenness" is assumed to be the same (e.g. when
	 * connecting a Branch, "hiddenness" can't have changed)
	 *
	 * @param newActive the new signal received
	 * @param index     the index at which it was received
	 */
	protected final void wake_up(boolean newActive, int index) {
		wake_up(newActive, index, hidden());
	}

	/**
	 * Same as wake_up, but index is assumed to be 0 (e.g. for Branch it's always 0)
	 *
	 * @param newActive  the new signal received
	 * @param prevHidden the "hiddenness" of the previous component
	 */
	protected final void wake_up(boolean newActive, boolean prevHidden) {
		wake_up(newActive, 0, prevHidden);
	}

	/**
	 * Same as wake_up, but both index and "hiddenness" are both assumed to be 0 and
	 * the same (e.g. see two above e.g.)
	 *
	 * @param newActive the new signal received
	 */
	protected final void wake_up(boolean newActive) {
		wake_up(newActive, 0, hidden());
	}

	@Override
	public final String toString() {
		return String.format("%s: %d-%d, UID: %d, hidden: %s", type().description(), inCount(),
				outCount(), getID(),
				hidden());
	}

	// ===== DRAWING =====

	private static final int SIZE = 40;

	/*
	 * used instead of hasFocus() because it does not return true immediately after
	 * requestFocus() is called and therefore the user has no indication of focus.
	 */
	private boolean focused = false;

	/** bit to make component dragable, keyboard-usable and focusable */
	protected static final byte DRAG_KB_FOCUS = 0x01;
	/** bit to make component (de)activate on click */
	protected static final byte ACTIVATE      = 0x02;

	/** Default constructor */
	Component() {
		this(0, 0, Component.SIZE, Component.SIZE);
	}

	/**
	 * Constructor specifying location, dimensions and ID.
	 *
	 * @param x the Component's X position
	 * @param y the Component's Y position
	 * @param w the Component's width
	 * @param h the Component's height
	 */
	private Component(int x, int y, int w, int h) {
		setBounds(x, y, w, h);
		attachListeners();
	}

	@Override
	public final void paintComponent(Graphics g) {
		if (hidden())
			throw new RuntimeException("Hidden Components can't be drawn");

		super.paintComponent(g);
		draw(g);
		drawID(g);
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

	private void moveWithKeyboard(KeyEvent e) {
		if (hasFocus()) {
			final int d = 10, dm = 4;
			int dx = 0, dy = 0;

			// find direction
			switch (e.getKeyCode()) {
			case KeyEvent.VK_LEFT:
				dx = -d;
				break;
			case KeyEvent.VK_RIGHT:
				dx = d;
				break;
			case KeyEvent.VK_UP:
				dy = -d;
				break;
			case KeyEvent.VK_DOWN:
				dy = d;
				break;
			default:
				break;
			}

			// check for 'fast' movement
			if (e.isShiftDown()) {
				dx *= dm;
				dy *= dm;
			}

			// check for drawing area bounds
			int newx = getX(), newy = getY();
			if ((dx != 0) && ((getX() + dx) >= 0)
					&& ((getX() + dx) <= (getParent().getWidth() - getWidth())))
				newx = (int) Math.floor((getX() + dx) / (double) d) * d;
			if ((dy != 0) && ((getY() + dy) >= 0)
					&& ((getY() + dy) <= (getParent().getHeight() - getHeight())))
				newy = (int) Math.floor((getY() + dy) / (double) d) * d;

			// update location
			if ((newx != getX()) || (newy != getY()))
				setLocation(newx, newy);
		}
	}

	/**
	 * Each Component specifies which listeners should be attached. This method may
	 * (and should) be defined to call the {@link Component#attachListeners_(byte)
	 * attachListeners_(byte)} method with the appropriate byte(s).
	 *
	 * @see Component#DRAG_KB_FOCUS
	 * @see Component#ACTIVATE
	 */
	protected abstract void attachListeners();

	/**
	 * Attaches listeners to this Component based on the {@code flags}.
	 *
	 * @param flags a byte whose bits correspond to different listeners
	 *
	 * @see Component#attachListeners()
	 */
	final void attachListeners_(byte flags) {
		if ((flags & Component.DRAG_KB_FOCUS) != 0) {
			addDragListener();
			addKeyboardListener();
			addFocusListener();
		}

		if ((flags & Component.ACTIVATE) != 0)
			addActivateListener();
	}

	private void addDragListener() {
		addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				// center on mouse
				setLocation(getX() + (e.getX() - (getWidth() / 2)),
						getY() + (e.getY() - (getHeight() / 2)));
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
					moveWithKeyboard(e);
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
			}
		});
	}

	private void addFocusListener() {

		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (!focused) {
					requestFocus();
					repaint();
				}
			}
		});

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
	protected abstract void draw(Graphics g);

	/**
	 * Draws the ID of the Component with a given {@code Graphics} object.
	 *
	 * @param g the Graphics object
	 */
	protected void drawID(Graphics g) {
		g.setColor(hidden() ? Color.ORANGE : focused ? Color.CYAN : Color.BLACK);
		g.drawString(getID(), 0, getHeight() - 1);
	}

	/** Specifies how this Component should react when it's moved or resized. */
	protected abstract void updateOnMovement();

	/**
	 * Returns information about the location of the, imaginary, pins on the
	 * Component's output so the {@code branch} knows precisely where to connect.
	 *
	 * @param branch the Branch (used for safety, only index is necessary)
	 * @param index  the Branch's index (used for safety, only Branch is necessary)
	 *
	 * @return a Point with the coordinates of the Branch
	 */
	protected Point getBranchInputCoords(Branch branch, int index) {
		throw new UnsupportedOperationException(String
				.format("Component of type %s don't support getBranchInputCoords(Branch, int)",
						type().description()));
	}

	/**
	 * Returns information about the location of the, imaginary, pins on the
	 * Component's input so the {@code branch} knows precisely where to connect.
	 *
	 * @param branch the Branch (used for safety, only index is necessary)
	 * @param index  the Branch's index (used for safety, only Branch is necessary)
	 *
	 * @return a Point with the coordinates of the Branch
	 */
	protected Point getBranchOutputCoords(Branch branch, int index) {
		throw new UnsupportedOperationException(String
				.format("Component of type %s don't support getBranchOutputCoords(Branch, int)",
						type().description()));
	}
}
