package components;

import static myUtil.Utility.all;
import static myUtil.Utility.foreach;

import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import exceptions.ComponentNotFoundException;
import exceptions.MalformedGateException;

/** Corresponds to the {@link ComponentType#GATE GATE} type. */
class Gate extends Component {

	private static final long serialVersionUID = 2L;

	private final Branch[] inputBranches;
	// Each pin has many Branches coming out of it (generic arrays aren't supported)
	private final Vector<Vector<Branch>> outputBranches;

	/** The Gate's inner Input Pins  (should only be accessed by subclasses) */
	protected final InputPin[] inputPins;

	/** The Gate's inner Output Pins (should only be accessed by subclasses) */
	protected final OutputPin[] outputPins;

	// TODO: make this work
	// private String customGateName;

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
	 * @param in  the Gate's inner Input Pins
	 * @param out the Gate's inner Output Pins
	 */
	Gate(InputPin[] in, OutputPin[] out) {
		inputBranches = new Branch[in.length];
		outputBranches = new Vector<>(out.length, 1);
		inputPins = in;
		outputPins = out;

		for (int i = 0; i < inputPins.length; ++i) {
			inputPins[i].setOuterGate();

			// propagate hiddenness and reset the state of the pins
			inputPins[i].wake_up(true);
			inputPins[i].wake_up(false, false);
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
	void wake_up(boolean newActive, int indexIn, boolean prevChangeable) {
		checkIndex(indexIn, inputBranches.length);

		// once hidden cannot be un-hidden
		if ((changeable == false) && (prevChangeable == true))
			throw new MalformedGateException(this);

		changeable = prevChangeable;

		// only propagate signal if all InputPins are connected to a Branch
		if (checkBranches())
			inputPins[indexIn].wake_up(newActive);
	}

	@Override
	void destroySelf() {
		foreach(inputBranches, Branch::destroy);
		Arrays.fill(inputBranches, null);

		List<Branch> ls = new ArrayList<>();
		foreach(outputBranches, vb -> ls.addAll(vb));
		foreach(ls, Branch::destroy);

		outputBranches.clear();
	}

	@Override
	void restore() {
		toBeRemoved = false;

		for (int i = 0; i < outputPins.length; ++i)
			outputBranches.add(new Vector<>(1, 1));
	}

	@Override
	boolean getActive(int index) {
		checkIndex(index, outputPins.length);
		return outputPins[index].getActive(0);
	}

	/**
	 * Informs this Gate that the state of an inner Output Pin has changed. The Gate
	 * then wakes up the Branches that are connected to that Output Pin.
	 *
	 * @param index the index of the OutputPin
	 */
	final void outputChanged(int index) {
		checkIndex(index, outputPins.length);

		foreach(outputBranches.get(index), b -> b.wake_up(outputPins[index].getActive(0), changeable));
	}

	@Override
	void setIn(Branch b, int index) {
		checkIndex(index, inputPins.length);
		checkChangeable();

		if (inputBranches[index] != null) {
			// declare that the connected branches should be destroyed
			// the application should take care of that using the appropriate factory method
			inputBranches[index].toBeRemoved = true;
		}

		inputBranches[index] = b;
	}

	@Override
	void addOut(Branch b, int index) {
		checkIndex(index, outputPins.length);
		checkChangeable();
		outputBranches.get(index).add(b);
	}

	@Override
	void removeIn(Branch b, int index) {
		checkIndex(index, inputPins.length);
		checkChangeable();
		if (inputBranches[index] == b) {
			inputBranches[index] = null;
		} else {
			// same as OutputPin.removeIn(Branch, int)
		}
	}

	@Override
	void removeOut(Branch b, int index) {
		checkIndex(index, outputPins.length);
		checkChangeable();

		if (!outputBranches.get(index).remove(b))
			throw new ComponentNotFoundException(b, this);
	}

	@Override
	public String toString() {
		// [<gate name>: <inN>-<outN> (UID: <UID>)], enclosed in '()' if hidden
		String str = String.format("%s: %d-%d", type().description(), inputPins.length, outputPins.length);
		return String.format("[%s (UID: %d)]", changeable ? str : "(" + str + ")", UID);
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
	void attachListeners() {
		attachListeners_(DRAG_KB_FOCUS);
	}

	@Override
	public void draw(Graphics g) {
		// crappy drawing
		g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
		g.drawString(getClass().getSimpleName(), 0, getHeight() / 2);

		int dh = getHeight() / (inputPins.length + 1);
		for (int i = 0; i < inputPins.length; ++i)
			g.drawRect(0, (i + 1) * dh, 5, 5);

		dh = getHeight() / (outputPins.length + 1);
		for (int i = 0; i < outputPins.length; ++i)
			g.drawRect(getWidth() - 5, (i + 1) * dh, 5, 5);
	}

	@Override
	void updateOnMovement() {
		foreach(inputBranches, Branch::updateOnMovement);
		foreach(outputBranches, vb -> foreach(vb, Branch::updateOnMovement));
	}

	@Override
	Point getBranchCoords(Branch b, int index) {
		if (inputBranches[index] == b) {
			int dh = getHeight() / (inputBranches.length + 1);
			return new Point(getX() + 0, getY() + ((index + 1) * dh));
		}

		if (outputBranches.get(index).contains(b)) {
			int dh = getHeight() / (outputBranches.size() + 1);
			return new Point(getX() + getWidth(), getY() + ((index + 1) * dh));
		}

		throw new ComponentNotFoundException(b, this);
	}
}
