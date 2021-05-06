package components;

import static components.ComponentType.GATEAND;
import static components.ComponentType.GATENOT;

/** A class to test the components package */
@SuppressWarnings({ "unused" })
final public class Test {

	/**
	 * The main method that runs the tests.
	 *
	 * @param args command line arguments (not used)
	 */
	public static void main(String[] args) {

		Component a = ComponentFactory.createInputPin();
		Component b = ComponentFactory.createOutputPin();
		Component c = ComponentFactory.createPrimitiveGate(GATEAND, 3);
		Component d = ComponentFactory.createPrimitiveGate(GATENOT, 1);
		Component e = ComponentFactory.connectComponents(a, 0, d, 0);
		Component f = ComponentFactory.connectComponents(d, 0, b, 0);
		System.out.printf("%s%n%s%n%s%n%s%n%s%n%s%n", a, b, c, d, e, f);

		// input to output
		System.out.println("INPUT TO OUTPUT");
		Component ip001 = ComponentFactory.createInputPin();
		Component op001 = ComponentFactory.createOutputPin();
		Component b001 = ComponentFactory.connectComponents(ip001, 0, op001, 0);

		System.out.printf("op001: %b%n", op001.getActive(0));
		ComponentFactory.setActive(ip001, true);
		System.out.printf("op001: %b%n", op001.getActive(0));



		// basic NOT with 2 outputs
		System.out.println("BASIC NOT / MULTIPLE OUTPUTS FROM GATE");
		Component ip01 = ComponentFactory.createInputPin();
		// ip01.setIn(null, 0);
		Component op01 = ComponentFactory.createOutputPin();
		// ComponentFactory.setActive(op01, false);
		Component op02 = ComponentFactory.createOutputPin();
		Component g01 = ComponentFactory.createPrimitiveGate(GATENOT, 1);
		Component b01 = ComponentFactory.connectComponents(ip01, 0, g01, 0);
		Component b02 = ComponentFactory.connectComponents(g01, 0, op01, 0);
		Component b03 = ComponentFactory.connectComponents(g01, 0, op02, 0);

		System.out.printf("op01, op02: %b, %b%n", op01.getActive(0), op02.getActive(0));
		ComponentFactory.setActive(ip01, false);
		System.out.printf("op01, op02: %b, %b%n", op01.getActive(0), op02.getActive(0));
		ComponentFactory.setActive(ip01, true);
		System.out.printf("op01, op02: %b, %b%n", op01.getActive(0), op02.getActive(0));


		// basic AND
		System.out.println("BASIC AND");
		Component ip11 = ComponentFactory.createInputPin();
		Component ip12 = ComponentFactory.createInputPin();
		Component op11 = ComponentFactory.createOutputPin();
		Component g11 = ComponentFactory.createPrimitiveGate(GATEAND, 2);

		Component b11 = ComponentFactory.connectComponents(ip11, 0, g11, 0);
		Component b12 = ComponentFactory.connectComponents(ip12, 0, g11, 1);
		Component b13 = ComponentFactory.connectComponents(g11, 0, op11, 0);

		ComponentFactory.setActive(ip11, true);
		ComponentFactory.setActive(ip12, true);
		System.out.printf("op11: %b%n", op11.getActive(0));
		ComponentFactory.setActive(ip12, false);
		System.out.printf("op11: %b%n", op11.getActive(0));



		// disconnecting NOT then AND
		System.out.println("DISCONNECTING AND");
		System.out.printf("op01, op02: %b, %b%n", op01.getActive(0), op02.getActive(0));
		ComponentFactory.destroyComponent(b01);
		b01 = null;
		System.out.printf("op01, op02: %b, %b%n", op01.getActive(0), op02.getActive(0));
		ComponentFactory.destroyComponent(b03);
		System.out.printf("op01, op02: %b, %b%n", op01.getActive(0), op02.getActive(0));

		System.out.println("DISCONNECTING NOT");
		ComponentFactory.setActive(ip12, true);
		System.out.printf("op11: %b%n", op11.getActive(0));
		ComponentFactory.destroyComponent(b11);
		b11 = null;
		System.out.printf("op11: %b%n", op11.getActive(0));
		b11 = ComponentFactory.connectComponents(ip11, 0,g11, 0);
		System.out.printf("op11: %b%n", op11.getActive(0));
		ComponentFactory.destroyComponent(b13);
		b13 = null;
		System.out.printf("op11: %b%n", op11.getActive(0));
		b13 = ComponentFactory.connectComponents(g11, 0, op11, 0);
		System.out.printf("op11: %b%n", op11.getActive(0));



		// 1st AND "feeding" into the 2nd AND
		System.out.println("1ST AND FEEDING INTO 2ND AND");
		Component ip21 = ComponentFactory.createInputPin();
		Component ip22 = ComponentFactory.createInputPin();
		Component ip23 = ComponentFactory.createInputPin();
		Component op21 = ComponentFactory.createOutputPin();

		Component g21 = ComponentFactory.createPrimitiveGate(GATEAND, 2);
		Component g22 = ComponentFactory.createPrimitiveGate(GATEAND, 2);

		Component b21 = ComponentFactory.connectComponents(ip21, 0, g21, 0);
		Component b22 = ComponentFactory.connectComponents(ip22, 0, g21, 1);
		Component b23 = ComponentFactory.connectComponents(ip23, 0, g22, 0);
		Component b24 = ComponentFactory.connectComponents(g22, 0, op21, 0);
		Component b25 = ComponentFactory.connectComponents(g21, 0, g22, 1);

		ComponentFactory.setActive(ip21, true);
		ComponentFactory.setActive(ip22, true);
		ComponentFactory.setActive(ip23, true);
		System.out.printf("op21: %b%n", op21.getActive(0));
		ComponentFactory.setActive(ip21, false);
		System.out.printf("op21: %b%n", op21.getActive(0));
		ComponentFactory.setActive(ip21, true);
		ComponentFactory.setActive(ip22, false);
		System.out.printf("op21: %b%n", op21.getActive(0));
		ComponentFactory.setActive(ip22, true);
		System.out.printf("op21: %b%n", op21.getActive(0));



		// multiple output branches
		System.out.println("MULTIPLE OUTPUTS FROM PIN");
		Component ip31 = ComponentFactory.createInputPin();
		Component op31 = ComponentFactory.createOutputPin();
		Component g31 = ComponentFactory.createPrimitiveGate(GATEAND, 3);
		Component b31 = ComponentFactory.connectComponents(ip31, 0, g31, 0);
		Component b32 = ComponentFactory.connectComponents(ip31, 0, g31, 1);
		Component b33 = ComponentFactory.connectComponents(ip31, 0, g31, 2);
		Component b34 = ComponentFactory.connectComponents(g31, 0, op31, 0);

		ComponentFactory.setActive(ip31, true);
		System.out.printf("op31: %b%n", op21.getActive(0));

		// create custom gate
		System.out.println("CUSTOM GATE");
		Component ip41 = ComponentFactory.createInputPin();
		Component ip42 = ComponentFactory.createInputPin();
		Component ip43 = ComponentFactory.createInputPin();
		Component op41 = ComponentFactory.createOutputPin();

		Component g41 = ComponentFactory.createPrimitiveGate(GATEAND, 2);
		Component g42 = ComponentFactory.createPrimitiveGate(GATEAND, 2);

		Component b41 = ComponentFactory.connectComponents(ip41, 0, g41, 0);
		Component b42 = ComponentFactory.connectComponents(ip42, 0, g41, 1);
		Component b43 = ComponentFactory.connectComponents(ip43, 0, g42, 0);
		Component b45 = ComponentFactory.connectComponents(g41, 0, g42, 1);
		Component b44 = ComponentFactory.connectComponents(g42, 0, op41, 0);

		Component and3 = ComponentFactory.createGate(new Component[] { ip41, ip42, ip43 }, new Component[] { op41 });

		// ComponentFactory.setActive(ip41, true);

		// connect to custom gate
		Component ip45 = ComponentFactory.createInputPin();
		Component ip46 = ComponentFactory.createInputPin();
		Component ip47 = ComponentFactory.createInputPin();
		Component op45 = ComponentFactory.createOutputPin();

		Component b46 = ComponentFactory.connectComponents(ip45, 0, and3, 0);
		Component b47 = ComponentFactory.connectComponents(ip46, 0, and3, 1);
		Component b48 = ComponentFactory.connectComponents(ip47, 0, and3, 2);
		Component b49 = ComponentFactory.connectComponents(and3, 0, op45, 0);

		ComponentFactory.setActive(ip45, true);
		ComponentFactory.setActive(ip46, true);
		ComponentFactory.setActive(ip47, true);
		System.out.printf("op45: %b%n", op45.getActive(0));
		ComponentFactory.setActive(ip45, false);
		System.out.printf("op45: %b%n", op45.getActive(0));
		ComponentFactory.setActive(ip45, true);
		ComponentFactory.setActive(ip46, false);
		System.out.printf("op45: %b%n", op45.getActive(0));
		ComponentFactory.setActive(ip46, true);
		System.out.printf("op45: %b%n", op45.getActive(0));
	}
}
