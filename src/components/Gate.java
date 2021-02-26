package components;

class Gate extends Drawable {

	Branch[] in, out;
	InputPin[] inputPins;
	OutputPin[] outputPins;

	Gate(int inN, int outN) {
		in = new Branch[inN];
		out = new Branch[outN];
		inputPins = new InputPin[inN];
		outputPins = new OutputPin[outN];

		for (int i = 0; i < inN; ++i)
			inputPins[i] = new InputPin();
		for (int i = 0; i < outN; ++i) {
			outputPins[i] = new OutputPin();
			outputPins[i].setOuterGate(this, i);
		}
	}

	Gate(InputPin[] in, OutputPin[] out) {
		this.in = new Branch[in.length];
		this.out = new Branch[out.length];
		inputPins = in;
		outputPins = out;

		for (int i = 0; i < in.length; ++i) {
			inputPins[i].deactivate();
			wake_up(false, i);
		}

		for (int i = 0; i < out.length; ++i) {
			outputPins[i].checkChangeable();
			outputPins[i].setOuterGate(this, i);
		}
	}

	void wake_up(boolean newActive, int indexIn) {
		inputPins[indexIn].wake_up(newActive);
	}

	void outputChanged(int index) {
		if (out[index] != null)
			out[index].wake_up(outputPins[index].active);
	}

	void setIn(Branch b, int index) {
		checkChangeable();
		in[index] = b;
	}

	void setOut(Branch b, int index) {
		checkChangeable();
		out[index] = b;
	}
}
