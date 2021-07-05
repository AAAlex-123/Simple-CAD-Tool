import javax.swing.SwingUtilities;

import application.Application;
import application.StringConstants;

/**
 * The entry point of the program. Constructs and runs an
 * {@link application.Application Application}.
 *
 * @author alexm
 */
public final class App {

	/**
	 * Constructs and runs an Application.
	 *
	 * @param args command line arguments (not used)
	 */
	public static void main(String[] args) {
		// force initialisation of the static fields of StringConstants
		System.out.printf("Loading settings from: %s%n", StringConstants.SETTINGS_FILE);
		Application app = new Application();
		SwingUtilities.invokeLater(() -> app.run());
	}
}
