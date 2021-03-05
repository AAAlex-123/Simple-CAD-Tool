package components;

import java.util.Vector;

import exceptions.ComponentNotFoundException;

// A Component that maps InputPins to OutputPins
class Gate extends Component {

	private final Branch[] inputBranches;
	private final Vector<Vector<Branch>> outputBranches;

	// inner pins
	final InputPin[] inputPins;
	final OutputPin[] outputPins;

	Gate(int inN, int outN) {
		inputBranches = new Branch[inN];
		outputBranches = new Vector<>(outN);
		inputPins = new InputPin[inN];
		outputPins = new OutputPin[outN];

		for (int i = 0; i < inN; ++i) {
			inputPins[i] = new InputPin();
			inputPins[i].setOuterGate(this, i);
		}
		for (int i = 0; i < outN; ++i) {
			outputPins[i] = new OutputPin();
			outputPins[i].setOuterGate(this, i);
			outputBranches.add(new Vector<>(1));
		}
	}

	// constructs a composite gate consisting of everything between the InputPins
	// and the OutputPins, essentially packing that circuit into a gate, and
	// behaves exactly as it would had it not been packed into a gate.
	Gate(InputPin[] in, OutputPin[] out) {
		inputBranches = new Branch[in.length];
		outputBranches = new Vector<>(out.length);
		inputPins = in;
		outputPins = out;

		for (int i = 0; i < inputPins.length; ++i) {
			inputPins[i].setOuterGate(this, i);
			inputPins[i].setActive(false);
		}

		for (int i = 0; i < outputPins.length; ++i) {
			outputPins[i].setOuterGate(this, i);
			outputBranches.add(new Vector<>(1));
		}
	}

	@Override
	void wake_up(boolean newActive, int indexIn, boolean prevChangeable) {
		checkIndex(indexIn, inputBranches.length);
		changeable = prevChangeable;

		// only propagate signal if all InputPins are connected
		for (int i = 0; i < inputBranches.length; ++i)
			if (inputBranches[i] == null)
				return;

		inputPins[indexIn].wake_up(newActive, false);
	}

	// informs this Gate that an OutputPin has changed value
	void outputChanged(int index) {
		checkIndex(index, outputPins.length);
		for (Branch b : outputBranches.get(index))
			if (b != null)
				b.wake_up(outputPins[index].active);
	}

	@Override
	void setIn(Branch b, int index) {
		checkIndex(index, inputPins.length);
		checkChangeable();
		inputBranches[index] = b;
		wake_up(inputBranches[index].active, index);
	}

	@Override
	void addOut(Branch b, int index) {
		checkIndex(index, outputPins.length);
		checkChangeable();
		outputBranches.get(index).add(b);
		b.wake_up(outputPins[index].active);
	}

	@Override
	void removeIn(Branch b, int index) {
		checkIndex(index, inputPins.length);
		checkChangeable();
		if (inputBranches[index] != b)
			throw new ComponentNotFoundException(b);

		inputBranches[index] = null;
	}

	@Override
	void removeOut(Branch b, int index) {
		checkIndex(index, outputPins.length);
		checkChangeable();
		for (Branch br : outputBranches.get(index)) {
			if (br == b) {
				outputBranches.get(index).remove(b);
				return;
			}
		}
		throw new ComponentNotFoundException(b);
	}

	@Override
	public String toString() {
		String str = String.format("%s: %d-%d", getClass().getSimpleName(), inputPins.length, outputPins.length);
		return changeable ? str : "(" + str + ")";
	}
}
