package application;

import static myUtil.Utility.all;
import static myUtil.Utility.foreach;
import static myUtil.Utility.max;

import java.awt.BorderLayout;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;

import components.Component;
import components.ComponentFactory;
import components.ComponentType;

/**
 * A class representing the application. It aggregates all of the individual
 * components and handles the communications between them. The client has to
 * create an {@code Application} object then call its {@code run} method to
 * start the program.
 */
public final class Application {

	public static final String menu_icon_path = "assets\\menu_icons\\";
	public static final String component_icon_path = "assets\\component_icons\\";
	private static final String user_data = "user_data\\";

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
		sb.addLabel("freeM");
		sb.addLabel("totalM");
		updateMemory();
	}

	/** Runs the application */
	public void run() {
		// TODO: autosave thread

		ui.setFocusable(true);
		// configure components only when run to make construction faster
		window.setLayout(new BorderLayout());
		window.add(ui, BorderLayout.CENTER);
		window.add(sb, BorderLayout.SOUTH);
		window.setJMenuBar(menu);

		addCreateCommand(new CreateCommand(this, ComponentType.INPUT_PIN));
		addCreateCommand(new CreateCommand(this, ComponentType.OUTPUT_PIN));
		addCreateCommand(new CreateCommand(this, ComponentType.BRANCH));
		addCreateCommand(new CreateCommand(this, ComponentType.GATEAND));
		addCreateCommand(new CreateCommand(this, ComponentType.GATEOR));
		addCreateCommand(new CreateCommand(this, ComponentType.GATENOT));
		addCreateCommand(new CreateCommand(this, ComponentType.GATEXOR));

		window.setTitle("Simple CAD Tool");
		window.setSize(1000, 600);
		window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		window.setVisible(true);
	}

	JFrame getFrame() {
		return window;
	}

	/**
	 * Adds a Command for creating Components to the Application.
	 *
	 * @param c the Command
	 */
	void addCreateCommand(Command c) {
		menu.addCreateCommand(c);
	}

	/**
	 * Adds a Component to the Application.
	 *
	 * @param c the Component
	 */
	void addComponent(Component c) {
		hm.put(c.UID(), c);
		ui.addComponent(c);
		sb.setLabelText("count", "Component count: %d", hm.size());
		updateMemory();
	}

	/**
	 * Removes a Component from the Application.
	 *
	 * @param c the Component
	 */
	void removeComponent(Component c) {
		hm.remove(c.UID());
		ui.removeComponent(c);
		sb.setLabelText("count", "Component count: %d", hm.size());
		updateMemory();
	}

	/**
	 * Returns the Component with the given ID or null.
	 *
	 * @param UID the ID
	 * @return the Component, or null if no such Component exists
	 */
	Component getComponent(int UID) {
		return hm.get(UID);
	}

	/** Clears the Application resetting it to its original state */
	void clear() {
		List<Component> ls = new ArrayList<>(getComponents());

		current_file = null;

		foreach(ls, this::removeComponent);

		undoableHistory.clear();

		Component.resetGlobalID();
	}

	/**
	 * Executes a Command
	 *
	 * @param c the Command to execute
	 * @return 0 indicating success, otherwise failure
	 */
	int do_(Command c) {
		int retval = c.execute();
		if (retval != 0)
			return retval;

		undoableHistory.add(c);
		return 0;
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
	List<Component> getComponents() {
		List<Component> ls = new LinkedList<>();
		foreach(hm.values(), c -> ls.add(c));
		return ls;
	}

	/** @return a list of the Application's deleted Components */
	List<Component> getDeletedComponents() {
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
		sb.setLabelText("message", StatusBar.MessageType.DEFAULT, text, args);
	}

	/**
	 * Updates the 'message' label with an error message. The message is formatted
	 * exactly as if String.format(text, args) was called.
	 *
	 * @param text the text
	 * @param args the format arguments
	 */
	void error(String text, Object... args) {
		sb.setLabelText("message", StatusBar.MessageType.FAILURE, text, args);
	}

	private void updateMemory() {
		Runtime rt = Runtime.getRuntime();
		sb.setLabelText("freeM", "Free: %d MB", rt.freeMemory() / 1_000_000);
		sb.setLabelText("totalM", "Total: %d MB", rt.totalMemory() / 1_000_000);
	}

	/** An enum-strategy for the different Actions the user may take. */
	enum Actions {

		/** An Action for creating a Component. */
		CREATE("command") {
			@Override
			void execute() {

				Command cte = (Command) reqs.get("command").value();

				if (!cte.requirements.fulfilled()) {
					context.status("Create %s cancelled", cte.desc());
					return;
				}

				int execResult = context.do_(cte);

				if (execResult == 0)
					context.status("%s successful", cte.desc());
				else if (all(new Integer[] { 1, 2, 3 }, i -> execResult != i))
					context.error("Inform the developer about error code Action-Create-%d", execResult);

			}
		},

		/** An Action for deleting a Component. */
		DELETE("command") {
			@Override
			void execute() {
				Command cte = (Command) reqs.get("command").value();

				if (!cte.requirements.fulfilled()) {
					context.status("Delete cancelled");
					return;
				}

				int execResult = context.do_(cte);
				String id = cte.requirements.get("id").value();

				if (execResult == 0)
					context.status("Component with ID %s deleted", id);
				else if (execResult == 1)
					context.error("Component with ID %s not found", id);
				else
					context.error("Inform the developer about error code Action-Delete-%d", execResult);
			}
		},

		/** An Action that saves the components of the Application to a File. */
		SAVE("filename", Requirement.StringType.FILENAME) {
			@Override
			void execute() {

				if (!reqs.fulfilled()) {
					context.status("File save cancelled");
					return;
				}

				String fname = ((String) reqs.get("filename").value());
				context.current_file = fname;

				int writeResult = writeToFile(fname);

				if (writeResult == 0)
					context.status("File %s saved successfully", fname);
				else if (writeResult == 1)
					context.error("File %s not found", fname);
				else if (writeResult == 2)
					context.error("Error while writing to file");
				else
					context.error("Inform the developer about error code Action-Save-%d", writeResult);

				reqs.clear();
			}
		},

		/** An Action that reads the contents of a File to the Application. */
		OPEN(new String[] { "filename", "gatename", "filetype" },
				new Requirement.StringType[] { Requirement.StringType.FILENAME, Requirement.StringType.ANY, Requirement.StringType.FILETYPE }) {
			@Override
			void execute() {

				String fname = (String) reqs.get("filename").value();
				String ftype = (String) reqs.get("filetype").value();

				if (!reqs.fulfilled()) {
					context.status("File %s cancelled", ftype.equals(circuit) ? "open" : "import");
					return;
				}

				List<Component> components = new ArrayList<>();
				List<Command> commands = new ArrayList<>();

				int readResult = readFromFile(fname, components, commands);

				if (readResult == 0) {
					if (ftype.equals(circuit)) {

						context.clear();

						foreach(components, c -> context.addComponent(c));
						foreach(commands, c -> context.undoableHistory.add(c));

						Component.setGlobalID(max(context.getComponents(), Component::UID).UID());

					} else if (ftype.equals(component)) {
						Command cgc = new CreateGateCommand(context, commands, (String) reqs.get("gatename").value());
						context.addCreateCommand(cgc);

					} else {
						throw new RuntimeException("ffs-open-filetype");
					}

					// specify this file as the current file
					// SAVE.reqs.get("filename").fulfill(fname);
					context.current_file = fname;

					context.status("File %s %s successfully", fname, ftype.equals(circuit) ? "opened" : "imported");

				} else if (readResult == 1)
					context.error("File %s not found", reqs.get("filename").value());
				else if (readResult == 2)
					context.error(
							"Error while reading from file. Try again or inform the developer about error code Action-Open-%d",
							readResult);
				else if (readResult == 4)
					context.error("File corrupted");
				else if (readResult == 5)
					context.error("File contains components of an earlier version that is no longer supported");
				else
					context.error("Inform the developer about error code Action-Open-%d", readResult);

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
			}
		},

		/** An Action for redoing a Command. */
		REDO {
			@Override
			public void execute() {
				context.redo();
				context.status("Redo");
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

		// byte at the start of file to differentiate between filetypes
		private static final Integer component_i, circuit_i, start, eof;

		// these match the subdirectories in `user_data`
		private static final String component, circuit;

		private static final Map<String, Integer> mp = new HashMap<>();
		static {
			// the string should match Requirement.Type :/
			component = "component";
			circuit = "circuit";
			component_i = 1;
			circuit_i = 2;
			mp.put(component, component_i);
			mp.put(circuit, circuit_i);

			start = 10;
			eof = 42;
		}

		/** The Requirements of the Action */
		final Requirements<Object> reqs;

		/** The context of the Action */
		Application context;

		Actions() {
			reqs = null;
		}

		Actions(String req) {
			reqs = new Requirements<>();
			reqs.add(req);
		}

		Actions(String req, Requirement.StringType stringType) {
			reqs = new Requirements<>();
			reqs.add(req, stringType);
		}

		Actions(String[] reqs) {
			this.reqs = new Requirements<>();
			for (String r : reqs)
				this.reqs.add(r);
		}

		Actions(String[] reqs, Requirement.StringType[] types) {
			this.reqs = new Requirements<>();
			if (reqs.length != types.length)
				throw new RuntimeException("fix enum constructors lmao");
			for (int i = 0; i < reqs.length; i++)
				this.reqs.add(reqs[i], types[i]);
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
		 * Specifies a Command to fulfill a specific Requirement.
		 *
		 * @param req the Requirement
		 * @param c   the Command
		 * @return this (used for chaining)
		 */
		Actions specify(String req, Command c) {
			reqs.get(req).fulfil(c);
			return this;
		}

		/**
		 * Writes the contents of the Application to a file
		 *
		 * @param filename the filename
		 * @return return code (0 success, 0&lt; exception occurred)
		 */
		@SuppressWarnings("unused")
		int writeToFile(String filename) {
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

			} catch (FileNotFoundException e) {
				return 1;
			} catch (IOException e) {
				e.printStackTrace();
				return 2;
			}
			return 0;
		}

		/**
		 * Fills the Lists with the Components and Commands from the file.
		 *
		 * @param filename   the filename
		 * @param components the list that will be filled with Components
		 * @param commands   the list that will be filled with Commands
		 * @return return code (0 success, &lt;0 exception occurred)
		 */
		@SuppressWarnings("unused")
		int readFromFile(String filename, List<Component> components, List<Command> commands) {
			String inputFile = String.format("%s\\%s", user_data, filename);

			try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(inputFile))) {

				// read start
				if (ois.readByte() != start)
					return 4;

				// read components
				int count = ois.readInt();
				for (int i = 0; i < count; ++i) {
					Component c = (Component) ois.readObject();
					ComponentFactory.attachListeners(c);
					components.add(c);
				}

				// read commands
				count = ois.readInt();
				for (int i = 0; i < count; ++i) {
					Command c = (Command) ois.readObject();
					c.context = context;
					commands.add(c);
				}

				// read eof
				if (ois.read() != eof)
					return 4;

			} catch (InvalidClassException e) {
				return 5;
			} catch (EOFException | StreamCorruptedException e) {
				e.printStackTrace();
				return 4;
			} catch (FileNotFoundException e) {
				return 1;
			} catch (IOException e) {
				e.printStackTrace();
				return 2;
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				return 3;
			}
			return 0;
		}
	}
}
