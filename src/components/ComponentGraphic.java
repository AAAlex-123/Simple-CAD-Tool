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

/**
 * A class encapsulating the drawing behaviour of a {@link Component}.
 *
 * @author alexm
 */
public abstract class ComponentGraphic extends JComponent {

	private static final long serialVersionUID = 1L;

	/**
	 * The {@link Component} that is drawn by this {@code ComponentGraphics}. It is
	 * used to access information necessary to properly draw the {@code Component}
	 * and in some cases alter its state.
	 */
	protected final Component component;

	/** Size of the drawn image in pixels */
	private static final int SIZE = 40;

	/** bit to make component dragable, keyboard-usable and focusable */
	protected static final byte DRAG_KB_FOCUS = 0x1;
	/** bit to make component (de)activate on click */
	protected static final byte ACTIVATE      = 0x2;

	/**
	 * Used instead of hasFocus() because it does not return true immediately after
	 * requestFocus() is called and therefore the user has no indication of focus.
	 */
	private boolean focused = false;

	/**
	 * Constructs the Graphics object with information about the
	 * {@link ComponentGraphic#component component} it's drawing.
	 *
	 * @param c the Component.
	 */
	ComponentGraphic(Component c) {
		this(c, 0, 0, ComponentGraphic.SIZE, ComponentGraphic.SIZE);
	}

	/**
	 * Constructor specifying Component, location and dimensions.
	 *
	 * @param c the Component that this graphics object is drawing
	 * @param x the Component's X position
	 * @param y the Component's Y position
	 * @param w the Component's width
	 * @param h the Component's height
	 */
	private ComponentGraphic(Component c, int x, int y, int w, int h) {
		component = c;
		setBounds(x, y, w, h);
		attachListeners();
	}

	// 3 draw methods

	@Override
	public final void paintComponent(Graphics g) {
		if (component.hidden())
			throw new RuntimeException("Hidden Components can't be drawn");

		super.paintComponent(g);
		draw(g);
		drawID(g);
	}

	/**
	 * Each {@code ComponentGraphic} specifies how it's drawn.
	 *
	 * @param g the Graphics object necessary to draw
	 */
	protected abstract void draw(Graphics g);

	/**
	 * Draws the ID of the {@code ComponentGraphic}.
	 *
	 * @param g the Graphics object necessary to draw
	 */
	protected void drawID(Graphics g) {
		g.setColor(focused ? Color.ORANGE : Color.BLACK);
		g.drawString(component.getID(), 0, getHeight() - 1);
	}

	// 5 methods for moving and resizing

	/** Specifies how this ComponentGraphic should react when moved or resized */
	protected abstract void updateOnMovement();

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

	/**
	 * Returns information about the location of the, imaginary, pins on the
	 * Component's output so the {@code branch} knows precisely where to connect.
	 *
	 * @param branch the Branch (used for safety, only index is necessary)
	 *
	 * @return a Point with the coordinates of the Branch
	 */
	protected Point getBranchInputCoords(Component branch) {
		throw new UnsupportedOperationException(String
		        .format("Component of type %s don't support getBranchInputCoords(Branch, int)",
		                component.type().description()));
	}

	/**
	 * Returns information about the location of the, imaginary, pins on the
	 * Component's output so the {@code branch} knows precisely where to connect.
	 *
	 * @param branch the Branch (used for safety, only index is necessary)
	 *
	 * @return a Point with the coordinates of the Branch
	 */
	protected Point getBranchOutputCoords(Component branch) {
		throw new UnsupportedOperationException(String
		        .format("Component of type %s don't support getBranchOutputCoords(Branch, int)",
		                component.type().description()));
	}

	// 2 main listener methods

	/**
	 * Each Component specifies which listeners should be attached. This method may
	 * (and should) be defined to call the
	 * {@link ComponentGraphic#attachListeners_(byte) attachListeners_(byte)}
	 * method with the appropriate byte(s).
	 *
	 * @see ComponentGraphic#DRAG_KB_FOCUS
	 * @see ComponentGraphic#ACTIVATE
	 */
	protected abstract void attachListeners();

	/**
	 * Attaches listeners to this Component based on the {@code flags}.
	 *
	 * @param flags a byte whose bits correspond to different listeners
	 *
	 * @see ComponentGraphic#attachListeners()
	 */
	protected final void attachListeners_(byte flags) {
		if ((flags & ComponentGraphic.DRAG_KB_FOCUS) != 0) {
			addDragListener();
			addKeyboardListener();
			addFocusListener();
		}

		if ((flags & ComponentGraphic.ACTIVATE) != 0)
			addActivateListener();
	}

	// 2 methods for restoring ComponentGraphics

	/** Restores the state of the ComponentGraphic after it was destroyed */
	protected void restoreDeleted() {
		focused = false;
		requestFocus();
	}

	/** Restores the state of the ComponentGraphic after it was serialised */
	protected void restoreSerialised() {
		attachListeners();
		focused = false;
		requestFocus();
	}

	// 5 secondary listener methods

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
					if (component.type() == ComponentType.INPUT_PIN)
						((InputPin) ComponentGraphic.this.component)
						        .setActive(!component.getActive(0));
					break;
				default:
					break;
				}
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

	private void addActivateListener() {
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				component.wake_up(!component.getActive(0));
			}
		});
	}

	private void moveWithKeyboard(KeyEvent e) {
		if (hasFocus()) {
			final int d  = 10, dm = 4;
			int       dx = 0, dy = 0;

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
}
