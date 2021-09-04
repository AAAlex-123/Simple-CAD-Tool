package component.components;

import java.util.List;

import component.graphics.ComponentGraphic;

/**
 * Defines static methods to give to {@link component.graphics.ComponentGraphic
 * Graphic} objects access to the necessary information to fully represent
 * {@link Component Components}. Because that information shouldn't be public
 * and the Graphic hierarchy is in a different package, it is necessary for a
 * class like this to exist.
 * <p>
 * <b>Note:</b> this class should only be used in the Graphic hierarchy and
 * nowhere else, since the data and methods it exposes aren't meant to be
 * visible outside of the {@code component.components} package.
 *
 * @author Alex Mandelias
 */
public final class GraphicHook {

	/**
	 * Delegate method.
	 *
	 * @param component the Component
	 *
	 * @return the description
	 *
	 * @see Component#description()
	 */
	public static String description(Component component) {
		return component.description();
	}

	/**
	 * Delegate method.
	 *
	 * @param component the Component
	 * @param g         the Graphic
	 *
	 * @see Component#setGraphics(ComponentGraphic)
	 */
	public static void setGraphics(Component component, ComponentGraphic g) {
		component.setGraphics(g);
	}

	/**
	 * Delegate method.
	 *
	 * @param component the Component
	 * @param newActive the new active state for the Component
	 *
	 * @see Component#wake_up(boolean)
	 */
	public static void wake_up(Component component, boolean newActive) {
		component.wake_up(newActive);
	}

	/**
	 * Delegate method.
	 *
	 * @param component the Component
	 * @param index     the index of the pin on the Component
	 *
	 * @return the active state of the Component at the index
	 *
	 * @see Component#getActive(int)
	 */
	public static boolean getActive(Component component, int index) {
		return component.getActive(index);
	}

	/**
	 * Delegate method.
	 *
	 * @param component the Component
	 *
	 * @return the Component's inputs
	 *
	 * @see Component#getInputs()
	 */
	public static List<Component> getInputs(Component component) {
		return component.getInputs();
	}

	/**
	 * Delegate method.
	 *
	 * @param component the Component
	 *
	 * @return the Component's outputs
	 *
	 * @see Component#getOutputs()
	 */
	public static List<List<Component>> getOutputs(Component component) {
		return component.getOutputs();
	}

	/**
	 * Delegate method.
	 *
	 * @param component the Component
	 *
	 * @return the number of Components connected to this Component's input
	 *
	 * @see Component#inCount()
	 */
	public static int inCount(Component component) {
		return component.inCount();
	}

	/**
	 * Delegate method.
	 *
	 * @param component the Component
	 *
	 * @return the number of Components connected to this Component's output
	 *
	 * @see Component#outCount()
	 */
	public static int outCount(Component component) {
		return component.outCount();
	}

	/**
	 * Delegate method.
	 *
	 * @param component the Component
	 *
	 * @return the Component's hiddenness
	 *
	 * @see Component#hidden()
	 */
	public static boolean hidden(Component component) {
		return component.hidden();
	}
}
