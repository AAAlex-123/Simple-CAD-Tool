package requirement.util;

import java.awt.Frame;
import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import component.components.Component;
import localisation.RequirementStrings;
import myUtil.Utility;
import requirement.exceptions.MissingRequirementException;
import requirement.requirements.AbstractRequirement;
import requirement.requirements.ComponentRequirement;
import requirement.requirements.ListRequirement;
import requirement.requirements.ObjectRequirement;
import requirement.requirements.StringRequirement;
import requirement.requirements.StringType;
import requirement.requirements.ComponentRequirement.Policy;

/**
 * A wrapper for creating, storing, retrieving and iterating over a collection
 * of {@code Requirements}. It provides additional functionality for fulfilling
 * every Requirement simultaneously using a {@link RequirementsDialog Dialog}.
 * <p>
 * For method details refer to the {@link AbstractRequirement} class or use the
 * {@literal @see} links provided.
 *
 * @author Alex Mandelias
 */
public final class Requirements implements Iterable<AbstractRequirement>, Serializable {

	private static final long serialVersionUID = 6L;

	// map for simpler lookup, linked to retain the order
	private final Map<String, AbstractRequirement> requirements = new LinkedHashMap<>(1, 1);

	/** Constructs the collection of Requirements */
	public Requirements() {}

	/**
	 * Copy constructor that constructs this Requirements object by performing a
	 * deep copy on another Requirements object. The deep copy means that every
	 * individual Requirement in the other object is also copy-constructed.
	 *
	 * @param other the Requirements object to be copied
	 */
	public Requirements(Requirements other) {
		Utility.foreach(other, req -> add(req.clone()));
	}

	/**
	 * Adds a Requirement to this collection. If there already exists a Requirement
	 * with the same key as the {@code requirement}, it is replaced.
	 *
	 * @param requirement the Requirement to add
	 */
	public void add(AbstractRequirement requirement) {
		requirements.put(requirement.key(), requirement);
	}

	/**
	 * Constructs and {@code adds} a general-purpose Requirement to this collection
	 * as specified by the {@link #add(AbstractRequirement)} method.
	 *
	 * @param key the key of the new Requirement
	 *
	 * @see ObjectRequirement
	 */
	public void add(String key) {
		add(new ObjectRequirement(key));
	}

	/**
	 * Constructs and {@code adds} a String-specific Requirement to this collection
	 * as specified by the {@link #add(AbstractRequirement)} method.
	 *
	 * @param key  the key of the new Requirement
	 * @param type the type of the new String Requirement
	 *
	 * @see StringRequirement
	 * @see StringType
	 */
	public void add(String key, StringType type) {
		add(new StringRequirement(key, type));
	}

	/**
	 * Constructs and {@code adds} a List-specific Requirement to this collection as
	 * specified by the {@link #add(AbstractRequirement)} method.
	 *
	 * @param <T>    the type of the values of the new Requirement's list
	 * @param key    the key of the new Requirement
	 * @param values the options of the new List Requirement
	 *
	 * @see ListRequirement
	 */
	public <T> void add(String key, List<T> values) {
		add(new ListRequirement<>(key, values));
	}

	/**
	 * Constructs and {@code adds} a Component-specific Requirement to this
	 * collection as specified by the {@link #add(AbstractRequirement)} method.
	 *
	 * @param key        the key of the new Requirement
	 * @param components the options of the new Component Requirement
	 * @param policy     the Policy for filtering the Components
	 *
	 * @see ComponentRequirement
	 * @see Policy
	 */
	public void add(String key, List<Component> components, Policy policy) {
		add(new ComponentRequirement(key, components, policy));
	}

	/**
	 * Returns a Requirement from this collection. Any methods that access the
	 * individual Requirements in this collection also call this method.
	 *
	 * @param key the key of the Requirement
	 *
	 * @return the Requirement with that key
	 *
	 * @throws MissingRequirementException if no Requirement with that key exists in
	 *                                     this collection
	 */
	public AbstractRequirement get(String key) {
		final AbstractRequirement req = requirements.get(key);
		if (req == null)
			throw new MissingRequirementException(key);

		return req;
	}

	/**
	 * Removes a Requirement from this collection.
	 *
	 * @param key the key of the Requirement
	 *
	 * @throws MissingRequirementException if no Requirement with that key exists in
	 *                                     this collection
	 */
	public void remove(String key) {
		final AbstractRequirement req = requirements.remove(key);
		if (req == null)
			throw new MissingRequirementException(key);
	}

	/**
	 * Returns the value of a Requirement in the collection. If the Requirement is
	 * not an ObjectRequirement then the type of the value is known and the client
	 * may cast the object returned to the desired class.
	 *
	 * @param key the key of the Requirement
	 *
	 * @return the value of the Requirement with that key
	 *
	 * @see AbstractRequirement#value
	 */
	public Object getValue(String key) {
		return get(key).value();
	}

	/**
	 * Casts the object returned by {@link #getValue(String)} to the desired class
	 * and returns it.
	 *
	 * @param <E>   the type of the returned object
	 * @param key   the key of the Requirement whose value will be cast
	 * @param clazz the class to cast the Requirement's value to
	 *
	 * @return the value of the Requirement that is cast to a specific class
	 *
	 * @throws ClassCastException if the class of the Requirement's value cannot be
	 *                            case to the given class
	 */
	public <E> E getValue(String key, Class<E> clazz) {
		return clazz.cast(getValue(key));
	}

	/**
	 * Applies a Function to cast the value of a Requirement to the desired class.
	 * This method is intended to be a hook into the casting process.
	 * <p>
	 * Working example: <blockquote>
	 *
	 * <pre>
	 * {@code private static <E> Function<Object, E> castAndLog(Class<E> clazz)} {
	 *     return (o -> {
	 * 		E castValue = null;
	 * 		try {
	 * 			castValue = clazz.cast(o);
	 * 		} catch (ClassCastException e) {
	 * 			System.err.printf("Cannot cast %s to %s%n", o, clazz);
	 * 		}
	 * 		return castValue;
	 * 	});
	 * }
	 *
	 * public static void main(String[] args) {
	 *     Requirements reqs = new Requirements();
	 *     reqs.add("stringValue");
	 *     reqs.add("intValue");
	 *
	 *     reqs.fulfil("stringValue", "foo");
	 *     reqs.fulfil("intValue", 5);
	 *
	 *     Integer intToInt = reqs.getValueAs("intValue", castAndLog(Integer.class));
	 *     String intToString = reqs.getValueAs("intValue", castAndLog(String.class));
	 *     Integer stringToInt = reqs.getValueAs("stringValue", castAndLog(Integer.class));
	 * }
	 * </pre>
	 *
	 * </blockquote>
	 *
	 * @param <E>          the type of the returned object
	 * @param key          the key of the Requirement whose value will be cast
	 * @param castFunction the Function responsible for the casting
	 *
	 * @return the value of the Requirement that is cast to a specific class by
	 *         applying the Function
	 * 
	 * @implSpec this method returns {@code castFunction.apply(getValue(key))}
	 */
	public <E> E getValue(String key, Function<Object, E> castFunction) {
		return castFunction.apply(getValue(key));
	}

	/**
	 * Offers a value for a Requirement in this collection.
	 *
	 * @param key   the key of the Requirement
	 * @param value the value to offer
	 *
	 * @see AbstractRequirement#offer(Object)
	 */
	public void offer(String key, Object value) {
		get(key).offer(value);
	}

	/**
	 * Fulfils a Requirement with a value.
	 *
	 * @param key   the key of the Requirement
	 * @param value the value to offer
	 *
	 * @see AbstractRequirement#fulfil(Object)
	 */
	public void fulfil(String key, Object value) {
		get(key).fulfil(value);
	}

	/**
	 * Finalises a Requirement with a value.
	 *
	 * @param key   the key of the Requirement
	 * @param value the value to offer
	 *
	 * @see AbstractRequirement#finalise(Object)
	 */
	public void finalise(String key, Object value) {
		get(key).finalise(value);
	}

	/**
	 * Attempts to fulfil the Requirements in this collection using a pop-up dialog.
	 * If all the Requirements are fulfilled, this method does nothing.
	 *
	 * @param frame       the parent of the dialog
	 * @param description the text that will be displayed
	 *
	 * @see RequirementsDialog
	 * @see AbstractRequirement#fulfilled
	 */
	public void fulfillWithDialog(Frame frame, String description) {
		if (fulfilled())
			return;

		RequirementsDialog.showDialog(description, this, frame);
	}

	/**
	 * Returns the number of Requirements in this collection.
	 *
	 * @return the number of Requirements
	 */
	public int size() {
		return requirements.size();
	}

	/**
	 * Returns whether or not this collection is empty.
	 *
	 * @return {@code true} if this collection doesn't contain any Requirements,
	 *         {@code false} otherwise
	 */
	public boolean isEmpty() {
		return size() == 0;
	}

	/**
	 * Clears all the Requirements in this collection.
	 * <p>
	 * <b>Note:</b> this method does <b>not</b> remove any Requirement from this
	 * collection. It simply calls the {@code clear()} method for each of them.
	 *
	 * @see AbstractRequirement#clear()
	 */
	public void clear() {
		Utility.foreach(this, AbstractRequirement::clear);
	}

	/**
	 * Resets all the Requirements in this collection
	 *
	 * @see AbstractRequirement#reset()
	 */
	public void reset() {
		Utility.foreach(this, AbstractRequirement::reset);
	}

	/**
	 * Returns whether or not all the Requirements in this collection are fulfilled.
	 *
	 * @return {@code true} if all the Requirements are fulfilled, {@code false}
	 *         otherwise
	 *
	 * @see AbstractRequirement#fulfilled
	 */
	public boolean fulfilled() {
		return Utility.all(this, AbstractRequirement::fulfilled);
	}

	/**
	 * Returns whether or not all the Requirements in this collection are finalised.
	 *
	 * @return {@code true} if all the Requirements are finalised, {@code false}
	 *         otherwise
	 *
	 * @see AbstractRequirement#fulfilled
	 */
	public boolean finalised() {
		return Utility.all(this, AbstractRequirement::finalised);
	}

	@Override
	public Iterator<AbstractRequirement> iterator() {
		return new RequirementsIterator();
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append(String.format("Requirements fulfilled: %s, finalised: %s%n", //$NON-NLS-1$
				fulfilled() ? RequirementStrings.YES : RequirementStrings.NO,
						finalised() ? RequirementStrings.YES : RequirementStrings.NO));
		Utility.foreach(this, req -> sb.append(req));
		return sb.toString();
	}

	/**
	 * Iterator for the AbstractRequirements present in this collection.
	 *
	 * @author Alex Mandelias
	 */
	private class RequirementsIterator implements Iterator<AbstractRequirement> {

		private final Iterator<AbstractRequirement> iter;

		public RequirementsIterator() {
			iter = requirements.values().iterator();
		}

		@Override
		public boolean hasNext() {
			return iter.hasNext();
		}

		@Override
		public AbstractRequirement next() {
			return iter.next();
		}
	}
}
