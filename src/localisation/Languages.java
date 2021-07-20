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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;

import myUtil.Utility;

/**
 * The language settings used to load the appropriate Strings according to the
 * chosen locale.
 *
 * @author alexm
 */
public class Languages {

	private Languages() {}

	/** The file that contains the language settings */
	public static final String LANGUAGES_FILE = "program_data\\languages.properties"; //$NON-NLS-1$

	private static final String LANGUAGES_DIRECTORY = "src\\localisation";                  //$NON-NLS-1$

	private static final Map<String, String> map = new LinkedHashMap<String, String>() {
														@Override
														public String get(
														        Object key) {
															final String rv = super.get(
															        key);
															if (rv == null)
																System.err.printf(
																        "No value found for key %s%n",              //$NON-NLS-1$
																        key);
															return rv;
														}
													};

	private static final String LANGUAGE, COUNTRY, VARIANT;
	private static final String LANGUAGE_STR, COUNTRY_STR, VARIANT_STR;

	private static final String  regex;
	private static final Pattern pattern;

	private static final String BUNDLE_NAME = "localisation.language"; //$NON-NLS-1$
	private static final ResourceBundle RESOURCE_BUNDLE;

	static {
		LANGUAGE_STR = "Language"; //$NON-NLS-1$
		COUNTRY_STR = "Country"; //$NON-NLS-1$
		VARIANT_STR = "Variant"; //$NON-NLS-1$
		regex = String.format(
		        "^language(?:_(?<%s>[a-zA-Z]{2})(?:_(?<%s>[a-zA-Z]{2})(?:_(?<%s>[a-zA-Z]{2}))?)?)?", //$NON-NLS-1$
		        LANGUAGE_STR, COUNTRY_STR, VARIANT_STR);
		pattern = Pattern.compile(regex);

		try {
			Languages.loadFromFile();
		} catch (final FileNotFoundException e) {
			System.err.printf(Languages.getString("StringConstants.2"), LANGUAGES_FILE); //$NON-NLS-1$
			System.exit(0);
		} catch (final IOException e) {
			System.err.printf(
			        Languages.getString("StringConstants.3"), //$NON-NLS-1$
			        LANGUAGES_FILE);
			System.exit(0);
		} finally {
			LANGUAGE = map.get(LANGUAGE_STR);
			COUNTRY = map.get(COUNTRY_STR);
			VARIANT = map.get(VARIANT_STR);
			RESOURCE_BUNDLE = ResourceBundle.getBundle(Languages.BUNDLE_NAME,
			        new Locale(LANGUAGE, COUNTRY, VARIANT));
		}
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

		Locale chosen  = (Locale) JOptionPane.showInputDialog(frame,
		        Languages.getString("Languages.3"), Languages.getString("Languages.4"), //$NON-NLS-1$ //$NON-NLS-2$
		        JOptionPane.PLAIN_MESSAGE, null, locales.toArray(), locales.get(0));
		Locale current = new Locale(map.get(LANGUAGE_STR), map.get(COUNTRY_STR),
		        map.get(VARIANT_STR));

		if ((chosen == null) || chosen.equals(current))
			return false;

		System.out.printf("Chosen locale: %s%n", chosen); //$NON-NLS-1$

		map.put(LANGUAGE_STR, chosen.getLanguage());
		map.put(COUNTRY_STR, chosen.getCountry());
		map.put(VARIANT_STR, chosen.getVariant());

		writeToFile();
		return true;
	}

	private static void writeToFile() throws IOException {
		try (BufferedWriter writer = new BufferedWriter(
		        new FileWriter(Languages.LANGUAGES_FILE))) {
			for (final Entry<String, String> e : map.entrySet()) {
				writer.write(String.format("%s=%s%n", e.getKey(), e.getValue())); //$NON-NLS-1$
			}
		}
	}

	private static void loadFromFile() throws FileNotFoundException, IOException {

		try (BufferedReader reader = new BufferedReader(
		        new FileReader(Languages.LANGUAGES_FILE))) {
			String line;
			while ((line = reader.readLine()) != null) {
				// lines starting with '#' are comments
				if (!line.equals("") && !line.startsWith("#")) { //$NON-NLS-1$ //$NON-NLS-2$
					final String[] parts = line.split("="); //$NON-NLS-1$
					map.put(parts[0], parts.length == 2 ? parts[1] : ""); //$NON-NLS-1$
				}
			}
		}
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
