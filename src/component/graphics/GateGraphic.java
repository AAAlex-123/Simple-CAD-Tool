package component.graphics;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import application.StringConstants;
import exceptions.MissingSpriteException;

/**
 * Handles the Graphics of a {@link Gate}.
 *
 * @author alexm
 */
class GateGraphic extends ComponentGraphic {

	private static final long serialVersionUID = 1L;

	private static final String sprite = StringConstants.COMPONENT_ICON_PATH
			+ "gate.png"; //$NON-NLS-1$

	private static final BufferedImage image;

	static {
		BufferedImage temp = null;
		File          file = null;

		try {
			file = new File(sprite);
			temp = ImageIO.read(file);
		} catch (IOException e) {
			throw new MissingSpriteException(file);
		}

		image = temp;
	}

	// only for custom gates
	private final String description;

	/**
	 * Constructs the graphics object
	 *
	 * @param c the related Component
	 */
	public GateGraphic(Component c) {
		this(c, ""); //$NON-NLS-1$
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
	}

	@Override
	protected final void draw(Graphics g) {
		drawSprite(g);

		g.setColor(Color.BLACK);
		g.drawString(description, 7, (getHeight() / 2) + 5);
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
	 * Returns the Image used to draw the Primitive Gates. Defaults to plain grey
	 * rectangle used for user-made Gates.
	 *
	 * @return the Image
	 */
	protected BufferedImage getImage() {
		return image;
	}

	@Override
	protected void attachListeners() {
		attachListeners_(DRAG_KB_FOCUS);
	}
}
