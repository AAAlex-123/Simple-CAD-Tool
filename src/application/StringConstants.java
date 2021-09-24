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
import requirement.requirements.StringType;
import requirement.util.Requirements;

/**
 * Defines many String and char constants that are used as settings throughout
 * the Application and provides a way to alter them.
 *
 * @author Alex Mandelias
 */
public final class StringConstants {

	/** The file containing the Application's settings */
	public static final String FILE = "program_data\\settings.properties"; //$NON-NLS-1$

	private static final Properties properties = new OrderedProperties();

	private static final Requirements reqs = new Requirements();

	/** Directory of the Component Icons */
	public static final String COMPONENT_ICON_PATH;

	/** Directory of the Menu Icons */
	public static final String MENU_ICON_PATH;

	/** Directory of the Log Files */
	public static final String LOG_PATH;

	/** Name for the Log Files. Should contain "{date}" */
	public static final String LOG_FILE_NAME;

	/** Directory of the user-created .scad files */
	public static final String USER_DATA;

	/** Accelerator for the {@code NEW} menu item */
	public static final String F_NEW_ACCEL;

	/** Accelerator for the {@code CLOSE} menu item */
	public static final String F_CLOSE_ACCEL;

	/** Accelerator for the {@code SAVE} menu item */
	public static final String F_SAVE_ACCEL;

	/** Accelerator for the {@code SAVE} menu item */
	public static final String F_SAVE_AS_ACCEL;

	/** Accelerator for the {@code OPEN} menu item */
	public static final String F_OPEN_ACCEL;

	/** Accelerator for the {@code CLEAR} menu item */
	public static final String F_CLEAR_ACCEL;

	/** Accelerator for the {@code IMPORT} menu item */
	public static final String F_IMPORT_ACCEL;

	/** Accelerator for the {@code UNDO} menu item */
	public static final String F_UNDO_ACCEL;

	/** Accelerator for the {@code REDO} menu item */
	public static final String F_REDO_ACCEL;

	/** Accelerator for the {@code ACTIVATE} menu item */
	public static final String E_ACTIVATE_ACCEL;

	/** Accelerator for the {@code FOCUS} menu item */
	public static final String E_FOCUS_ACCEL;

	/** Accelerator for the {@code DELETE} menu item */
	public static final String D_COMPONENT_ACCEL;

	/** Accelerator for the {@code SETTINGS} menu item */
	public static final String P_SETTINGS_ACCEL;

	/** Accelerator for the {@code LANGUAGE} menu item */
	public static final String P_LANGUAGE_ACCEL;

	/** Accelerator for the {@code HELP} menu item */
	public static final String H_HELP_ACCEL;

	/** Prefix for accelerator of the {@code built-in commands} */
	public static final String BUILTIN_COMMAND_ACCEL_PREFIX;

	/** Prefix for accelerator of the {@code user-created commands} */
	public static final String USER_COMMAND_ACCEL_PREFIX;

	/** Format for the {@code INPUT_PIN} names */
	public static final String G_INPUT_PIN;

	/** Format for the {@code OUTPUT_PIN} names */
	public static final String G_OUTPUT_PIN;

	/** Format for the {@code BRANCH} names */
	public static final String G_BRANCH;

	/** Format for the {@code GATE} names */
	public static final String G_GATE;

	/** Format for the {@code GATEAND} names */
	public static final String G_GATEAND;

	/** Format for the {@code GATEOR} names */
	public static final String G_GATEOR;

	/** Format for the {@code GATENOT} names */
	public static final String G_GATENOT;

	/** Format for the {@code GATEXOR} names */
	public static final String G_GATEXOR;

	/** Mnemonic for the {@code FILE} menu */
	public static final char M_FILE_MNEMONIC;

	/** Mnemonic for the {@code EDIT} menu */
	public static final char M_EDIT_MNEMONIC;

	/** Mnemonic for the {@code CREATE} menu */
	public static final char M_CREATE_MNEMONIC;

	/** Mnemonic for the {@code DELETE} menu */
	public static final char M_DELETE_MNEMONIC;

	/** Mnemonic for the {@code PREFERENCES} menu */
	public static final char M_PREFERENCES_MNEMONIC;

	/** Mnemonic for the {@code HELP} menu */
	public static final char M_HELP_MNEMONIC;

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
			reqs.add(key, StringType.ANY);
			reqs.offer(key, value);
		});

		COMPONENT_ICON_PATH = properties.getProperty("Component_Icon_Directory"); //$NON-NLS-1$
		MENU_ICON_PATH = properties.getProperty("Menu_Icon_Directory"); //$NON-NLS-1$
		LOG_PATH = properties.getProperty("Log_Directory"); //$NON-NLS-1$
		LOG_FILE_NAME = properties.getProperty("Log_File_Name"); //$NON-NLS-1$
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
	 * Displays a pop-up dialog to edit the settings and writes them to the file if
	 * any changes were committed.
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
				final String k = r.key(), v = (String) r.value();
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
