package application.editor;

/**
 * An Interface for objects that can be executed and un-executed.
 *
 * @author Alex Mandelias
 */
@FunctionalInterface
public interface Undoable {

	/**
	 * Executes this Undoable.
	 *
	 * @throws Exception if an exception occurred during execution
	 */
	void execute() throws Exception;

	/** Un-does this Undoable */
	default void unexecute() {}
}
