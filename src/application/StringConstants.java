package application;

import java.awt.Frame;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import localisation.Languages;
import myUtil.Utility;
import requirement.Requirements;

/**
 * A bunch of String and char constants used throughout the project.
 *
 * @author alexm
 */
@SuppressWarnings("javadoc")
public final class StringConstants {

	private StringConstants() {}

	public static final String SETTINGS = "program_data\\settings.properties"; //$NON-NLS-1$

	private static final Map<String, String>  map  = new LinkedHashMap<>() {
														@Override
														public String get(Object key) {
															final String rv = super.get(key);
															if (rv == null)
																System.err.printf(
																        "No value found for key %s%n",              //$NON-NLS-1$
																        key);
															return rv;
														}
													};
	private static final Requirements<String> reqs = new Requirements<>();

	public static String COMPONENT_ICON_PATH, MENU_ICON_PATH, USER_DATA, F_NEW_ACCEL, F_CLOSE_ACCEL,
	        F_SAVE_ACCEL, F_SAVE_AS_ACCEL, F_OPEN_ACCEL, F_CLEAR_ACCEL, F_IMPORT_ACCEL,
	        F_UNDO_ACCEL, F_REDO_ACCEL, E_ACTIVATE_ACCEL, E_FOCUS_ACCEL, D_COMPONENT_ACCEL,
	        P_SETTINGS_ACCEL, H_HELP_ACCEL, BUILTIN_COMMAND_ACCEL_PREFIX, USER_COMMAND_ACCEL_PREFIX,
	        G_INPUT_PIN, G_OUTPUT_PIN, G_BRANCH, G_GATE, G_GATEAND, G_GATEOR, G_GATENOT, G_GATEXOR,
	        L_LANGUAGE, L_COUNTRY, L_VARIANT;
	public static char   M_FILE_MNEMONIC, M_EDIT_MNEMONIC, M_CREATE_MNEMONIC, M_DELETE_MNEMONIC,
	        M_PREFERENCES_MNEMONIC, M_HELP_MNEMONIC;

	public static void init() {
		try {
			StringConstants.loadFromFile();
			for (final Entry<String, String> e : StringConstants.map.entrySet()) {
				final String key = e.getKey(), value = e.getValue();
				StringConstants.reqs.add(key);
				StringConstants.reqs.offer(key, value);
			}
		} catch (final FileNotFoundException e) {
			System.err.printf(Languages.getString("StringConstants.2"), StringConstants.SETTINGS); //$NON-NLS-1$
			System.exit(0);
		} catch (final IOException e) {
			System.err.printf(
			        Languages.getString("StringConstants.3"), //$NON-NLS-1$
			        StringConstants.SETTINGS);
			System.exit(0);
		}
	}

	/**
	 * @return {@code true} if some settings were edited, {@code false} otherwise
	 */
	public static boolean edit(Frame frame) {
		StringConstants.reqs.clear();
		StringConstants.reqs.fulfillWithDialog(frame, Languages.getString("StringConstants.4")); //$NON-NLS-1$

		final boolean committedChanges = StringConstants.reqs.fulfilled();
		if (committedChanges) {
			Utility.foreach(StringConstants.reqs, r -> {
				final String k = r.key(), v = r.value();
				if (!StringConstants.map.get(k).equals(v))
					StringConstants.map.put(k, v);
			});
		}
		return committedChanges;
	}

	public static void writeToFile() throws IOException {
		try (BufferedWriter writer = new BufferedWriter(
		        new FileWriter(StringConstants.SETTINGS))) {
			for (final Entry<String, String> e : StringConstants.map.entrySet())
				writer.write(String.format("%s=%s%n", e.getKey(), e.getValue())); //$NON-NLS-1$
		}
	}

	private static void loadFromFile() throws FileNotFoundException, IOException {

		try (BufferedReader reader = new BufferedReader(
		        new FileReader(StringConstants.SETTINGS))) {
			String line;
			while ((line = reader.readLine()) != null) {
				// lines starting with '#' are comments
				if (!line.equals("") && !line.startsWith("#")) { //$NON-NLS-1$ //$NON-NLS-2$
					final String[] parts = line.split("="); //$NON-NLS-1$
					StringConstants.map.put(parts[0], parts.length == 2 ? parts[1] : ""); //$NON-NLS-1$
				}
			}
		}

		StringConstants.COMPONENT_ICON_PATH = StringConstants.map.get("Component_Icon_Directory"); //$NON-NLS-1$
		StringConstants.MENU_ICON_PATH = StringConstants.map.get("Menu_Icon_Directory"); //$NON-NLS-1$
		StringConstants.USER_DATA = StringConstants.map.get("User_Directory"); //$NON-NLS-1$
		StringConstants.M_FILE_MNEMONIC = StringConstants.map.get("File_Mnemonic").charAt(0); //$NON-NLS-1$
		StringConstants.M_EDIT_MNEMONIC = StringConstants.map.get("Edit_Mnemonic").charAt(0); //$NON-NLS-1$
		StringConstants.M_CREATE_MNEMONIC = StringConstants.map.get("Create_Mnemonic").charAt(0); //$NON-NLS-1$
		StringConstants.M_DELETE_MNEMONIC = StringConstants.map.get("Delete_Mnemonic").charAt(0); //$NON-NLS-1$
		StringConstants.M_PREFERENCES_MNEMONIC = StringConstants.map.get("Preferences_Mnemonic") //$NON-NLS-1$
		        .charAt(0);
		StringConstants.M_HELP_MNEMONIC = StringConstants.map.get("Help_Mnemonic").charAt(0); //$NON-NLS-1$
		StringConstants.F_NEW_ACCEL = StringConstants.map.get("New_Editor"); //$NON-NLS-1$
		StringConstants.F_CLOSE_ACCEL = StringConstants.map.get("Close_Editor"); //$NON-NLS-1$
		StringConstants.F_SAVE_ACCEL = StringConstants.map.get("Save"); //$NON-NLS-1$
		StringConstants.F_SAVE_AS_ACCEL = StringConstants.map.get("Save_As"); //$NON-NLS-1$
		StringConstants.F_OPEN_ACCEL = StringConstants.map.get("Open_File"); //$NON-NLS-1$
		StringConstants.F_CLEAR_ACCEL = StringConstants.map.get("Clear_Editor"); //$NON-NLS-1$
		StringConstants.F_IMPORT_ACCEL = StringConstants.map.get("Import"); //$NON-NLS-1$
		StringConstants.F_UNDO_ACCEL = StringConstants.map.get("Undo"); //$NON-NLS-1$
		StringConstants.F_REDO_ACCEL = StringConstants.map.get("Redo"); //$NON-NLS-1$
		StringConstants.E_ACTIVATE_ACCEL = StringConstants.map.get("Activate_Component"); //$NON-NLS-1$
		StringConstants.E_FOCUS_ACCEL = StringConstants.map.get("Focus_Component"); //$NON-NLS-1$
		StringConstants.D_COMPONENT_ACCEL = StringConstants.map.get("Delete_Component"); //$NON-NLS-1$
		StringConstants.P_SETTINGS_ACCEL = StringConstants.map.get("Edit_Settings"); //$NON-NLS-1$
		StringConstants.H_HELP_ACCEL = StringConstants.map.get("Help"); //$NON-NLS-1$
		StringConstants.BUILTIN_COMMAND_ACCEL_PREFIX = StringConstants.map
		        .get("Built_in_create_component_prefix"); //$NON-NLS-1$
		StringConstants.USER_COMMAND_ACCEL_PREFIX = StringConstants.map
		        .get("User_create_component_prefix"); //$NON-NLS-1$
		StringConstants.G_INPUT_PIN = StringConstants.map.get("Input_Pin_Sequence"); //$NON-NLS-1$
		StringConstants.G_OUTPUT_PIN = StringConstants.map.get("Output_Pin_Sequence"); //$NON-NLS-1$
		StringConstants.G_BRANCH = StringConstants.map.get("Branch_Sequence"); //$NON-NLS-1$
		StringConstants.G_GATE = StringConstants.map.get("Gate_Sequence"); //$NON-NLS-1$
		StringConstants.G_GATEAND = StringConstants.map.get("Gate_AND_Sequence"); //$NON-NLS-1$
		StringConstants.G_GATEOR = StringConstants.map.get("Gate_OR_Sequence"); //$NON-NLS-1$
		StringConstants.G_GATENOT = StringConstants.map.get("Gate_NOT_Sequence"); //$NON-NLS-1$
		StringConstants.G_GATEXOR = StringConstants.map.get("Gate_XOR_Sequence"); //$NON-NLS-1$
		StringConstants.L_LANGUAGE = StringConstants.map.get("Language"); //$NON-NLS-1$
		StringConstants.L_COUNTRY = StringConstants.map.get("Country"); //$NON-NLS-1$
		StringConstants.L_VARIANT = StringConstants.map.get("Variant"); //$NON-NLS-1$
	}
}
