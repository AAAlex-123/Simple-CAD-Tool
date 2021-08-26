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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import application.StringConstants;
import command.Command;
import components.Component;
import components.ComponentFactory;
import exceptions.MalformedBranchException;
import localisation.EditorStrings;
import localisation.Languages;
import myUtil.Utility;
import requirement.requirements.AbstractRequirement;
import requirement.requirements.Requirements;
import requirement.requirements.ListRequirement;
import requirement.requirements.ComponentRequirement;
import requirement.requirements.StringType;

/**
 * An enum-strategy for the different Actions the {@link Editor} may take.
 *
 * @author alexm
 */
public enum Actions {

	/** Action for creating a {@code Component} */
	CREATE(EditorStrings.COMMAND) {
		@Override
		public void execute() {

			if (!reqs.fulfilled())
				throw new RuntimeException("Execute CREATE without requirements"); //$NON-NLS-1$

			final Command cte = (Command) reqs.getValue(EditorStrings.COMMAND);

			try {
				if (!cte.canExecute()) {
					context.status(Languages.getString("Actions.1"), cte); //$NON-NLS-1$
					return;
				}

				context.execute(cte);
				context.status(Languages.getString("Actions.2"), cte); //$NON-NLS-1$
				context.setDirty(true);
			} catch (MissingComponentException | MalformedBranchException e) {
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
	DELETE(EditorStrings.COMMAND) {
		
		@Override
		public void execute() {

			if (!reqs.fulfilled())
				throw new RuntimeException("Execute DELETE without requirements"); //$NON-NLS-1$

			final Command cte = (Command) reqs.getValue(EditorStrings.COMMAND);

			try {
				if (!cte.canExecute()) {
					context.status(Languages.getString("Actions.4")); //$NON-NLS-1$
					return;
				}

				context.execute(cte);
				context.status(Languages.getString("Actions.5")); //$NON-NLS-1$
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
	SAVE(EditorStrings.FILENAME, StringType.FILENAME) {
		@Override
		public void execute() {

			final String fname = (String) reqs.getValue(EditorStrings.FILENAME);

			try {
				if (!reqs.fulfilled()) {
					context.status(Languages.getString("Actions.6")); //$NON-NLS-1$
					return;
				}

				Actions.writeToFile(fname, context.getComponents_(), context.getPastCommands());

				context.status(Languages.getString("Actions.7"), fname); //$NON-NLS-1$
				context.setFile(fname);
				context.setDirty(false);

			} catch (final IOException e) {
				context.error(
						Languages.getString("Actions.8"), //$NON-NLS-1$
						fname);
				throw new RuntimeException(e);
			} finally {
				reqs.clear();
			}
		}
	},

	/** An Action that reads the contents of a File to the Editor */
	OPEN() {
		
		@Override
		public void constructAdditionalRequirements() {
			reqs.add(EditorStrings.FILENAME, new ArrayList<String>());
			reqs.add(EditorStrings.FILETYPE, StringType.FILETYPE);
			reqs.add(EditorStrings.GATENAME, StringType.ANY);
		}
		
		@SuppressWarnings("unchecked") //yes this is safe
		@Override 
		public Actions specifyWithDialog(Editor editor) {
			Path dir = Paths.get(System.getProperty("user.dir") + File.separator + "user_data");
			if(!Files.exists(dir)) { //create directory if it doesn't exist
				try {
					Files.createDirectory(dir);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}	
			
			List<String> files;
			try (Stream<Path> paths = Files.walk(dir)) {
			    	files = paths
			    			.filter(file -> file.toString().contains(".scad"))
			    			.map(file -> file.toString().substring(file.toString().lastIndexOf(File.separator)+1)) //get file name
			    			.collect(Collectors.toList());
			} catch (IOException e) {
				throw new UncheckedIOException(e);
			} 
			
			((ListRequirement<String>) reqs.get(EditorStrings.FILENAME)).setOptions(files);
			return super.specifyWithDialog(editor);
		}
		
		@Override
		public void execute() {

			final String fname = (String) reqs.getValue(EditorStrings.FILENAME);
			final String ftype = (String) reqs.getValue(EditorStrings.FILETYPE);

			final List<Component> components = new ArrayList<>();
			final List<Command> commands = new ArrayList<>();

			try {
				if (!reqs.fulfilled()) {
					context.status(Languages.getString("Actions.9"), //$NON-NLS-1$
					        ftype.equals(EditorStrings.CIRCUIT) ? Languages.getString("Actions.10") : Languages.getString("Actions.11")); //$NON-NLS-1$ //$NON-NLS-2$
					return;
				}

				Actions.readFromFile(fname, components, commands);

				if (ftype.equals(EditorStrings.CIRCUIT)) {

					context.clear();

					Utility.foreach(components, component -> {
						ComponentFactory.restoreSerialisedComponent(component);
						context.addComponent(component);
					});
					Utility.foreach(commands, command -> {
						command.context(context);
						context.addToHistory(command);
					});

					context.setFile(fname);
					context.status(Languages.getString("Actions.12"), fname); //$NON-NLS-1$
					context.setDirty(false);

				} else if (ftype.equals(EditorStrings.COMPONENT)) {

					final Command cgc = Command.create(commands,
					        (String) reqs.getValue(EditorStrings.GATENAME));
					context.context().addCreateCommand(cgc);
					context.status(Languages.getString("Actions.13"), fname); //$NON-NLS-1$

				} else {
					throw new RuntimeException(
							Languages.getString("Actions.14")); //$NON-NLS-1$
				}
			} catch (Actions.IncompatibleFileException | Actions.FileCorruptedException e) {
				context.error(e);
			} catch (final FileNotFoundException e) {
				context.error(Languages.getString("Actions.15"), fname); //$NON-NLS-1$
			} catch (final IOException e) {
				context.error(
						Languages.getString("Actions.16"), //$NON-NLS-1$
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
			context.status(Languages.getString("Actions.17")); //$NON-NLS-1$
			context.setDirty(true);
		}
	},

	/** An Action for undoing a Command */
	UNDO {
		@Override
		public void execute() {
			context.undo();
			context.status(Languages.getString("Actions.18")); //$NON-NLS-1$
			context.setDirty(true);
		}
	},

	/** An Action for redoing a Command */
	REDO {
		@Override
		public void execute() {
			context.redo();
			context.status(Languages.getString("Actions.19")); //$NON-NLS-1$
			context.setDirty(true);
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
				        "Number of help titles doesn't match number of messages."); //$NON-NLS-1$

			// yes=0 no=1 cancel=2 x=-1 (+1)
			final int[] res = { 0, 0, 0, 0 };

			final Frame frame = context.context().getFrame();

			for (int i = 0; i < messages.length; i++)
				++res[1 + msg(frame, messages[i], titles[i])];
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

	/** The context of the Action */
	Editor context;

	Actions() {
		reqs = new Requirements();
		constructAdditionalRequirements();
	}

	Actions(String reqKey) {
		this();
		reqs.add(reqKey);
	}

	Actions(String reqKey, StringType stringType) {
		this();
		reqs.add(reqKey, stringType);
	}

	Actions(String[] reqKeys) {
		this();
		Utility.foreach(reqKeys, reqs::add);
	}

	Actions(String[] reqKeys, StringType[] types) {
		this();

		if (reqKeys.length != types.length)
			throw new RuntimeException("Invalid arguments in Actions enum constructor"); //$NON-NLS-1$

		for (int i = 0; i < reqKeys.length; i++)
			reqs.add(reqKeys[i], types[i]);
	}
	
	/**
	 * Extra code to be invoked by the constructor. By default does nothing, override
	 * for specialized requirements.
	 */
	public void constructAdditionalRequirements() {}

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
	public Actions specifyWithDialog(Editor editor) {
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
	 * @see AbstractRequirement#finalise
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
			super(String.format(Languages.getString("Actions.48"), //$NON-NLS-1$
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
					.compile(".*? serialVersionUID = (\\d+), .*? serialVersionUID = (\\d+)"); //$NON-NLS-1$
			final Matcher m = p.matcher(e.getMessage());

			if (!m.matches())
				throw new RuntimeException("Invalid regex in IncompatibleFileException"); //$NON-NLS-1$

			final int idInFile = Integer.parseInt(m.group(1));
			final int idInClass = Integer.parseInt(m.group(2));

			return String.format(Languages.getString("Actions.51"), //$NON-NLS-1$
					filename, idInFile > idInClass ? Languages.getString("Actions.52") : Languages.getString("Actions.53")); //$NON-NLS-1$ //$NON-NLS-2$
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

		final String outputFile = String.format("%s%s%s", StringConstants.USER_DATA, //$NON-NLS-1$
		        System.getProperty("file.separator"), filename); //$NON-NLS-1$

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
