package myUtil;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * Properties subclass that modifies functionality by:
 * <ol>
 * <li>Retaining the order with which the properties are added</li>
 * <li>Printing an error message when a property doesn't exist for a given
 * key</li>
 * </ol>
 *
 * @author alexm
 */
public final class OrderedProperties extends Properties {

	private final Map<Object, Object> map = new LinkedHashMap<>();

	@Override
	public String getProperty(String key) {
		String superVal = super.getProperty(key);
		if (superVal == null) {
			System.err.printf("No value found for key %s%n", key); //$NON-NLS-1$
		}

		return superVal;
	}

	@Override
	public synchronized Object put(Object key, Object value) {
		map.put(key, value);
		return super.put(key, value);
	}

	@Override
	public Set<Map.Entry<Object, Object>> entrySet() {
		return map.entrySet();
	}
}
