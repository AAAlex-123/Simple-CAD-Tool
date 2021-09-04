package components;

/**
 * An Interface for objects that can be identified.
 *
 * @param <T> the type of the ID
 *
 * @author alexm
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
}
