package exceptions;

import java.io.File;

/**
 * Thrown when a ComponentGraphic couldn't load its sprite.
 *
 * @author alexm
 */
public final class MissingSpriteException extends RuntimeException {

	/**
	 * Constructs the Exception with information about the {@code filename}.
	 *
	 * @param filename
	 */
	public MissingSpriteException(File filename) {
		super(String.format("Could not load image %s", filename));
	}
}
