import javax.swing.SwingUtilities;

import application.Application;
import application.StringConstants;
import localisation.Languages;

/**
 * The entry point of the program. Constructs and runs an {@link Application}.
 *
 * @author Alex Mandelias
 */
public final class App {

	/**
	 * Constructs and runs an {@link Application}.
	 *
	 * @param args command line arguments (not used)
	 */
	public static void main(String[] args) {
		System.out.printf(Languages.getString("App.0"), Languages.FILE); //$NON-NLS-1$
		System.out.printf(Languages.getString("App.1"), StringConstants.FILE); //$NON-NLS-1$

		Application app = new Application();
		SwingUtilities.invokeLater(() -> app.run());
	}
}
