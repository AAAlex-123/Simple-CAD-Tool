import javax.swing.SwingUtilities;

import application.Application;
import application.StringConstants;
import localisation.Languages;

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
		StringConstants.init();
		System.out.printf(Languages.getString("App.0"), StringConstants.SETTINGS); //$NON-NLS-1$
		Application app = new Application();
		SwingUtilities.invokeLater(() -> app.run());
	}
}
