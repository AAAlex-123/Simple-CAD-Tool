package components;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import application.StringConstants;

/** A Primitive Gate that maps the inputs to their logical {@code and}. */
final class GateAND extends PrimitiveGate {

	private static final long serialVersionUID = 3L;

	private static final String sprite = StringConstants.component_icon_path
	        + "gate_and.png";

	private static final BufferedImage image;

	static {
		BufferedImage temp = null;
		File file = null;

		try {
			file = new File(sprite);
			temp = ImageIO.read(file);
		} catch (IOException e) {
			System.err.printf("Could not load image %s", file);
		}

		image = temp;
	}

	/**
	 * Constructs the AND Gate with the given number of inputs and one output.
	 *
	 * @param in the number of input pins
	 */
	GateAND(int in) {
		super(in, 1);
	}

	@Override
	public ComponentType type() {
		return ComponentType.GATEAND;
	}

	@Override
	protected void calculateOutput() {
		if (checkBranches()) {
			boolean res = true;

			// perform logical `and` all of the input pins
			for (int i = 0; i < inputPins.length; ++i)
				res &= inputPins[i].getActive(0);

			// and outputs the result
			outputPins[0].wake_up(res);
		}
	}

	@Override
	protected BufferedImage getImage() {
		return image;
	}
}
