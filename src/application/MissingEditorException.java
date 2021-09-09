package application;

/**
 * Thrown when an {@code EditorInterface} object was attempted to be removed
 * from an {@code EditorManager} but the Manager didn't contain it.
 *
 * @author Alex Mandelias
 */
public class MissingEditorException extends RuntimeException {

	/**
	 * Constructs the Exception with an {@code EditorInterface} object.
	 *
	 * @param editor  the EditorInterface that is not found in a Manager
	 * @param manager the Manager that doesn't contain the EditorInterface
	 */
	public MissingEditorException(EditorInterface editor, EditorManager<?> manager) {
		super(String.format("EditorInterface object %s not found in Manager %s", editor, //$NON-NLS-1$
		        manager));
	}
}
