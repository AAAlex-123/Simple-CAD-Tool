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

	private static final Map<String, String> map = new LinkedHashMap<String, String>() {
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

	public static final String COMPONENT_ICON_PATH, MENU_ICON_PATH, USER_DATA, F_NEW_ACCEL,
	        F_CLOSE_ACCEL,
	        F_SAVE_ACCEL, F_SAVE_AS_ACCEL, F_OPEN_ACCEL, F_CLEAR_ACCEL, F_IMPORT_ACCEL,
	        F_UNDO_ACCEL, F_REDO_ACCEL, E_ACTIVATE_ACCEL, E_FOCUS_ACCEL, D_COMPONENT_ACCEL,
	        P_SETTINGS_ACCEL, P_LANGUAGE_ACCEL, H_HELP_ACCEL, BUILTIN_COMMAND_ACCEL_PREFIX,
	        USER_COMMAND_ACCEL_PREFIX,
	        G_INPUT_PIN, G_OUTPUT_PIN, G_BRANCH, G_GATE, G_GATEAND, G_GATEOR, G_GATENOT, G_GATEXOR;
	public static final char   M_FILE_MNEMONIC, M_EDIT_MNEMONIC, M_CREATE_MNEMONIC,
	        M_DELETE_MNEMONIC,
	        M_PREFERENCES_MNEMONIC, M_HELP_MNEMONIC;

	static {
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
		} finally {
			COMPONENT_ICON_PATH = StringConstants.map.get("Component_Icon_Directory"); //$NON-NLS-1$
			MENU_ICON_PATH = StringConstants.map.get("Menu_Icon_Directory"); //$NON-NLS-1$
			USER_DATA = StringConstants.map.get("User_Directory"); //$NON-NLS-1$
			M_FILE_MNEMONIC = StringConstants.map.get("File_Mnemonic").charAt(0); //$NON-NLS-1$
			M_EDIT_MNEMONIC = StringConstants.map.get("Edit_Mnemonic").charAt(0); //$NON-NLS-1$
			M_CREATE_MNEMONIC = StringConstants.map.get("Create_Mnemonic").charAt(0); //$NON-NLS-1$
			M_DELETE_MNEMONIC = StringConstants.map.get("Delete_Mnemonic").charAt(0); //$NON-NLS-1$
			M_PREFERENCES_MNEMONIC = StringConstants.map.get("Preferences_Mnemonic").charAt(0); //$NON-NLS-1$
			M_HELP_MNEMONIC = StringConstants.map.get("Help_Mnemonic").charAt(0); //$NON-NLS-1$
			F_NEW_ACCEL = StringConstants.map.get("New_Editor"); //$NON-NLS-1$
			F_CLOSE_ACCEL = StringConstants.map.get("Close_Editor"); //$NON-NLS-1$
			F_SAVE_ACCEL = StringConstants.map.get("Save"); //$NON-NLS-1$
			F_SAVE_AS_ACCEL = StringConstants.map.get("Save_As"); //$NON-NLS-1$
			F_OPEN_ACCEL = StringConstants.map.get("Open_File"); //$NON-NLS-1$
			F_CLEAR_ACCEL = StringConstants.map.get("Clear_Editor"); //$NON-NLS-1$
			F_IMPORT_ACCEL = StringConstants.map.get("Import"); //$NON-NLS-1$
			F_UNDO_ACCEL = StringConstants.map.get("Undo"); //$NON-NLS-1$
			F_REDO_ACCEL = StringConstants.map.get("Redo"); //$NON-NLS-1$
			E_ACTIVATE_ACCEL = StringConstants.map.get("Activate_Component"); //$NON-NLS-1$
			E_FOCUS_ACCEL = StringConstants.map.get("Focus_Component"); //$NON-NLS-1$
			D_COMPONENT_ACCEL = StringConstants.map.get("Delete_Component"); //$NON-NLS-1$
			P_SETTINGS_ACCEL = StringConstants.map.get("Edit_Settings"); //$NON-NLS-1$
			P_LANGUAGE_ACCEL = StringConstants.map.get("Change_Language"); //$NON-NLS-1$
			H_HELP_ACCEL = StringConstants.map.get("Help"); //$NON-NLS-1$
			BUILTIN_COMMAND_ACCEL_PREFIX = StringConstants.map
			        .get("Built_in_create_component_prefix"); //$NON-NLS-1$
			USER_COMMAND_ACCEL_PREFIX = StringConstants.map.get("User_create_component_prefix"); //$NON-NLS-1$
			G_INPUT_PIN = StringConstants.map.get("Input_Pin_Sequence"); //$NON-NLS-1$
			G_OUTPUT_PIN = StringConstants.map.get("Output_Pin_Sequence"); //$NON-NLS-1$
			G_BRANCH = StringConstants.map.get("Branch_Sequence"); //$NON-NLS-1$
			G_GATE = StringConstants.map.get("Gate_Sequence"); //$NON-NLS-1$
			G_GATEAND = StringConstants.map.get("Gate_AND_Sequence"); //$NON-NLS-1$
			G_GATEOR = StringConstants.map.get("Gate_OR_Sequence"); //$NON-NLS-1$
			G_GATENOT = StringConstants.map.get("Gate_NOT_Sequence"); //$NON-NLS-1$
			G_GATEXOR = StringConstants.map.get("Gate_XOR_Sequence"); //$NON-NLS-1$
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
		StringConstants.reqs.clear();
		StringConstants.reqs.fulfillWithDialog(frame, Languages.getString("StringConstants.4")); //$NON-NLS-1$

		final boolean committedChanges = StringConstants.reqs.fulfilled();
		if (committedChanges) {
			Utility.foreach(StringConstants.reqs, r -> {
				final String k = r.key(), v = r.value();
				if (!StringConstants.map.get(k).equals(v))
					StringConstants.map.put(k, v);
			});
		} else {
			return false;
		}

		writeToFile();
		return true;
	}

	private static void writeToFile() throws IOException {
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
	}
}
