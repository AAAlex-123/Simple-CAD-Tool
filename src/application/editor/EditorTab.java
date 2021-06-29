package application.editor;

import java.awt.FlowLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * The Component that will be displayed in the Editor's tab
 *
 * @author alexm
 */
final class EditorTab extends JPanel {

	private final JLabel label;

	/** Constructs the EditorTab for the given Editor */
	EditorTab() {
		super(new FlowLayout());
		setOpaque(false);
		add(label = new JLabel());
	}

	/**
	 * Updates the label's text
	 *
	 * @param fname the file name
	 * @param dirty the dirty-ness of the file
	 */
	void updateTitle(String fname, boolean dirty) {
		label.setText((dirty ? "*" : "") + fname);
	}
}
