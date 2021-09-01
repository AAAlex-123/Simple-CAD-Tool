package component.components;

import java.util.Objects;

/**
 * An Interface for objects that can be identified by an ID.
 *
 * @param <T> the type of the ID
 *
 * @author Alex Mandelias
 */
public interface Identifiable<T> {

	/**
	 * Returns the ID of this object.
	 *
	 * @return the ID
	 */
	T getID();

	/**
	 * Sets the ID of this object.
	 *
	 * @param id the new ID
	 */
	void setID(T id);

	/**
	 * Returns whether or not two Identifiable objects have the same ID.
	 *
	 * @param <T> the type of the objects' ID
	 * @param i1  the first Identifiable object
	 * @param i2  the second Identifiable object
	 *
	 * @return {@code true} if the ID of the first object is equal to the ID of the
	 *         second object, {@code false} otherwise
	 *
	 * @throws NullPointerException if any argument is {@code null}
	 *
	 * @see Objects#equals
	 */
	static <T> boolean equals(Identifiable<T> i1, Identifiable<T> i2) {
		Objects.requireNonNull(i1, "i1 is null"); //$NON-NLS-1$
		Objects.requireNonNull(i2, "i2 is null"); //$NON-NLS-1$
		return Objects.equals(i1.getID(), i2.getID());
	}
}
