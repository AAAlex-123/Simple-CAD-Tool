package application.editor;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import components.Component;

/**
 * The UI the Editor uses to display Components.
 *
 * @author alexm
 */
final class UI extends JPanel {

	/** Creates the UI that displays components */
	UI() {
		setLayout(null);
		setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
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
}
