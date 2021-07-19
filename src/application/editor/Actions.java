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
import requirement.Requirement;
import requirement.Requirements;
import requirement.StringType;

/**
 * An enum-strategy for the different Actions the {@link Editor} may take
 *
 * @author alexm
 */
public enum Actions {

	/** Action for creating a {@code Component} */
	CREATE("command") {
		@Override
		public void execute() {

			if (!reqs.fulfilled())
				throw new RuntimeException("Execute CREATE without requirements");

			final Command cte = (Command) reqs.getV("command");

			try {
				if (!cte.canExecute()) {
					context.status("%s cancelled", cte);
					return;
				}

				context.execute(cte);
				context.status("%s successful", cte);
				context.setDirty(true);
			} catch (MissingComponentException | MalformedBranchException | CycleException e) {
				context.error(e);
			} catch (final Exception e) {
				// Undoable.execute() declares 'throw Exception'
				throw new RuntimeException(e);
			} finally {
				reqs.clear();
			}
		}
	},

	/** Action for deleting a {@code Component} */
	DELETE("command") {
		@Override
		public void execute() {

			if (!reqs.fulfilled())
				throw new RuntimeException("Execute DELETE without requirements");

			final Command cte = (Command) reqs.getV("command");

			try {
				if (!cte.canExecute()) {
					context.status("Delete cancelled");
					return;
				}

				context.execute(cte);
				context.status("Component deleted");
				context.setDirty(true);
			} catch (final MissingComponentException e) {
				context.error(e);
			} catch (final Exception e) {
				// Undoable.execute() declares 'throw Exception'
				throw new RuntimeException(e);
			} finally {
				reqs.clear();
			}
		}
	},

	/** Action for saving the components of the {@code Editor} to a File */
	SAVE("filename", StringType.FILENAME) {
		@Override
		public void execute() {

			final String fname = (String) reqs.getV("filename");

			try {
				if (!reqs.fulfilled()) {
					context.status("File save cancelled");
					return;
				}

				Actions.writeToFile(fname, context.getComponents_(), context.getPastCommands());

				context.status("File %s saved successfully", fname);
				context.setFile(fname);
				context.setDirty(false);

			} catch (final IOException e) {
				context.error(
						"Error while writing to file %s. Inform the developer about 'Action-SAVE-IO'",
						fname);
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

			final String fname = (String) reqs.getV("filename");
			final String ftype = (String) reqs.getV("filetype");

			final List<Component> components = new ArrayList<>();
			final List<Command> commands = new ArrayList<>();

			try {
				if (!reqs.fulfilled()) {
					context.status("File %s cancelled",
							ftype.equals(Actions._circuit) ? "open" : "import");
					return;
				}

				Actions.readFromFile(fname, components, commands);

				if (ftype.equals(Actions._circuit)) {

					context.clear();

					Utility.foreach(components, component -> {
						ComponentFactory.restoreSerialisedComponent(component);
						context.addComponent(component);
					});
					Utility.foreach(commands, command -> {
						// System.out.println(command.getClass());
						command.context(context);
						context.addToHistory(command);
					});

					// System.out.println(context.getPastCommands());

					context.setFile(fname);
					context.status("File %s opened successfully", fname);
					context.setDirty(false);

				} else if (ftype.equals(Actions._component)) {

					final Command cgc = Command.create(commands, (String) reqs.getV("gatename"));
					context.context().addCreateCommand(cgc);
					context.status("File %s imported successfully", fname);

				} else {
					throw new RuntimeException(
							"Inform the developer about 'Action-OPEN-Invalid-Filetype'");
				}
			} catch (Actions.IncompatibleFileException | Actions.FileCorruptedException e) {
				context.error(e);
			} catch (final FileNotFoundException e) {
				context.error("File %s doesn't exist", fname);
			} catch (final IOException e) {
				context.error(
						"Error while reading from file %s. Inform the developer about 'Action-Open-IO'",
						fname);
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
			final String[] messages = {
					"Not-so-good help ahead, brace yourselves",
					"Create new Editor / Close current Editor",
					"Open a file in current Editor",
					"Save the current file to disk / Save with a different name",
					"Clear the Editor",
					"Import a file as a custom Component",
					"Undo/Redo last/previous action",
					"Turn on/off InputPin / Focus Component",
					"Creates a Component (by type and parameters)",
					"Deletes a Component (by identifier)",
					"Edit settings",
					"Display these messages",
					"Drag to move, click to turn on/off",
					"Move with arrows (fast with shift), turn on/off with space",
			};

			final String[] titles = {
					"Disclaimer",
					"File: New / Close",
					"File: Open",
					"File: Save / Save as",
					"File: Clear",
					"File: Import",
					"File: Undo / Redo",
					"Edit: Turn on/off / Focus",
					"Create",
					"Delete",
					"Preferences",
					"Help",
					"Mouse Actions",
					"Keyboard Actions",
			};

			// yes=0 no=1 cancel=2 x=-1 (+1)
			final int[] res = { 0, 0, 0, 0 };

			for (int i = 0; i < messages.length; i++)
				++res[1 + msg(messages[i], titles[i])];

			final int y = res[1], n = res[2], c = res[3], x = res[0];
			final int res1 = msg(String.format("You clicked: %d yes, %d no, %d cancel, %d 'x'", y, n, c, x), "Fun Fact");

			if (res1 == -1)
				msg(String.format("(actually %d 'x')", x + 1), "Bonus Fun Fact");
			else if (res1 == 0)
				msg(String.format("(actually %d yes)", y + 1), "Bonus Fun Fact");
			else if (res1 == 1)
				msg(String.format("(actually %d no)", n + 1), "Bonus Fun Fact");
			else if (res1 == 2)
				msg(String.format("(actually %d cancel)", c + 1), "Bonus Fun Fact");
		}
		private int msg(String msg, String title) {
			return JOptionPane.showConfirmDialog(null, msg, title, JOptionPane.YES_NO_CANCEL_OPTION);
		}
	};

	// bytes at the start and end of file
	private static final Integer start, eof;
	// the strings should match Requirement.Type
	private static final String _component, _circuit;

	static {
		_component = "component";
		_circuit = "circuit";

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

		for (int i = 0; i < reqKeys.length; i++)
			reqs.add(reqKeys[i], types[i]);
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
	 * Specifies an Object to finalise a specific Requirement.
	 *
	 * @param req the Requirement
	 * @param c   the Command
	 *
	 * @return this (used for chaining)
	 *
	 * @see Requirement#finalise
	 */
	public final Actions specify(String req, Object c) {
		reqs.finalise(req, c);
		return this;
	}

	/** Thrown when a file is corrupted and can't be read */
	protected final static class FileCorruptedException extends Exception {

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
					filename, idInFile > idInClass ? "a later" : "a previous");
		}
	}

	/**
	 * Writes the contents of Lists of Components and Commands to a file.
	 *
	 * @param filename   the filename
	 * @param commands   the list of commands to write
	 * @throws IOException when an IOExcetpion occurs
	 */
	protected static void writeToFile(String filename, List<Component> components, List<Undoable> commands)
			throws IOException {

		final String outputFile = String.format("%s\\%s", StringConstants.USER_DATA, filename);

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
	 * @param commands   the list that will be filled with Commands
	 * @throws IOException               when an IOException occurred
	 * @throws FileNotFoundException     when the file couldn't be found
	 * @throws FileCorruptedException    when the contents of the file are corrupted
	 * @throws IncompatibleFileException when the file data corresponds to a
	 *                                   previous version of the program
	 */
	protected static void readFromFile(String filename, List<Component> components, List<Command> commands)
			throws FileNotFoundException, IOException, Actions.FileCorruptedException,
			Actions.IncompatibleFileException {

		final String inputFile = String.format("%s\\%s", StringConstants.USER_DATA, filename);

		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(inputFile))) {

			// read start
			if (ois.readByte() != Actions.start)
				throw new FileCorruptedException(filename);

			// read commands
			int count = ois.readInt();
			for (int i = 0; i < count; i++)
				components.add((Component) ois.readObject());

			count = ois.readInt();
			for (int i = 0; i < count; ++i)
				commands.add((Command) ois.readObject());

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
