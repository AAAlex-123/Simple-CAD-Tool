package application.editor;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;

import application.StringConstants;
import command.Command;
import components.Component;
import components.ComponentFactory;
import exceptions.MalformedBranchException;
import myUtil.Utility;
import requirement.Requirements;
import requirement.StringType;

/** An enum-strategy for the different Actions the Editor may take */
public enum Actions {

	/** Action for creating a Component */
	CREATE("command") {
		@Override
		public void execute() {

			if (!reqs.fulfilled())
				throw new RuntimeException("Execute CREATE without reqs");

			final Command cte = (Command) reqs.getV("command");

			if (!cte.canExecute()) {
				context.status("%s cancelled", cte);
				return;
			}

			try {
				context.do_(cte);
				context.status("%s successful", cte);
				context.setDirty(true);
			} catch (Editor.MissingComponentException | MalformedBranchException e) {
				context.error(e);
			} catch (final Exception e) {
				// semi-spaghetti throws declaration on Undoable.execute();
				throw new RuntimeException(e);
			} finally {
				reqs.clear();
			}
		}
	},

	/** Action for deleting a Component */
	DELETE("command") {
		@Override
		public void execute() {

			if (!reqs.fulfilled())
				throw new RuntimeException("Execute DELETE without reqs");

			final Command cte = (Command) reqs.getV("command");

			if (!cte.canExecute()) {
				context.status("Delete cancelled");
				return;
			}

			try {
				context.do_(cte);
				context.status("Component deleted");
				context.setDirty(true);
			} catch (final Editor.MissingComponentException e) {
				context.error(e);
			} catch (final Exception e) {
				// semi-spaghetti throws declaration on Undoable.execute();
				throw new RuntimeException(e);
			} finally {
				reqs.clear();
			}
		}
	},

	/** Action for saving the components of the Editor to a File */
	SAVE("filename", StringType.FILENAME) {
		@Override
		public void execute() {

			if (!reqs.fulfilled()) {
				context.status("File save cancelled");
				return;
			}

			final String fname = (String) reqs.getV("filename");
			context.setFile(fname);

			try {
				Actions.writeToFile(fname, context.getComponents_(),
				        context.getPastCommands());
				context.status("File %s saved successfully", fname);
				context.setFile(fname);
				context.setDirty(false);
			} catch (final IOException e) {
				context.error(
				        "Error while writing to file. Try again or inform the developer about 'Action-Save-IO'");
				throw new RuntimeException(e);
			} finally {
				reqs.clear();
			}
		}
	},

	/** An Action that reads the contents of a File to the Editor */
	OPEN(new String[] { "filename", "gatename", "filetype" },
	        new StringType[] { StringType.FILENAME, StringType.ANY, StringType.FILETYPE }) {
		@Override
		public void execute() {

			if (!reqs.fulfilled()) {
				context.status("File %s cancelled",
				        reqs.getV("filetype").equals(Actions.circuit) ? "open" : "import");
				return;
			}

			final String fname = (String) reqs.getV("filename");
			final String ftype = (String) reqs.getV("filetype");

			final List<Component> components = new ArrayList<>();
			final List<Command> commands = new ArrayList<>();

			try {
				Actions.readFromFile(fname, components, commands);

				if (ftype.equals(Actions.circuit)) {

					context.clear();

					// foreach(components, c -> context.addComponent(c));
					// foreach(commands, c -> context.undoableHistory.add(c));
					for (final Command c : commands) {
						c.context(context);
						context.do_(c);
					}

					Component.setGlobalID(
					        Utility.max(context.getComponents_(), Component::UID).UID() + 1);

					context.setFile(fname);

				} else if (ftype.equals(Actions.component)) {

					final Command cgc = Command.create(commands, (String) reqs.getV("gatename"));
					context.context().addCreateCommand(cgc);

				} else
					throw new RuntimeException("Invalid filetype specified");

				context.status("File %s %s successfully", fname,
				        ftype.equals(Actions.circuit) ? "opened" : "imported");

			} catch (Actions.IncompatibleFileException | Actions.FileCorruptedException e) {
				context.error(e);
			} catch (final FileNotFoundException e) {
				context.error("File %s doesn't exist", fname);
			} catch (final IOException e) {
				context.error(
				        "Error while reading from file. Try again or inform the developer about 'Action-Open-IO'");
				throw new RuntimeException(e);
			} catch (final Exception e) {
				throw new RuntimeException(e);
			} finally {
				reqs.clear();
			}
		}
	},

	/** An action for resetting the Editor */
	CLEAR {
		@Override
		public void execute() {
			context.clear();
			context.status("Editor cleared");
			context.setDirty(true);
		}
	},

	/** An Action for undoing a Command */
	UNDO {
		@Override
		public void execute() {
			context.undo();
			context.status("Undo");
			context.setDirty(true);
		}
	},

	/** An Action for redoing a Command */
	REDO {
		@Override
		public void execute() {
			context.redo();
			context.status("Redo");
			context.setDirty(true);
		}
	},

	/** An Action for showing Help */
	HELP {
		@Override
		public void execute() {
			context.status("Someone doesn't know how to use a UI...");
			final int[] res = new int[16];
			int i = -1;
			res[++i] = JOptionPane.showConfirmDialog(null,
			        "PLEASE NOTE THAT THIS HELP IS KINDA CRAPPY");
			res[++i] = JOptionPane.showConfirmDialog(null,
			        "but i wont fix it rn, it will probably be replaced with something fancier in the future");
			res[++i] = JOptionPane.showConfirmDialog(null, "use the menus");
			res[++i] = JOptionPane.showConfirmDialog(null,
			        "file: read buttons click buttons");
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
			res[++i] = JOptionPane.showConfirmDialog(null,
			        "delete: click 'component'. Then specify the ID.");
			res[++i] = JOptionPane.showConfirmDialog(null,
			        "'help' displays this helpful help message");
			res[++i] = JOptionPane.showConfirmDialog(null, "everything has keybinds");
			res[++i] = JOptionPane.showConfirmDialog(null,
			        "When filling in parameters, (shift + )tab to move (backward)forward, enter for ok, escape to cancel");
			res[++i] = JOptionPane.showConfirmDialog(null,
			        "click on input pins to turn them on and off and drag components (not branches) to move them around");
			res[++i] = JOptionPane.showConfirmDialog(null,
			        "example branch: from 1st pin of component with id=1 to 3rd pin of component with id=2:'in id'=1 'in index'=0 'out id'=2 'out index'=2");
			res[++i] = JOptionPane.showConfirmDialog(null,
			        "to start, open file `nand` and play around with it (undo is saved when saving files). WITHOUT SAVING, import file `nand` with custom component name, place it, and see your nand come to life");

			// yes=0 no=1 cancel=2 x=-1

			int count;
			count = 0;
			for (i = 0; i < res.length; ++i)
				if (res[i] == -1) { count++; }
			final int x = count;
			count = 0;
			for (i = 0; i < res.length; ++i)
				if (res[i] == 0) { count++; }
			final int y = count;
			count = 0;
			for (i = 0; i < res.length; ++i)
				if (res[i] == 1) { count++; }
			final int n = count;
			count = 0;
			for (i = 0; i < res.length; ++i)
				if (res[i] == 2) { count++; }
			final int c = count;
			final int res1 = JOptionPane.showConfirmDialog(null,
			        String.format("fun fact: you clicked: %d yes, %d no, %d cancel, %d 'x'",
			                y, n, c, x));

			if (res1 == -1) {
				JOptionPane.showConfirmDialog(null, String.format("(%d 'x')", x + 1));
			} else if (res1 == 0) {
				JOptionPane.showConfirmDialog(null, String.format("(%d yes)", y + 1));
			} else if (res1 == 1) {
				JOptionPane.showConfirmDialog(null, String.format("(%d no)", n + 1));
			} else if (res1 == 2) {
				JOptionPane.showConfirmDialog(null, String.format("(%d cancel)", c + 1));
			}
		}
	};

	// bytes at the start and end of file
	private static final Integer start, eof;
	// the strings should match Requirement.Type
	private static final String component, circuit;

	static {
		component = "component";
		circuit = "circuit";

		start = 10;
		eof = 42;
	}

	/** The Requirements of the Action */
	protected final Requirements<Object> reqs;

	/** The context of the Action */
	Editor context;

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
		Utility.foreach(reqKeys, reqs::add);
	}

	Actions(String[] reqKeys, StringType[] types) {
		reqs = new Requirements<>();

		if (reqKeys.length != types.length)
			throw new RuntimeException("Invalid arguments in enum constructor");

		for (int i = 0; i < reqKeys.length; i++) {
			reqs.add(reqKeys[i], types[i]);
		}
	}

	/** Executes the Action */
	public abstract void execute();

	/**
	 * Specifies the Action's context.
	 *
	 * @param editor the context
	 *
	 * @return this (used for chaining)
	 */
	public final Actions context(Editor editor) {
		context = editor;
		return this;
	}

	/**
	 * Creates a pop-up dialog to let the user fulfil the Requirements.
	 *
	 * @param editor the frame for the dialog
	 *
	 * @return this (used for chaining)
	 */
	public final Actions specifyWithDialog(Editor editor) {
		reqs.fulfillWithDialog(editor.context().getFrame(), toString());
		return this;
	}

	/**
	 * Specifies an Object to fulfil a specific Requirement.
	 *
	 * @param req the Requirement
	 * @param c   the Command
	 *
	 * @return this (used for chaining)
	 */
	public final Actions specify(String req, Object c) {
		reqs.fulfil(req, c);
		return this;
	}

	/** Thrown when a file is corrupted and can't be read */
	protected final static class FileCorruptedException extends Exception {

		private static final long serialVersionUID = 1L;

		/**
		 * Constructs the Exception with information about the {@code filename}.
		 *
		 * @param filename the name of the corrupted file
		 */
		public FileCorruptedException(String filename) {
			super(String.format("Can't read file %s because its contents are corrupted",
			        filename));
		}
	}

	/** Thrown when a file's data are incompatible with current program version */
	protected final static class IncompatibleFileException extends Exception {

		private static final long serialVersionUID = 1L;

		/**
		 * Constructs the Exception with information about the {@code filename}.
		 *
		 * @param filename the name of the file with incompatible data
		 * @param e        the Exception that triggered this Exception
		 */
		public IncompatibleFileException(String filename, InvalidClassException e) {
			super(IncompatibleFileException.formatMessage(filename, e));
		}

		private static String formatMessage(String filename, InvalidClassException e) {
			// extract version information from exception message
			final Pattern p = Pattern
			        .compile(".*? serialVersionUID = (\\d+), .*? serialVersionUID = (\\d+)");
			final Matcher m = p.matcher(e.getMessage());

			if (!m.matches())
				throw new RuntimeException("Invalid regex in IncompatibleFileException");

			final int idInFile = Integer.parseInt(m.group(1));
			final int idInClass = Integer.parseInt(m.group(2));

			return String.format("Data in file %s corresponds to %s version of the program",
			        filename,
			        idInFile > idInClass ? "a later" : "a previous");
		}
	}

	/**
	 * Writes the contents of Lists of Components and Commands to a file.
	 *
	 * @param filename   the filename
	 * @param components the list of components to write
	 * @param commands   the list of commands to write
	 *
	 * @throws IOException when an IOExcetpion occurs
	 */
	protected static void writeToFile(
	        String filename, List<Component> components, List<Undoable> commands)
	        throws IOException {

		final String outputFile = String.format("%s\\%s", StringConstants.user_data, filename);

		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(outputFile))) {
			oos.writeByte(Actions.start);

			oos.writeInt(components.size());
			for (final Component c : components)
				oos.writeObject(c);

			oos.writeInt(commands.size());
			for (final Undoable u : commands)
				oos.writeObject(u);

			oos.writeByte(Actions.eof);
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
	protected static void readFromFile(
	        String filename, List<Component> components, List<Command> commands)
	        throws FileNotFoundException, IOException, Actions.FileCorruptedException,
	        Actions.IncompatibleFileException {

		final String inputFile = String.format("%s\\%s", StringConstants.user_data, filename);

		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(inputFile))) {

			// read start
			if (ois.readByte() != Actions.start)
				throw new FileCorruptedException(filename);

			// read components
			int count = ois.readInt();
			for (int i = 0; i < count; ++i) {
				final Component c = (Component) ois.readObject();
				ComponentFactory.restoreSerialisedComponent(c);
				components.add(c);
			}

			// read commands
			count = ois.readInt();
			for (int i = 0; i < count; ++i) {
				final Command c = (Command) ois.readObject();
				commands.add(c);
			}

			// read eof
			if (ois.readByte() != Actions.eof)
				throw new FileCorruptedException(filename);

		} catch (final ClassNotFoundException e) {
			throw new FileCorruptedException(filename);
		} catch (final InvalidClassException e) {
			throw new IncompatibleFileException(filename, e);
		}
	}
}
