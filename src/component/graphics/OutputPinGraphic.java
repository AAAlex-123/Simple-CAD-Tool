package component.graphics;

import java.awt.image.BufferedImage;

import application.StringConstants;
import component.components.Component;
import component.components.GraphicHook;

/**
 * Graphics for an {@link component.ComponentType#OUTPUT_PIN OUTPUT_PIN}.
 *
 * @author Alex Mandelias
 */
final class OutputPinGraphic extends ComponentGraphic {

	private static final long serialVersionUID = 2L;

	private static final String sprite = StringConstants.COMPONENT_ICON_PATH
	        + "output_pin_{state}.png"; //$NON-NLS-1$

	private static final BufferedImage image_on, image_off;

	static {
		image_on = ComponentGraphic.loadImage(OutputPinGraphic.sprite.replace("{state}", "on")); //$NON-NLS-1$ //$NON-NLS-2$
		image_off = ComponentGraphic.loadImage(OutputPinGraphic.sprite.replace("{state}", "off")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * Constructs the Graphics object.
	 *
	 * @param component the related {@code Component}
	 */
	public OutputPinGraphic(Component component) {
		super(component);
	}

	@Override
	protected BufferedImage getImage() {
		return GraphicHook.getActiveIn(component, 0) ? OutputPinGraphic.image_on
		        : OutputPinGraphic.image_off;
	}

	@Override
	protected void attachListeners() {
		attachListenersByFlags(ComponentGraphic.DRAG_KB_FOCUS);
	}
}
