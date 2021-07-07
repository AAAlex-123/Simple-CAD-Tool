package components;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.function.Function;

import javax.imageio.ImageIO;

import application.StringConstants;
import exceptions.ComponentNotFoundException;

/**
 * Handles the Graphics of a {@link Gate}.
 *
 * @author alexm
 */
class GateGraphic extends ComponentGraphic {

	private static final long serialVersionUID = 1L;

	private static final String sprite = StringConstants.COMPONENT_ICON_PATH
	        + "gate.png";

	private static final BufferedImage image;

	static {
		BufferedImage temp = null;
		File          file = null;

		try {
			file = new File(sprite);
			temp = ImageIO.read(file);
		} catch (IOException e) {
			System.err.printf("Could not load image %s%n", file);
		}

		image = temp;
	}

	// only for custom gates
	private final String description;

	// functions to draw the pins. let subclasses specify any number of them
	private transient Function<Integer, Integer> dxi, dyi, dxo, dyo;

	/**
	 * Constructs the graphics object
	 *
	 * @param c the related Component
	 */
	public GateGraphic(Component c) {
		this(c, "");
	}

	/**
	 * Constructs the graphics object
	 *
	 * @param c    the related Component
	 * @param desc the Component's description
	 */
	public GateGraphic(Component c, String desc) {
		super(c);
		description = desc;
		addFunctions();
	}

	@Override
	protected final void draw(Graphics g) {
		drawSprite(g);

		g.setColor(Color.BLACK);
		g.drawString(description, 7, (getHeight() / 2) + 5);

		final List<Component>       inputs  = component.getInputs();
		final List<List<Component>> outputs = component.getOutputs();

		for (int i = 0; i < inputs.size(); ++i) {
			g.setColor(inputs.get(i) != null
			        ? inputs.get(i).getActive(0) ? Color.GREEN : Color.RED
			        : Color.RED);
			drawPin(g, new Point(dxi.apply(i), dyi.apply(i)));
		}

		for (int i = 0; i < outputs.size(); ++i) {
			// shhh
			g.setColor(((Gate) component).outputPins[i].getActive(0) ? Color.GREEN : Color.RED);
			drawPin(g, new Point(dxo.apply(i), dyo.apply(i)));
		}
	}

	/**
	 * Draws the sprite of the Gate.
	 *
	 * @param g the Graphics object with which to draw
	 */
	protected final void drawSprite(Graphics g) {
		BufferedImage bImage = getImage();
		if (bImage != null)
			g.drawImage(bImage, 0, 0, null);
		else
			g.drawImage(image, 0, 0, null);
	}

	/**
	 * Returns the Image used to draw the Primitive Gates.
	 *
	 * @return the Image
	 */
	protected BufferedImage getImage() {
		return image;
	}

	@Override
	protected Point getBranchInputCoords(Component b) {
		for (List<Component> ls : component.getOutputs()) {
			int index = ls.indexOf(b);
			if (index != -1)
				return new Point(getX() + dxo().apply(index), getY() + dyo().apply(index));
		}

		throw new ComponentNotFoundException(b, this.component);
	}

	@Override
	protected Point getBranchOutputCoords(Component b) {
		int index = component.getInputs().indexOf(b);
		if (index != -1)
			return new Point(getX() + dxi().apply(index), getY() + dyi().apply(index));

		throw new ComponentNotFoundException(b, this.component);
	}

	@Override
	protected void attachListeners() {
		attachListeners_(DRAG_KB_FOCUS);
	}

	@Override
	protected void restoreSerialised() {
		super.restoreSerialised();
		addFunctions();
	}

	// fancy functions for pin locations

	private static final int BOX_SIZE = 3;

	private static void drawPin(Graphics g, Point p) {
		g.fillRect(p.x - (BOX_SIZE / 2), p.y - (BOX_SIZE / 2), BOX_SIZE,
		        BOX_SIZE);
	}

	private void addFunctions() {
		dxi = dxi();
		dyi = dyi();
		dxo = dxo();
		dyo = dyo();
	}

	/** @return a Function for the x coordinate of the Input Pin at index i */
	protected Function<Integer, Integer> dxi() {
		return i -> BOX_SIZE / 2;
	}

	/** @return a Function for the y coordinate of the Input Pin at index i */
	protected Function<Integer, Integer> dyi() {
		final int count = component.inCount();
		final int gap   = (getHeight() - (count * BOX_SIZE)) / (count + 1);
		return i -> (((i + 1) * (gap + BOX_SIZE)) - (BOX_SIZE / 2));
	}

	/** @return a Function for the x coordinate of the Output Pin at index i */
	protected Function<Integer, Integer> dxo() {
		return i -> (getWidth() - (BOX_SIZE / 2)) - 1;
	}

	/** @return a Function for the y coordinate of the Output Pin at index i */
	protected Function<Integer, Integer> dyo() {
		final int count = component.outCount();
		final int gap   = (getHeight() - (count * BOX_SIZE)) / (count + 1);
		return i -> (((i + 1) * (gap + BOX_SIZE)) - (BOX_SIZE / 2));
	}
}
