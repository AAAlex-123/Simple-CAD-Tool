package components;

@SuppressWarnings({ "javadoc", "static-access" })
public class Test {

	// TODO: unspaghettify everything

	static ComponentFactory cf = new ComponentFactory();

	public static void main(String[] args) {
		final Pin[] p = new Pin[30];
		final InputPin[] ip = new InputPin[30];
		final Branch[] b = new Branch[7];
		final Gate[] g = new Gate[5];

		ip[0] = cf.createInputPin(true);
		p[1] = new Pin();
		p[2] = new Pin();

		b[0] = cf.connectPins(ip[0], p[1]);
		b[1] = cf.connectPins(p[1], p[2]);

		System.out.printf("p0: %b, p1: %b, p2: %b, b0: %b, b1: %b%n", ip[0].getActive(),
				p[1].getActive(), p[2].getActive(), b[0].getActive(), b[1].getActive());

		ip[0].wake_up();
		System.out.printf("p0: %b, p1: %b, p2: %b, b0: %b, b1: %b%n", ip[0].getActive(),
				p[1].getActive(), p[2].getActive(), b[0].getActive(), b[1].getActive());


		ip[2] = cf.createInputPin(true);
		ip[3] = cf.createInputPin(true);
		p[7] = new Pin();

		g[0] = new GateAND(2, 1);
		cf.connectToGateInput(ip[2], g[0]);
		cf.connectToGateInput(ip[3], g[0]);
		cf.connectToGateOutput(g[0], p[7]);

		ip[2].wake_up();
		ip[3].wake_up();
		System.out.printf("p[7]: %b%n", p[7].getActive());



		p[15] = new Pin();
		p[16] = new Pin();
		p[17] = new Pin();
		p[19] = new Pin();
		p[20] = new Pin();

		g[1] = new GateAND(2, 1);
		cf.connectToGateInput(p[15], g[1]);
		cf.connectToGateInput(p[16], g[1]);
		cf.connectToGateOutput(g[1], p[19]);

		g[2] = new GateAND(2, 1);
		cf.connectToGateInput(p[17], g[2]);
		cf.connectToGateInput(p[19], g[2]);
		cf.connectToGateOutput(g[2], p[20]);

		g[3] = new Gate(new Pin[] { p[15], p[16], p[17] }, new Pin[] { p[20] });

		ip[15] = cf.createInputPin(true);
		ip[16] = cf.createInputPin(true);
		ip[17] = cf.createInputPin(true);
		p[25] = new Pin();

		cf.connectToGateInput(ip[15], g[3]);
		cf.connectToGateInput(ip[16], g[3]);
		cf.connectToGateInput(ip[17], g[3]);
		cf.connectToGateOutput(g[3], p[25]);

		ip[15].wake_up();
		ip[16].wake_up();
		ip[17].wake_up();
		System.out.printf("p[25]: %b%n", p[25].getActive());




		Gate and3 = g[3];

		ip[20] = cf.createInputPin(false);
		ip[21] = cf.createInputPin(true);
		ip[22] = cf.createInputPin(true);
		p[29] = new Pin();

		cf.clearGate(and3);

		cf.connectToGateInput(ip[20], and3);
		cf.connectToGateInput(ip[21], and3);
		cf.connectToGateInput(ip[22], and3);
		cf.connectToGateOutput(and3, p[29]);

		ip[20].wake_up();
		ip[21].wake_up();
		ip[22].wake_up();
		System.out.printf("p[29]: %b%n", p[29].getActive());
	}
}
