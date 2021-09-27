package application.editor;

import static component.ComponentType.BRANCH;
import static component.ComponentType.GATE;
import static component.ComponentType.GATEAND;
import static component.ComponentType.GATENOT;
import static component.ComponentType.GATEOR;
import static component.ComponentType.GATEXOR;
import static component.ComponentType.INPUT_PIN;
import static component.ComponentType.OUTPUT_PIN;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Frame;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JOptionPane;

import application.Application;
import application.EditorInterface;
import application.StringConstants;
import command.Command;
import component.ComponentType;
import component.components.Component;
import component.components.ComponentFactory;
import localisation.EditorStrings;
import localisation.Languages;
import myUtil.Utility;

/**
 * An Editor to edit a file. The Editor manages {@link Component Components}
 * with the help of an {@link ItemManager} (which takes care of creating and
 * deleting them) and a {@link UI} (which displays them on the screen).
 * Additional information is displayed at the bottom of the screen using the
 * Editor's {@link StatusBar}.
 * <p>
 * The creation and deletion of Components is accomplished using {@link Command}
 * objects, which are managed using a {@link UndoableHistory}.
 * <p>
 * Every Action the Editor takes is encapsulated in an {@link Actions} constant.
 *
 * @author Alex Mandelias
 */
public final class Editor extends JComponent implements EditorInterface {

	/** This Editor's context, the Application in which it exists */
	final Application app;

	private final UI        editorUI;
	private final StatusBar statusBar;

	/** Encapsulates information about the File this Editor edits */
	final FileInfo fileInfo;

	/** Encapsulates information about the Components of this Editor */
	final ItemManager<Component>           componentManager;
	private final UndoableHistory<Command> undoableHistory;

	/**
	 * Constructs an Editor.
	 *
	 * @param application this Editor's context
	 * @param filename    the name of the (potentially nonexistent) file this Editor
	 *                    edits
	 */
	public Editor(Application application, String filename) {
		app = application;
		editorUI = new UI();
		statusBar = new StatusBar();

		fileInfo = new FileInfo();
		fileInfo.markSaved();
		fileInfo.setFile(filename);

		componentManager = new ItemManager<>();
		undoableHistory = new UndoableHistory<>();

		// configure the components of the editor
		statusBar.addLabel(EditorStrings.MESSAGE);
		statusBar.addLabel(EditorStrings.COUNT);
		statusBar.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));

		setLayout(new BorderLayout());
		setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		add(editorUI, BorderLayout.CENTER);
		add(statusBar, BorderLayout.SOUTH);

		componentManager.addGenerator(INPUT_PIN.description(), StringConstants.G_INPUT_PIN);
		componentManager.addGenerator(OUTPUT_PIN.description(), StringConstants.G_OUTPUT_PIN);
		componentManager.addGenerator(BRANCH.description(), StringConstants.G_BRANCH);
		componentManager.addGenerator(GATE.description(), StringConstants.G_GATE);
		componentManager.addGenerator(GATEAND.description(), StringConstants.G_GATEAND);
		componentManager.addGenerator(GATEOR.description(), StringConstants.G_GATEOR);
		componentManager.addGenerator(GATENOT.description(), StringConstants.G_GATENOT);
		componentManager.addGenerator(GATEXOR.description(), StringConstants.G_GATEXOR);
	}

	@Override
	public boolean close() {
		int res = JOptionPane.YES_OPTION;
		if (getFileInfo().isDirty()) {
			final String messageString = Languages.getString("Editor.2"); //$NON-NLS-1$
			final String titleString   = Languages.getString("Editor.3"); //$NON-NLS-1$
			res = JOptionPane.showConfirmDialog(getFrame(),
			        messageString,
			        String.format("%s %s", titleString, getFileInfo().getFile()), //$NON-NLS-1$
			        JOptionPane.YES_NO_CANCEL_OPTION,
			        JOptionPane.WARNING_MESSAGE);

			if (res == JOptionPane.YES_OPTION)
				Actions.SAVE.specify(EditorStrings.FILENAME, getFileInfo().getFile())
				        .context(this)
				        .execute();
		}

		return (res == JOptionPane.YES_OPTION) || (res == JOptionPane.NO_OPTION);
	}

	/**
	 * Returns the {@code Frame} of this Editor's Application, the window that is
	 * displayed on the user's screen.
	 *
	 * @return the Frame where this Editor is displayed
	 *
	 * @see #app
	 */
	Frame getFrame() {
		return app.getFrame();
	}

	/**
	 * Adds a {@code Component} to this Editor.
	 *
	 * @param component the Component to add
	 */
	public void addComponent(Component component) {
		componentManager.add(component);
		editorUI.addComponent(component);
		statusBar.setLabelText(EditorStrings.COUNT, Languages.getString("Editor.6"), //$NON-NLS-1$
		        componentManager.size());
	}

	/**
	 * Removes a {@code Component} from this Editor.
	 *
	 * @param component the Component to remove
	 */
	public void removeComponent(Component component) {
		componentManager.remove(component);
		editorUI.removeComponent(component);
		statusBar.setLabelText(EditorStrings.COUNT, Languages.getString("Editor.8"), //$NON-NLS-1$
		        componentManager.size());
	}

	/**
	 * Returns the {@code Component} with the given ID.
	 *
	 * @param ID the Component's ID
	 *
	 * @return the Component with that ID
	 *
	 * @throws MissingComponentException if no Component with that ID exists
	 */
	public Component getComponent_(String ID) throws MissingComponentException {
		return componentManager.get(ID);
	}

	/**
	 * Returns the {@code Component} with the given ID or {@code null} if no such
	 * {@code Component} exists.
	 *
	 * @param ID the Component's ID
	 *
	 * @return the Component with that ID or {@code null}
	 */
	public Component getComponentOrNull(String ID) {
		Component component;
		try {
			component = getComponent_(ID);
		} catch (final MissingComponentException e) {
			component = null;
		}
		return component;
	}

	/**
	 * Returns every {@code Component} of this Editor.
	 * <p>
	 * <b>Note:</b> this method does <i>not</i> return a copy of each Component. Any
	 * changes to them will be reflected in this Editor.
	 *
	 * @return a List with the Components of this Editor
	 */
	public List<Component> getComponents_() {
		return componentManager.getall();
	}

	/**
	 * Returns every deleted {@code Component} of this Editor.
	 * <p>
	 * <b>Note:</b> this method does <i>not</i> return a copy of each Component. Any
	 * changes to them will be reflected in this Editor.
	 *
	 * @return a List with the deleted Components of this Editor
	 */
	public List<Component> getDeletedComponents() {
		return componentManager.getall(ComponentFactory::toRemove);
	}

	/**
	 * Returns the next generated ID for a {@code ComponentType}.
	 *
	 * @param type the type of the Component
	 *
	 * @return the next ID
	 *
	 * @see ComponentType
	 */
	public String getNextID(ComponentType type) {
		return componentManager.getNextID(type.description());
	}

	/** Clears this Editor resetting it to its original state */
	void clear() {
		Utility.foreach(new ArrayList<>(getComponents_()), this::removeComponent);
		undoableHistory.clear();
	}

	/**
	 * Executes a {@code Command}.
	 *
	 * @param command the Command to execute
	 *
	 * @throws Exception when something exceptional happens
	 */
	void execute(Command command) throws Exception {
		command.execute();
		undoableHistory.add(command);
		System.out.println(undoableHistory);
	}

	/** Undoes the most recently executed {@code Command} */
	void undo() {
		undoableHistory.undo();
		System.out.println(undoableHistory);
	}

	/** Re-does the most recently undone {@code Command} */
	void redo() {
		undoableHistory.redo();
		System.out.println(undoableHistory);
	}

	/**
	 * Returns a copy of the {@code Commands} previously executed.
	 *
	 * @return the list of Commands
	 */
	public List<Command> getPastCommands() {
		return new ArrayList<>(undoableHistory.getPast());
	}

	@Override
	public FileInfo getFileInfo() {
		return fileInfo;
	}

	/**
	 * Updates the 'message' label with a status message. The message is formatted
	 * exactly as if {@code String.format(format, args)} was called.
	 *
	 * @param format the format
	 * @param args   the format arguments
	 */
	public void status(String format, Object... args) {
		statusBar.setLabelText(EditorStrings.MESSAGE, StatusBar.MessageType.DEFAULT, format, args);
	}

	/**
	 * Updates the 'message' label with an error message. The message is formatted
	 * exactly as if {@code String.format(format, args)} was called.
	 *
	 * @param format the format
	 * @param args   the format arguments
	 */
	public void error(String format, Object... args) {
		statusBar.setLabelText(EditorStrings.MESSAGE, StatusBar.MessageType.FAILURE, format, args);
	}

	/**
	 * Updates the 'message' label with the error message of an Exception.
	 *
	 * @param exception the Exception
	 */
	public void error(Exception exception) {
		error("%s", exception.getMessage()); //$NON-NLS-1$
	}

	@Override
	public String toString() {
		return String.format("{%s, %s, %s, %s, %s}", fileInfo, app, componentManager, //$NON-NLS-1$
		        undoableHistory, statusBar);
	}
}
