package components;

import java.awt.Graphics;
import java.awt.Point;
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
			inputPins[i].setActive(false);
			inputPins[i].setOuterGate(this, i);
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
		if (checkBranches())
			inputPins[indexIn].wake_up(newActive, false);
	}

	@Override
	boolean getActive(int index) {
		checkIndex(index, outputPins.length);
		return outputPins[index].getActive(0);
	}

	@Override
	final void destroy() {
		for (Branch b : inputBranches)
			if (b != null)
				b.toBeRemoved = true;
		for (Vector<Branch> vb : outputBranches)
			for (Branch b : vb)
				if (b != null)
					b.toBeRemoved = true;
	}

	// informs this Gate that the state of an OutputPin has changed
	void outputChanged(int index) {
		checkIndex(index, outputPins.length);
		for (Branch b : outputBranches.get(index))
			if (b != null)
				b.wake_up(outputPins[index].getActive(0));
	}

	@Override
	void setIn(Branch b, int index) {
		checkIndex(index, inputPins.length);
		checkChangeable();

		if (inputBranches[index] != null) {
			inputBranches[index].toBeRemoved = true;
		}
		inputBranches[index] = b;
	}

	@Override
	void addOut(Branch b, int index) {
		checkIndex(index, outputPins.length);
		checkChangeable();
		outputBranches.get(index).add(b);
		b.wake_up(outputPins[index].getActive(0));
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
		for (Branch br : outputBranches.get(index)) {
			if (br == b) {
				outputBranches.get(index).remove(b);
				return;
			}
		}
		throw new ComponentNotFoundException(b, this);
	}

	@Override
	public String toString() {
		String str = String.format("%s: %d-%d", getClass().getSimpleName(), inputPins.length, outputPins.length);
		return String.format("%s (UID: %d)", changeable ? str : "(" + str + ")", UID);
	}

	boolean checkBranches() {
		for (int i = 0; i < inputBranches.length; ++i)
			if (inputBranches[i] == null)
				return false;
		return true;
	}

	@Override
	public void draw(Graphics g) {

		g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
		g.drawString(getClass().getSimpleName(), 0, getHeight() / 2);

		int dh = getHeight() / (inputPins.length + 1);
		for (int i = 0; i < inputPins.length; ++i) {
			g.drawRect(0, (i + 1) * dh, 5, 5);
		}
		dh = getHeight() / (outputPins.length + 1);
		for (int i = 0; i < outputPins.length; ++i) {
			g.drawRect(getWidth() - 5, (i + 1) * dh, 5, 5);
		}
	}

	@Override
	void updateOnMovement() {
		for (Branch b : inputBranches)
			if (b != null)
				b.updateOnMovement();
		for (Vector<Branch> vb : outputBranches)
			for (Branch b : vb)
				if (b != null)
					b.updateOnMovement();
	}

	@Override
	Point getBranchCoords(Branch b, int index) {
		if (inputBranches[index] == b) {
			int dh = getHeight() / (inputBranches.length + 1);
			return new Point(getX() + 0, getY() + ((index + 1) * dh));
		}

		for (int i = 0; i < outputBranches.get(index).size(); ++i) {
			if (outputBranches.get(index).get(i) == b) {
				int dh = getHeight() / (outputBranches.size() + 1);
				return new Point(getX() + getWidth(), getY() + ((index + 1) * dh));
			}
		}
		throw new ComponentNotFoundException(b, this);
	}
}
