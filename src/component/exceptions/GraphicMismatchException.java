package component.exceptions;

import component.ComponentType;
import component.components.Component;
import component.graphics.ComponentGraphic;

/**
 * Thrown when a {@code Component} tries to set its {@code Graphic} but fails.
 *
 * @author Alex Mandelias
 */
public class GraphicMismatchException extends RuntimeException {

	/**
	 * Constructs the exception with two {@code ComponentTypes}.
	 *
	 * @param componentType the type of the Component
	 * @param graphicType   the type of the Graphic, that is different than the
	 *                      Component's type
	 */
	public GraphicMismatchException(ComponentType componentType, ComponentType graphicType) {
		super(String.format("Graphic of type %s can't be used for Component of type %s", //$NON-NLS-1$
		        graphicType, componentType));
	}

	/**
	 * Constructs the exception with a {@code Component} and a {@code Graphic}.
	 *
	 * @param component the Component
	 * @param g         the Graphic whose associated Component is different than the
	 *                  one given
	 */
	public GraphicMismatchException(Component component, ComponentGraphic g) {
		super(String.format(
		        "Graphic %s is not associated with Component %s. Another Component is using this Graphic.", //$NON-NLS-1$
		        g, component));
	}
}
