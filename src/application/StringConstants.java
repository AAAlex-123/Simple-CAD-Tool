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

import exceptions.InitializationError;
import myUtil.Utility;
import requirement.Requirements;

/**
 * A bunch of String and char constants used throughout the codebase.
 *
 * @author alexm
 */
@SuppressWarnings("javadoc")
public final class StringConstants {

	private StringConstants() {}

	public static final String                SETTINGS_FILE = "program_data\\settings.properties";
	private static final Map<String, String>  map;
	private static final Requirements<String> reqs;

	public static String COMPONENT_ICON_PATH, MENU_ICON_PATH, USER_DATA, F_NEW_ACCEL, F_CLOSE_ACCEL, F_SAVE_ACCEL,
	F_SAVE_AS_ACCEL, F_OPEN_ACCEL, F_CLEAR_ACCEL, F_IMPORT_ACCEL, F_UNDO_ACCEL, F_REDO_ACCEL, E_ACTIVATE_ACCEL,
	E_FOCUS_ACCEL, D_COMPONENT_ACCEL, P_SETTINGS_ACCEL, H_HELP_ACCEL, BUILTIN_COMMAND_ACCEL_PREFIX,
	USER_COMMAND_ACCEL_PREFIX, G_INPUT_PIN, G_OUTPUT_PIN, G_BRANCH, G_GATE, G_GATEAND, G_GATEOR, G_GATENOT,
	G_GATEXOR;
	public static char M_FILE_MNEMONIC, M_EDIT_MNEMONIC, M_CREATE_MNEMONIC, M_DELETE_MNEMONIC, M_PREFERENCES_MNEMONIC,
	M_HELP_MNEMONIC;

	static {
		map = new LinkedHashMap<String, String>() {
			@Override
			public String get(Object key) {
				final String rv = super.get(key);
				if (rv == null)
					System.err.printf("No value found for key %s%n", key);
				return rv;
			}
		};
		reqs = new Requirements<>();
		
		StringConstants.loadFromFile();
		for (final Entry<String, String> e : StringConstants.map.entrySet()) {
			final String key = e.getKey(), value = e.getValue();
			StringConstants.reqs.add(key);
			StringConstants.reqs.offer(key, value);
		
	}

	/**
	 * @return {@code true} if some settings were edited, {@code false} otherwise
	 */
	public static boolean edit(Frame frame) {
		StringConstants.reqs.clear();
		StringConstants.reqs.fulfillWithDialog(frame, "Settings");

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
				new FileWriter(StringConstants.SETTINGS_FILE))) {
			for (final Entry<String, String> e : StringConstants.map.entrySet())
				writer.write(String.format("%s=%s%n", e.getKey(), e.getValue()));
		}
	}

	private static void loadFromFile() throws InitializationError {

		try (BufferedReader reader = new BufferedReader(
				new FileReader(StringConstants.SETTINGS_FILE))) {
			String line;
			while ((line = reader.readLine()) != null) {
				// lines starting with '#' are comments
				if (!line.equals("") && !line.startsWith("#")) {
					final String[] parts = line.split("=");
					StringConstants.map.put(parts[0], parts[1]);
				}
			}
		} catch(FileNotFoundException e) {
			throw new InitializationError(String.format("File %s doesn't exist%n", StringConstants.SETTINGS_FILE));
		} catch(IOException ioe) {
			throw new InitializationError(String.format("Error while reading from file %s. Inform the developer about 'StringConstants.static-IO'%n",
			StringConstants.SETTINGS_FILE));
		} catch(Throwable e) {
			throw new InitializationError(String.format("Error while reading file %s: %s", StringConstants.SETTINGS_FILE, e));
		}

		StringConstants.COMPONENT_ICON_PATH = StringConstants.map.get("Component_Icon_Directory");
		StringConstants.MENU_ICON_PATH = StringConstants.map.get("Menu_Icon_Directory");
		StringConstants.USER_DATA = StringConstants.map.get("User_Directory");
		StringConstants.M_FILE_MNEMONIC = StringConstants.map.get("File_Mnemonic").charAt(0);
		StringConstants.M_EDIT_MNEMONIC = StringConstants.map.get("Edit_Mnemonic").charAt(0);
		StringConstants.M_CREATE_MNEMONIC = StringConstants.map.get("Create_Mnemonic").charAt(0);
		StringConstants.M_DELETE_MNEMONIC = StringConstants.map.get("Delete_Mnemonic").charAt(0);
		StringConstants.M_PREFERENCES_MNEMONIC = StringConstants.map.get("Preferences_Mnemonic")
				.charAt(0);
		StringConstants.M_HELP_MNEMONIC = StringConstants.map.get("Help_Mnemonic").charAt(0);
		StringConstants.F_NEW_ACCEL = StringConstants.map.get("New_Editor");
		StringConstants.F_CLOSE_ACCEL = StringConstants.map.get("Close_Editor");
		StringConstants.F_SAVE_ACCEL = StringConstants.map.get("Save");
		StringConstants.F_SAVE_AS_ACCEL = StringConstants.map.get("Save_As");
		StringConstants.F_OPEN_ACCEL = StringConstants.map.get("Open_File");
		StringConstants.F_CLEAR_ACCEL = StringConstants.map.get("Clear_Editor");
		StringConstants.F_IMPORT_ACCEL = StringConstants.map.get("Import");
		StringConstants.F_UNDO_ACCEL = StringConstants.map.get("Undo");
		StringConstants.F_REDO_ACCEL = StringConstants.map.get("Redo");
		StringConstants.E_ACTIVATE_ACCEL = StringConstants.map.get("Activate_Component");
		StringConstants.E_FOCUS_ACCEL = StringConstants.map.get("Focus_Component");
		StringConstants.D_COMPONENT_ACCEL = StringConstants.map.get("Delete_Component");
		StringConstants.P_SETTINGS_ACCEL = StringConstants.map.get("Edit_Settings");
		StringConstants.H_HELP_ACCEL = StringConstants.map.get("Help");
		StringConstants.BUILTIN_COMMAND_ACCEL_PREFIX = StringConstants.map
				.get("Built_in_create_component_prefix");
		StringConstants.USER_COMMAND_ACCEL_PREFIX = StringConstants.map
				.get("User_create_component_prefix");
		StringConstants.G_INPUT_PIN = StringConstants.map.get("Input_Pin_Sequence");
		StringConstants.G_OUTPUT_PIN = StringConstants.map.get("Output_Pin_Sequence");
		StringConstants.G_BRANCH = StringConstants.map.get("Branch_Sequence");
		StringConstants.G_GATE = StringConstants.map.get("Gate_Sequence");
		StringConstants.G_GATEAND = StringConstants.map.get("Gate_AND_Sequence");
		StringConstants.G_GATEOR = StringConstants.map.get("Gate_OR_Sequence");
		StringConstants.G_GATENOT = StringConstants.map.get("Gate_NOT_Sequence");
		StringConstants.G_GATEXOR = StringConstants.map.get("Gate_XOR_Sequence");
	}
}
