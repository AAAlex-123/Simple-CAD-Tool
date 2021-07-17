package application.editor;

import java.awt.Color;
import java.awt.GridLayout;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * Convenient way to display multiple messages in JLabels. The labels may be
 * formatted according to the {@link TextType Type} of message they display and
 * are displayed horizontally across this JPanel.
 *
 * @author alexm
 */
public final class StatusBar extends JPanel {

	private final Map<String, JLabel> map;

	/** Provides formatting according to the type of the Label's message */
	public enum TextType {

		/** Default text */
		DEFAULT {
			@Override
			protected void format(JLabel label) {
				label.setForeground(Color.BLACK);
			}
		},

		/** Text indicating success */
		SUCCESS {
			@Override
			protected void format(JLabel label) {
				label.setForeground(Color.GREEN);
			}
		},

		/** Text indicating failure */
		FAILURE {
			@Override
			protected void format(JLabel label) {
				label.setForeground(Color.RED);
			}
		};

		/**
		 * Formats the text on the {@code label} according to this {@code Type}.
		 *
		 * @param label the label
		 */
		protected abstract void format(JLabel label);
	}

	/** Creates the Status Bar */
	public StatusBar() {
		map = new HashMap<>();
		setLayout(new GridLayout(1, 0));
	}

	/**
	 * Adds a new Label to the {@code StatusBar} with default {@code TextType}.
	 *
	 * @param labelID the Label's ID
	 */
	public void addLabel(String labelID) {
		addLabel(labelID, TextType.DEFAULT);
	}

	/**
	 * Adds a new Label to the {@code StatusBar} with the given {@code TextType}.
	 *
	 * @param labelID the Label's ID
	 * @param type    the Label's text type
	 */
	public void addLabel(String labelID, TextType type) {
		final JLabel newLabel = new JLabel(" "); //$NON-NLS-1$
		newLabel.setHorizontalAlignment(SwingConstants.LEFT);
		type.format(newLabel);

		map.put(labelID, newLabel);
		add(newLabel);
	}

	/**
	 * Removes a Label from the {@code StatusBar}.
	 *
	 * @param labelID the Label's ID
	 */
	public void removeLabel(String labelID) {
		remove(getLabel(labelID));
	}

	private JLabel getLabel(String labelID) {
		final JLabel label = map.get(labelID);
		if (label == null)
			throw new MissingLabelException(labelID);

		return label;
	}

	/**
	 * Sets the text that the Label with the {@code labelID} displays without
	 * changing its appearance. The {@code text} is formatted as if
	 * {@code String.format} was called with parameter {@code args}.
	 *
	 * @param labelID the ID of the Label
	 * @param text    the text
	 * @param args    the format arguments for the text
	 */
	public void setLabelText(String labelID, String text, Object... args) {
		final JLabel label = getLabel(labelID);

		label.setText(String.format(text, args));
	}

	/**
	 * Sets the text that the Label with the {@code labelID} displays and changes
	 * its appearance according to the {@code type} of the message. The {@code text}
	 * is formatted as if {@code String.format} was called with parameter
	 * {@code args}.
	 *
	 * @param labelID the ID of the Label
	 * @param type    the type of the text
	 * @param text    the text
	 * @param args    the format arguments for the text
	 *
	 * @see TextType
	 */
	public void setLabelText(String labelID, TextType type, String text, Object... args) {
		final JLabel label = getLabel(labelID);

		type.format(label);
		label.setText(String.format(text, args));
	}

	/** Thrown when no Label with the {@code labelID} exists */
	public static class MissingLabelException extends RuntimeException {

		/**
		 * Constructs the Exception with information about the {@code labelID}.
		 *
		 * @param labelID the ID for which no Label exists
		 */
		public MissingLabelException(String labelID) {
			super(String.format("No label with ID %s exists", labelID)); //$NON-NLS-1$
		}
	}
}
