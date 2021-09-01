package component.components;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import component.ComponentType;
import component.exceptions.ComponentNotFoundException;
import component.exceptions.MalformedGateException;
import myUtil.Utility;

/**
 * Corresponds to the {@link ComponentType#GATE GATE} type.
 *
 * @author Alex Mandelias
 */
class Gate extends Component {

	private static final long serialVersionUID = 6L;

	/** The Gate's incoming Branches (should only be accessed by subclasses) */
	protected final Branch[] inputBranches;

	/**
	 * The Gate's outgoing Branches (should only be accessed by subclasses). Each
	 * pin has many Branches coming out of it (generic arrays aren't supported)
	 */
	protected final List<List<Branch>> outputBranches;

	/** The Gate's inner Input Pins (should only be accessed by subclasses) */
	protected final InputPin[] inputPins;

	/** The Gate's inner Output Pins (should only be accessed by subclasses) */
	protected final OutputPin[] outputPins;

	/** Returned by {@link #description()} */
	private final String description;

	/**
	 * Constructs a Gate with the given number of Input and Output pins.
	 *
	 * @param inCount  the number of input pins
	 * @param outCount the number of output pins
	 */
	protected Gate(int inCount, int outCount) {
		inputBranches = new Branch[inCount];
		outputBranches = new ArrayList<>(outCount);
		inputPins = new InputPin[inCount];
		outputPins = new OutputPin[outCount];
		description = ""; //$NON-NLS-1$

		for (int i = 0; i < inCount; ++i) {
			inputPins[i] = new InputPin();
			inputPins[i].setOuterGate();
		}

		for (int i = 0; i < outCount; ++i) {
			outputPins[i] = new OutputPin();
			outputPins[i].setOuterGate(this, i);
			outputBranches.add(new ArrayList<>(1));
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
	protected Gate(InputPin[] in, OutputPin[] out, String desc) {
		inputBranches = new Branch[in.length];
		outputBranches = new ArrayList<>(out.length);
		inputPins = in;
		outputPins = out;
		description = desc;

		for (int i = 0, count = inputPins.length; i < count; ++i) {
			inputPins[i].setOuterGate();

			// propagate hiddenness and reset the state of the pins
			inputPins[i].wake_up(true);
			inputPins[i].wake_up(false, true);
		}

		for (int i = 0, count = outputPins.length; i < count; ++i) {
			outputPins[i].setOuterGate(this, i);
			outputBranches.add(new ArrayList<>(1));
		}
	}

	@Override
	public ComponentType type() {
		return ComponentType.GATE;
	}

	@Override
	protected final String description() {
		return description;
	}

	@Override
	protected void wake_up(boolean newActive, int indexIn, boolean prevHidden) {
		checkIndex(indexIn, inCount());
		// once hidden cannot be un-hidden
		if (hidden() && !prevHidden)
			throw new MalformedGateException(this);

		if (prevHidden)
			hideComponent();

		getGraphics().repaint();

		// only propagate signal if all InputPins are connected to a Branch
		if (checkBranches())
			inputPins[indexIn].wake_up(newActive);
	}

	@Override
	protected final int inCount() {
		return inputPins.length;
	}

	@Override
	protected final int outCount() {
		return outputPins.length;
	}

	/**
	 * Informs this Gate that the state of an inner Output Pin has changed. The Gate
	 * then wakes up the Branches that are connected to that Output Pin.
	 *
	 * @param index the index of the OutputPin
	 */
	final void outputChanged(int index) {
		checkIndex(index, outCount());

		Utility.foreach(outputBranches.get(index),
		        b -> b.wake_up(outputPins[index].getActive(0), hidden()));
	}

	@Override
	protected final boolean getActive(int index) {
		checkIndex(index, outCount());
		return outputPins[index].getActive(0);
	}

	@Override
	protected final void setIn(Branch b, int index) {
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
	protected final void addOut(Branch b, int index) {
		checkIndex(index, outCount());
		checkChangeable();
		outputBranches.get(index).add(b);
	}

	@Override
	protected final void removeIn(Branch b, int index) {
		checkIndex(index, inCount());
		checkChangeable();

		if (inputBranches[index] == b)
			inputBranches[index] = null;
		else {
			// same as OutputPin.removeIn(Branch, int)
		}
	}

	@Override
	protected final void removeOut(Branch b, int index) {
		checkIndex(index, outCount());
		checkChangeable();
		if (!outputBranches.get(index).remove(b))
			throw new ComponentNotFoundException(b, this);
	}

	@Override
	protected final void destroySelf() {
		Utility.foreach(inputBranches, Branch::destroy);
		Arrays.fill(inputBranches, null);
		final List<Branch> ls = new ArrayList<>();
		Utility.foreach(outputBranches, vb -> ls.addAll(vb));
		Utility.foreach(ls, Branch::destroy);

		outputBranches.clear();
	}

	@Override
	protected final void restoreDeletedSelf() {
		for (int i = 0, count = outputPins.length; i < count; ++i)
			outputBranches.add(new ArrayList<>(1));
	}

	@Override
	protected final void restoreSerialisedSelf() {}

	/**
	 * Checks if the Input Pin at the given index is connected to a Branch. Should
	 * only be called by primitive gates that contain multiple gates e.g. NOT.
	 *
	 * @param index the index
	 *
	 * @return true if a branch is connected, false otherwise
	 */
	protected final boolean checkBranch(int index) {
		return (inputBranches[index] != null);
	}

	/**
	 * Checks if all of the Input Pins are connected to a Branch. A Gate shouldn't
	 * produce an output unless all of its Input Pins are connected to a Branch.
	 *
	 * @return true if all inputs are connected; if an output will be produced
	 */
	protected final boolean checkBranches() {
		return Utility.all(inputBranches, b -> b != null);
	}

	@Override
	protected final List<Component> getInputs() {
		return Collections.unmodifiableList(Arrays.asList(inputBranches));
	}

	@Override
	protected final List<List<Component>> getOutputs() {
		final List<List<Component>> ls = new ArrayList<>();
		Utility.foreach(outputBranches, v -> ls.add(Collections.unmodifiableList(v)));
		return Collections.unmodifiableList(ls);
	}
}
