package component.graphics;

import java.awt.image.BufferedImage;

import application.StringConstants;
import component.components.Component;
import component.components.GraphicHook;

/**
 * Graphics for an {@link component.ComponentType#INPUT_PIN INPUT_PIN}.
 *
 * @author Alex Mandelias
 */
final class InputPinGraphic extends ComponentGraphic {

	private static final long serialVersionUID = 2L;

	private static final String sprite = StringConstants.COMPONENT_ICON_PATH
	        + "input_pin_{state}.png"; //$NON-NLS-1$

	private static final BufferedImage image_on, image_off;

	static {
		image_on = ComponentGraphic.loadImage(InputPinGraphic.sprite.replace("{state}", "on")); //$NON-NLS-1$ //$NON-NLS-2$
		image_off = ComponentGraphic.loadImage(InputPinGraphic.sprite.replace("{state}", "off")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * Constructs the Graphics object.
	 *
	 * @param component the related {@code Component}
	 */
	public InputPinGraphic(Component component) {
		super(component);
	}

	@Override
	protected BufferedImage getImage() {
		return GraphicHook.getActiveOut(component, 0) ? InputPinGraphic.image_on
		        : InputPinGraphic.image_off;
	}

	@Override
	protected void attachListeners() {
		attachListenersByFlags((byte) (ComponentGraphic.DRAG_KB_FOCUS | ComponentGraphic.ACTIVATE));
	}
}
