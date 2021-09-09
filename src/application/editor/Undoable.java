package application.editor;

/**
 * An Interface for objects that can be executed and un-executed.
 * <p>
 * TODO: contemplate if it's worth adding a type parameter for the upper bound
 * of Exceptions that {@code execute()} throws
 *
 * @author Alex Mandelias
 */
@FunctionalInterface
public interface Undoable {

	/**
	 * Executes the Undoable.
	 *
	 * @throws Exception if an exception occurred during execution
	 */
	void execute() throws Exception;

	/** Un-does the Undoable */
	default void unexecute() {}
}
