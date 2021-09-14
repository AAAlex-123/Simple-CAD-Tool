package application.editor;

import java.awt.Frame;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.JOptionPane;

import application.StringConstants;
import command.Command;
import component.components.Component;
import component.components.ComponentFactory;
import component.exceptions.MalformedBranchException;
import localisation.EditorStrings;
import localisation.Languages;
import myUtil.Utility;
import requirement.requirements.AbstractRequirement;
import requirement.requirements.HasRequirements;
import requirement.requirements.ListRequirement;
import requirement.requirements.Requirements;
import requirement.requirements.StringType;

/**
 * An enum-strategy for the different Actions the Editor may take. Actions have
 * {@code context} that must be set using the {@link #context(Editor)} method
 * before the Action can be executed.
 * <p>
 * Actions operate on an {@code Editor} and can execute {@code Commands} on it
 * to manipulate {@code Components}.
 *
 * @author Alex Mandelias
 *
 * @see Editor
 * @see Command
 * @see Component
 */
public enum Actions implements HasRequirements {

	/** Action for creating a {@code Component} */
	CREATE {
		@Override
		public void execute() {

			if (!reqs.fulfilled())
				throw new RuntimeException("Execute CREATE without requirements"); //$NON-NLS-1$

			final Command commandToExecute = reqs.getValue(EditorStrings.COMMAND, Command.class);

			try {
				if (!commandToExecute.canExecute()) {
					context.status(Languages.getString("Actions.1"), commandToExecute); //$NON-NLS-1$
					return;
				}

				context.execute(commandToExecute);
				context.status(Languages.getString("Actions.2"), commandToExecute); //$NON-NLS-1$
				context.getFileInfo().markUnsaved();
			} catch (MissingComponentException | MalformedBranchException e) {
				context.error(e);
			} catch (final Exception e) {
				// exists because Undoable.execute() declares 'throw Exception'
				throw new RuntimeException(e);
			} finally {
				reqs.clear();
				context = null;
			}
		}

		@Override
		public void constructRequirements() {
			reqs.add(EditorStrings.COMMAND);
		}
	},

	/** Action for deleting a {@code Component} */
	DELETE {
		@Override
		public void execute() {

			if (!reqs.fulfilled())
				throw new RuntimeException("Execute DELETE without requirements"); //$NON-NLS-1$

			final Command commandToExecute = reqs.getValue(EditorStrings.COMMAND, Command.class);

			try {
				if (!commandToExecute.canExecute()) {
					context.status(Languages.getString("Actions.4")); //$NON-NLS-1$
					return;
				}

				context.execute(commandToExecute);
				context.status(Languages.getString("Actions.5")); //$NON-NLS-1$
				context.getFileInfo().markUnsaved();
			} catch (final MissingComponentException e) {
				context.error(e);
			} catch (final Exception e) {
				// exists because Undoable.execute() declares 'throw Exception'
				throw new RuntimeException(e);
			} finally {
				reqs.clear();
				context = null;
			}
		}

		@Override
		public void constructRequirements() {
			reqs.add(EditorStrings.COMMAND);
		}
	},

	/** Action for saving the state of an {@code Editor} to a File */
	SAVE {
		@Override
		public void execute() {

			final String fileToSave = reqs.getValue(EditorStrings.FILENAME, String.class);

			try {
				if (!reqs.fulfilled()) {
					context.status(Languages.getString("Actions.6")); //$NON-NLS-1$
					return;
				}

				Actions.writeToFile(fileToSave, context.getComponents_(),
				        context.getPastCommands());

				context.status(Languages.getString("Actions.7"), fileToSave); //$NON-NLS-1$
				context.getFileInfo().markSaved();
				context.getFileInfo().setFile(fileToSave);

			} catch (final IOException e) {
				context.error(
				        Languages.getString("Actions.8"), //$NON-NLS-1$
				        fileToSave);
				throw new RuntimeException(e);
			} finally {
				reqs.clear();
				context = null;
			}
		}

		@Override
		public void constructRequirements() {
			reqs.add(EditorStrings.FILENAME, StringType.FILENAME);
		}
	},

	/** An Action that reads the contents of a File to an {@code Editor} */
	OPEN {
		@Override
		public void execute() {

			final String fileToRead       = reqs.getValue(EditorStrings.FILENAME, String.class);
			final String typeOfFileToRead = reqs.getValue(EditorStrings.FILETYPE, String.class);

			final List<Component> components = new ArrayList<>();
			final List<Command>   commands   = new ArrayList<>();

			try {
				if (!reqs.fulfilled()) {
					context.status(Languages.getString("Actions.9"), //$NON-NLS-1$
					        typeOfFileToRead.equals(EditorStrings.CIRCUIT)
					                ? Languages.getString("Actions.10") //$NON-NLS-1$
					                : Languages.getString("Actions.11")); //$NON-NLS-1$
					return;
				}

				Actions.readFromFile(fileToRead, components, commands);

				if (typeOfFileToRead.equals(EditorStrings.CIRCUIT)) {

					context.clear();

					Utility.foreach(components, component -> {
						ComponentFactory.restoreSerialisedComponent(component);
						context.addComponent(component);
					});

					Utility.foreach(commands, command -> {
						command.context(context);
						context.addToHistory(command);
					});

					context.getFileInfo().markSaved();
					context.getFileInfo().setFile(fileToRead);
					context.status(Languages.getString("Actions.12"), fileToRead); //$NON-NLS-1$

				} else if (typeOfFileToRead.equals(EditorStrings.COMPONENT)) {

					final Command createCompositeGateCommand = Command.create(commands,
					        reqs.getValue(EditorStrings.GATENAME, String.class));
					context.context().addCreateCommand(createCompositeGateCommand);
					context.status(Languages.getString("Actions.13"), fileToRead); //$NON-NLS-1$

				} else
					throw new RuntimeException(
					        Languages.getString("Actions.14")); //$NON-NLS-1$
			} catch (Actions.IncompatibleFileException | Actions.FileCorruptedException e) {
				context.error(e);
			} catch (final FileNotFoundException e) {
				context.error(Languages.getString("Actions.15"), fileToRead); //$NON-NLS-1$
			} catch (final IOException e) {
				context.error(
				        Languages.getString("Actions.16"), //$NON-NLS-1$
				        fileToRead);
				throw new RuntimeException(e);
			} catch (final Exception e) {
				throw new RuntimeException(e);
			} finally {
				reqs.clear();
				context = null;
			}
		}

		@Override
		public void constructRequirements() {
			reqs.add(EditorStrings.FILENAME, new ArrayList<String>());
			reqs.add(EditorStrings.FILETYPE,
			        Arrays.asList(EditorStrings.CIRCUIT, EditorStrings.COMPONENT));
			reqs.add(EditorStrings.GATENAME, StringType.NON_EMPTY);
		}

		@SuppressWarnings("unchecked") //yes this is safe
		@Override
		public void adjustRequirements() {
			final Path dir = Paths.get(
			        System.getProperty("user.dir") + File.separator + StringConstants.USER_DATA); //$NON-NLS-1$

			//create directory if it doesn't exist
			if (!Files.exists(dir))
				try {
					Files.createDirectory(dir);
				} catch (final IOException e1) {
					e1.printStackTrace();
				}

			List<String> files;
			try (Stream<Path> paths = Files.walk(dir)) {
				files = paths
				        // TODO: regret later not externalising and not syncing it to the file name generator
				        .filter(file -> file.toString().contains(".scad")) //$NON-NLS-1$
				        .map(file -> file.toString()
				                .substring(file.toString().lastIndexOf(File.separator) + 1)) //get file name
				        .collect(Collectors.toList());
			} catch (final IOException e) {
				throw new UncheckedIOException(e);
			}

			ListRequirement<String> filenameReq = (ListRequirement<String>) reqs
			        .get(EditorStrings.FILENAME);
			filenameReq.setOptions(files);
			filenameReq.setCaseOfNullGraphic(false, "No files found in directory %s", dir);
		}
	},

	/** An action for resetting an {@code Editor} */
	CLEAR {
		@Override
		public void execute() {
			context.clear();
			context.status(Languages.getString("Actions.17")); //$NON-NLS-1$
			context.getFileInfo().markUnsaved();
			context = null;
		}
	},

	/** An Action for undoing a {@code Command} */
	UNDO {
		@Override
		public void execute() {
			context.undo();
			context.status(Languages.getString("Actions.18")); //$NON-NLS-1$
			context.getFileInfo().markUnsaved();
			context = null;
		}
	},

	/** An Action for redoing a {@code Command} */
	REDO {
		@Override
		public void execute() {
			context.redo();
			context.status(Languages.getString("Actions.19")); //$NON-NLS-1$
			context.getFileInfo().markUnsaved();
			context = null;
		}
	},

	/** An Action for showing Help */
	HELP {
		@Override
		public void execute() {
			final String[] titles = {
			        Languages.getString("Actions.20"), //$NON-NLS-1$
			        Languages.getString("Actions.21"), //$NON-NLS-1$
			        Languages.getString("Actions.22"), //$NON-NLS-1$
			        Languages.getString("Actions.23"), //$NON-NLS-1$
			        Languages.getString("Actions.24"), //$NON-NLS-1$
			        Languages.getString("Actions.25"), //$NON-NLS-1$
			        Languages.getString("Actions.26"), //$NON-NLS-1$
			        Languages.getString("Actions.27"), //$NON-NLS-1$
			        Languages.getString("Actions.28"), //$NON-NLS-1$
			        Languages.getString("Actions.29"), //$NON-NLS-1$
			        Languages.getString("Actions.30"), //$NON-NLS-1$
			        Languages.getString("Actions.31"), //$NON-NLS-1$
			        Languages.getString("Actions.32"), //$NON-NLS-1$
			};

			final String[] messages = {
			        Languages.getString("Actions.33"), //$NON-NLS-1$
			        Languages.getString("Actions.34"), //$NON-NLS-1$
			        Languages.getString("Actions.35"), //$NON-NLS-1$
			        Languages.getString("Actions.36"), //$NON-NLS-1$
			        Languages.getString("Actions.37"), //$NON-NLS-1$
			        Languages.getString("Actions.38"), //$NON-NLS-1$
			        Languages.getString("Actions.39"), //$NON-NLS-1$
			        Languages.getString("Actions.40"), //$NON-NLS-1$
			        Languages.getString("Actions.41"), //$NON-NLS-1$
			        Languages.getString("Actions.42"), //$NON-NLS-1$
			        Languages.getString("Actions.43"), //$NON-NLS-1$
			        Languages.getString("Actions.44"), //$NON-NLS-1$
			        Languages.getString("Actions.45"), //$NON-NLS-1$
			};

			if (titles.length != messages.length)
				throw new RuntimeException(
				        "Number of help titles doesn't match number of messages"); //$NON-NLS-1$

			// yes=0, no=1, cancel=2, x=-1 (+1 to adjust for array index)
			final int[] res = { 0, 0, 0, 0 };

			final Frame frame = context.context().getFrame();

			for (int i = 0, count = messages.length; i < count; i++)
				++res[1 + msg(frame, messages[i], titles[i])];

			context = null;
		}

		private int msg(Frame frame, String message, String title) {
			return JOptionPane.showConfirmDialog(frame, message, title,
			        JOptionPane.YES_NO_CANCEL_OPTION);
		}
	};

	// bytes at the start and end of file
	private static final Integer start = 10, eof = 42;

	/** The Requirements of the Action */
	protected final Requirements reqs;

	/** The context of the Action, the Editor whose state it changes */
	protected Editor context;

	Actions() {
		reqs = new Requirements();
		constructRequirements();
	}

	/**
	 * Executes the Action and clears its context.
	 *
	 * @throws NullPointerException if its {@code context} has not been set prior to
	 *                              execution
	 *
	 * @see #context
	 */
	public abstract void execute();

	@Override
	public void constructRequirements() {}

	@Override
	public void adjustRequirements() {}

	/**
	 * Specifies the Action's context. This method must be called before every
	 * Action execution.
	 *
	 * @param editor the context
	 *
	 * @return this (used for chaining)
	 *
	 * @see #context
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
		adjustRequirements();
		reqs.fulfillWithDialog(editor.context().getFrame(), toString());
		return this;
	}

	/**
	 * Specifies an Object to finalise a specific Requirement.
	 *
	 * @param requirement the Requirement
	 * @param command     the Command
	 *
	 * @return this (used for chaining)
	 *
	 * @see AbstractRequirement#finalise
	 */
	public final Actions specify(String requirement, Object command) {
		reqs.finalise(requirement, command);
		return this;
	}

	/** Thrown when a File is corrupted and can't be read */
	protected final static class FileCorruptedException extends Exception {

		/**
		 * Constructs the Exception with a {@code filename}.
		 *
		 * @param filename the name of the corrupted File
		 */
		public FileCorruptedException(String filename) {
			super(String.format(Languages.getString("Actions.48"), //$NON-NLS-1$
			        filename));
		}
	}

	/** Thrown when a File's data are incompatible with current program version */
	protected final static class IncompatibleFileException extends Exception {

		/**
		 * Constructs the Exception with a {@code filename}.
		 *
		 * @param filename the name of the file with incompatible data
		 * @param e        the InvalidClassException that triggered this Exception
		 */
		public IncompatibleFileException(String filename, InvalidClassException e) {
			super(IncompatibleFileException.formatMessage(filename, e));
		}

		private static String formatMessage(String filename, InvalidClassException e) {
			// extract version information from exception message
			final Pattern p = Pattern
			        .compile(".*? serialVersionUID = (\\d+), .*? serialVersionUID = (\\d+)"); //$NON-NLS-1$
			final Matcher m = p.matcher(e.getMessage());

			if (!m.matches())
				throw new RuntimeException("Invalid regex in IncompatibleFileException"); //$NON-NLS-1$

			final int idInFile  = Integer.parseInt(m.group(1));
			final int idInClass = Integer.parseInt(m.group(2));

			return String.format(Languages.getString("Actions.51"), //$NON-NLS-1$
			        filename, idInFile > idInClass ? Languages.getString("Actions.52") //$NON-NLS-1$
			                : Languages.getString("Actions.53")); //$NON-NLS-1$
		}
	}

	/**
	 * Writes the contents of Lists of Components and Commands to a file.
	 *
	 * @param filename   the filename
	 * @param components the list of Components to write to the file
	 * @param commands   the list of Commands to write to the file
	 *
	 * @throws IOException if an IOExcetpion occurred
	 *
	 * @see #readFromFile(String, List, List)
	 */
	protected static void writeToFile(String filename, List<Component> components,
	        List<Command> commands)
	        throws IOException {

		final File dir = Paths.get(StringConstants.USER_DATA).toFile();

		if (!dir.isDirectory()) {
			final boolean createDirectorySuccess = dir.mkdir();
			if (!createDirectorySuccess)
				throw new IOException(
				        String.format("Didn't find directory %s and could not create it", //$NON-NLS-1$
				                StringConstants.USER_DATA));
		}

		final String outputFile = String.format("%s%s%s", StringConstants.USER_DATA, //$NON-NLS-1$
		        System.getProperty("file.separator"), filename); //$NON-NLS-1$

		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(outputFile))) {
			oos.writeByte(Actions.start);

			oos.writeInt(components.size());
			for (final Component component : components)
				oos.writeObject(component);

			oos.writeInt(commands.size());
			for (final Command command : commands)
				oos.writeObject(command);

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
	 * @throws IOException               if an IOException occurred
	 * @throws FileNotFoundException     if the file couldn't be found
	 * @throws FileCorruptedException    if the contents of the file are corrupted
	 * @throws IncompatibleFileException if the file data corresponds to a previous
	 *                                   version of the program
	 *
	 * @see #writeToFile(String, List, List)
	 */
	protected static void readFromFile(String filename, List<Component> components,
	        List<Command> commands)
	        throws FileNotFoundException, IOException, Actions.FileCorruptedException,
	        Actions.IncompatibleFileException {

		final String inputFile = String.format("%s%s%s", StringConstants.USER_DATA, //$NON-NLS-1$
		        System.getProperty("file.separator"), filename); //$NON-NLS-1$

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
