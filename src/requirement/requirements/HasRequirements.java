package requirement.requirements;

/**
 * An Interface for classes that use {@link AbstractRequirement Requirements}.
 * Since {@code Requirements} are by design extremely loosely coupled with the
 * classes that use them, finding where exactly they are created, adjusted and
 * fulfilled can be rather difficult. To ease design, maintenance and allow to
 * more easily extend the functionality of the class, it should implement this
 * interface so that as much of the code regarding the {@code Requirements} is
 * contained in the methods defined in this interface. The code in the methods
 * can then be read and changed easily, as well as extended in subclasses.
 * <p>
 * Because each class fulfils its {@code Requirements} in its own, unique way,
 * it is hard and limiting to define a method with a specific signature to fit
 * all needs. The same can be said for using them. For this reason, each class
 * that uses {@code Requirements} should aim to provide a simple and clear way
 * to fulfil them, so that adding new {@code Requirements} to the class can be
 * done as easily as possible.
 * <p>
 * <b>Note:</b> the methods in this interface simply aid code organisation and
 * readability. They act as template methods and should under no circumstances
 * be called from outside of the class implements them.
 *
 * @author Alex Mandelias
 */
public interface HasRequirements {

	/** Constructs the {@code Requirements} that the class needs */
	void constructRequirements();

	/**
	 * Makes any necessary changes to the {@code Requirements} so that they are
	 * ready to be fulfilled in whatever fashion each class that uses them wants.
	 * Adjustments include updating the options of a {@code ListRequirement} or
	 * altering the regex of a {@code StringRequirement} with the {@code CUSTOM}
	 * type.
	 */
	void adjustRequirements();
}
