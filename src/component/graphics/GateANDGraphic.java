package component.graphics;

import java.awt.image.BufferedImage;

import application.StringConstants;
import component.components.Component;

/**
 * Graphics for a {@link component.ComponentType#GATEAND GATEAND}.
 *
 * @author Alex Mandelias
 */
final class GateANDGraphic extends GateGraphic {

	private static final long serialVersionUID = 2L;

	private static final String sprite = StringConstants.COMPONENT_ICON_PATH
	        + "gate_and.png"; //$NON-NLS-1$

	private static final BufferedImage image;

	static {
		image = ComponentGraphic.loadImage(GateANDGraphic.sprite);
	}

	/**
	 * Constructs the Graphics object.
	 *
	 * @param component the related {@code Component}
	 */
	public GateANDGraphic(Component component) {
		super(component);
	}

	@Override
	protected BufferedImage getImage() {
		return GateANDGraphic.image;
	}
}
