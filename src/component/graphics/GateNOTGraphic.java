package component.graphics;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import application.StringConstants;
import exceptions.MissingSpriteException;

/**
 * Handles the Graphics of a {@link GateNOT}.
 *
 * @author alexm
 */
final class GateNOTGraphic extends GateGraphic {

	private static final long serialVersionUID = 1L;

	private static final String sprite = StringConstants.COMPONENT_ICON_PATH
			+ "gate_not.png"; //$NON-NLS-1$

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

	/**
	 * Constructs the graphics object
	 *
	 * @param c the related Component
	 */
	public GateNOTGraphic(Component c) {
		super(c);
	}

	@Override
	protected BufferedImage getImage() {
		// the triangle and circle image can't be used with multiple inputs
		if (component.inCount() > 1)
			return null;
		return image;
	}
}
