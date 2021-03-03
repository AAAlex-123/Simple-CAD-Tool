package components;

@SuppressWarnings({ "javadoc", "static-access", "unused" })
final public class Test {

	static ComponentFactory cf = new ComponentFactory();

	public static void main(String[] args) {

		// input to output
		// lmao can't do that



		// basic NOT with 2 outputs
		System.out.println("BASIC NOT / MULTIPLE OUTPUTS FROM GATE");
		InputPin ip01 = cf.createInputPin();
		// ip01.setIn(null, 0);
		OutputPin op01 = cf.createOutputPin();
		OutputPin op02 = cf.createOutputPin();
		Gate g01 = cf.createNOT();
		Branch b01 = cf.connectToGateInput(g01, ip01, 0);
		Branch b02 = cf.connectToGateOutput(g01, op01, 0);
		Branch b03 = cf.connectToGateOutput(g01, op02, 0);

		System.out.printf("op01, op02: %b, %b%n", op01.active, op02.active);
		cf.setActive(ip01, false);
		System.out.printf("op01, op02: %b, %b%n", op01.active, op02.active);
		cf.setActive(ip01, true);
		System.out.printf("op01, op02: %b, %b%n", op01.active, op02.active);



		// basic AND
		System.out.println("BASIC AND");
		InputPin ip11 = cf.createInputPin();
		InputPin ip12 = cf.createInputPin();
		OutputPin op11 = cf.createOutputPin();
		Gate g11 = cf.createAND(2);

		Branch b11 = cf.connectToGateInput(g11, ip11, 0);
		Branch b12 = cf.connectToGateInput(g11, ip12, 1);
		Branch b13 = cf.connectToGateOutput(g11, op11, 0);

		cf.setActive(ip11, true);
		cf.setActive(ip12, true);
		System.out.printf("op11: %b%n", op11.active);
		cf.setActive(ip12, false);
		System.out.printf("op11: %b%n", op11.active);



		// disconnecting NOT then AND (look with debugger too)
		System.out.println("DISCONNECTING AND");
		System.out.printf("op01, op02: %b, %b%n", op01.active, op02.active);
		cf.deleteBranch(b01);
		b01 = null;
		System.out.printf("op01, op02: %b, %b%n", op01.active, op02.active);
		b03.disconnect();
		System.out.printf("op01, op02: %b, %b%n", op01.active, op02.active);

		System.out.println("DISCONNECTING NOT");
		cf.setActive(ip12, true);
		System.out.printf("op11: %b%n", op11.active);
		cf.deleteBranch(b11);
		b11 = null;
		System.out.printf("op11: %b%n", op11.active);
		b11 = cf.connectToGateInput(g11, ip11, 0);
		System.out.printf("op11: %b%n", op11.active);
		cf.deleteBranch(b13);
		b13 = null;
		System.out.printf("op11: %b%n", op11.active);
		b13 = cf.connectToGateOutput(g11, op11, 0);
		System.out.printf("op11: %b%n", op11.active);



		// 1st AND "feeding" into the 2nd AND
		System.out.println("1ST AND FEEDING INTO 2ND AND");
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

		cf.setActive(ip21, true);
		cf.setActive(ip22, true);
		cf.setActive(ip23, true);
		System.out.printf("op21: %b%n", op21.active);
		cf.setActive(ip21, false);
		System.out.printf("op21: %b%n", op21.active);
		cf.setActive(ip21, true);
		cf.setActive(ip22, false);
		System.out.printf("op21: %b%n", op21.active);
		cf.setActive(ip22, true);
		System.out.printf("op21: %b%n", op21.active);



		// multiple output branches
		System.out.println("MULTIPLE OUTPUTS FROM PIN");
		InputPin ip31 = cf.createInputPin();
		OutputPin op31 = cf.createOutputPin();
		Gate g31 = cf.createAND(3);
		Branch b31 = cf.connectToGateInput(g31, ip31, 0);
		Branch b32 = cf.connectToGateInput(g31, ip31, 1);
		Branch b33 = cf.connectToGateInput(g31, ip31, 2);
		Branch b34 = cf.connectToGateOutput(g31, op31, 0);

		cf.setActive(ip31, true);
		System.out.printf("op31: %b%n", op21.active);



		// create custom gate
		System.out.println("CUSTOM GATE");
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

		cf.setActive(ip45, true);
		cf.setActive(ip46, true);
		cf.setActive(ip47, true);
		System.out.printf("op45: %b%n", op45.active);
		cf.setActive(ip45, false);
		System.out.printf("op45: %b%n", op45.active);
		cf.setActive(ip45, true);
		cf.setActive(ip46, false);
		System.out.printf("op45: %b%n", op45.active);
		cf.setActive(ip46, true);
		System.out.printf("op45: %b%n", op45.active);
	}
}
