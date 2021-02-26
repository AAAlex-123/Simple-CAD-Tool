package components;

@SuppressWarnings({ "javadoc", "static-access", "unused" })
final public class Test {

	static ComponentFactory cf = new ComponentFactory();

	public static void main(String[] args) {

		// input to output
		// lmao can't do that

		// basic AND
		InputPin ip11 = cf.createInputPin();
		InputPin ip12 = cf.createInputPin();
		OutputPin op11 = cf.createOutputPin();
		Gate g11 = cf.createAND(2);

		Branch b11 = cf.connectToGateInput(g11, ip11, 0);
		Branch b12 = cf.connectToGateInput(g11, ip12, 1);
		Branch b13 = cf.connectToGateOutput(g11, op11, 0);

		ip11.wake_up(true);
		ip12.wake_up(true);

		System.out.printf("op11: %b%n", op11.active);



		// 1st AND "feeding" into the 2nd AND
		InputPin ip21 = cf.createInputPin();
		InputPin ip22 = cf.createInputPin();
		InputPin ip23 = cf.createInputPin();
		OutputPin op21 = cf.createOutputPin();

		Gate g21 = cf.createAND(2);
		Gate g22 = cf.createAND(2);

		Branch b21 = cf.connectToGateInput(g21, ip21, 0);
		Branch b22 = cf.connectToGateInput(g21, ip22, 1);
		Branch b23 = cf.connectToGateInput(g22, ip23, 0);
		Branch b24 = cf.connectToGateOutput(g22, op21, 0);
		Branch b25 = cf.connectGates(g21, 0, g22, 1);

		ip21.wake_up(true);
		ip22.wake_up(true);
		ip23.wake_up(true);
		System.out.printf("op21: %b%n", op21.active);
		ip21.wake_up(false);
		System.out.printf("op21: %b%n", op21.active);
		ip21.wake_up(true);
		ip22.wake_up(false);
		System.out.printf("op21: %b%n", op21.active);
		ip22.wake_up(true);
		System.out.printf("op21: %b%n", op21.active);



		// multiple output branches
		InputPin ip31 = cf.createInputPin();
		OutputPin op31 = cf.createOutputPin();
		Gate g31 = cf.createAND(2);
		Branch b31 = cf.connectToGateInput(g31, ip31, 0);
		Branch b32 = cf.connectToGateInput(g31, ip31, 1);
		Branch b33 = cf.connectToGateOutput(g31, op31, 0);

		ip31.wake_up(true);
		System.out.printf("op31: %b%n", op21.active);



		// create custom gate
		InputPin ip41 = cf.createInputPin();
		InputPin ip42 = cf.createInputPin();
		InputPin ip43 = cf.createInputPin();
		OutputPin op41 = cf.createOutputPin();

		Gate g41 = cf.createAND(2);
		Gate g42 = cf.createAND(2);

		Branch b41 = cf.connectToGateInput(g41, ip41, 0);
		Branch b42 = cf.connectToGateInput(g41, ip42, 1);
		Branch b43 = cf.connectToGateInput(g42, ip43, 0);
		Branch b45 = cf.connectGates(g41, 0, g42, 1);
		Branch b44 = cf.connectToGateOutput(g42, op41, 0);


		Gate and3 = cf.createGate(new InputPin[] { ip41, ip42, ip43 }, new OutputPin[] { op41 });

		// connect to custom gate
		InputPin ip45 = cf.createInputPin();
		InputPin ip46 = cf.createInputPin();
		InputPin ip47 = cf.createInputPin();
		OutputPin op45 = cf.createOutputPin();

		Branch b46 = cf.connectToGateInput(and3, ip45, 0);
		Branch b47 = cf.connectToGateInput(and3, ip46, 1);
		Branch b48 = cf.connectToGateInput(and3, ip47, 2);
		Branch b49 = cf.connectToGateOutput(and3, op45, 0);



		ip45.wake_up(true);
		ip46.wake_up(true);
		ip47.wake_up(true);
		System.out.printf("op45: %b%n", op45.active);
		ip45.wake_up(false);
		System.out.printf("op45: %b%n", op45.active);
		ip45.wake_up(true);
		ip46.wake_up(false);
		System.out.printf("op45: %b%n", op45.active);
		ip46.wake_up(true);
		System.out.printf("op45: %b%n", op45.active);
	}
}
