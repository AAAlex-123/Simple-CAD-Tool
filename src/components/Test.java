package components;

@SuppressWarnings({ "javadoc", "static-access" })
public class Test {

	// TODO: unspaghettify everything

	static ComponentFactory cf = new ComponentFactory();

	public static void main(String[] args) {
		final Pin[] p = new Pin[30];
		final InputPin[] ip = new InputPin[30];
		final OutputPin[] op = new OutputPin[30];
		final Branch[] b = new Branch[7];
		final Gate[] g = new Gate[5];

		ip[0] = cf.createInputPin();
		p[1] = cf.createPin();
		op[2] = cf.createOutputPin();
		op[3] = cf.createOutputPin();
		// cf.connectPins(op[2], op[3]);
		// cf.connectPins(ip[0], p[5]);

		b[0] = cf.connectPins(ip[0], p[1]);
		// b[0].setIn(op[2]);
		b[1] = cf.connectPins(p[1], op[2]);

		System.out.printf("p0: %b, p1: %b, op2: %b, b0: %b, b1: %b%n", ip[0].getActive(), p[1].getActive(),
				op[2].getActive(), b[0].getActive(), b[1].getActive());

		ip[0].setActive(true);
		System.out.printf("p0: %b, p1: %b, op2: %b, b0: %b, b1: %b%n", ip[0].getActive(), p[1].getActive(),
				op[2].getActive(), b[0].getActive(), b[1].getActive());


		ip[2] = cf.createInputPin();
		ip[3] = cf.createInputPin();
		op[7] = cf.createOutputPin();

		g[0] = cf.createAND(2);
		cf.connectToGateInput(ip[2], g[0]);
		cf.connectToGateInput(ip[3], g[0]);
		cf.connectToGateOutput(g[0], op[7]);

		ip[2].setActive(true);
		ip[3].setActive(true);
		System.out.printf("op[7]: %b%n", op[7].getActive());



		p[15] = cf.createPin();
		p[16] = cf.createPin();
		p[17] = cf.createPin();
		p[19] = cf.createPin();
		p[20] = cf.createPin();

		g[1] = cf.createAND(2);
		cf.connectToGateInput(p[15], g[1]);
		cf.connectToGateInput(p[16], g[1]);
		cf.connectToGateOutput(g[1], p[19]);

		g[2] = cf.createAND(2);
		cf.connectToGateInput(p[17], g[2]);
		cf.connectToGateInput(p[19], g[2]);
		cf.connectToGateOutput(g[2], p[20]);

		g[3] = cf.createGate(new Pin[] { p[15], p[16], p[17] }, new Pin[] { p[20] });

		ip[15] = cf.createInputPin();
		ip[16] = cf.createInputPin();
		ip[17] = cf.createInputPin();
		op[25] = cf.createOutputPin();

		cf.connectToGateInput(ip[15], g[3]);
		cf.connectToGateInput(ip[16], g[3]);
		cf.connectToGateInput(ip[17], g[3]);
		cf.connectToGateOutput(g[3], op[25]);

		ip[15].setActive(true);
		ip[16].setActive(true);
		ip[17].setActive(true);
		System.out.printf("op[25]: %b%n", op[25].getActive());




		Gate and3 = g[3];

		ip[20] = cf.createInputPin();
		ip[21] = cf.createInputPin();
		ip[22] = cf.createInputPin();
		op[29] = cf.createOutputPin();

		cf.clearGate(and3);

		cf.connectToGateInput(ip[20], and3);
		cf.connectToGateInput(ip[21], and3);
		cf.connectToGateInput(ip[22], and3);
		cf.connectToGateOutput(and3, op[29]);

		ip[20].setActive(true);
		ip[21].setActive(false);
		ip[22].setActive(true);
		System.out.printf("op[29]: %b%n", op[29].getActive());
	}
}
