package components;

import static java.lang.Math.abs;
import static java.lang.Math.min;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

/**
 * Handles the Graphics of an {@link OutputPin}.
 *
 * @author alexm
 */
final class BranchGraphic extends ComponentGraphic {

	private static final long serialVersionUID = 1L;

	// graphics, 1 or -1, slope of the line
	//  1 = draw top-left to bottom-right
	// -1 = draw bottom-left to top-right
	private int direction;

	/**
	 * Constructs the graphics object
	 *
	 * @param c the related Component
	 */
	public BranchGraphic(Component c) {
		super(c);
	}

	@Override
	protected void attachListeners() {
		attachListeners_((byte) 0);
	}

	@Override
	protected void draw(Graphics g) {
		g.setColor(component.getActive(0) ? Color.green : Color.red);

		// draw with correct direction (as specified in the `direction` declaration)
		if (direction == 1)
			g.drawLine(5, 5, getWidth() - 5, getHeight() - 6);
		else if (direction == -1)
			g.drawLine(5, getHeight() - 6, getWidth() - 5, 5);
		else
			throw new RuntimeException("Invalid Branch direction"); //$NON-NLS-1$
	}

	@Override
	protected void drawPins(Graphics g) {
	}

	@Override
	protected void drawID(Graphics g) {
		g.setColor(Color.BLACK);
		g.drawString(component.getID(), (getWidth() / 2) - 4, (getHeight() / 2) + 5);
	}

	@Override
	protected void updateOnMovement() {
		// from the new coordinates calculate the Branch's start point, width and height
		// and also calculate its direction (as specified in the declaration).
		final Component in  = component.getInputs().get(0);
		final Component out = component.getOutputs().get(0).get(0);
		Point           p1  = in.getGraphics().getBranchInputCoords(component);
		Point           p2  = out.getGraphics().getBranchOutputCoords(component);
		direction = ((p2.x - p1.x) * (p2.y - p1.y)) > 0 ? 1 : -1;
		// components with a dimension = 0 aren't drawn and text can't be drawn on a
		// small space so add extra width/height here and remove it when drawing
		setBounds(min(p1.x, p2.x) - 5, min(p1.y, p2.y) - 5, abs(p2.x - p1.x) + 11,
				abs(p2.y - p1.y) + 11);
	}
}
