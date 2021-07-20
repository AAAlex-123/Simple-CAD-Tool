package application;

import java.awt.Frame;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

import localisation.Languages;
import myUtil.OrderedProperties;
import myUtil.Utility;
import requirement.Requirements;

/**
 * A bunch of String and char constants used throughout the project.
 *
 * @author alexm
 */
public final class StringConstants {

	/** The file containing the application's settings */
	public static final String FILE = "program_data\\settings.properties"; //$NON-NLS-1$

	private static final Properties properties = new OrderedProperties();

	private static final Requirements<String> reqs = new Requirements<>();

	public static String COMPONENT_ICON_PATH, MENU_ICON_PATH, USER_DATA, F_NEW_ACCEL, F_CLOSE_ACCEL,
	        F_SAVE_ACCEL, F_SAVE_AS_ACCEL, F_OPEN_ACCEL, F_CLEAR_ACCEL, F_IMPORT_ACCEL,
	        F_UNDO_ACCEL, F_REDO_ACCEL, E_ACTIVATE_ACCEL, E_FOCUS_ACCEL, D_COMPONENT_ACCEL,
	        P_SETTINGS_ACCEL, P_LANGUAGE_ACCEL, H_HELP_ACCEL, BUILTIN_COMMAND_ACCEL_PREFIX,
	        USER_COMMAND_ACCEL_PREFIX, G_INPUT_PIN, G_OUTPUT_PIN, G_BRANCH, G_GATE, G_GATEAND,
	        G_GATEOR, G_GATENOT, G_GATEXOR;
	public static char   M_FILE_MNEMONIC, M_EDIT_MNEMONIC, M_CREATE_MNEMONIC, M_DELETE_MNEMONIC,
	        M_PREFERENCES_MNEMONIC, M_HELP_MNEMONIC;

	static {
		try (BufferedReader reader = new BufferedReader(new FileReader(FILE))) {
			properties.load(reader);
		} catch (final FileNotFoundException e) {
			System.err.printf(Languages.getString("StringConstants.2"), FILE); //$NON-NLS-1$
			System.exit(0);
		} catch (final IOException e) {
			System.err.printf(Languages.getString("StringConstants.3"), FILE); //$NON-NLS-1$
			System.exit(0);
		}

		Utility.foreach(properties.entrySet(), entry -> {
			final String key   = (String) entry.getKey();
			final String value = (String) entry.getValue();
			reqs.add(key);
			reqs.offer(key, value);
		});

		COMPONENT_ICON_PATH = properties.getProperty("Component_Icon_Directory"); //$NON-NLS-1$
		MENU_ICON_PATH = properties.getProperty("Menu_Icon_Directory"); //$NON-NLS-1$
		USER_DATA = properties.getProperty("User_Directory"); //$NON-NLS-1$
		M_FILE_MNEMONIC = properties.getProperty("File_Mnemonic").charAt(0); //$NON-NLS-1$
		M_EDIT_MNEMONIC = properties.getProperty("Edit_Mnemonic").charAt(0); //$NON-NLS-1$
		M_CREATE_MNEMONIC = properties.getProperty("Create_Mnemonic").charAt(0); //$NON-NLS-1$
		M_DELETE_MNEMONIC = properties.getProperty("Delete_Mnemonic").charAt(0); //$NON-NLS-1$
		M_PREFERENCES_MNEMONIC = properties.getProperty("Preferences_Mnemonic").charAt(0); //$NON-NLS-1$
		M_HELP_MNEMONIC = properties.getProperty("Help_Mnemonic").charAt(0); //$NON-NLS-1$
		F_NEW_ACCEL = properties.getProperty("New_Editor"); //$NON-NLS-1$
		F_CLOSE_ACCEL = properties.getProperty("Close_Editor"); //$NON-NLS-1$
		F_SAVE_ACCEL = properties.getProperty("Save"); //$NON-NLS-1$
		F_SAVE_AS_ACCEL = properties.getProperty("Save_As"); //$NON-NLS-1$
		F_OPEN_ACCEL = properties.getProperty("Open_File"); //$NON-NLS-1$
		F_CLEAR_ACCEL = properties.getProperty("Clear_Editor"); //$NON-NLS-1$
		F_IMPORT_ACCEL = properties.getProperty("Import"); //$NON-NLS-1$
		F_UNDO_ACCEL = properties.getProperty("Undo"); //$NON-NLS-1$
		F_REDO_ACCEL = properties.getProperty("Redo"); //$NON-NLS-1$
		E_ACTIVATE_ACCEL = properties.getProperty("Activate_Component"); //$NON-NLS-1$
		E_FOCUS_ACCEL = properties.getProperty("Focus_Component"); //$NON-NLS-1$
		D_COMPONENT_ACCEL = properties.getProperty("Delete_Component"); //$NON-NLS-1$
		P_SETTINGS_ACCEL = properties.getProperty("Edit_Settings"); //$NON-NLS-1$
		P_LANGUAGE_ACCEL = properties.getProperty("Change_Language"); //$NON-NLS-1$
		H_HELP_ACCEL = properties.getProperty("Help"); //$NON-NLS-1$
		BUILTIN_COMMAND_ACCEL_PREFIX = properties.getProperty("Built_in_create_component_prefix"); //$NON-NLS-1$
		USER_COMMAND_ACCEL_PREFIX = properties.getProperty("User_create_component_prefix"); //$NON-NLS-1$
		G_INPUT_PIN = properties.getProperty("Input_Pin_Sequence"); //$NON-NLS-1$
		G_OUTPUT_PIN = properties.getProperty("Output_Pin_Sequence"); //$NON-NLS-1$
		G_BRANCH = properties.getProperty("Branch_Sequence"); //$NON-NLS-1$
		G_GATE = properties.getProperty("Gate_Sequence"); //$NON-NLS-1$
		G_GATEAND = properties.getProperty("Gate_AND_Sequence"); //$NON-NLS-1$
		G_GATEOR = properties.getProperty("Gate_OR_Sequence"); //$NON-NLS-1$
		G_GATENOT = properties.getProperty("Gate_NOT_Sequence"); //$NON-NLS-1$
		G_GATEXOR = properties.getProperty("Gate_XOR_Sequence"); //$NON-NLS-1$
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
		reqs.clear();
		reqs.fulfillWithDialog(frame, Languages.getString("StringConstants.4")); //$NON-NLS-1$

		final boolean committedChanges = reqs.fulfilled();

		if (committedChanges) {
			Utility.foreach(reqs, r -> {
				final String k = r.key(), v = r.value();
				if (!properties.getProperty(k).equals(v))
					properties.setProperty(k, v);
			});

			try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE))) {
				properties.store(writer, null);
			}
		}

		return committedChanges;
	}
}
