package component.graphics;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;

import component.components.Component;
import component.components.GraphicHook;

/**
 * Graphics for a {@link component.ComponentType#BRANCH BRANCH}.
 *
 * @author Alex Mandelias
 */
final class BranchGraphic extends ComponentGraphic {

	private static final long serialVersionUID = 1L;

	// graphics, 1 or -1, slope of the line
	//  1 = draw top-left to bottom-right
	// -1 = draw bottom-left to top-right
	private int direction;

	/**
	 * Constructs the Graphics object.
	 *
	 * @param component the related {@code Component}
	 */
	public BranchGraphic(Component component) {
		super(component);
	}

	@Override
	protected void attachListeners() {
		attachListenersByFlags((byte) 0);
	}

	@Override
	protected void draw(Graphics g) {
		g.setColor(GraphicHook.getActiveIn(component, 0) ? Color.green : Color.red);

		// draw with correct direction (as specified in its declaration)
		if (direction == 1)
			g.drawLine(5, 5, getWidth() - 5, getHeight() - 6);
		else if (direction == -1)
			g.drawLine(5, getHeight() - 6, getWidth() - 5, 5);
		else
			throw new RuntimeException("Invalid Branch direction"); //$NON-NLS-1$
	}

	@Override
	protected void drawPins(Graphics g) {}

	@Override
	protected void drawID(Graphics g) {
		g.setColor(Color.BLACK);
		g.drawString(component.getID(), (getWidth() / 2) - 4, (getHeight() / 2) + 5);
	}

	@Override
	protected BufferedImage getImage() {
		return null;
	}

	@Override
	protected void updateOnMovement() {
		// from the new coordinates calculate the Branch's start point, width and height
		// and also calculate its direction (as specified in its declaration).
		final Component in  = GraphicHook.getInputs(component).get(0);
		final Component out = GraphicHook.getOutputs(component).get(0).get(0);
		final Point     p1  = in.getGraphics().getBranchInputCoords(component);
		final Point     p2  = out.getGraphics().getBranchOutputCoords(component);
		direction = ((p2.x - p1.x) * (p2.y - p1.y)) > 0 ? 1 : -1;
		// components with a dimension = 0 aren't drawn and text can't be drawn on a
		// small space, so extra width/height is added here and removed it when drawing
		setBounds(Math.min(p1.x, p2.x) - 5, Math.min(p1.y, p2.y) - 5, Math.abs(p2.x - p1.x) + 11,
		        Math.abs(p2.y - p1.y) + 11);
	}
}
