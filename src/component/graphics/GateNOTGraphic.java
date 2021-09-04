package component.graphics;

import java.awt.image.BufferedImage;

import application.StringConstants;
import component.components.Component;
import component.components.GraphicHook;

/**
 * Graphics for a {@link component.ComponentType#GATENOT GATENOT}.
 *
 * @author Alex Mandelias
 */
final class GateNOTGraphic extends GateGraphic {

	private static final long serialVersionUID = 2L;

	private static final String sprite = StringConstants.COMPONENT_ICON_PATH
	        + "gate_not.png"; //$NON-NLS-1$

	private static final BufferedImage image;

	static {
		image = ComponentGraphic.loadImage(GateNOTGraphic.sprite);
	}

	/**
	 * Constructs the Graphics object.
	 *
	 * @param component the related {@code Component}
	 */
	public GateNOTGraphic(Component component) {
		super(component);
	}

	@Override
	protected BufferedImage getImage() {
		// the triangle with a circle image can't be used with multiple inputs
		if (GraphicHook.inCount(component) > 1)
			return super.getImage();

		return GateNOTGraphic.image;
	}
}
