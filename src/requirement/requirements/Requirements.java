package requirement.requirements;

import java.awt.Frame;
import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.List;

import localisation.Languages;
import localisation.RequirementStrings;
import myUtil.Utility;
import requirement.exceptions.MissingRequirementException;
import requirement.graphics.RequirementsDialog;

/**
 * A wrapper for a collection of {@code Requirements} providing additional
 * functionality for fulfilling all Requirement simultaneously using a
 * {@link RequirementsDialog Dialog}.
 * <p>
 * For method details refer to the {@link AbstractRequirement} class or use the
 * {@literal @see} links provided.
 *
 * @author alexm
 */
public final class Requirements implements Iterable<AbstractRequirement>, Serializable {

	private static final long serialVersionUID = 6L;

	private final Map<String, AbstractRequirement> requirements;

	/** Constructs the collection of Requirements */
	public Requirements() {
		// map for simpler lookup, linked to retain the order
		requirements = new LinkedHashMap<>(1, 1);
	}

	/**
	 * Copy constructor.
	 *
	 * @param other the object to be copied
	 */
	public Requirements(Requirements other) {
		this();
		Utility.foreach(other, req -> add(req.clone()));
	}

	/**
	 * Adds a general-purpose Requirement with a {@code key}.
	 *
	 * @param key the key
	 *
	 * @see ObjectRequirement
	 */
	public void add(String key) {
		requirements.put(key, new ObjectRequirement(key));
	}
	
		/**
	 * Adds a String-specific Requirement with a {@code key} and a {@code type}.
	 *
	 * @param key  the key
	 * @param type the type
	 *
	 * @see StringRequirement
	 */
	public void add(String key, StringType type) {
		requirements.put(key, new StringRequirement(key, type));
	}

	/**
	 * Adds a Requirement of any type.
	 *
	 * @param requirement the Requirement
	 */
	public void add(AbstractRequirement requirement) {
		requirements.put(requirement.key(), requirement);
	}
`	/**
	 * Adds a List-specific Requirement with a {@code key} and {@code values}.
	 *
	 * @param <T>  the type of the values
	 * @param key  the key
	 * @param type the values
	 *
	 * @see ListRequirement
	 */
	public <T> void addListRequirement(String key, List<T> values) {
	    requirements.put(key, new ListRequirement<T>(key, values));
	}
	
	/**
	 * Adds a List-specific Requirement with a {@code key} and values
	 * lazily determined later.
	 *
	 * @param <T>  the type of the values
	 * @param key  the key
	 *
	 * @see ListRequirement
	 */
	public <T> void addListRequirement(String key) {
		 requirements.put(key, new ListRequirement<T>(key));
	}


	/**
	 * Adds a filtered {@link ComponentRequirement} with a {@code key} and {@code components}
	 * that will be determined later.
	 *
	 * @param key  the key
	 * @param policy the way components will be filtered
	 *
	 * @see ListRequirement
	 */
	public void addComponentRequirement(String key, ComponentRequirement.Policy policy) {
		requirements.put(key, new ComponentRequirement(key, policy));
	}

	/**
	 * Adds a {@link ComponentRequirement} with a {@code key} and {@code components}.
	 *
	 * @param key  the key
	 * @param components the suitable components
	 *
	 */
	public void addComponentRequirement(String key, List<components.Component> components, ComponentRequirement.Policy policy) {
		requirements.put(key, new ComponentRequirement(key, components, policy));
	}


	/**
	 * Adds a {@link ComponentRequirement} with a {@code key} and {@code components}
	 * that will be determined later.
	 *
	 * @param key  the key
	 *
	 * @see ListRequirement
	 */
	public void addComponentRequirement(String key) {
		requirements.put(key, new ComponentRequirement(key, ComponentRequirement.Policy.ANY));
	}

	/**
	 * Returns the Requirement with the {@code key}.
	 *
	 * @param key the key
	 *
	 * @return the Requirement with that key
	 */
	public AbstractRequirement get(String key) {
		final AbstractRequirement req = requirements.get(key);
		if (req == null)
			throw new MissingRequirementException(key);

		return req;
	}

	/**
	 * Returns the value of the Requirement with the {@code key}.
	 *
	 * @param key the key
	 *
	 * @return the value of the
	 *
	 * @see AbstractRequirement#value
	 */
	public Object getValue(String key) {
		return get(key).value();
	}

	/**
	 * Offers {@code v} for the Requirement with key {@code k}.
	 *
	 * @param key   the key
	 * @param value the value
	 *
	 * @see AbstractRequirement#offer(Object)
	 */
	public void offer(String key, Object value) {
		get(key).offer(value);
	}

	/**
	 * Fulfils the Requirement with key {@code k} using {@code v}.
	 *
	 * @param key   the key
	 * @param value the value
	 *
	 * @see AbstractRequirement#fulfil(Object)
	 */
	public void fulfil(String key, Object value) {
		get(key).fulfil(value);
	}

	/**
	 * Finalises the Requirement with key {@code k} using {@code v}.
	 *
	 * @param key   the key
	 * @param value the value
	 *
	 * @see AbstractRequirement#finalise(Object)
	 */
	public void finalise(String key, Object value) {
		get(key).finalise(value);
	}

	/**
	 * Attempts to fulfil the Requirements (if they aren't already fulfilled) in
	 * this collection using a pop-up dialog.
	 *
	 * @param frame       the parent of the dialog
	 * @param description the text that will be displayed
	 *
	 * @see RequirementsDialog
	 */
	public void fulfillWithDialog(Frame frame, String description) {
		if (fulfilled())
			return;

		final RequirementsDialog dialog = new RequirementsDialog(description, this, frame);
		dialog.setVisible(true);
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
	 * Clears all the Requirements in this collection.
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
	 * @return {@code true} if all Requirements are fulfilled, {@code false}
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
	 * @return {@code true} if all Requirements are finalised, {@code false}
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
		sb.append(String.format(Languages.getString("Requirements.0"), //$NON-NLS-1$
		        fulfilled() ? RequirementStrings.YES : RequirementStrings.NO,
		        finalised() ? RequirementStrings.YES : RequirementStrings.NO));
		Utility.foreach(this, req -> sb.append(req));
		return sb.toString();
	}

	/**
	 * Iterator for the AbstractRequirements present in this collection. Makes heavy
	 * use of the underlying map's iterator.
	 *
	 * @author alexm
	 */
	private class RequirementsIterator implements Iterator<AbstractRequirement> {

		private final Iterator<AbstractRequirement> iter;

		RequirementsIterator() {
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
