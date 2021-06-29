package application.editor;

/**
 * Wrapper for file name and file "dirty-ness".
 *
 * @author alexm
 */
final class EditorFile {

	private String  filename;
	private boolean dirty;

	/** Constructs the EditorFile object */
	EditorFile() {}

	/**
	 * Returns the filename.
	 *
	 * @return the file name
	 */
	String get() {
		return filename;
	}

	/**
	 * Sets the file name to fname and marks as dirty.
	 *
	 * @param fname the new file name
	 */
	void set(String fname) {
		filename = fname;
		dirty = true;
	}

	/**
	 * Returns the dirty-ness of the file.
	 *
	 * @return the dirty-ness
	 */
	boolean isDirty() {
		return dirty;
	}

	/**
	 * Sets the dirty-ness of the file to newDirty.
	 *
	 * @param newDirty the new dirty-ness
	 */
	void setDirty(boolean newDirty) {
		dirty = newDirty;
	}
}
