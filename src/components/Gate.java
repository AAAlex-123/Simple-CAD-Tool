package components;

import static myUtil.Utility.all;
import static myUtil.Utility.foreach;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import java.util.function.Function;

import javax.imageio.ImageIO;

import exceptions.ComponentNotFoundException;
import exceptions.MalformedGateException;

/** Corresponds to the {@link ComponentType#GATE GATE} type. */
class Gate extends Component {

	private static final long serialVersionUID = 5L;

	private static final String sprite = application.Application.component_icon_path + "gate.png";
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

	/** The Gate's incoming Branches (should only be accessed by subclasses) */
	protected final Branch[] inputBranches;

	/**
	 * The Gate's outgoing Branches (should only be accessed by subclasses). Each
	 * pin has many Branches coming out of it (generic arrays aren't supported)
	 */
	protected final Vector<Vector<Branch>> outputBranches;

	/** The Gate's inner Input Pins (should only be accessed by subclasses) */
	protected final InputPin[] inputPins;

	/** The Gate's inner Output Pins (should only be accessed by subclasses) */
	protected final OutputPin[] outputPins;

	// only for user-made gates
	private final String description;

	/**
	 * Constructs a Gate with the given number of Input and Output pins.
	 *
	 * @param inN  the number of input pins
	 * @param outN the number of output pins
	 */
	Gate(int inN, int outN) {
		inputBranches = new Branch[inN];
		outputBranches = new Vector<>(outN, 1);
		inputPins = new InputPin[inN];
		outputPins = new OutputPin[outN];
		description = "";

		addFunctions();

		for (int i = 0; i < inN; ++i) {
			inputPins[i] = new InputPin();
			inputPins[i].setOuterGate();
		}

		for (int i = 0; i < outN; ++i) {
			outputPins[i] = new OutputPin();
			outputPins[i].setOuterGate(this, i);
			outputBranches.add(new Vector<>(1, 1));
		}
	}

	/**
	 * Constructs a Gate using as Input and Output Pins the ones provided. During
	 * construction, the Input Pins are marked as "hidden" and the hiddenness is
	 * propagated all the way to the Output Pins. This essentially packs the circuit
	 * between the Input and Output Pins into this Gate and it behaves exactly as it
	 * would have, had it not been packed into a Gate.
	 *
	 * @param in   the Gate's inner Input Pins
	 * @param out  the Gate's inner Output Pins
	 * @param desc the Gate's description
	 */
	Gate(InputPin[] in, OutputPin[] out, String desc) {
		inputBranches = new Branch[in.length];
		outputBranches = new Vector<>(out.length, 1);
		inputPins = in;
		outputPins = out;
		description = desc;

		addFunctions();

		for (int i = 0; i < inputPins.length; ++i) {
			inputPins[i].setOuterGate();

			// propagate hiddenness and reset the state of the pins
			inputPins[i].wake_up(true);
			inputPins[i].wake_up(false, true);
		}

		for (int i = 0; i < outputPins.length; ++i) {
			outputPins[i].setOuterGate(this, i);
			outputBranches.add(new Vector<>(1, 1));
		}
	}

	@Override
	public ComponentType type() {
		return ComponentType.GATE;
	}

	@Override
	protected int inCount() {
		return inputPins.length;
	}

	@Override
	protected int outCount() {
		return outputPins.length;
	}

	@Override
	protected void wake_up(boolean newActive, int indexIn, boolean prevHidden) {
		checkIndex(indexIn, inCount());
		// once hidden cannot be un-hidden
		if (hidden() && !prevHidden)
			throw new MalformedGateException(this);

		if (prevHidden)
			hideComponent();

		repaint();

		// only propagate signal if all InputPins are connected to a Branch
		if (checkBranches())
			inputPins[indexIn].wake_up(newActive);
	}

	@Override
	protected void destroySelf() {
		foreach(inputBranches, Branch::destroy);
		Arrays.fill(inputBranches, null);
		List<Branch> ls = new ArrayList<>();
		foreach(outputBranches, vb -> ls.addAll(vb));
		foreach(ls, Branch::destroy);

		outputBranches.clear();
	}

	@Override
	protected void restoreDeletedSelf() {
		for (int i = 0; i < outputPins.length; ++i)
			outputBranches.add(new Vector<>(1, 1));
	}

	@Override
	protected void restoreSerialisedSelf() {
		addFunctions();
	}

	@Override
	protected boolean getActive(int index) {
		checkIndex(index, outCount());
		return outputPins[index].getActive(0);
	}

	/**
	 * Informs this Gate that the state of an inner Output Pin has changed. The Gate
	 * then wakes up the Branches that are connected to that Output Pin.
	 *
	 * @param index the index of the OutputPin
	 */
	final void outputChanged(int index) {
		checkIndex(index, outCount());

		foreach(outputBranches.get(index), b -> b.wake_up(outputPins[index].getActive(0), hidden()));
	}

	@Override
	protected void setIn(Branch b, int index) {
		checkIndex(index, inCount());
		checkChangeable();
		if (inputBranches[index] != null) {
			// declare that the connected branches should be destroyed
			// the application should take care of that using the appropriate factory method
			inputBranches[index].toBeRemoved = true;
		}

		inputBranches[index] = b;
	}

	@Override
	protected void addOut(Branch b, int index) {
		checkIndex(index, outCount());
		checkChangeable();
		outputBranches.get(index).add(b);
	}

	@Override
	protected void removeIn(Branch b, int index) {
		checkIndex(index, inCount());
		checkChangeable();

		if (inputBranches[index] == b) {
			inputBranches[index] = null;
		} else {
			// same as OutputPin.removeIn(Branch, int)
		}
	}

	@Override
	protected void removeOut(Branch b, int index) {
		checkIndex(index, outCount());
		checkChangeable();
		if (!outputBranches.get(index).remove(b))
			throw new ComponentNotFoundException(b, this);
	}

	/**
	 * Checks if the Input Pin at the given index is connected to a Branch. Should
	 * only be called by primitive gates that contain multiple gates e.g. NOT.
	 *
	 * @param index the index
	 * @return true if a branch is connected, false otherwise
	 */
	boolean checkBranch(int index) {
		return !(inputBranches[index] == null);
	}

	/**
	 * Checks if all of the Input Pins are connected to a Branch. A Gate shouldn't
	 * produce an output unless all of its Input Pins are connected to a Branch.
	 *
	 * @return true if all inputs are connected; if an output will be produced
	 */
	boolean checkBranches() {
		return all(inputBranches, b -> b != null);
	}

	@Override
	protected void attachListeners() {
		attachListeners_(DRAG_KB_FOCUS);
	}

	private static final int BOX_SIZE = 3;

	// functions to draw the pins. let subclasses specify any number of them
	private transient Function<Integer, Integer> dxi, dyi, dxo, dyo;

	private void addFunctions() {
		dxi = dxi();
		dyi = dyi();
		dxo = dxo();
		dyo = dyo();
	}

	@Override
	protected final void draw(Graphics g) {

		drawSprite(g);

		g.setColor(Color.BLACK);
		g.drawString(description, 7, (getHeight() / 2) + 5);

		for (int i = 0; i < inputPins.length; ++i) {
			g.setColor(inputBranches[i] != null ? inputBranches[i].getActive(0) ? Color.GREEN : Color.RED : Color.RED);
			drawPin(g, new Point(dxi.apply(i), dyi.apply(i)));
		}

		for (int i = 0; i < outputPins.length; ++i) {
			g.setColor(outputPins[i].getActive(0) ? Color.GREEN : Color.RED);
			drawPin(g, new Point(dxo.apply(i), dyo.apply(i)));
		}
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

	/** @return the Image used to draw the Primitive Gate */
	protected BufferedImage getImage() {
		return image;
	}

	private static void drawPin(Graphics g, Point p) {
		g.fillRect(p.x - (BOX_SIZE / 2), p.y - (BOX_SIZE / 2), BOX_SIZE, BOX_SIZE);
	}

	@Override
	protected void updateOnMovement() {
		foreach(inputBranches, Branch::updateOnMovement);
		foreach(outputBranches, vb -> foreach(vb, Branch::updateOnMovement));
	}

	@Override
	protected Point getBranchInputCoords(Branch b, int index) {
		checkIndex(index, outCount());

		if (outputBranches.get(index).contains(b))
			return new Point(getX() + dxo().apply(index), getY() + dyo().apply(index));

		throw new ComponentNotFoundException(b, this);
	}

	@Override
	protected Point getBranchOutputCoords(Branch b, int index) {
		checkIndex(index, inCount());

		if (inputBranches[index] == b)
			return new Point(getX() + dxi().apply(index), getY() + dyi().apply(index));

		throw new ComponentNotFoundException(b, this);
	}

	/** @return a Function for the x coordinate of the Input Pin at index i */
	protected Function<Integer, Integer> dxi() {
		return i -> BOX_SIZE / 2;
	}

	/** @return a Function for the y coordinate of the Input Pin at index i */
	protected Function<Integer, Integer> dyi() {
		final int gap = (getHeight() - (inputPins.length * BOX_SIZE)) / (inputPins.length + 1);
		return i -> (((i + 1) * (gap + BOX_SIZE)) - (BOX_SIZE / 2));
	}

	/** @return a Function for the x coordinate of the Output Pin at index i */
	protected Function<Integer, Integer> dxo() {
		return i -> (getWidth() - (BOX_SIZE / 2)) - 1;
	}

	/** @return a Function for the y coordinate of the Output Pin at index i */
	protected Function<Integer, Integer> dyo() {
		final int gap = (getHeight() - (outputPins.length * BOX_SIZE)) / (outputPins.length + 1);
		return i -> (((i + 1) * (gap + BOX_SIZE)) - (BOX_SIZE / 2));
	}
}
