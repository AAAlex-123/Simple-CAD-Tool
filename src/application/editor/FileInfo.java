package application.editor;

import java.awt.FlowLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * A JLabel that displays the {@code filename} with a leading '*' if the,
 * hypothetical, file associated with that name is {@code dirty}.
 *
 * @author alexm
 */
final class FileLabel extends JPanel {

	private final JLabel label;

	/** Constructs the FileLabel */
	FileLabel() {
		super(new FlowLayout());
		setOpaque(false);
		add(label = new JLabel());
	}

	/**
	 * Updates the Label's text.
	 *
	 * @param fname the file name
	 * @param dirty the dirtiness of the file
	 */
	void updateText(String fname, boolean dirty) {
		label.setText((dirty ? "*" : "") + fname); //$NON-NLS-1$ //$NON-NLS-2$
	}
}
