package component;

import localisation.Languages;

/**
 * An enum containing information about the type of different components. Each
 * class of the {@link components} package links to one enum item. Users may
 * refer to the documentation found here for more information about how each
 * Component works in principle without worrying about specific implementations.
 * <p>
 * Component types include:
 * <ul>
 * <li>{@link ComponentType#INPUT_PIN Input Pin}</li>
 * <li>{@link ComponentType#OUTPUT_PIN Output Pin}</li>
 * <li>{@link ComponentType#BRANCH Branch}</li>
 * <li>{@link ComponentType#GATE Gate}</li>
 * <li>{@link ComponentType#GATEAND AND Gate}</li>
 * <li>{@link ComponentType#GATEAND OR Gate}</li>
 * <li>{@link ComponentType#GATENOT NOT Gate}</li>
 * <li>{@link ComponentType#GATENOT XOR Gate}</li>
 * </ul>
 */
public enum ComponentType {

	/**
	 * Input Pins are Components whose state may be altered only by the user.
	 * Specifically, the user may alter its state (on or off) by means provided by
	 * an Application, while other Components cannot alter its state. Unlike Gates
	 * and Output Pins, Input Pins do not have Branches as input, only as output.
	 */
	INPUT_PIN(Languages.getString("ComponentType.0")), //$NON-NLS-1$

	/**
	 * Output Pins are Components whose state may only be accessed by the user.
	 * Specifically, the user may access its state (on or off) by means provided by
	 * an Application, while other Component cannot access its state. Unlike Gates
	 * and Input Pins, Output Pins do not have Branches as output, only as input.
	 */
	OUTPUT_PIN(Languages.getString("ComponentType.1")), //$NON-NLS-1$

	/**
	 * Branches are connections between Components. Specifically, they transfer a
	 * signal from Component {@code c1} from its pin at index {@code i1} to
	 * Component {@code c2} to its pin at index {@code i2}. The term {@code pin}
	 * refers to the physical pin that would connect a component onto a circuit
	 * board. The indexes start at 0.
	 * <p>
	 * For example, an InputPin (i.e. a switch) may have one output that connects
	 * via a single pin to the circuit board and an AND Gate may have three inputs
	 * that connect via three pins. Branches connect to these pins.
	 */
	BRANCH(Languages.getString("ComponentType.2")), //$NON-NLS-1$

	/**
	 * Gates are Components that map a set of Input Pins to a set of Output Pins.
	 * Specifically, they essentially compute a boolean function from any number of
	 * inputs to any number of outputs. Components of this type (and not the other
	 * Gate types) may be defined by the user and compute the outputs by feeding the
	 * inputs into another circuit and taking its outputs.
	 */
	GATE(Languages.getString("ComponentType.3")), //$NON-NLS-1$

	/**
	 * AND Gates are a Gates that compute a single function. Specifically, they map
	 * the set of inputs to its logical `and`.
	 */
	GATEAND(Languages.getString("ComponentType.4")), //$NON-NLS-1$

	/**
	 * OR Gates are a Gates that compute a single function. Specifically, they map
	 * the set of inputs to its logical `or`.
	 */
	GATEOR(Languages.getString("ComponentType.5")), //$NON-NLS-1$

	/**
	 * NOT Gates are a Gates that compute a single function. Specifically, each
	 * input is matched to its logical `not`.
	 */
	GATENOT(Languages.getString("ComponentType.6")), //$NON-NLS-1$

	/**
	 * XOR Gates are a Gates that compute a single function. Specifically, they map
	 * the set of inputs to its logical `xor`.
	 */
	GATEXOR(Languages.getString("ComponentType.7")); //$NON-NLS-1$

	private String desc;

	private ComponentType(String description) {
		this.desc = description;
	}

	/**
	 * Returns the description of this ComponentType.
	 *
	 * @return the description
	 */
	public String description() {
		return desc;
	}
}
