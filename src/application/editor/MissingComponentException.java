package application.editor;

import localisation.Languages;

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
		super(String.format(Languages.getString("MissingComponentException.0"), id)); //$NON-NLS-1$
	}
}
