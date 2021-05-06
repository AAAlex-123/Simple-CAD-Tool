package application;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import components.Component;

/** A UI. Probably will be replaced by an Editor */
class UI extends JPanel {

	/** Creates a UI that can display components */
	UI() {
		setLayout(null);
		setBorder(BorderFactory.createLineBorder(Color.black));
	}

	/**
	 * Adds a {@code Component} and repaints.
	 *
	 * @param c the Component
	 */
	void addComponent(Component c) {
		add(c);
		repaint();
	}

	/**
	 * Removes a {@code Component} and repaints.
	 *
	 * @param c the Component
	 */
	void removeComponent(Component c) {
		remove(c);
		repaint();
	}
}
