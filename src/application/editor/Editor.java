package application;

import static myUtil.Utility.foreach;
import static myUtil.Utility.max;

import java.awt.BorderLayout;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.swing.WindowConstants;

import command.Command;
import components.Component;
import components.ComponentFactory;
import components.ComponentType;
import exceptions.MalformedBranchException;
import requirement.Requirements;
import requirement.StringType;

/**
 * A class representing an Application. It aggregates all of the individual
 * components and handles the communications between them. The client has to
 * create an {@code Application} object then call its {@code run} method to
 * start the program.
 */
public final class Application {

	/** Location for the icons of the menu */
	public static final String menu_icon_path = "assets\\menu_icons\\";
	/** Location for the icons of the components */
	public static final String component_icon_path = "assets\\component_icons\\";

	private static final String user_data = "user_data\\";
	private static final String autosaveFname = ".autosave.scad";
	private boolean tempSaved = true;

	/**
	 * a spaghetti way to check if the user has opened a file. if not, the first
	 * save is a save_as
	 */
	String current_file;

	// UI elements
	private final JFrame window;
	private final UI ui;
	private final MyMenu menu;
	private final StatusBar sb;

	/** HashMap containing all of the active Components */
	private final Map<Integer, Component> hm = new HashMap<>();

	/** An {@link application.UndoableHistory UndoableHistory} instance to keep track of Commands */
	private final UndoableHistory<Command> undoableHistory;

	/** Constructs the application including its different UI elements */
	public Application() {
		window = new JFrame();
		undoableHistory = new UndoableHistory<>();

		ui = new UI();
		sb = new StatusBar();
		menu = new MyMenu(this);

		sb.addLabel("message");
		sb.addLabel("count");
	}

	/** Runs the application */
	public void run() {

		// configure components only when run to make construction faster
		window.setLayout(new BorderLayout());
		window.add(ui, BorderLayout.CENTER);
		window.add(sb, BorderLayout.SOUTH);
		window.setJMenuBar(menu);

		addCreateCommand(Command.create(this, ComponentType.INPUT_PIN));
		addCreateCommand(Command.create(this, ComponentType.OUTPUT_PIN));
		addCreateCommand(Command.create(this, ComponentType.BRANCH));
		addCreateCommand(Command.create(this, ComponentType.GATEAND));
		addCreateCommand(Command.create(this, ComponentType.GATEOR));
		addCreateCommand(Command.create(this, ComponentType.GATENOT));
		addCreateCommand(Command.create(this, ComponentType.GATEXOR));

		window.setTitle("Simple CAD Tool");
		window.setSize(1000, 600);
		window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		window.setVisible(true);

		checkForAutosave();
		(new BackgroundTasks()).execute();
	}

	/** Terminates the applciation */
	public void terminate() {
		System.out.println("bye");
	}

	private void checkForAutosave() {

		File file = new File(user_data + autosaveFname);

		if (file.isFile() && (file.length() > 0)) {
			int rv = JOptionPane.showOptionDialog(getFrame(), "Autosave file detected. Would you like to load it?",
					"Load Autosave",
					JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);

			if (rv == JOptionPane.YES_OPTION) {

				Actions.OPEN.specify("filename", autosaveFname)
				.specify("gatename", "N/A")
				.specify("filetype", "circuit")
				.context(this)
				.execute();

				current_file = null;

			} else if ((rv == JOptionPane.NO_OPTION) || (rv == JOptionPane.CLOSED_OPTION)) {

			}
		}
	}

	private class BackgroundTasks extends SwingWorker<Void, Void> {

		@Override
		protected Void doInBackground() throws Exception {
			while (!isCancelled()) {
				try {
					Thread.sleep(5000);
				} catch (@SuppressWarnings("unused") InterruptedException ignored) {

				}

				// don't save if there are no recent changes or if there are no Components
				if (!tempSaved && (hm.size() > 0)) {

					String workingFile = current_file;

					Actions.SAVE.specify("filename", autosaveFname)
					.context(Application.this)
					.execute();

					tempSaved = true;
					current_file = workingFile;
				}
			}
			return null;
		}
	}

	/** @return the application's frame */
	JFrame getFrame() {
		return window;
	}

	/**
	 * Adds a Command for creating Components to the Application.
	 *
	 * @param c the Command
	 */
	public void addCreateCommand(Command c) {
		menu.addCreateCommand(c);
	}

	/**
	 * Adds a Component to the Application.
	 *
	 * @param c the Component
	 */
	public void addComponent(Component c) {
		hm.put(c.UID(), c);
		ui.addComponent(c);
		sb.setLabelText("count", "Component count: %d", hm.size());
	}

	/**
	 * Removes a Component from the Application.
	 *
	 * @param c the Component
	 */
	public void removeComponent(Component c) {
		hm.remove(c.UID());
		ui.removeComponent(c);
		sb.setLabelText("count", "Component count: %d", hm.size());
	}

	/**
	 * Returns the Component with the given ID.
	 *
	 * @param UID the ID
	 * @return the Component
	 * 
	 * @throws MissingComponentException if no Component with the UID exists
	 */
	public Component getComponent(int UID) throws MissingComponentException {
		Component c = hm.get(UID);
		if (c == null)
			throw new MissingComponentException(UID);

		return c;
	}

	/** Clears the Application resetting it to its original state */
	void clear() {
		List<Component> ls = new ArrayList<>(getComponents());

		foreach(ls, this::removeComponent);

		undoableHistory.clear();

		Component.resetGlobalID();
	}

	/**
	 * Executes a Command.
	 *
	 * @param c the Command to execute
	 * @throws Exception when something exceptional happens
	 */
	void do_(Command c) throws Exception {
		c.execute();
		undoableHistory.add(c);
	}

	/** Undoes the most recent Command */
	void undo() {
		undoableHistory.undo();
	}

	/** Re-does the most recently undone Command */
	void redo() {
		undoableHistory.redo();
	}

	/** @return a list of the Application's Components */
	public List<Component> getComponents() {
		return new LinkedList<>(hm.values());
	}

	/** @return a list of the Application's deleted Components */
	public List<Component> getDeletedComponents() {
		List<Component> ls = new LinkedList<>();
		foreach(hm.values(), c -> {
			if (ComponentFactory.toRemove(c))
				ls.add(c);
		});
		return ls;
	}

	/**
	 * Updates the 'message' label with a status message. The message is formatted
	 * exactly as if String.format(text, args) was called.
	 *
	 * @param text the text
	 * @param args the format arguments
	 */
	void status(String text, Object... args) {
		sb.setLabelText("message", StatusBar.TextType.DEFAULT, text, args);
	}

	/**
	 * Updates the 'message' label with an error message. The message is formatted
	 * exactly as if String.format(text, args) was called.
	 *
	 * @param text the text
	 * @param args the format arguments
	 */
	void error(String text, Object... args) {
		sb.setLabelText("message", StatusBar.TextType.FAILURE, text, args);
	}

	/**
	 * Updates the 'message' label with the message of the {@code exception}.
	 * 
	 * @param exception the Exception
	 */
	void error(Exception exception) {
		error("%s", exception.getMessage());
	}

	/** Thrown when no Component with the {@code ID} exists */
	public static class MissingComponentException extends Exception {

		/**
		 * Constructs the Exception with information about the {@code ID}.
		 * 
		 * @param id the id for which there is no Component
		 */
		public MissingComponentException(int id) {
			super(String.format("No Component with ID %d exists", id));
		}
	}
}
