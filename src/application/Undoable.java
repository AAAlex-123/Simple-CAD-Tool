package application;

/** An Interface for classes that can be executed and un-done */
interface Undoable {

	/**
	 * Executes the Undoable.
	 * 
	 * @return a return code
	 */
	int execute();

	/**
	 * Un-does the Undoable.
	 * 
	 * @return a return code
	 */
	int unexecute();
}
