package components;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import exceptions.ComponentNotFoundException;
import exceptions.MalformedGateException;

/** Corresponds to the {@link ComponentType#OUTPUT_PIN OUTPUT_PIN} type. */
final class OutputPin extends Component {

	private static final long serialVersionUID = 3L;

	private static final String sprite = application.Application.component_icon_path + "output_pin_{state}.png";
	private static final BufferedImage image_on, image_off;

	static {
		// yes it has to be done this way
		BufferedImage temp_on = null, temp_off = null;
		File file = null;

		try {
			file = new File(sprite.replace("{state}", "on"));
			temp_on = ImageIO.read(file);
		} catch (@SuppressWarnings("unused") IOException e) {
			System.err.printf("Could not load image %s", file);
		}

		try {
			file = new File(sprite.replace("{state}", "off"));
			temp_off = ImageIO.read(file);
		} catch (@SuppressWarnings("unused") IOException e) {
			System.err.printf("Could not load image %s", file);
		}

		image_on = temp_on;
		image_off = temp_off;
	}

	private Branch inputBranch;
	private boolean active;

	// information about the enclosing Gate necessary for signal transmiion
	private Gate outerGate;
	private int outerGateIndex;

	/** Constructs the OuputPin */
	OutputPin() {
		active = false;
	}

	@Override
	public ComponentType type() {
		return ComponentType.OUTPUT_PIN;
	}

	@Override
	protected int outCount() {
		return 0;
	}

	@Override
	protected void wake_up(boolean newActive, int index, boolean prevHidden) {
		checkIndex(index, inCount());

		// once hidden cannot be un-hidden
		if (hidden() && !prevHidden)
			throw new MalformedGateException(this);

		if (prevHidden)
			hideComponent();

		// propagate signal only if it's different
		if (active != newActive) {
			active = newActive;
			repaint();

			// inform the enclosing Gate that an output has changed
			if (outerGate != null)
				outerGate.outputChanged(outerGateIndex);
		}
	}

	@Override
	protected void destroySelf() {
		if (inputBranch != null) {
			inputBranch.destroy();
			inputBranch = null;
		}
	}

	@Override
	protected void restoreDeletedSelf() {
	}

	@Override
	protected void restoreSerialisedSelf() {
	}

	@Override
	protected boolean getActive(int index) {
		checkIndex(index, inCount());
		return active;
	}

	@Override
	protected void setIn(Branch b, int index) {
		checkIndex(index, inCount());
		checkChangeable();

		if (inputBranch != null) {
			// declare that the connected branches should be destroyed
			// the application should take care of destroying the Branch
			inputBranch.toBeRemoved = true;
		}

		inputBranch = b;
	}

	@Override
	protected void removeIn(Branch b, int index) {
		checkIndex(index, inCount());
		checkChangeable();

		if ((inputBranch == b)) {
			inputBranch = null;
		} else {
			// throw new ComponentNotFoundException(b, this);

			// when a Branch is created, setIn is called
			// this component has in the new branch but
			// the old branch has out this component
			// therefore the old branch must be destroyed
			// but it will call removeIn on this component
			// but it isn't the in of this component :)
		}
	}

	/**
	 * Proper way for the client (the Factory) to get output.
	 *
	 * @return the active state of this OutputPin
	 */
	boolean getActive() {
		checkChangeable();
		return active;
	}

	/**
	 * Marks this Component as unchangeable because it's hidden in a {@code Gate}
	 * and sets the {@code gate} as the next component to be woken up. Normally
	 * should only be called during the construction of the {@code gate}.
	 *
	 * @param gate  the next component to be woken up
	 * @param index the pin's index in the gate
	 */
	void setOuterGate(Gate gate, int index) {
		if (outerGate != null)
			checkChangeable();

		hideComponent();

		outerGate = gate;
		outerGateIndex = index;
	}

	@Override
	protected void attachListeners() {
		attachListeners_(DRAG_KB_FOCUS);
	}

	@Override
	protected void draw(Graphics g) {
		g.drawImage(getActive(0) ? image_on : image_off, 0, 0, null);
	}

	@Override
	protected void updateOnMovement() {
		// this component is moved by the user; tell branch to update
		if (inputBranch != null)
			inputBranch.updateOnMovement();
	}

	@Override
	protected Point getBranchOutputCoords(Branch b, int index) {
		checkIndex(index, inCount());

		if (b != inputBranch)
			throw new ComponentNotFoundException(b, this);

		return new Point(getX(), getY() + (getHeight() / 2));
	}
}
