package component.graphics;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.function.Function;

import javax.imageio.ImageIO;

import application.StringConstants;
import exceptions.MissingSpriteException;

/**
 * Handles the Graphics of a {@link GateNOT}.
 *
 * @author alexm
 */
final class GateORGraphic extends GateGraphic {

	private static final long serialVersionUID = 1L;

	private static final String sprite = StringConstants.COMPONENT_ICON_PATH
			+ "gate_or.png"; //$NON-NLS-1$

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
	public GateORGraphic(Component c) {
		super(c);
	}

	@Override
	protected BufferedImage getImage() {
		return image;
	}

	@Override
	protected Function<Integer, Integer> dxi() {
		return i -> (int) (0.55 * Math.cos(1.2 * Math.asin(1 - (dyi().apply(i) / 20.0))) * 20);
	}
}
