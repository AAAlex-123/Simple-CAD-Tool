package application.editor;

/**
 * Thrown when no {@link components.Component Component} with the {@code ID}
 * exists
 *
 * @author alexm
 */
public class MissingComponentException extends Exception {

	/**
	 * Constructs the Exception with information about the {@code ID}.
	 *
	 * @param id the id for which no {@code Component} exists
	 */
	public MissingComponentException(String id) {
		super(String.format("No Component with ID %s exists", id));
	}
}
