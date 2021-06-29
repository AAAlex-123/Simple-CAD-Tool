package application;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;

import components.Component;

/** A UI. Will probably be replaced by an Editor */
class UI extends JPanel {

	/** Creates the UI that displays components */
	UI() {
		setLayout(null);
	}

	/**
	 * Adds a {@code Component} to the UI.
	 *
	 * @param c the Component
	 */
	void addComponent(Component c) {
		add(c);
		repaint();
	}

	/**
	 * Removes a {@code Component} from the UI.
	 *
	 * @param c the Component
	 */
	void removeComponent(Component c) {
		remove(c);
		repaint();
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.setColor(Color.BLACK);
		g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
	}
}
