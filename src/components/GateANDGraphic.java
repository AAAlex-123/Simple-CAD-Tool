package components;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import application.StringConstants;

/**
 * Handles the Graphics of an {@link GateAND}.
 *
 * @author alexm
 */
final class GateANDGraphic extends GateGraphics {

	private static final long serialVersionUID = 1L;

	private static final String sprite = StringConstants.COMPONENT_ICON_PATH
	        + "gate_and.png";

	private static final BufferedImage image;

	static {
		BufferedImage temp = null;
		File          file = null;

		try {
			file = new File(sprite);
			temp = ImageIO.read(file);
		} catch (IOException e) {
			System.err.printf("Could not load image %s", file);
		}

		image = temp;
	}

	/**
	 * Constructs the graphics object
	 *
	 * @param c the related Component
	 */
	public GateANDGraphic(Component c) {
		super(c);
	}

	@Override
	protected BufferedImage getImage() {
		return image;
	}
}
