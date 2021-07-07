package components;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import application.StringConstants;
import exceptions.ComponentNotFoundException;

/**
 * Handles the Graphics of an {@link OutputPin}.
 *
 * @author alexm
 */
final class OutputPinGraphic extends ComponentGraphic {

	private static final long serialVersionUID = 1L;

	private static final String sprite = StringConstants.COMPONENT_ICON_PATH
	        + "output_pin_{state}.png";

	private static final BufferedImage image_on, image_off;

	static {
		BufferedImage temp_on = null, temp_off = null;
		File          file    = null;

		try {
			file = new File(sprite.replace("{state}", "on"));
			temp_on = ImageIO.read(file);
		} catch (IOException e) {
			System.err.printf("Could not load image %s%n", file);
		}

		try {
			file = new File(sprite.replace("{state}", "off"));
			temp_off = ImageIO.read(file);
		} catch (IOException e) {
			System.err.printf("Could not load image %s%n", file);
		}

		image_on = temp_on;
		image_off = temp_off;
	}

	/**
	 * Constructs the graphics object
	 *
	 * @param c the related Component
	 */
	public OutputPinGraphic(Component c) {
		super(c);
	}

	@Override
	protected void draw(Graphics g) {
		g.drawImage(component.getActive(0) ? image_on : image_off, 0, 0, null);
	}

	@Override
	protected Point getBranchOutputCoords(Component b) {
		if (!component.getInputs().contains(b))
			throw new ComponentNotFoundException(b, component);

		return new Point(getX(), getY() + (getHeight() / 2));
	}

	@Override
	protected void attachListeners() {
		attachListeners_(DRAG_KB_FOCUS);
	}
}
