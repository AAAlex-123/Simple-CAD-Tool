package components;

@SuppressWarnings({ "javadoc", "unused" })
final public class Test {

	public static void main(String[] args) {

		// input to output
		// lmao can't do that



		// basic NOT with 2 outputs
		System.out.println("BASIC NOT / MULTIPLE OUTPUTS FROM GATE");
		Component ip01 = ComponentFactory.createInputPin();
		// ip01.setIn(null, 0);
		Component op01 = ComponentFactory.createOutputPin();
		// ComponentFactory.setActive(op01, false);
		Component op02 = ComponentFactory.createOutputPin();
		Component g01 = ComponentFactory.createNOT();
		Component b01 = ComponentFactory.connectToGateInput(g01, ip01, 0);
		Component b02 = ComponentFactory.connectToGateOutput(g01, op01, 0);
		Component b03 = ComponentFactory.connectToGateOutput(g01, op02, 0);

		System.out.printf("op01, op02: %b, %b%n", op01.active, op02.active);
		ComponentFactory.setActive(ip01, false);
		System.out.printf("op01, op02: %b, %b%n", op01.active, op02.active);
		ComponentFactory.setActive(ip01, true);
		System.out.printf("op01, op02: %b, %b%n", op01.active, op02.active);



		// basic AND
		System.out.println("BASIC AND");
		Component ip11 = ComponentFactory.createInputPin();
		Component ip12 = ComponentFactory.createInputPin();
		Component op11 = ComponentFactory.createOutputPin();
		Component g11 = ComponentFactory.createAND(2);

		Component b11 = ComponentFactory.connectToGateInput(g11, ip11, 0);
		Component b12 = ComponentFactory.connectToGateInput(g11, ip12, 1);
		Component b13 = ComponentFactory.connectToGateOutput(g11, op11, 0);

		ComponentFactory.setActive(ip11, true);
		ComponentFactory.setActive(ip12, true);
		System.out.printf("op11: %b%n", op11.active);
		ComponentFactory.setActive(ip12, false);
		System.out.printf("op11: %b%n", op11.active);



		// disconnecting NOT then AND
		System.out.println("DISCONNECTING AND");
		System.out.printf("op01, op02: %b, %b%n", op01.active, op02.active);
		ComponentFactory.deleteBranch(b01);
		b01 = null;
		System.out.printf("op01, op02: %b, %b%n", op01.active, op02.active);
		ComponentFactory.deleteBranch(b03);
		System.out.printf("op01, op02: %b, %b%n", op01.active, op02.active);

		System.out.println("DISCONNECTING NOT");
		ComponentFactory.setActive(ip12, true);
		System.out.printf("op11: %b%n", op11.active);
		ComponentFactory.deleteBranch(b11);
		b11 = null;
		System.out.printf("op11: %b%n", op11.active);
		b11 = ComponentFactory.connectToGateInput(g11, ip11, 0);
		System.out.printf("op11: %b%n", op11.active);
		ComponentFactory.deleteBranch(b13);
		b13 = null;
		System.out.printf("op11: %b%n", op11.active);
		b13 = ComponentFactory.connectToGateOutput(g11, op11, 0);
		System.out.printf("op11: %b%n", op11.active);



		// 1st AND "feeding" into the 2nd AND
		System.out.println("1ST AND FEEDING INTO 2ND AND");
		Component ip21 = ComponentFactory.createInputPin();
		Component ip22 = ComponentFactory.createInputPin();
		Component ip23 = ComponentFactory.createInputPin();
		Component op21 = ComponentFactory.createOutputPin();

		Component g21 = ComponentFactory.createAND(2);
		Component g22 = ComponentFactory.createAND(2);

		Component b21 = ComponentFactory.connectToGateInput(g21, ip21, 0);
		Component b22 = ComponentFactory.connectToGateInput(g21, ip22, 1);
		Component b23 = ComponentFactory.connectToGateInput(g22, ip23, 0);
		Component b24 = ComponentFactory.connectToGateOutput(g22, op21, 0);
		Component b25 = ComponentFactory.connectGates(g21, 0, g22, 1);

		ComponentFactory.setActive(ip21, true);
		ComponentFactory.setActive(ip22, true);
		ComponentFactory.setActive(ip23, true);
		System.out.printf("op21: %b%n", op21.active);
		ComponentFactory.setActive(ip21, false);
		System.out.printf("op21: %b%n", op21.active);
		ComponentFactory.setActive(ip21, true);
		ComponentFactory.setActive(ip22, false);
		System.out.printf("op21: %b%n", op21.active);
		ComponentFactory.setActive(ip22, true);
		System.out.printf("op21: %b%n", op21.active);



		// multiple output branches
		System.out.println("MULTIPLE OUTPUTS FROM PIN");
		Component ip31 = ComponentFactory.createInputPin();
		Component op31 = ComponentFactory.createOutputPin();
		Component g31 = ComponentFactory.createAND(3);
		Component b31 = ComponentFactory.connectToGateInput(g31, ip31, 0);
		Component b32 = ComponentFactory.connectToGateInput(g31, ip31, 1);
		Component b33 = ComponentFactory.connectToGateInput(g31, ip31, 2);
		Component b34 = ComponentFactory.connectToGateOutput(g31, op31, 0);

		ComponentFactory.setActive(ip31, true);
		System.out.printf("op31: %b%n", op21.active);



		// create custom gate
		System.out.println("CUSTOM GATE");
		Component ip41 = ComponentFactory.createInputPin();
		Component ip42 = ComponentFactory.createInputPin();
		Component ip43 = ComponentFactory.createInputPin();
		Component op41 = ComponentFactory.createOutputPin();

		Component g41 = ComponentFactory.createAND(2);
		Component g42 = ComponentFactory.createAND(2);

		Component b41 = ComponentFactory.connectToGateInput(g41, ip41, 0);
		Component b42 = ComponentFactory.connectToGateInput(g41, ip42, 1);
		Component b43 = ComponentFactory.connectToGateInput(g42, ip43, 0);
		Component b45 = ComponentFactory.connectGates(g41, 0, g42, 1);
		Component b44 = ComponentFactory.connectToGateOutput(g42, op41, 0);

		Component and3 = ComponentFactory.createGate(new Component[] { ip41, ip42, ip43 }, new Component[] { op41 });

		// ComponentFactory.setActive(ip41, true);

		// connect to custom gate
		Component ip45 = ComponentFactory.createInputPin();
		Component ip46 = ComponentFactory.createInputPin();
		Component ip47 = ComponentFactory.createInputPin();
		Component op45 = ComponentFactory.createOutputPin();

		Component b46 = ComponentFactory.connectToGateInput(and3, ip45, 0);
		Component b47 = ComponentFactory.connectToGateInput(and3, ip46, 1);
		Component b48 = ComponentFactory.connectToGateInput(and3, ip47, 2);
		Component b49 = ComponentFactory.connectToGateOutput(and3, op45, 0);

		ComponentFactory.setActive(ip45, true);
		ComponentFactory.setActive(ip46, true);
		ComponentFactory.setActive(ip47, true);
		System.out.printf("op45: %b%n", op45.active);
		ComponentFactory.setActive(ip45, false);
		System.out.printf("op45: %b%n", op45.active);
		ComponentFactory.setActive(ip45, true);
		ComponentFactory.setActive(ip46, false);
		System.out.printf("op45: %b%n", op45.active);
		ComponentFactory.setActive(ip46, true);
		System.out.printf("op45: %b%n", op45.active);
	}
}
