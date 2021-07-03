package application;

import java.awt.Color;
import java.awt.GridLayout;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/** Convenient way to display multiple messages in JLabels */
final class StatusBar extends JPanel {

	private final Map<String, JLabel> map;

	/** Represents the message type. Text is formatted according to that type. */
	enum MessageType {

		/** Default text */
		DEFAULT {
			@Override
			void format(JLabel label) {
				label.setForeground(Color.BLACK);
			}
		},

		/** Text indicating success */
		SUCCESS {
			@Override
			void format(JLabel label) {
				label.setForeground(Color.GREEN);
			}
		},

		/** Text indicating failure */
		FAILURE {
			@Override
			void format(JLabel label) {
				label.setForeground(Color.RED);
			}
		};

		/**
		 * Formats the text on the {@code label} according to the type.
		 * 
		 * @param label the label
		 */
		abstract void format(JLabel label);
	}

	/** Creates the Status Bar */
	StatusBar() {
		map = new HashMap<>();
		setLayout(new GridLayout(1, 0));
	}

	/**
	 * Adds a new Label to the Status Bar.
	 * 
	 * @param labelID the Label's ID
	 */
	void addLabel(String labelID) {
		JLabel newLabel = new JLabel(" ");
		newLabel.setHorizontalAlignment(SwingConstants.LEFT);
		map.put(labelID, newLabel);
		add(newLabel);
	}

	/**
	 * Removes a Label from the Status Bar.
	 * 
	 * @param labelID the Label's ID
	 */
	void removeLabel(String labelID) {
		remove(map.get(labelID));
		map.remove(labelID);
	}

	/**
	 * Sets the text that a Label displays. The {@code text} will be formatted as if
	 * String.format was called using the {@code args}.
	 * 
	 * @param labelID the ID of the Label
	 * @param text    the text
	 * @param args    the format arguments for the text
	 */
	void setLabelText(String labelID, String text, Object... args) {
		MessageType.DEFAULT.format(map.get(labelID));
		map.get(labelID).setText(String.format(text, args));
	}

	/**
	 * Sets the text that a Label displays and changes the appearance according to
	 * the {@code type} of the message. The {@code text} will be formatted as if
	 * String.format was called using the {@code args}.
	 * 
	 * @param labelID     the ID of the Label
	 * @param messageType the type of the text, one of {@link MessageType}
	 * @param text        the text
	 * @param args        the format arguments for the text
	 * 
	 * @see MessageType
	 */
	void setLabelText(String labelID, MessageType messageType, String text, Object... args) {
		messageType.format(map.get(labelID));
		map.get(labelID).setText(String.format(text, args));
	}
}
