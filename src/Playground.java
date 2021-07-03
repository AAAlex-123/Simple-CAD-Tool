

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.StringTokenizer;

import components.ComponentFactory;
import components.Component;

@SuppressWarnings("javadoc")
public class Playground {

	private static final HashMap<String, Component> map = new HashMap<>();
	private static final Scanner scanner = new Scanner(System.in);

	public static void main(String[] args) {

		while (true) {
			try {
				System.out.printf("> ");
				String input = scanner.nextLine();
				StringTokenizer tk = new StringTokenizer(input);

				String action = tk.nextToken();
				if (action.equalsIgnoreCase("create")) {
					String component = tk.nextToken();
					if (component.equalsIgnoreCase("in")) {
						String name;
						while (!(name = tk.nextToken()).equals("/"))
							map.put(name, ComponentFactory.createInputPin());
					} else if (component.equalsIgnoreCase("out")) {
						String name;
						while (!(name = tk.nextToken()).equals("/"))
							map.put(name, ComponentFactory.createOutputPin());
					} else if (component.equalsIgnoreCase("and")) {
						String amt;
						String name;
						while (!(amt = tk.nextToken()).equals("/") && (!(name = tk.nextToken()).equals("/")))
							map.put(name, ComponentFactory.createAND(Integer.parseInt(amt)));
					} else if (component.equalsIgnoreCase("not")) {
						String name;
						while ((!(name = tk.nextToken()).equals("/")))
							map.put(name, ComponentFactory.createNOT());
					} else if (component.equalsIgnoreCase("gate")) {
						String name = tk.nextToken();
						String pin;
						ArrayList<Component> inputs = new ArrayList<>(), outputs = new ArrayList<>();

						while (!(pin = tk.nextToken()).equals("/"))
							inputs.add(map.get(pin));
						while (!(pin = tk.nextToken()).equals("/"))
							outputs.add(map.get(pin));
						map.put(name, ComponentFactory.createGate((Component[]) inputs.toArray(),
								(Component[]) outputs.toArray()));
					}
					else {
						System.err.printf("'in', 'out', 'and', 'not', 'gate' only");
					}
				} else if (action.equalsIgnoreCase("connect")) {
					String type = tk.nextToken();
					if (type.equalsIgnoreCase("gti")) {
						String gate = tk.nextToken();
						String pin = tk.nextToken();
						String index = tk.nextToken();
						String name = tk.nextToken();
						map.put(name, ComponentFactory.connectToGateInput(map.get(gate), map.get(pin),
								Integer.parseInt(index)));
					} else if (type.equalsIgnoreCase("gto")) {
						String gate = tk.nextToken();
						String pin = tk.nextToken();
						String index = tk.nextToken();
						String name = tk.nextToken();
						map.put(name, ComponentFactory.connectToGateOutput(map.get(gate), map.get(pin),
								Integer.parseInt(index)));
					} else if (type.equalsIgnoreCase("gtg")) {
						String gate1 = tk.nextToken();
						String index1 = tk.nextToken();
						String gate2 = tk.nextToken();
						String index2 = tk.nextToken();
						String name = tk.nextToken();
						map.put(name, ComponentFactory.connectGates(map.get(gate1), Integer.parseInt(index1),
								map.get(gate2), Integer.parseInt(index2)));
					} else {
						System.err.printf("'gti', 'gto', 'gtg' only");
					}
				} else if (action.equalsIgnoreCase("set")) {
					String pin = tk.nextToken();
					String value = tk.nextToken();
					ComponentFactory.setActive(map.get(pin), Boolean.parseBoolean(value));
				} else if (action.equalsIgnoreCase("get")) {
					String pin = tk.nextToken();
					System.out.println(ComponentFactory.getActive(map.get(pin)));
				} else if (action.equalsIgnoreCase("rb")) {
					String branch = tk.nextToken();
					ComponentFactory.deleteBranch(map.get(branch));
				} else {
					System.err.printf("'create', 'connect', 'set', 'get', 'rb' only");
				}
			} catch (Exception e) {
				System.err.printf("Exception %s occured.%n", e.getClass());
				e.printStackTrace();
			}
		}
	}
}
