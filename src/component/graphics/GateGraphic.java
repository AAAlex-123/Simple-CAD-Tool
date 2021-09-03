package component.graphics;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import application.StringConstants;
import component.components.Component;

/**
 * Graphics for a {@link component.ComponentType#GATE GATE}.
 *
 * @author Alex Mandelias
 */
class GateGraphic extends ComponentGraphic {

	private static final long serialVersionUID = 1L;

	private static final String sprite = StringConstants.COMPONENT_ICON_PATH
	        + "gate.png"; //$NON-NLS-1$

	private static final BufferedImage image;

	static {
		image = ComponentGraphic.loadImage(GateGraphic.sprite);
	}

	/**
	 * Constructs the Graphics object.
	 *
	 * @param component the related {@code Component}
	 */
	public GateGraphic(Component component) {
		super(component);
	}

	@Override
	protected final void draw(Graphics g) {
		g.drawImage(getImage(), 0, 0, null);
	}

	/** @implNote the default implementation returns a grey rectangle */
	@Override
	protected BufferedImage getImage() {
		return GateGraphic.image;
	}

	@Override
	protected void attachListeners() {
		attachListenersByFlags(ComponentGraphic.DRAG_KB_FOCUS);
	}
}
