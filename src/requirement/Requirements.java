package requirement;
import static localisation.RequirementStrings.NO;
import static localisation.RequirementStrings.YES;

import java.awt.Frame;
import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import localisation.Languages;
import myUtil.Utility;

/**
 * A wrapper for a collection of {@code Requirements}. For details refer to the
 * {@link Requirement} class.
 *
 * @param <V> the type of the Requirement objects
 *
 * @author alexm
 */
public final class Requirements<V> implements Iterable<Requirement<V>>, Serializable {

	private static final long serialVersionUID = 6L;

	private final Map<String, Requirement<V>> requirements;

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
	public Requirements(Requirements<V> other) {
		this();
		Utility.foreach(other, r -> add(new Requirement<>(r)));
	}

	/**
	 * Adds a Requirement with a {@code key}.
	 *
	 * @param key the key
	 */
	public void add(String key) {
		requirements.put(key, new Requirement<V>(key));
	}

	/**
	 * Adds a Requirement with a {@code key} and a {@code type}.
	 *
	 * @param key  the key
	 * @param type the type
	 */
	public void add(String key, StringType type) {
		requirements.put(key, new Requirement<V>(key, type));
	}

	/**
	 * Adds the Requirement
	 *
	 * @param r the Requirement
	 */
	public void add(Requirement<V> r) {
		requirements.put(r.key(), r);
	}

	/**
	 * Returns the Requirement with the {@code key}.
	 *
	 * @param key the key
	 *
	 * @return the Requirement with that key
	 */
	public Requirement<V> get(String key) {
		final Requirement<V> r = requirements.get(key);
		if (r == null)
			throw new MissingRequirementException(key);

		return r;
	}

	/**
	 * Returns the value of the Requirement with the {@code key}.
	 *
	 * @param key the key
	 *
	 * @return the value of the
	 */
	public V getV(String key) {
		return get(key).value();
	}

	/**
	 * Offers {@code v} for the Requirement with key {@code k}.
	 *
	 * @param key   the key
	 * @param value the value
	 *
	 * @see Requirement#offer(Object)
	 */
	public void offer(String key, V value) {
		get(key).offer(value);
	}

	/**
	 * Fulfils the Requirement with key {@code k} using {@code v}.
	 *
	 * @param key   the key
	 * @param value the value
	 *
	 * @see Requirement#fulfil(Object)
	 */
	public void fulfil(String key, V value) {
		get(key).fulfil(value);
	}

	/**
	 * Finalises the Requirement with key {@code k} using {@code v}.
	 *
	 * @param key   the key
	 * @param value the value
	 *
	 * @see Requirement#finalise(Object)
	 */
	public void finalise(String key, V value) {
		get(key).finalise(value);
	}

	/**
	 * Attempts to fulfil the Requirements (if they aren't already fulfilled) in
	 * this collection using a pop-up dialog.
	 * <p>
	 * <b>Note:</b> only Requirements of type {@code String} should use this method.
	 *
	 * @param frame       the parent of the dialog
	 * @param description the text that will be displayed
	 */
	@SuppressWarnings("unchecked")
	public void fulfillWithDialog(Frame frame, String description) {
		if (fulfilled())
			return;

		final RequirementsDialog rd = new RequirementsDialog(description,
				(Requirements<String>) this,
				frame);
		rd.setVisible(true);
	}

	/**
	 * Returns the number of Requirements in this collection.
	 *
	 * @return the number of Requirements
	 */
	public int size() {
		return requirements.size();
	}

	/** Clears all the Requirements in this collection */
	public void clear() {
		Utility.foreach(this, Requirement::clear);
	}

	/** Resets all the Requirements in this collection */
	public void reset() {
		Utility.foreach(this, Requirement::reset);
	}

	/** @return {@code true} if all Requirements are fulfilled */
	public boolean fulfilled() {
		return Utility.all(this, Requirement::fulfilled);
	}

	/** @return {@code true} if all Requirements are finalised */
	public boolean finalised() {
		return Utility.all(this, Requirement::finalised);
	}

	@Override
	public Iterator<Requirement<V>> iterator() {
		return new RequirementsIterator();
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append(String.format(Languages.getString("Requirements.0"), //$NON-NLS-1$
				fulfilled() ? YES : NO, finalised() ? YES : NO));
		Utility.foreach(this, r -> sb.append(r));
		return sb.toString();
	}

	// Fancy iterator stuff. Totally didn't just use the iterator of the underlying map.
	private class RequirementsIterator implements Iterator<Requirement<V>> {

		private final Iterator<Requirement<V>> iter;

		RequirementsIterator() {
			iter = requirements.values().iterator();
		}

		@Override
		public boolean hasNext() {
			return iter.hasNext();
		}

		@Override
		public Requirement<V> next() {
			return iter.next();
		}
	}
}
