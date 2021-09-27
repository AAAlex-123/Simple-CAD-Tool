package application.editor;

import java.awt.Color;
import java.awt.GridLayout;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * Convenient way to display multiple messages in JLabels. The labels may be
 * formatted according the {@code Type} of the message they display and are
 * displayed horizontally across this JPanel.
 * <p>
 * All methods that access a specific Label throw a RuntimeException if the
 * Label is not found.
 *
 * @author Alex Mandelias
 *
 * @see MessageType
 * @see MissingLabelException
 */
public final class StatusBar extends JPanel {

	private final Map<String, JLabel> map;

	/**
	 * Provides formatting according to the type of the Label's message
	 *
	 * @author Alex Mandelias
	 */
	public enum MessageType {

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
		 * Formats a {@code label} according to this {@code MessageType}.
		 *
		 * @param label the label to format
		 */
		protected abstract void format(JLabel label);
	}

	/** Creates an empty Status Bar */
	public StatusBar() {
		map = new HashMap<>();
		setLayout(new GridLayout(1, 0));
	}

	/**
	 * Adds a new Label to the StatusBar with default message {@code type}.
	 *
	 * @param labelID the new Label's ID
	 *
	 * @see MessageType
	 */
	public void addLabel(String labelID) {
		addLabel(labelID, MessageType.DEFAULT);
	}

	/**
	 * Adds a new Label to the StatusBar with a message {@code type}.
	 *
	 * @param labelID the new Label's ID
	 * @param type    the type according to which the new Label will be formatted
	 *
	 * @see MessageType
	 */
	public void addLabel(String labelID, MessageType type) {
		final JLabel newLabel = new JLabel(" "); //$NON-NLS-1$
		newLabel.setHorizontalAlignment(SwingConstants.LEFT);
		type.format(newLabel);

		map.put(labelID, newLabel);
		add(newLabel);
	}

	/**
	 * Removes a Label from this StatusBar.
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
	 * Sets the text that the Label displays without changing its appearance. The
	 * {@code format} is formatted with {@code String.format(format, args)} before
	 * being set as the Label's text.
	 *
	 * @param labelID the ID of the Label
	 * @param format  the format
	 * @param args    the format arguments
	 */
	public void setLabelText(String labelID, String format, Object... args) {
		getLabel(labelID).setText(String.format(format, args));
	}

	/**
	 * Sets the text that the Label displays and changes its appearance according to
	 * the {@code type} of the message. The {@code format} is formatted with
	 * {@code String.format(format, args)} before being set as the Label's text.
	 *
	 * @param labelID the ID of the Label
	 * @param type    the type of the text
	 * @param format  the format
	 * @param args    the format arguments
	 *
	 * @see MessageType
	 */
	public void setLabelText(String labelID, MessageType type, String format, Object... args) {
		final JLabel label = getLabel(labelID);
		type.format(label);
		label.setText(String.format(format, args));
	}

	/**
	 * Thrown when no Label with the {@code labelID} exists
	 *
	 * @author Alex Mandelias
	 */
	public static class MissingLabelException extends RuntimeException {

		/**
		 * Constructs the Exception with a {@code labelID}.
		 *
		 * @param labelID the ID for which no Label exists
		 */
		public MissingLabelException(String labelID) {
			super(String.format("No label with ID '%s' exists", labelID)); //$NON-NLS-1$
		}
	}
}
