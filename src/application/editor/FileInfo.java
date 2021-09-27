package application.editor;

import java.awt.FlowLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Encapsulates information about a File that is being edited. This includes its
 * {@code filename} and whether or not it is {@code dirty}. A dirty file is a
 * file that has unsaved changes.
 * <p>
 * <b>Note:</b> the File doesn't have to actually exists, this class simply
 * defines file-like behaviour, which an Editor of that file may find useful.
 * None of the methods actually read from or write to the disk.
 * <p>
 * A JPanel that displays the file's relevant information can be obtained with
 * the {@link #getGraphic()} method and is created on-demand the first time the
 * method is called.
 *
 * @author Alex Mandelias
 */
public class FileInfo {

	private JPanel panel;
	private JLabel label;

	private String  filename;
	private boolean dirty;

	/** Constructs a FileInfo object */
	public FileInfo() {}

	/**
	 * Returns a Graphic for this FileInfo object that accurately represents it. It
	 * consists of a JLabel whose text is formed by calling:
	 *
	 * <pre>
	 * String.format("%s%s", dirty ? "*" : "", filename)
	 * </pre>
	 *
	 * @return the JPanel with that text
	 */
	public JPanel getGraphic() {
		if (panel == null) {
			panel = new JPanel(new FlowLayout());
			panel.setOpaque(false);
			panel.add(label = new JLabel(getLabelText()));
		}
		return panel;
	}

	/**
	 * Returns the name of the File.
	 *
	 * @return the filename
	 */
	public String getFile() {
		return filename;
	}

	/**
	 * Returns the dirty state of this File. A {@code dirty} file has unsaved
	 * changes while a {@code non-dirty} one doesn't.
	 *
	 * @return {@code true} if this file is dirty, {@code false} otherwise
	 */
	public boolean isDirty() {
		return dirty;
	}

	/**
	 * Updates the name of this File.
	 *
	 * @param newFilename the new name of the File.
	 */
	public void setFile(String newFilename) {
		updateState(newFilename, dirty);
	}

	/** Marks this File as having unsaved changes */
	public void markUnsaved() {
		updateState(filename, true);
	}

	/** Marks this File as saved */
	public void markSaved() {
		updateState(filename, false);
	}

	@Override
	public String toString() {
		return getLabelText();
	}

	private void updateState(String newFilename, boolean newDirty) {
		filename = newFilename;
		dirty = newDirty;
		if (label != null)
			label.setText(getLabelText());
	}

	private String getLabelText() {
		return (dirty ? "*" : "") + filename; //$NON-NLS-1$ //$NON-NLS-2$
	}
}
