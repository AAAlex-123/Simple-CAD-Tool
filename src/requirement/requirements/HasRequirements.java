package requirement.requirements;

/**
 * Interface for classes that use {@link AbstractRequirement Requirements}.
 * Since Requirements are by design extremely loosely coupled with the objects
 * that use them, figuring out where they are created, adjusted, fulfilled and
 * where their value is used can often be a difficult task. To ease design and
 * maintenance this interface defines 1 method for adjusting * requirements.
 * Classes that use Requirements should implement and call these methods
 * (instead of manually inlining them) to increase code readability,
 * extensibility and ease of maintenance, since the code regarding the
 * Requirements is contained within these methods and nowhere else.
 *
 * @author Alex Mandelias
 */
public interface HasRequirements {

	/**
	 * Makes any necessary changes to them so that they can be fulfilled in whatever
	 * fashion each class that uses them wants.
	 */
	void adjustRequirements();
}
