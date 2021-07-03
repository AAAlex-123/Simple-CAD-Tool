import javax.swing.SwingUtilities;

import application.Application;

/**
 * The entry point of the program. Constructs and runs an
 * {@link application.Application Application}.
 */
public final class App {

	/**
	 * Constructs and runs an application.
	 *
	 * @param args command line arguments (not used)
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			Application app = new Application();
			app.run();
		});
	}
}
