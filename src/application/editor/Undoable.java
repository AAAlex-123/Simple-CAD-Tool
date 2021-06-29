package application.editor;

/**
 * An Interface for objects that can be executed and un-executed
 *
 * @author alexm
 */
public interface Undoable {

	/**
	 * Executes the Undoable
	 *
	 * @throws Exception when an exception occurred
	 */
	void execute() throws Exception;

	/** Un-does the Undoable */
	void unexecute();
}
