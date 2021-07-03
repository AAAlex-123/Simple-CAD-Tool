import javax.swing.SwingUtilities;

import application.Application;

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
		Application app = new Application();
		SwingUtilities.invokeLater(() -> app.run());
	}
}
