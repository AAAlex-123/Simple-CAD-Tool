package components;

import exceptions.InvalidComponentException;

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
 * In order to hide implementation details, methods of this factory handle
 * objects of type {@code Component} but will only accept specific subclasses,
 * indicated both by the parameter name and in the javadoc comment. In case of
 * an object of the wrong subclass being provided, a RuntimeException will be
 * thrown.
 */
public final class ComponentFactory {

	/** Don't let anyone instantiate this class */
	private ComponentFactory() {}

	/**
	 * Creates an {@code InputPin}.
	 *
	 * @return the InputPin
	 */
	public static Component createInputPin() {
		return new InputPin();
	}

	/**
	 * Creates an {@code OutputPin}.
	 *
	 * @return the OutputPin
	 */
	public static Component createOutputPin() {
		return new OutputPin();
	}

	/**
	 * Creates a composite {@code Gate} by, effectively, packing all of the
	 * Components between the {@code InputPin}s and the {@code OutputPin}s into a
	 * new {@code Gate}. This renders the components hidden and cannot be directly
	 * accessed or modified in any way.
	 *
	 * @param inputPins  the new gate's input pins
	 * @param outputPins the new gate's output pins
	 * @return the created Gate
	 */
	public static Component createGate(Component[] inputPins, Component[] outputPins) {
		InputPin[] inp = new InputPin[inputPins.length];
		OutputPin[] outp = new OutputPin[outputPins.length];
		try {
			for (int i = 0; i < inp.length; ++i)
				inp[i] = (InputPin) inputPins[i];
			for (int i = 0; i < outp.length; ++i)
				outp[i] = (OutputPin) outputPins[i];
			return new Gate(inp, outp);
		} catch (ClassCastException e) {
			throw new InvalidComponentException(e);
		}
	}

	/**
	 * Creates an AND {@code Gate} with a given number of inputs.
	 *
	 * @param inCount the number of inputs
	 * @return the AND Gate
	 */
	public static Component createAND(int inCount) {
		return new GateAND(inCount);
	}

	/**
	 * Creates a NOT {@code Gate}.
	 *
	 * @return the NOT Gate
	 */
	public static Component createNOT() {
		return new GateNOT();
	}

	/**
	 * Connects two {@code Component}s by creating a {@code Branch} between them at
	 * the specified indexes.
	 *
	 * @param in       the Branch's input
	 * @param indexIn  the index of the pin on the input gate
	 * @param out      the Branch's input
	 * @param indexOut the index of the pin on the output gate
	 * @return the created Branch
	 */
	public static Component connectComponents(Component in, int indexIn, Component out, int indexOut) {
		return new Branch(in, indexIn, out, indexOut);
	}

	/**
	 * Removes {@code Component} and deletes some {@code Branch}es.
	 *
	 * @param c the Component to delete
	 */
	public static void deleteComponent(Component c) {
		c.destroy();
	}

	/**
	 * Sets the state of the {@code InputPin} as Active or Inactive.
	 *
	 * @param inputPin the InputPin
	 * @param active   true or false (active or inactive)
	 */
	public static void setActive(Component inputPin, boolean active) {
		try {
			((InputPin) inputPin).setActive(active);
		} catch (ClassCastException e) {
			throw new InvalidComponentException(e);
		}
	}

	/**
	 * Returns the state of the {@code OutputPin}.
	 *
	 * @param outputPin the OutputPin
	 * @return true or false (active or inactive)
	 */
	public static boolean getActive(Component outputPin) {
		try {
			return ((OutputPin) outputPin).getActive();
		} catch (ClassCastException e) {
			throw new InvalidComponentException(e);
		}
	}

	/**
	 * Returns whether or not the {@code Component} is to be destroyed.
	 *
	 * @param c the component to check
	 * @return boolean
	 */
	public static boolean toDestroy(Component c) {
		return c.toRemove();
	}

	/** @param b the {@code Branch} to reconnect */
	public static void reconnectBranch(Component b) {
		try {
			((Branch) b).connect();
		} catch (ClassCastException e) {
			throw new InvalidComponentException(e);
		}
	}
}
