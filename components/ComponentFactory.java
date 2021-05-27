package components;

import static components.ComponentType.INPUT_PIN;
import static components.ComponentType.OUTPUT_PIN;
import static myUtil.Utility.foreach;

import exceptions.InvalidComponentException;
import exceptions.MalformedBranchException;

/**
 * A set of static methods that acts as the interface of the {@link components}
 * package. Clients can only interact with {@link components.Component
 * Components} using methods of this class.
 * <p>
 * Available Components are:
 * <ul>
 * <li>{@code InputPin}: get input signal from client</li>
 * <li>{@code OutputPin}: return output signal to client</li>
 * <li>{@code Gate}: map a set of InputPins to a set of OutputPins</li>
 * <li>{@code Branch}: connect the above components</li>
 * </ul>
 * A more detailed list of them and their properties can be found in the
 * {@link ComponentType} enum.
 * <p>
 * In order to hide implementation details, methods of this factory handle
 * objects of type {@code Component} but will only accept specific subclasses,
 * indicated both by the parameter name and in the javadoc comment. In case of
 * an object of the wrong subclass being provided, an Exception will be thrown.
 * If everything is designed correctly, however, the methods should never throw.
 */
public final class ComponentFactory {

	/** Don't let anyone instantiate this class */
	private ComponentFactory() {}

	/**
	 * Creates an {@code InputPin}.
	 *
	 * @return the InputPin
	 * @see ComponentType#INPUT_PIN
	 */
	public static Component createInputPin() {
		return new InputPin();
	}

	/**
	 * Creates an {@code OutputPin}.
	 *
	 * @return the OutputPin
	 * @see ComponentType#OUTPUT_PIN
	 */
	public static Component createOutputPin() {
		return new OutputPin();
	}

	/**
	 * Connects two {@code Component}s by creating a {@code Branch} between them at
	 * the specified {@code indexes}.
	 *
	 * @param in       the Branch's input
	 * @param indexIn  the index of the pin on the input gate
	 * @param out      the Branch's input
	 * @param indexOut the index of the pin on the output gate
	 * @return the created Branch
	 * @throws MalformedBranchException in the case of connecting invalid components
	 * @see ComponentType#BRANCH
	 */
	public static Component connectComponents(Component in, int indexIn, Component out, int indexOut)
			throws MalformedBranchException {
		return new Branch(in, indexIn, out, indexOut);
	}

	/**
	 * Creates a {@code Primitive Gate} of a specific {@code type} with a given
	 * number of inputs.
	 *
	 * @param type    the type of the Primitive Gate
	 * @param inCount the number of inputs
	 * @return the Primitive Gate
	 * @see ComponentType#GATEAND
	 * @see ComponentType#GATEOR
	 * @see ComponentType#GATENOT
	 * @see ComponentType#GATEXOR
	 */
	public static Component createPrimitiveGate(ComponentType type, int inCount) {
		switch (type) {
		case GATEAND:
			return new GateAND(inCount);
		case GATEOR:
			return new GateOR(inCount);
		case GATENOT:
			return new GateNOT(inCount);
		case GATEXOR:
			return new GateXOR(inCount);
		default:
			throw new RuntimeException("wrong primitive gate type");
		}
	}

	/**
	 * Creates a composite {@code Gate} by, effectively, packing all of the
	 * Components between the {@code InputPin}s and the {@code OutputPin}s into a
	 * new {@code Gate}. This renders the components hidden and cannot be directly
	 * accessed or modified in any way.
	 *
	 * @param inputPins   the new Gate's input pins
	 * @param outputPins  the new Gate's output pins
	 * @param description the new Gate's description
	 * @return the created Gate
	 * 
	 * @see ComponentType#GATE
	 */
	public static Component createGate(Component[] inputPins, Component[] outputPins, String description) {

		// check component type
		foreach(inputPins, t -> checkType(t, INPUT_PIN));
		foreach(outputPins, t -> checkType(t, OUTPUT_PIN));

		// cast to correct type and create Gate
		InputPin[] inp = new InputPin[inputPins.length];
		OutputPin[] outp = new OutputPin[outputPins.length];

		for (int i = 0; i < inp.length; ++i)
			inp[i] = (InputPin) inputPins[i];

		for (int i = 0; i < outp.length; ++i)
			outp[i] = (OutputPin) outputPins[i];

		return new Gate(inp, outp, description);
	}

	/**
	 * Destroys a {@code Component}.
	 *
	 * @param c the Component to delete
	 * @see Component#destroy()
	 */
	public static void destroyComponent(Component c) {
		c.destroy();
	}

	/**
	 * Returns whether or not the {@code Component} is to be removed from the
	 * Application. Only destroyed {@code Components} can be removed.
	 *
	 * @param c the component to check
	 * @return boolean
	 * @see Component#toBeRemoved
	 */
	public static boolean toRemove(Component c) {
		return c.toRemove();
	}

	/**
	 * Sets the state of the {@code InputPin} as Active or Inactive.
	 *
	 * @param inputPin the InputPin
	 * @param active   true or false (active or inactive)
	 */
	public static void setActive(Component inputPin, boolean active) {
		checkType(inputPin, INPUT_PIN);

		((InputPin) inputPin).setActive(active);
	}

	/**
	 * Returns the state of the {@code OutputPin}.
	 *
	 * @param outputPin the OutputPin
	 * @return true or false (active or inactive)
	 */
	public static boolean getActive(Component outputPin) {
		checkType(outputPin, OUTPUT_PIN);

		return ((OutputPin) outputPin).getActive();
	}

	/**
	 * Restores the state of a destroyed {@code Component} so that it can function.
	 *
	 * @param component the Component
	 */
	public static void restoreDeletedComponent(Component component) {
		component.restoreDeleted();
	}

	/**
	 * Restores the state of a serialised {@code Component} so that it can function.
	 *
	 * @param component the Component
	 */
	public static void restoreSerialisedComponent(Component component) {
		component.restoreSerialised();
	}

	/**
	 * Checks if the type of a Component matches the required type. If there is a
	 * mismatch, an exception is thrown. If everything is designed correctly, this
	 * should never throw.
	 *
	 * @param c    the Component to check
	 * @param type the required type
	 */
	private static void checkType(Component c, ComponentType type) {
		if (c.type() != type)
			throw new InvalidComponentException(c, type);
	}
}
