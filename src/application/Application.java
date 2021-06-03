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

	/** An enum-strategy for the different Actions the user may take. */
	enum Actions {

		/** Action for creating a Component. */
		CREATE("command") {
			@Override
			void execute() {

				if (!reqs.fulfilled())
					throw new RuntimeException("Execute CREATE without reqs");

				Command cte = (Command) reqs.getV("command");

				if (!cte.canExecute()) {
					context.status("%s cancelled", cte);
					return;
				}

				try {
					context.do_(cte);
					context.status("%s successful", cte);
					context.tempSaved = false;
				} catch (MissingComponentException | MalformedBranchException e) {
					context.error(e);
				} catch (Exception e) {
					// semi-spaghetti throws declaration on execute();
					throw new RuntimeException(e);
				}

				reqs.clear();
			}
		},

		/** Action for deleting a Component. */
		DELETE("command") {
			@Override
			void execute() {

				if (!reqs.fulfilled())
					throw new RuntimeException("Execute DELETE without reqs");

				Command cte = (Command) reqs.getV("command");

				if (!cte.canExecute()) {
					context.status("Delete cancelled");
					return;
				}

				try {
					context.do_(cte);
					context.status("Component deleted");
					context.tempSaved = false;
				} catch (MissingComponentException e) {
					context.error(e);
				} catch (Exception e) {
					// semi-spaghetti throws declaration on execute();
					throw new RuntimeException(e);
				}

				reqs.clear();
			}
		},

		/** Action for saving the components of the Application to a File. */
		SAVE("filename", StringType.FILENAME) {
			@Override
			void execute() {

				if (!reqs.fulfilled()) {
					context.status("File save cancelled");
					return;
				}

				String fname = (String) reqs.getV("filename");
				context.current_file = fname;

				try {
					writeToFile(fname);
					context.status("File %s saved successfully", fname);
				} catch (IOException e) {
					context.error(
							"Error while writing to file. Try again or inform the developer about 'Action-Open-IO'");
					throw new RuntimeException(e);
				}

				reqs.clear();
			}
		},

		/** An Action that reads the contents of a File to the Application. */
		OPEN(new String[] { "filename", "gatename", "filetype" },
				new StringType[] { StringType.FILENAME, StringType.ANY, StringType.FILETYPE }) {
			@Override
			void execute() {

				if (!reqs.fulfilled()) {
					context.status("File %s cancelled", reqs.getV("filetype").equals(circuit) ? "open" : "import");
					return;
				}

				String fname = (String) reqs.getV("filename");
				String ftype = (String) reqs.getV("filetype");

				List<Component> components = new ArrayList<>();
				List<Command> commands = new ArrayList<>();

				try {
					readFromFile(fname, components, commands);

					if (ftype.equals(circuit)) {

						context.clear();

						foreach(components, c -> context.addComponent(c));
						foreach(commands, c -> context.undoableHistory.add(c));

						Component.setGlobalID(max(context.getComponents(), Component::UID).UID() + 1);

					} else if (ftype.equals(component)) {

						Command cgc = Command.create(context, commands, (String) reqs.getV("gatename"));
						context.addCreateCommand(cgc);

					} else {
						throw new RuntimeException("Invalid filetype specified");
					}

					context.current_file = fname;

					context.status("File %s %s successfully", fname, ftype.equals(circuit) ? "opened" : "imported");
					context.tempSaved = false;

				} catch (IncompatibleFileException | FileCorruptedException e) {
					context.error(e);
				} catch (@SuppressWarnings("unused") FileNotFoundException e) {
					context.error("File %s doesn't exist", fname);
				} catch (IOException e) {
					context.error(
							"Error while reading from file. Try again or inform the developer about 'Action-Open-IO'");
					throw new RuntimeException(e);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}

				reqs.clear();
			}
		},

		/** An action for resetting the Application. */
		CLEAR {
			@Override
			public void execute() {
				context.clear();
				context.status("Application cleared");
			}
		},

		/** An Action for undoing a Command. */
		UNDO {
			@Override
			public void execute() {
				context.undo();
				context.status("Undo");
				context.tempSaved = false;
			}
		},

		/** An Action for redoing a Command. */
		REDO {
			@Override
			public void execute() {
				context.redo();
				context.status("Redo");
				context.tempSaved = false;
			}
		},

		/** An Action for showing Help. */
		HELP {
			@Override
			public void execute() {
				context.status("Someone doesn't know how to use a UI...");
				int[] res = new int[16];
				int i=-1;
				res[++i] = JOptionPane.showConfirmDialog(null, "PLEASE NOTE THAT THIS HELP IS KINDA CRAPPY");
				res[++i] = JOptionPane.showConfirmDialog(null,
						"but i wont fix it rn, it will probably be replaced with something fancier in the future");
				res[++i] = JOptionPane.showConfirmDialog(null, "use the menus");
				res[++i] = JOptionPane.showConfirmDialog(null, "file: read buttons click buttons");
				res[++i] = JOptionPane.showConfirmDialog(null,
						"open opens to edit, import imports as a component that can be placed, clear deletes everything");
				res[++i] = JOptionPane.showConfirmDialog(null,
						"WARNING: TO SAVE AS A COMPONENT AND THEN IMPORT IT  A L W A Y S  CLEAR FIRST");
				res[++i] = JOptionPane.showConfirmDialog(null,
						"i am not to be held responsible for the error messages you will create if you don't follow the previous warning");
				res[++i] = JOptionPane.showConfirmDialog(null,
						"edit: edit components without mouse; currently only turn pins on/off");
				res[++i] = JOptionPane.showConfirmDialog(null,
						"create: click the type of component. Some components require additional parameters (i hope you know regex)");
				res[++i] = JOptionPane.showConfirmDialog(null, "delete: click 'component'. Then specify the ID.");
				res[++i] = JOptionPane.showConfirmDialog(null, "'help' displays this helpful help message");
				res[++i] = JOptionPane.showConfirmDialog(null, "everything has keybinds");
				res[++i] = JOptionPane.showConfirmDialog(null,
						"When filling in parameters, (shift + )tab to move (backward)forward, enter for ok, escape to cancel");
				res[++i] = JOptionPane.showConfirmDialog(null,
						"click on input pins to turn them on and off and drag components (not branches) to move them around");
				res[++i] = JOptionPane.showConfirmDialog(null,"example branch: from 1st pin of component with id=1 to 3rd pin of component with id=2:'in id'=1 'in index'=0 'out id'=2 'out index'=2");
				res[++i] = JOptionPane.showConfirmDialog(null,
						"to start, open file `nand` and play around with it (undo is saved when saving files). WITHOUT SAVING, import file `nand` with custom component name, place it, and see your nand come to life");

				// yes=0 no=1 cancel=2 x=-1

				int count;
				count = 0; for (i = 0; i < res.length; ++i) if (res[i] == -1) count++; int x = count;
				count = 0; for (i = 0; i < res.length; ++i) if (res[i] == 0) count++; int y = count;
				count = 0; for (i = 0; i < res.length; ++i) if (res[i] == 1) count++; int n = count;
				count = 0; for (i = 0; i < res.length; ++i) if (res[i] == 2) count++; int c = count;
				int res1 = JOptionPane.showConfirmDialog(null,
						String.format("fun fact: you clicked: %d yes, %d no, %d cancel, %d 'x'", y, n, c, x));

				if (res1 == -1) JOptionPane.showConfirmDialog(null, String.format("(%d 'x')", x + 1));
				else if (res1 == 0) JOptionPane.showConfirmDialog(null, String.format("(%d yes)", y + 1));
				else if (res1 == 1) JOptionPane.showConfirmDialog(null, String.format("(%d no)", n + 1));
				else if (res1 == 2) JOptionPane.showConfirmDialog(null, String.format("(%d cancel)", c + 1));
			}
		};

		// bytes at the start and end of file
		private static final Integer start, eof;

		private static final String component, circuit;

		static {
			// the string should match Requirement.Type
			component = "component";
			circuit = "circuit";

			start = 10;
			eof = 42;
		}

		/** The Requirements of the Action */
		protected final Requirements<Object> reqs;

		/** The context of the Action */
		Application context;

		Actions() {
			reqs = null;
		}

		Actions(String reqKey) {
			reqs = new Requirements<>();
			reqs.add(reqKey);
		}

		Actions(String reqKey, StringType stringType) {
			reqs = new Requirements<>();
			reqs.add(reqKey, stringType);
		}

		Actions(String[] reqKeys) {
			reqs = new Requirements<>();
			foreach(reqKeys, reqs::add);
		}

		Actions(String[] reqKeys, StringType[] types) {
			reqs = new Requirements<>();

			if (reqKeys.length != types.length)
				throw new RuntimeException("Invalid arguments in enum constructor");

			for (int i = 0; i < reqKeys.length; i++)
				reqs.add(reqKeys[i], types[i]);
		}

		/** Executes the Action */
		abstract void execute();

		/**
		 * Specifies the Action's context, where it will operate.
		 *
		 * @param app the context
		 * @return this (used for chaining)
		 */
		Actions context(Application app) {
			context = app;
			return this;
		}

		/**
		 * Creates a pop-up dialog to let the user fulfil the Requirements.
		 * 
		 * @param parentFrame the parent frame for the dialog
		 * 
		 * @return this (used for chaining)
		 */
		Actions specifyWithDialog(Application parentFrame) {
			reqs.fulfillWithDialog(parentFrame.getFrame(), toString());
			return this;
		}

		/**
		 * Specifies an Object to fulfil a specific Requirement.
		 *
		 * @param req the Requirement
		 * @param c   the Command
		 * @return this (used for chaining)
		 */
		Actions specify(String req, Object c) {
			reqs.fulfil(req, c);
			return this;
		}

		/** Thrown when a file is corrupted and can't be read */
		protected static class FileCorruptedException extends Exception {

			/**
			 * Constructs the Exception with information about the {@code filename}.
			 * 
			 * @param filename the name of the corrupted file
			 */
			public FileCorruptedException(String filename) {
				super(String.format("Can't read file %s because its contents are corrupted", filename));
			}
		}

		/** Thrown when a file's data are incompatible with current program version */
		protected static class IncompatibleFileException extends Exception {

			/**
			 * Constructs the Exception with information about the {@code filename}.
			 * 
			 * @param filename the name of the file with incompatible data
			 */
			public IncompatibleFileException(String filename, InvalidClassException e) {
				super(formatMessage(filename, e));
			}

			private static String formatMessage(String filename, InvalidClassException e) {
				Pattern p = Pattern
						.compile(".*? serialVersionUID = (\\d+), .*? serialVersionUID = (\\d+)");
				Matcher m = p.matcher(e.getMessage());

				if (!m.matches())
					throw new RuntimeException("Invalid regex in IncompatibleFileException");

				int idInFile = Integer.valueOf(m.group(1));
				int idInClass = Integer.valueOf(m.group(2));

				return String.format("Data in file %s corresponds to %s version of the program", filename,
						idInFile > idInClass ? "a later" : "a previous");
			}
		}

		/**
		 * Writes the contents of the Application to a file.
		 *
		 * @param filename the filename
		 * @throws IOException when an IOExcetpion occurs
		 */
		protected void writeToFile(String filename) throws IOException {

			String outputFile = String.format("%s\\%s", user_data, filename);

			try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(outputFile))) {

				oos.writeByte(start);

				oos.writeInt(context.getComponents().size());
				for (Component c : context.getComponents())
					oos.writeObject(c);

				oos.writeInt(context.undoableHistory.getHistory().size());
				for (Undoable u : context.undoableHistory.getHistory())
					oos.writeObject(u);

				oos.writeByte(eof);
			}
		}

		/**
		 * Fills the Lists with the Components and Commands from the file.
		 *
		 * @param filename   the filename
		 * @param components the list that will be filled with Components
		 * @param commands   the list that will be filled with Commands
		 * 
		 * @throws IOException               when an IOException occurred
		 * @throws FileNotFoundException     when the file couldn't be found
		 * @throws FileCorruptedException    when the contents of the file are corrupted
		 * @throws IncompatibleFileException when the file data corresponds to a
		 *                                   previous version of the program
		 */
		@SuppressWarnings("unused")
		protected void readFromFile(String filename, List<Component> components, List<Command> commands)
				throws FileNotFoundException, IOException, FileCorruptedException, IncompatibleFileException {

			String inputFile = String.format("%s\\%s", user_data, filename);

			try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(inputFile))) {

				// read start
				if (ois.readByte() != start)
					throw new FileCorruptedException(filename);

				// read components
				int count = ois.readInt();
				for (int i = 0; i < count; ++i) {
					Component c = (Component) ois.readObject();
					ComponentFactory.restoreSerialisedComponent(c);
					components.add(c);
				}

				// read commands
				count = ois.readInt();
				for (int i = 0; i < count; ++i) {
					Command c = (Command) ois.readObject();
					c.context(context);
					commands.add(c);
				}

				// read eof
				if (ois.readByte() != eof)
					throw new FileCorruptedException(filename);

			} catch (ClassNotFoundException e) {
				throw new FileCorruptedException(filename);
			} catch (InvalidClassException e) {
				throw new IncompatibleFileException(filename, e);
			}
		}
	}
}
