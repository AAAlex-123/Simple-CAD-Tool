package application.editor;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import component.components.Component;

/**
 * A JPanel to display {@code Components} that repaints itself when a Component
 * is added or removed.
 *
 * @author Alex Mandelias
 *
 * @see Component
 */
final class UI extends JPanel {

	/** Creates the UI */
	public UI() {
		setLayout(null);
		setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
	}

	/**
	 * Adds a {@code Component} to the JPanel and repaints.
	 *
	 * @param component the Component to add
	 */
	public void addComponent(Component component) {
		add(component.getGraphics());
		repaint();
	}

	/**
	 * Removes a {@code Component} from the JPanel and repaints.
	 *
	 * @param component the Component and remove
	 */
	public void removeComponent(Component component) {
		remove(component.getGraphics());
		repaint();
	}
}
