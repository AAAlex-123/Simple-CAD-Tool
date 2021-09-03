package component.graphics;

import static components.ComponentType.BRANCH;
import static components.ComponentType.INPUT_PIN;
import static components.ComponentType.OUTPUT_PIN;

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
import java.util.List;
import java.util.function.Function;

import javax.swing.JComponent;

import exceptions.ComponentNotFoundException;
import myUtil.Utility;

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

	/** Size of the drawn pins in pixels */
	private static final int PIN_SIZE = 3;

	/** Functions to draw the pins. let subclasses specify any number of them */
	private transient Function<Integer, Integer> dxi, dyi, dxo, dyo;

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
		addFunctions();
	}

	// 5 draw methods

	@Override
	public final void paintComponent(Graphics g) {
		if (component.hidden())
			throw new RuntimeException("Hidden Components can't be drawn"); //$NON-NLS-1$

		super.paintComponent(g);
		draw(g);
		drawPins(g);
		drawID(g);
	}

	/**
	 * Each {@code ComponentGraphic} specifies how it's drawn.
	 *
	 * @param g the Graphics object necessary to draw
	 */
	protected abstract void draw(Graphics g);

	protected void drawPins(Graphics g) {
		final List<Component> inputs = component.getInputs();
		final List<List<Component>> outputs = component.getOutputs();

		for (int i = 0; i < inputs.size(); ++i) {
			Component c = inputs.get(i);
			if (c != null)
				g.setColor(c.getActive(0) ? Color.GREEN : Color.RED);
			else
				g.setColor(Color.RED);
			drawPin(g, new Point(dxi.apply(i), dyi.apply(i)));
		}

		// TODO: contemplate whether or not this is worth improving

		switch (component.type()) {
		case INPUT_PIN:
		case OUTPUT_PIN:
			for (int i = 0; i < outputs.size(); ++i) {
				List<Component> pins = outputs.get(i);
				if (pins.size() == 0)
					g.setColor(Color.RED);
				else
					g.setColor(pins.get(0).getActive(0) ? Color.GREEN : Color.RED);

				g.setColor(component.getActive(0) ? Color.GREEN : Color.RED);
				drawPin(g, new Point(dxo.apply(i), dyo.apply(i)));
			}
			break;
		default:
			for (int i = 0; i < outputs.size(); ++i) {
				g.setColor(((Gate) component).outputPins[i].getActive(0) ? Color.GREEN : Color.RED);
				drawPin(g, new Point(dxo.apply(i), dyo.apply(i)));
			}
		}
	}

	/**
	 * Draws the ID of the {@code ComponentGraphic}.
	 *
	 * @param g the Graphics object necessary to draw
	 */
	protected void drawID(Graphics g) {
		g.setColor(focused ? Color.YELLOW : Color.BLACK);
		g.drawString(component.getID(), 0, getHeight() - 1);
	}

	private static void drawPin(Graphics g, Point p) {
		g.fillRect(p.x - (PIN_SIZE / 2), p.y - (PIN_SIZE / 2), PIN_SIZE, PIN_SIZE);
	}

	// 5 methods for moving and resizing

	/** Specifies how this ComponentGraphic should react when moved (or resized) */
	protected void updateOnMovement() {
		// for Components that are moved by the user (all except for Branches),
		// tell their inputs and outputs (the Branches connected to them) to update.
		Utility.foreach(component.getInputs(), comp -> comp.getGraphics().updateOnMovement());
		Utility.foreach(component.getOutputs(),
				vc -> Utility.foreach(vc, comp -> comp.getGraphics().updateOnMovement()));
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

	/**
	 * Returns information about the location of the, imaginary, pins on the
	 * Component's output so the {@code branch} knows precisely where to connect.
	 *
	 * @param branch the Branch (used for safety, only index is necessary)
	 *
	 * @return a Point with the coordinates of the Branch
	 */
	protected final Point getBranchInputCoords(Component branch) {
		ComponentType type = component.type();
		if ((type == BRANCH) || (type == OUTPUT_PIN))
			throw new UnsupportedOperationException(String.format(
					"Component of type %s don't support getBranchInputCoords(Branch, int)", type.description())); //$NON-NLS-1$

		List<List<Component>> outputs = component.getOutputs();
		for (List<Component> ls : outputs) {
			if (ls.contains(branch)) {
				final int index = outputs.indexOf(ls);
				return new Point(getX() + dxo().apply(index), getY() + dyo().apply(index));
			}
		}

		throw new ComponentNotFoundException(branch, component);
	}

	/**
	 * Returns information about the location of the, imaginary, pins on the
	 * Component's output so the {@code branch} knows precisely where to connect.
	 *
	 * @param branch the Branch (used for safety, only index is necessary)
	 *
	 * @return a Point with the coordinates of the Branch
	 */
	protected final Point getBranchOutputCoords(Component branch) {
		ComponentType type = component.type();
		if ((type == BRANCH) || (type == INPUT_PIN))
			throw new UnsupportedOperationException(String
					.format("Component of type %s don't support getBranchOutputCoords(Branch, int)", //$NON-NLS-1$
							type.description()));

		int index = component.getInputs().indexOf(branch);
		if (index != -1)
			return new Point(getX() + dxi().apply(index), getY() + dyi().apply(index));

		throw new ComponentNotFoundException(branch, component);
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
		addFunctions();
	}

	// 5 function methods for pin locations

	private void addFunctions() {
		dxi = dxi();
		dyi = dyi();
		dxo = dxo();
		dyo = dyo();
	}

	/**
	 * Returns a Function for the x coordinate of the Input Pin at index i.
	 * 
	 * @return the Function
	 */
	protected Function<Integer, Integer> dxi() {
		return i -> PIN_SIZE / 2;
	}

	/**
	 * Returns a Function for the y coordinate of the Input Pin at index i.
	 * 
	 * @return the Function
	 */
	protected Function<Integer, Integer> dyi() {
		final int count = component.inCount();
		final int gap = (getHeight() - (count * PIN_SIZE)) / (count + 1);
		return i -> (((i + 1) * (gap + PIN_SIZE)) - (PIN_SIZE / 2));
	}

	/**
	 * Returns a Function for the x coordinate of the Output Pin at index i.
	 * 
	 * @return the Function
	 */
	protected Function<Integer, Integer> dxo() {
		return i -> (getWidth() - (PIN_SIZE / 2)) - 1;
	}

	/**
	 * Returns a Function for the y coordinate of the Output Pin at index i.
	 * 
	 * @return the Function
	 */
	protected Function<Integer, Integer> dyo() {
		final int count = component.outCount();
		final int gap = (getHeight() - (count * PIN_SIZE)) / (count + 1);
		return i -> (((i + 1) * (gap + PIN_SIZE)) - (PIN_SIZE / 2));
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
						((InputPin) component)
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
