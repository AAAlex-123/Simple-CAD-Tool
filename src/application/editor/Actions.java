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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.JOptionPane;

import application.StringConstants;
import command.Command;
import component.components.Component;
import component.exceptions.MalformedBranchException;
import localisation.EditorStrings;
import localisation.Languages;
import myUtil.ErrorDumpDialog;
import myUtil.StringGenerator;
import requirement.requirements.AbstractRequirement;
import requirement.requirements.ListRequirement;
import requirement.requirements.StringType;
import requirement.util.HasRequirements;
import requirement.util.Requirements;

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
		public void executeAction() throws Exception {

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
				context.fileInfo.markUnsaved();
			} catch (MissingComponentException | MalformedBranchException e) {
				context.error(e);
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
		public void executeAction() throws Exception {

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
				context.fileInfo.markUnsaved();
			} catch (final MissingComponentException e) {
				context.error(e);
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
		public void executeAction() throws Exception {

			final String fileToSave = reqs.getValue(EditorStrings.FILENAME, String.class);

			try {
				if (!reqs.fulfilled()) {
					context.status(Languages.getString("Actions.6")); //$NON-NLS-1$
					return;
				}

				Actions.writeToFile(fileToSave, context.getPastCommands(), context.componentManager);

				context.status(Languages.getString("Actions.7"), fileToSave); //$NON-NLS-1$
				context.fileInfo.markSaved();
				context.fileInfo.setFile(fileToSave);

			} catch (final IOException e) {
				context.error(Languages.getString("Actions.8"), fileToSave); //$NON-NLS-1$
				throw e;
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
		public void executeAction() throws Exception {

			final String fileToRead       = reqs.getValue(EditorStrings.FILENAME, String.class);
			final String typeOfFileToRead = reqs.getValue(EditorStrings.FILETYPE, String.class);

			final List<Command> commands = new ArrayList<>();

			try {
				if (!reqs.fulfilled()) {
					context.status(Languages.getString("Actions.9"), //$NON-NLS-1$
					        typeOfFileToRead.equals(EditorStrings.CIRCUIT)
					                ? Languages.getString("Actions.10") //$NON-NLS-1$
					                : Languages.getString("Actions.11")); //$NON-NLS-1$
					return;
				}

				Actions.readFromFile(fileToRead, commands, context.componentManager);

				if (typeOfFileToRead.equals(EditorStrings.CIRCUIT)) {

					context.clear();

					for (final Command command : commands) {
						command.context(context);
						context.execute(command);
					}

					context.fileInfo.markSaved();
					context.fileInfo.setFile(fileToRead);
					context.status(Languages.getString("Actions.12"), fileToRead); //$NON-NLS-1$

				} else if (typeOfFileToRead.equals(EditorStrings.COMPONENT)) {

					final Command createCompositeGateCommand = Command.create(commands,
					        reqs.getValue(EditorStrings.GATENAME, String.class));
					context.app.addCreateCommand(createCompositeGateCommand);
					context.status(Languages.getString("Actions.13"), fileToRead); //$NON-NLS-1$

				} else
					throw new RuntimeException(
					        Languages.getString("Actions.14")); //$NON-NLS-1$
			} catch (Actions.IncompatibleFileException | Actions.FileCorruptedException e) {
				context.error(e);
			} catch (final FileNotFoundException e) {
				context.error(Languages.getString("Actions.15"), fileToRead); //$NON-NLS-1$
			} catch (final IOException e) {
				context.error(Languages.getString("Actions.16"), fileToRead); //$NON-NLS-1$
				throw e;
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
				ErrorDumpDialog.showDialog(context.getFrame(), e);
				return;
			}

			final ListRequirement<String> filenameReq = (ListRequirement<String>) reqs
			        .get(EditorStrings.FILENAME);
			filenameReq.setOptions(files);
			filenameReq.setCaseOfNullGraphic(false, Languages.getString("Actions.0"), dir); //$NON-NLS-1$
		}
	},

	/** An action for resetting an {@code Editor} */
	CLEAR {
		@Override
		public void executeAction() throws Exception {
			context.clear();
			context.status(Languages.getString("Actions.17")); //$NON-NLS-1$
			context.fileInfo.markUnsaved();
		}
	},

	/** An Action for undoing a {@code Command} */
	UNDO {
		@Override
		public void executeAction() throws Exception {
			context.undo();
			context.status(Languages.getString("Actions.18")); //$NON-NLS-1$
			context.fileInfo.markUnsaved();
		}
	},

	/** An Action for redoing a {@code Command} */
	REDO {
		@Override
		public void executeAction() throws Exception {
			context.redo();
			context.status(Languages.getString("Actions.19")); //$NON-NLS-1$
			context.fileInfo.markUnsaved();
		}
	},

	/** An Action for showing Help */
	HELP {
		@Override
		public void executeAction() throws Exception {
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

			final Frame frame = context.getFrame();

			for (int i = 0, count = messages.length; i < count; i++)
				msg(frame, messages[i], titles[i]);
		}

		private void msg(Frame frame, String message, String title) {
			JOptionPane.showConfirmDialog(frame, message, title, JOptionPane.YES_NO_CANCEL_OPTION);
		}
	};

	// increment whenever the protocol that is used to store user data is altered
	// ensures that the data is never read in a way different than that it was stored
	private static final Integer storeProtocolVersion = 2;

	// bytes to mark the start and end of a file (should never change, used to check for corruption)
	private static final Byte startOfFile = 10, endOfFile = 42;

	/** The Requirements of the Action */
	protected final Requirements reqs;

	/** The context of the Action, the Editor whose state it changes */
	protected Editor context;

	Actions() {
		reqs = new Requirements();
		constructRequirements();
	}

	/**
	 * Defines a template for executing Actions. After execution, the Action's
	 * context is cleared. If an unexpected Exception occurs, a Dialog is shown
	 * informing the user about it.
	 *
	 * @throws NullPointerException if its {@code context} has not been set prior to
	 *                              execution
	 *
	 * @see #context
	 * @see ErrorDumpDialog
	 */
	public final void execute() {
		if (context == null)
			throw new NullPointerException(/* text */);

		try {
			executeAction();
		} catch (Exception e) {
			ErrorDumpDialog.showDialog(context.getFrame(), e);
		}

		reqs.clear();
		context = null;
	}

	/**
	 * Defines how this Action should execute.
	 *
	 * @throws Exception if anything unexpected occurs
	 */
	protected abstract void executeAction() throws Exception;

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
		reqs.fulfillWithDialog(editor.getFrame(), toString());
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

	/**
	 * Thrown when a File is corrupted and can't be read.
	 *
	 * @author Alex Mandelias
	 */
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

	/**
	 * Thrown when a File's data are incompatible with the current program version.
	 *
	 * @author Alex Mandelias
	 */
	protected final static class IncompatibleFileException extends Exception {

		/**
		 * Constructs the Exception with a {@code filename} and a {@code version}.
		 *
		 * @param filename          the name of the file which contains data written
		 *                          using a different store protocol version
		 * @param versionReadInFile the store protocol version read in the file
		 */
		public IncompatibleFileException(String filename, int versionReadInFile) {
			super(IncompatibleFileException.formatVersionMessage(filename, versionReadInFile,
			        Actions.storeProtocolVersion));
		}

		/**
		 * Constructs the Exception with a {@code filename} and an
		 * {@code InvalidClassException}.
		 *
		 * @param filename the name of the file which contains data
		 * @param e        the InvalidClassException that triggered this Exception
		 */
		public IncompatibleFileException(String filename, InvalidClassException e) {
			super(IncompatibleFileException.formatMessageFromException(filename, e));
		}

		private static final Pattern invalidClassExceptionPattern = Pattern
		        .compile(".*? serialVersionUID = (\\d+), .*? serialVersionUID = (\\d+)");//$NON-NLS-1$;

		private static String formatVersionMessage(String filename, int versionInFile,
		        int versionInCode) {

			final String actionString = "Actions." + (versionInFile > versionInCode ? "52" : "53"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

			return String.format(Languages.getString("Actions.51"), //$NON-NLS-1$
			        filename, Languages.getString(actionString));
		}

		private static String formatMessageFromException(String filename, InvalidClassException e) {

			// extract version information from exception message
			final Matcher m = IncompatibleFileException.invalidClassExceptionPattern
			        .matcher(e.getMessage());

			if (!m.matches())
				throw new RuntimeException("Invalid regex in IncompatibleFileException"); //$NON-NLS-1$

			final int idInFile  = Integer.parseInt(m.group(1));
			final int idInClass = Integer.parseInt(m.group(2));
			return IncompatibleFileException.formatVersionMessage(filename, idInFile, idInClass);
		}
	}

	/**
	 * Writes the contents of Lists of Components and Commands to a file.
	 *
	 * @param filename    the filename
	 * @param commands    the list of Commands to write to the file
	 * @param itemManager the manager whose generators to write to the file
	 *
	 * @throws IOException if an IOExcetpion occurred
	 *
	 * @see #readFromFile(String, List, ItemManager)
	 */
	protected static void writeToFile(String filename, List<Command> commands,
	        ItemManager<Component> itemManager) throws IOException {

		final Path dir = Paths.get(StringConstants.USER_DATA);

		// create directory if it doesn't exist
		if (!Files.exists(dir))
			try {
				Files.createDirectory(dir);
			} catch (final IOException e) {
				throw new IOException(
				        String.format("Didn't find directory %s and could not create it", //$NON-NLS-1$
				                StringConstants.USER_DATA));
			}

		final String outputFile = String.format("%s%s%s", dir.toString(), //$NON-NLS-1$
		        System.getProperty("file.separator"), filename); //$NON-NLS-1$

		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(outputFile))) {

			// write start
			oos.writeByte(Actions.startOfFile);

			// write version
			oos.writeByte(Actions.storeProtocolVersion);

			// write commands
			oos.writeInt(commands.size());
			for (final Command command : commands)
				oos.writeObject(command);

			// write generators
			final Map<String, StringGenerator> generators = itemManager.idGenerators;
			oos.writeInt(generators.size());
			for (Entry<String, StringGenerator> generatorEntry : generators.entrySet()) {
				oos.writeObject(generatorEntry.getKey());
				oos.writeObject(generatorEntry.getValue());
			}

			// write eof
			oos.writeByte(Actions.endOfFile);
		}
	}

	/**
	 * Fills the Lists with the Components and Commands from the file.
	 *
	 * @param filename    the filename
	 * @param commands    the list that will be filled with Commands
	 * @param itemManager the manager whose generators will be read from the file
	 *
	 * @throws IOException               if an IOException occurred
	 * @throws FileNotFoundException     if the file couldn't be found
	 * @throws FileCorruptedException    if the contents of the file are corrupted
	 * @throws IncompatibleFileException if the file data corresponds to a different
	 *                                   version of the program
	 *
	 * @see #writeToFile(String, List, ItemManager)
	 */
	protected static void readFromFile(String filename, List<Command> commands,
	        ItemManager<Component> itemManager) throws FileNotFoundException, IOException,
	        Actions.FileCorruptedException, Actions.IncompatibleFileException {

		final String inputFile = String.format("%s%s%s", StringConstants.USER_DATA, //$NON-NLS-1$
		        System.getProperty("file.separator"), filename); //$NON-NLS-1$

		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(inputFile))) {

			// read start
			if (ois.readByte() != Actions.startOfFile)
				throw new FileCorruptedException(filename);

			// read version
			final int versionRead = ois.readByte();
			if (versionRead != Actions.storeProtocolVersion)
				throw new IncompatibleFileException(filename, versionRead);

			// read commands
			int count = ois.readInt();
			for (int i = 0; i < count; ++i)
				commands.add((Command) ois.readObject());

			// read generators
			final Map<String, StringGenerator> generators = itemManager.idGenerators;
			count = ois.readInt();
			for (int i = 0; i < count; ++i) {
				final String          key   = (String) ois.readObject();
				final StringGenerator value = (StringGenerator) ois.readObject();
				generators.put(key, value);
			}

			// read eof
			if (ois.readByte() != Actions.endOfFile)
				throw new FileCorruptedException(filename);

		} catch (final ClassNotFoundException e) {
			throw new FileCorruptedException(filename);
		} catch (final InvalidClassException e) {
			throw new IncompatibleFileException(filename, e);
		}
	}
}
