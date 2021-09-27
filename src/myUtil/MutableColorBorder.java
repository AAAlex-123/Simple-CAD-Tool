package myUtil;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;

import javax.swing.border.LineBorder;

/**
 * A border for a {@link java.awt.Component} that can dynamically switch between
 * colors.
 *
 * @author dimits
 */
public class MutableColorBorder extends LineBorder {

	private static final long serialVersionUID = 1649237099464980877L;

	private Color currentColor;

	/**
	 * Creates a new line border with the specified color. The color can later be
	 * changed by calling the {@link #setColor(Color)} method.
	 *
	 * @param color the original color of the border
	 */
	public MutableColorBorder(Color color) {
		super(color);
		currentColor = color;
	}

	/**
	 * Changes the color of the border.
	 *
	 * @param newColor the new Color
	 */
	public void setColor(Color newColor) {
		currentColor = newColor;
	}

	/**
	 * Used by the renderer. Do NOT call this method directly. Use
	 * {@link #setColor(Color)} instead.
	 */
	@Override
	public void paintBorder(final Component c, final Graphics g, final int x, final int y,
	        final int width, final int height) {
		g.setColor(currentColor);
		super.lineColor = currentColor;
		super.paintBorder(c, g, x, y, width, height);
	}
}
