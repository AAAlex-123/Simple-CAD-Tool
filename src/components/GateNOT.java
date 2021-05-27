package components;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * A Primitive Gate that maps every input to its logical not. In this circuit
 * there may be multiple input pins. Each of them is mapped to its logical
 * {@code not} and output at the output pin at the same index, provided that a
 * Branch is connected to that input pin.
 */
final class GateNOT extends PrimitiveGate {

	private static final long serialVersionUID = 3L;

	private static final String sprite = application.Application.component_icon_path + "gate_not.png";
	private static final BufferedImage image;

	static {
		BufferedImage temp = null;
		File file = null;

		try {
			file = new File(sprite);
			temp = ImageIO.read(file);
		} catch (@SuppressWarnings("unused") IOException e) {
			System.err.printf("Could not load image %s", file);
		}

		image = temp;
	}

	/**
	 * Constructs the NOT Gate with the given number of inputs and outputs.
	 * 
	 * @param n the number of pairs of pins.
	 */
	GateNOT(int n) {
		super(n, n);
	}

	@Override
	public ComponentType type() {
		return ComponentType.GATENOT;
	}

	@Override
	protected void calculateOutput() {
		for (int i = 0; i < inputPins.length; ++i) {
			// for each individual NOT gate check if a Branch is connected
			// and if it is, produce the correct output
			if (checkBranch(i)) {
				boolean res = !inputPins[i].getActive(0);
				outputPins[i].wake_up(res);
			}
		}
	}

	@Override
	protected BufferedImage getImage() {
		// the triangle and circle image can't be used with multiple inputs
		if (inCount() > 1)
			return null;
		return image;
	}
}
