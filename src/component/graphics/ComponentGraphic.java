package component.graphics;

import static component.ComponentType.BRANCH;
import static component.ComponentType.INPUT_PIN;
import static component.ComponentType.OUTPUT_PIN;

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
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.function.Function;

import javax.imageio.ImageIO;
import javax.swing.JComponent;

import component.ComponentType;
import component.components.Component;
import component.components.GraphicHook;
import component.exceptions.ComponentNotFoundException;
import component.exceptions.MissingSpriteException;
import myUtil.Utility;

/**
 * A Graphic object that is responsible for drawing a representation of a
 * {@link Component}. It uses information from the {@code Component} to
 * accurately represent it (including ID, description, number of inputs and
 * outputs) and can also alter the state of it when the user interacts with the
 * Graphic (e.g. turn it on/off). A {@link GraphicHook} is used to access the
 * {@code Component's} protected members.
 *
 * @author Alex Mandelias
 */
public abstract class ComponentGraphic extends JComponent {

	private static final long serialVersionUID = 2L;

	/** Size of the drawn image in pixels */
	private static final int SIZE = 40;

	/** Size of the drawn pins in pixels */
	private static final int PIN_SIZE = 3;

	/** byte to make component dragable, keyboard-usable and focusable */
	protected static final byte DRAG_KB_FOCUS = 0x1;
	/** byte to make component (de-)activate on click */
	protected static final byte ACTIVATE      = 0x2;

	/**
	 * The {@code Component} that is drawn by this Graphic. It is used to access
	 * information necessary to correctly draw the {@code Component} and alter its
	 * state.
	 */
	protected final Component component;

	/**
	 * Used instead of {@code hasFocus()} because it does not return {@code true}
	 * immediately after {@code requestFocus()} is called and therefore the user has
	 * no indication of focus.
	 */
	private boolean focused = false;

	/**
	 * Functions that together define the curve along which the pins are placed.
	 * Subclasses may specify any number of them by overriding the corresponding
	 * methods.
	 *
	 * @see #dxi()
	 * @see #dyi()
	 * @see #dxo()
	 * @see #dyo()
	 */
	private transient Function<Integer, Integer> dxi, dyi, dxo, dyo;

	/**
	 * Constructs a Graphic associated with the {@code component}.
	 * <p>
	 * <b>Note:</b> getting the Graphic of a {@code Component} with its
	 * {@code getGraphics()} method will not return this Graphic.
	 *
	 * @param component the Component related to the Graphic
	 *
	 * @return the Graphic for that Component
	 */
	public static ComponentGraphic forComponent(Component component) {
		switch (component.type()) {
		case INPUT_PIN:
			return new InputPinGraphic(component);
		case OUTPUT_PIN:
			return new OutputPinGraphic(component);
		case BRANCH:
			return new BranchGraphic(component);
		case GATE:
			return new GateGraphic(component);
		case GATEAND:
			return new GateANDGraphic(component);
		case GATENOT:
			return new GateNOTGraphic(component);
		case GATEOR:
			return new GateORGraphic(component);
		case GATEXOR:
			return new GateXORGraphic(component);
		default:
			return null;
		}
	}

	/**
	 * Loads the contents of an image file into a BufferedImage and returns it.
	 *
	 * @param imageFileName the name of file with the sprite
	 *
	 * @return the BufferedImage
	 *
	 * @throws MissingSpriteException if the file couldn't be opened
	 */
	protected static final BufferedImage loadImage(String imageFileName) {
		BufferedImage img  = null;
		File          file = null;

		try {
			file = new File(imageFileName);
			img = ImageIO.read(file);
		} catch (final IOException e) {
			throw new MissingSpriteException(file);
		}

		return img;
	}

	/**
	 * Constructs the Graphics object with information about the {@link #component}
	 * that it is drawing.
	 *
	 * @param component the Component.
	 */
	protected ComponentGraphic(Component component) {
		this(component, 0, 0, ComponentGraphic.SIZE, ComponentGraphic.SIZE);
	}

	/**
	 * Constructor specifying the component, location and dimensions.
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
		updateOnMovement();
	}

	// 7 draw methods

	@Override
	public final void paintComponent(Graphics g) {
		if (GraphicHook.hidden(component))
			throw new RuntimeException("Hidden Components can't be drawn"); //$NON-NLS-1$

		super.paintComponent(g);
		draw(g);
		drawPins(g);
		drawID(g);
		drawDescription(g);
	}

	/**
	 * Each Graphic specifies how it's drawn.
	 *
	 * @param g the Graphics object necessary to draw
	 *
	 * @implNote the default implementation draws the Image returned by the
	 *           {@link #getImage()} method
	 */
	protected void draw(Graphics g) {
		g.drawImage(getImage(), 0, 0, null);
	}

	/**
	 * Draws the pins of the {@code Component}.
	 *
	 * @param g the Graphics object necessary to draw
	 *
	 * @implNote the default implementation uses the 4 {@code Functions} to draw the
	 *           correct number of pins to the locations specified by the
	 *           {@code Functions}
	 */
	protected void drawPins(Graphics g) {
		for (int i = 0, size = GraphicHook.inCount(component); i < size; ++i) {
			g.setColor(GraphicHook.getActive(component, i) ? Color.GREEN : Color.RED);
			ComponentGraphic.drawPin(g, new Point(dxi.apply(i), dyi.apply(i)));
		}

		for (int i = 0, size = GraphicHook.outCount(component); i < size; ++i) {
			g.setColor(GraphicHook.getActive(component, i) ? Color.GREEN : Color.RED);
			ComponentGraphic.drawPin(g, new Point(dxo.apply(i), dyo.apply(i)));
		}
	}

	/**
	 * Draws the ID of the {@code Component}. The ID is the Component's unique
	 * identifier that can be used to distinguish it among other Components.
	 *
	 * @param g the Graphics object necessary to draw
	 *
	 * @implNote the default implementation uses yellow or black (focused or not)
	 *           colour to draw the ID of the {@code component} on the lower-left
	 *           corner of the graphic
	 */
	protected void drawID(Graphics g) {
		g.setColor(focused ? Color.YELLOW : Color.BLACK);
		g.drawString(component.getID(), 0, getHeight() - 1);
	}

	/**
	 * Draws the description of the {@code Component}. The description is a very
	 * short string describing the type of the Component in case it is not obvious
	 * from the Graphic alone.
	 *
	 * @param g the Graphics object necessary to draw
	 *
	 * @implNote the default implementation uses black colour to draw the
	 *           description of the {@code component} in the center of the graphic,
	 *           aligned close to its left edge
	 */
	protected void drawDescription(Graphics g) {
		g.setColor(Color.BLACK);
		g.drawString(GraphicHook.description(component), 7, (getHeight() / 2) + 5);
	}

	/**
	 * Returns the Image used to draw the Graphic or null if no Image is suitable
	 * for drawing. In this case, the {@link #draw(Graphics)} method should also be
	 * overridden to draw the Graphic without the Image.
	 *
	 * @return the Image
	 */
	protected abstract BufferedImage getImage();

	private static void drawPin(Graphics g, Point p) {
		final int size = ComponentGraphic.PIN_SIZE;
		g.fillRect(p.x - (size / 2), p.y - (size / 2), size, size);
	}

	// 5 methods for moving and resizing

	/** Defines how this Graphic should react when moved or resized */
	protected void updateOnMovement() {
		// for Components that are moved by the user (all except for Branches),
		// tell their inputs and outputs (the Branches connected to them) to update.
		Utility.foreach(GraphicHook.getInputs(component),
		        comp -> comp.getGraphics().updateOnMovement());
		Utility.foreach(GraphicHook.getOutputs(component),
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
	 * Returns information about the location of the imaginary pins on the
	 * Component's output so the {@code branch} knows precisely where to connect.
	 *
	 * @param branch the Branch (used for safety, only index is necessary)
	 *
	 * @return a Point with the coordinates of the Branch
	 */
	protected final Point getBranchInputCoords(Component branch) {
		final ComponentType type = component.type();
		if ((type == BRANCH) || (type == OUTPUT_PIN))
			throw new UnsupportedOperationException(String.format(
			        "Component of type %s don't support getBranchInputCoords(Branch, int)", //$NON-NLS-1$
			        type.description()));

		final List<List<Component>> outputs = GraphicHook.getOutputs(component);
		for (final List<Component> ls : outputs)
			if (ls.contains(branch)) {
				final int index = outputs.indexOf(ls);
				return new Point(getX() + dxo.apply(index), getY() + dyo.apply(index));
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
		final ComponentType type = component.type();
		if ((type == BRANCH) || (type == INPUT_PIN))
			throw new UnsupportedOperationException(String
			        .format("Component of type %s don't support getBranchOutputCoords(Branch, int)", //$NON-NLS-1$
			                type.description()));

		final int index = GraphicHook.getInputs(component).indexOf(branch);
		if (index != -1)
			return new Point(getX() + dxi.apply(index), getY() + dyi.apply(index));

		throw new ComponentNotFoundException(branch, component);
	}

	// 2 main listener methods

	/**
	 * Each Component specifies which listeners should be attached. This method may
	 * (and should) be defined to call the {@link #attachListenersByFlags(byte)}
	 * method with the appropriate byte.
	 */
	protected abstract void attachListeners();

	/**
	 * Attaches listeners to this Component based on the {@code flags} which may be
	 * formed by {@code OR}-ing together any number of bytes from those defined in
	 * this class.
	 *
	 * @param flags a byte whose bits correspond to different listeners
	 *
	 * @see #DRAG_KB_FOCUS
	 * @see #ACTIVATE
	 */
	protected final void attachListenersByFlags(byte flags) {
		if ((flags & ComponentGraphic.DRAG_KB_FOCUS) != 0) {
			addDragListener();
			addKeyboardListener();
			addFocusListener();
		}

		if ((flags & ComponentGraphic.ACTIVATE) != 0)
			addActivateListener();
	}

	// 2 methods for restoring Graphics

	/** Restores the state of the Graphic after it was destroyed */
	public void restoreDeleted() {
		focused = false;
		requestFocus();
	}

	/** Restores the state of the Graphic after it was serialised */
	public void restoreSerialised() {
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
	 * Function for the {@code x} coordinate of the Input Pin at index {@code i}.
	 *
	 * @return the Function
	 */
	protected Function<Integer, Integer> dxi() {
		return i -> ComponentGraphic.PIN_SIZE / 2;
	}

	/**
	 * Function for the {@code y} coordinate of the Input Pin at index {@code i}.
	 *
	 * @return the Function
	 */
	protected Function<Integer, Integer> dyi() {
		final int count = GraphicHook.inCount(component);
		final int gap   = (getHeight() - (count * ComponentGraphic.PIN_SIZE)) / (count + 1);
		return i -> (((i + 1) * (gap + ComponentGraphic.PIN_SIZE))
		        - (ComponentGraphic.PIN_SIZE / 2));
	}

	/**
	 * Function for the {@code x} coordinate of the Output Pin at index {@code i}.
	 *
	 * @return the Function
	 */
	protected Function<Integer, Integer> dxo() {
		return i -> (getWidth() - (ComponentGraphic.PIN_SIZE / 2)) - 1;
	}

	/**
	 * Function for the {@code y} coordinate of the Output Pin at index {@code i}.
	 *
	 * @return the Function
	 */
	protected Function<Integer, Integer> dyo() {
		final int count = GraphicHook.outCount(component);
		final int gap   = (getHeight() - (count * ComponentGraphic.PIN_SIZE)) / (count + 1);
		return i -> (((i + 1) * (gap + ComponentGraphic.PIN_SIZE))
		        - (ComponentGraphic.PIN_SIZE / 2));
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
						GraphicHook.wake_up(component, (!GraphicHook.getActive(component, 0)));
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
				GraphicHook.wake_up(component, !GraphicHook.getActive(component, 0));
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

			// check for fast movement
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
