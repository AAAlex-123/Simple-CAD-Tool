package components;

import static myUtil.Utility.foreach;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;

import javax.imageio.ImageIO;

import exceptions.ComponentNotFoundException;
import exceptions.MalformedGateException;

/** Corresponds to the {@link ComponentType#INPUT_PIN INPUT_PIN} type. */
final class InputPin extends Component {

	private static final long serialVersionUID = 3L;

	private static final String sprite = application.Application.component_icon_path + "input_pin_{state}.png";
	private static final BufferedImage image_on, image_off;

	static {
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

	private final Vector<Branch> outputBranches;
	private boolean active;

	/** Constructs an {@code InputPin} */
	InputPin() {
		outputBranches = new Vector<>(1, 1);
		active = false;
	}

	@Override
	public ComponentType type() {
		return ComponentType.INPUT_PIN;
	}

	@Override
	protected int inCount() {
		return 0;
	}

	@Override
	protected void wake_up(boolean newActive, int index, boolean prevHidden) {
		checkIndex(index, 1);

		// once hidden cannot be un-hidden
		if (hidden() && !prevHidden)
			throw new MalformedGateException(this);

		if (prevHidden)
			hideComponent();

		// propagate signal only if it's different
		if (active != newActive) {
			active = newActive;
			repaint();
			foreach(outputBranches, b -> b.wake_up(active, hidden()));
		}
	}

	@Override
	protected void destroySelf() {
		foreach(new ArrayList<>(outputBranches), Branch::destroy);
		outputBranches.clear();
	}

	@Override
	protected void restoreDeletedSelf() {
	}

	@Override
	protected void restoreSerialisedSelf() {
	}

	@Override
	protected boolean getActive(int index) {
		checkIndex(index, 1);
		return active;
	}

	@Override
	protected void addOut(Branch b, int index) {
		checkIndex(index, 1);
		checkChangeable();
		outputBranches.add(b);
	}

	@Override
	protected void removeOut(Branch b, int index) {
		checkIndex(index, 1);
		checkChangeable();
		if (!outputBranches.remove(b))
			throw new ComponentNotFoundException(b, this);
	}

	/**
	 * Proper way for the client (the Factory) to set input. This method, unlike
	 * wake_up, will throw when it is called on a hidden {@code InputPin}.
	 *
	 * @param newActive the new value for the active state of this InputPin
	 */
	void setActive(boolean newActive) {
		checkChangeable();
		wake_up(newActive);
	}

	/**
	 * Marks this Component as unchangeable because it's hidden in a {@code Gate}.
	 * Normally should only be called during the construction of a {@code Gate}.
	 */
	void setOuterGate() {
		checkChangeable();
		hideComponent();
	}

	@Override
	protected void attachListeners() {
		attachListeners_((byte) (DRAG_KB_FOCUS | ACTIVATE));
	}

	@Override
	protected void draw(Graphics g) {
		g.drawImage(getActive(0) ? image_on : image_off, 0, 0, null);
	}

	@Override
	protected void updateOnMovement() {
		// this component is moved by the user; tell branches to update
		foreach(outputBranches, Branch::updateOnMovement);
	}

	@Override
	protected Point getBranchCoords(Branch b, int index) {
		checkIndex(index, 1);

		if (!outputBranches.contains(b))
			throw new ComponentNotFoundException(b, this);

		return new Point((getX() + getWidth()) - 1, getY() + (getHeight() / 2));
	}
}
