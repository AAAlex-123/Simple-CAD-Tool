package application.editor;

import localisation.Languages;

/**
 * Thrown when no {@code Component} with an {@code ID} exists
 *
 * @author Alex Mandelias
 */
public class MissingComponentException extends Exception {

	/**
	 * Constructs the Exception an {@code ID}.
	 *
	 * @param id the id for which no {@code Component} exists
	 */
	public MissingComponentException(String id) {
		super(String.format(Languages.getString("MissingComponentException.0"), id)); //$NON-NLS-1$
	}
}
