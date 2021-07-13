package components;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import application.StringConstants;

/**
 * Handles the Graphics of an {@link InputPin}.
 *
 * @author alexm
 */
final class InputPinGraphic extends ComponentGraphic {

	private static final long serialVersionUID = 1L;

	private static final String sprite = StringConstants.COMPONENT_ICON_PATH
	        + "input_pin_{state}.png";

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
	public InputPinGraphic(Component c) {
		super(c);
	}

	@Override
	protected void draw(Graphics g) {
		g.drawImage(component.getActive(0) ? image_on : image_off, 0, 0, null);
	}

	@Override
	protected void attachListeners() {
		attachListeners_((byte) (DRAG_KB_FOCUS | ACTIVATE));
	}
}
