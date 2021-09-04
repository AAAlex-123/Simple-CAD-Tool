package application.editor;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import component.components.Component;

/**
 * A JPanel to display {@link component.components.Component Components} that repaints
 * itself when a {@code Component} is added or removed.
 *
 * @author alexm
 */
final class UI extends JPanel {

	/** Creates the UI */
	UI() {
		setLayout(null);
		setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
	}

	/**
	 * Adds a {@code Component} to the UI and repaints.
	 *
	 * @param c the Component
	 */
	void addComponent(Component c) {
		add(c.getGraphics());
		repaint();
	}

	/**
	 * Removes a {@code Component} from the UI and repaints.
	 *
	 * @param c the Component
	 */
	void removeComponent(Component c) {
		remove(c.getGraphics());
		repaint();
	}
}
