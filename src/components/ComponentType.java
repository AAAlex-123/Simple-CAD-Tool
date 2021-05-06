package components;

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
	INPUT_PIN("Input Pin"),

	/**
	 * Output Pins are Components whose state may only be accessed by the user.
	 * Specifically, the user may access its state (on or off) by means provided by
	 * an Application, while other Component cannot access its state. Unlike Gates
	 * and Input Pins, Output Pins do not have Branches as output, only as input.
	 */
	OUTPUT_PIN("Output Pin"),

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
	BRANCH("Branch"),

	/** A value representing a Gate */
	GATE("Gate"),

	/** A value representing an AND Gate */
	GATEAND("AND Gate"),

	/** A value representing an OR Gate */
	GATEOR("OR Gate"),

	/** A value representing a NOT Gate */
	GATENOT("NOT Gate"),

	/** A value representing a XOR Gate */
	GATEXOR("XOR Gate");

	private String desc;

	private ComponentType(String desc) {
		this.desc = desc;
	}

	/**
	 * Returns the description of the ComponentType.
	 * 
	 * @return the description
	 */
	public String description() {
		return desc;
	}
}
