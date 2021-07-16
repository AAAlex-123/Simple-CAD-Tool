package localisation;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import application.StringConstants;

/**
 * TODO
 *
 * @author alexm
 */
public class Languages {
	private static final String BUNDLE_NAME = "localisation.language"; //$NON-NLS-1$

	private static final ResourceBundle RESOURCE_BUNDLE;

	static {
		final String language = StringConstants.L_LANGUAGE;
		final String country  = StringConstants.L_COUNTRY;
		final String variant  = StringConstants.L_VARIANT;
		RESOURCE_BUNDLE = ResourceBundle.getBundle(Languages.BUNDLE_NAME,
		        new Locale(language, country, variant));
	}

	private Languages() {}

	public static String getString(String key) {
		try {
			return Languages.RESOURCE_BUNDLE.getString(key);
		} catch (final MissingResourceException e) {
			return '!' + key + '!';
		}
	}
}
