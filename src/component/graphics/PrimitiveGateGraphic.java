package component.graphics;

import java.awt.image.BufferedImage;

import component.components.Component;

/**
 * Graphics for Primitive Gates. Since the behaviour of these {@code Components}
 * is hard-coded, their Graphic consists of a sprite that is loaded from an
 * image file.
 *
 * @author Alex Mandelias
 */
abstract class PrimitiveGateGraphic extends GateGraphic {

	private static final long serialVersionUID = 1L;

	/**
	 * Constructs the Graphics object.
	 *
	 * @param component the related {@code Component}
	 */
	public PrimitiveGateGraphic(Component component) {
		super(component);
	}

	/**
	 * Override to return a custom BufferedImage to be used to draw the Graphic.
	 *
	 * @implNote the default implementation returns a grey rectangle
	 */
	@Override
	protected BufferedImage getImage() {
		return super.getImage();
	}
}
