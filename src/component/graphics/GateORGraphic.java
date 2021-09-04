package component.graphics;

import java.awt.image.BufferedImage;
import java.util.function.Function;

import application.StringConstants;
import component.components.Component;

/**
 * Graphics for a {@link component.ComponentType#GATEOR GATEOR}.
 *
 * @author Alex Mandelias
 */
final class GateORGraphic extends GateGraphic {

	private static final long serialVersionUID = 2L;

	private static final String sprite = StringConstants.COMPONENT_ICON_PATH
	        + "gate_or.png"; //$NON-NLS-1$

	private static final BufferedImage image;

	static {
		image = ComponentGraphic.loadImage(GateORGraphic.sprite);
	}

	/**
	 * Constructs the Graphics object.
	 *
	 * @param component the related {@code Component}
	 */
	public GateORGraphic(Component component) {
		super(component);
	}

	@Override
	protected BufferedImage getImage() {
		return GateORGraphic.image;
	}

	@Override
	protected Function<Integer, Integer> dxi() {
		return i -> (int) (0.55 * Math.cos(1.2 * Math.asin(1 - (dyi().apply(i) / 20.0))) * 20);
	}
}
