package component.graphics;

import java.awt.image.BufferedImage;
import java.util.function.Function;

import application.StringConstants;
import component.components.Component;

/**
 * Graphics for a {@link component.ComponentType#GATEXOR GATEXOR}.
 *
 * @author Alex Mandelias
 */
final class GateXORGraphic extends GateGraphic {

	private static final long serialVersionUID = 2L;

	private static final String sprite = StringConstants.COMPONENT_ICON_PATH + "gate_xor.png"; //$NON-NLS-1$

	private static final BufferedImage image;

	static {
		image = ComponentGraphic.loadImage(GateXORGraphic.sprite);
	}

	/**
	 * Constructs the Graphics object.
	 *
	 * @param component the related {@code Component}
	 */
	public GateXORGraphic(Component component) {
		super(component);
	}

	@Override
	protected BufferedImage getImage() {
		return GateXORGraphic.image;
	}

	@Override
	protected Function<Integer, Integer> dxi() {
		return i -> (int) (0.55 * Math.cos(1.2 * Math.asin(1 - (dyi().apply(i) / 20.0))) * 20);
	}
}
