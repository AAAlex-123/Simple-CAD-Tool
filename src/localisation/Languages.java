package localisation;

import java.awt.Frame;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;

import myUtil.OrderedProperties;
import myUtil.Utility;

/**
 * The language settings used to load the appropriate Strings according to the
 * chosen locale.
 *
 * @author alexm
 */
public class Languages {

	/** The file containing information about the current language */
	public static final String  FILE                = "program_data\\languages.properties"; //$NON-NLS-1$
	private static final String LANGUAGES_DIRECTORY = "src\\localisation";                  //$NON-NLS-1$

	private static final Properties properties = new OrderedProperties();

	private static final String  LANGUAGE_STR, COUNTRY_STR, VARIANT_STR;
	private static final String  regex;
	private static final Pattern pattern;

	private static final String BUNDLE_NAME = "localisation.language"; //$NON-NLS-1$

	private static String         LANGUAGE, COUNTRY, VARIANT;
	private static ResourceBundle RESOURCE_BUNDLE;

	static {
		LANGUAGE_STR = "Language"; //$NON-NLS-1$
		COUNTRY_STR = "Country"; //$NON-NLS-1$
		VARIANT_STR = "Variant"; //$NON-NLS-1$
		regex = String.format(
		        "^language(?:_(?<%s>[a-zA-Z]{2})(?:_(?<%s>[a-zA-Z]{2})(?:_(?<%s>[a-zA-Z]{2}))?)?)?", //$NON-NLS-1$
		        LANGUAGE_STR, COUNTRY_STR, VARIANT_STR);
		pattern = Pattern.compile(regex);

		try (BufferedReader reader = new BufferedReader(new FileReader(FILE))) {
			properties.load(reader);
		} catch (final FileNotFoundException e) {
			System.err.printf("File %s doesn't exist%n", FILE); //$NON-NLS-1$
			System.exit(0);
		} catch (final IOException e) {
			System.err.printf(
			        "Error while reading from file %s. Inform the developer about 'Settings.static-IO'%", //$NON-NLS-1$
			        FILE);
			System.exit(0);
		}

		LANGUAGE = properties.getProperty(LANGUAGE_STR);
		COUNTRY = properties.getProperty(COUNTRY_STR);
		VARIANT = properties.getProperty(VARIANT_STR);
		RESOURCE_BUNDLE = ResourceBundle.getBundle(Languages.BUNDLE_NAME,
		        new Locale(LANGUAGE, COUNTRY, VARIANT));
	}

	/**
	 * Returns the String associated with the {@code key} in the ResourceBundle.
	 *
	 * @param key the key
	 *
	 * @return the String associated with the key
	 */
	public static String getString(String key) {
		try {
			return Languages.RESOURCE_BUNDLE.getString(key);
		} catch (final MissingResourceException e) {
			return '!' + key + '!';
		}
	}


	/**
	 * Displays a pop-up dialog to edit the language settings and writes them to the
	 * file if they are altered.
	 *
	 * @param frame the parent frame of the pop-up dialog.
	 *
	 * @return {@code true} if they were altered, {@code false} otherwise
	 *
	 * @throws IOException if an error occurred while writing to file
	 */
	public static boolean editAndWriteToFile(Frame frame) throws IOException {

		final List<Locale> locales = new ArrayList<>();

		File directory = new File(LANGUAGES_DIRECTORY);
		Utility.foreach(directory.listFiles(), file -> {

			final String fname = file.getName();

			if (isLanguageFile(fname)) {
				Matcher m = pattern.matcher(fname);
				if (!m.find())
					throw new RuntimeException(
					        String.format("Invalid properties file name: %s", fname)); //$NON-NLS-1$

				final String language = emptyStringIfNull(m.group(LANGUAGE_STR));
				final String country  = emptyStringIfNull(m.group(COUNTRY_STR));
				final String variant  = emptyStringIfNull(m.group(VARIANT_STR));
				locales.add(new Locale(language, country, variant));
			}
		});

		if (locales.isEmpty()) {
			JOptionPane.showMessageDialog(frame,
			        String.format(Languages.getString("Languages.1"), LANGUAGES_DIRECTORY), //$NON-NLS-1$
			        Languages.getString("Languages.2"), JOptionPane.WARNING_MESSAGE); //$NON-NLS-1$
			return false;
		}

		Requirements reqWrapper = new Requirements();
		reqWrapper.add("languages", locales);
		reqWrapper.fulfillWithDialog(frame, "languages");
		Locale chosen = (Locale) reqWrapper.getValue("languages");

		Locale current = new Locale(properties.getProperty(LANGUAGE_STR),
		        properties.getProperty(COUNTRY_STR), properties.getProperty(VARIANT_STR));

		if ((chosen == null) || chosen.equals(current))
			return false;

		properties.setProperty(LANGUAGE_STR, chosen.getLanguage());
		properties.setProperty(COUNTRY_STR, chosen.getCountry());
		properties.setProperty(VARIANT_STR, chosen.getVariant());

		try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE))) {
			properties.store(writer, null);
		}
		return true;
	}

	private static String emptyStringIfNull(String s) {
		if (s == null)
			return ""; //$NON-NLS-1$
		return s;
	}

	private static boolean isLanguageFile(String filename) {
		return filename.startsWith("language") && filename.endsWith(".properties"); //$NON-NLS-1$ //$NON-NLS-2$
	}
}
