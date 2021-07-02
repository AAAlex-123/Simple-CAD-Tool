package application.editor;

import static components.ComponentType.BRANCH;
import static components.ComponentType.GATE;
import static components.ComponentType.GATEAND;
import static components.ComponentType.GATENOT;
import static components.ComponentType.GATEOR;
import static components.ComponentType.GATEXOR;
import static components.ComponentType.INPUT_PIN;
import static components.ComponentType.OUTPUT_PIN;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JOptionPane;

import application.Application;
import command.Command;
import components.Component;
import components.ComponentFactory;
import components.ComponentType;
import myUtil.Utility;

/**
 * An Editor to edit a file. The Editor manages {@link components.Component
 * Components} with the help of an {@link ItemManager} (which takes care of
 * creating and deleting them) and a {@link UI} (which displays them on the
 * screen). Additional information is displayed at the bottom of the screen
 * using the Editor's {@link StatusBar}.
 * <p>
 * Component creation and deletion is accomplished using {@link command.Command
 * Commands} which are managed using a {@link UndoableHistory}.
 * <p>
 * Every Action the Editor takes is encapsulated in an {@link Actions Action}
 * instance.
 *
 * @author alexm
 */
public final class Editor extends JComponent {

	private final Application app;
	private final UI          editorUI;
	private final FileLabel   fileLabel;
	private String            filename;
	private boolean           dirty;
	private final StatusBar   statusBar;

	private final ItemManager<Component>   components;
	private final UndoableHistory<Command> undoableHistory;

	/**
	 * Constructs the Editor with the given context.
	 *
	 * @param app         the Editor's context
	 * @param initialFile the initial file name
	 */
	public Editor(Application app, String initialFile) {
		this.app = app;
		editorUI = new UI();
		fileLabel = new FileLabel();
		setDirty(false);
		setFile(initialFile);
		statusBar = new StatusBar();

		components = new ItemManager<>();
		undoableHistory = new UndoableHistory<>();

		// configure the components of the editor
		statusBar.addLabel("message");
		statusBar.addLabel("count");

		setLayout(new BorderLayout());
		setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		add(editorUI, BorderLayout.CENTER);

		components.addGenerator(INPUT_PIN.description(), "in%d");
		components.addGenerator(OUTPUT_PIN.description(), "out%d");
		components.addGenerator(BRANCH.description(), "br%d");
		components.addGenerator(GATE.description(), "custom%d");
		components.addGenerator(GATEAND.description(), "and%d");
		components.addGenerator(GATEOR.description(), "or%d");
		components.addGenerator(GATENOT.description(), "not%d");
		components.addGenerator(GATEXOR.description(), "xor%d");
	}

	/**
	 * Closes the Editor asking for confirmation to save if dirty.
	 *
	 * @return {@code false} if cancel was selected, {@code true} otherwise
	 */
	public boolean close() {
		int res = JOptionPane.YES_OPTION;
		if (isDirty()) {
			res = JOptionPane.showConfirmDialog(null, "Would you like keep unsaved changed?",
					"Close " + filename, JOptionPane.YES_NO_CANCEL_OPTION,
					JOptionPane.WARNING_MESSAGE);

			if (res == JOptionPane.YES_OPTION)
				Actions.SAVE.specify("filename", filename).context(this).execute();
		}
		return (res == JOptionPane.YES_OPTION) || (res == JOptionPane.NO_OPTION);
	}

	/** @return the Editor's context */
	Application context() {
		return app;
	}

	/**
	 * Adds a {@code Component} to the {@code Editor}.
	 *
	 * @param component the Component
	 */
	public void addComponent(Component component) {
		components.add(component);
		editorUI.addComponent(component);
		statusBar.setLabelText("count", "Component count: %d", components.size());
	}

	/**
	 * Removes a {@code Component} from the {@code Editor}.
	 *
	 * @param component the Component
	 */
	public void removeComponent(Component component) {
		components.remove(component);
		editorUI.removeComponent(component);
		statusBar.setLabelText("count", "Component count: %d", components.size());
	}

	/**
	 * Returns the {@code Component} with the given ID.
	 *
	 * @param ID the ID
	 *
	 * @return the Component
	 *
	 * @throws MissingComponentException if no Component with the ID exists
	 */
	public Component getComponent_(String ID) throws MissingComponentException {
		return components.get(ID);
	}

	/**
	 * Returns a list of the Editor's {@code Components}.
	 * <p>
	 * <b>Note</b> that this does <i>not</i> return a copy of the items. Any changes
	 * to the Components will be reflected in the Editor.
	 *
	 * @return the list
	 */
	public List<Component> getComponents_() {
		return components.getall();
	}

	/**
	 * Returns a list of the Editor's deleted {@code Components}.
	 * <p>
	 * <b>Note</b> that this does <i>not</i> return a copy of the items. Any changes
	 * to the Components will be reflected in the Editor.
	 *
	 * @return the list
	 */
	public List<Component> getDeletedComponents() {
		return components.getall((c) -> ComponentFactory.toRemove(c));
	}

	/**
	 * Returns the next generated ID for the given {@code ComponentType}.
	 *
	 * @param type the type of the Component
	 *
	 * @return the next ID
	 */
	public String getNextID(ComponentType type) {
		return components.getNextID(type.description());
	}

	/** Clears the {@code Editor} resetting it to its original state */
	void clear() {
		Utility.foreach(new ArrayList<>(getComponents_()), this::removeComponent);
		undoableHistory.clear();
	}

	/**
	 * Executes a {@code Command}.
	 *
	 * @param c the Command to execute
	 *
	 * @throws Exception when something exceptional happens
	 */
	void execute(Command c) throws Exception {
		c.execute();
		undoableHistory.add(c);
	}

	/** Undoes the most recent {@code Command} */
	void undo() {
		undoableHistory.undo();
	}

	/** Re-does the most recently undone {@code Command} */
	void redo() {
		undoableHistory.redo();
	}

	/**
	 * Returns a list with the {@code Commands} executed on this {@code Editor}.
	 * <p>
	 * <b>Note</b> that this does <i>not</i> return a copy of the Commands. Any
	 * changes to the Commands will be reflected in the {@code Editor}.
	 *
	 * @return the list
	 */
	public List<Undoable> getPastCommands() {
		return new Vector<>(undoableHistory.getPast());
	}

	/**
	 * Returns the Editor's {@code StatusBar}.
	 *
	 * @return the StatusBar
	 */
	public StatusBar getStatusBar() {
		return statusBar;
	}

	/**
	 * Returns the File LabeL.
	 *
	 * @return the fileLabel
	 */
	public FileLabel getFileLabel() {
		return fileLabel;
	}

	/**
	 * Returns the name of the file that is being edited.
	 *
	 * @return the filename
	 */
	public String getFile() {
		return filename;
	}

	/**
	 * Sets the name of the file that is being edited.
	 *
	 * @param filename the file name
	 */
	void setFile(String filename) {
		this.filename = filename;
		updateTitle();
	}

	/**
	 * Returns the dirtiness of the editor.
	 *
	 * @return the dirtiness
	 */
	boolean isDirty() {
		return dirty;
	}

	/**
	 * Sets the dirtiness of the Editor.
	 *
	 * @param newDirty the new dirtiness
	 */
	void setDirty(boolean newDirty) {
		dirty = newDirty;
		updateTitle();
	}

	private void updateTitle() {
		fileLabel.updateText(getFile(), isDirty());
	}

	/**
	 * Updates the 'message' label with a status message. The message is formatted
	 * exactly as if String.format(text, args) was called.
	 *
	 * @param text the text
	 * @param args the format arguments
	 */
	public void status(String text, Object... args) {
		statusBar.setLabelText("message", StatusBar.TextType.DEFAULT, text, args);
	}

	/**
	 * Updates the 'message' label with an error message. The message is formatted
	 * exactly as if String.format(text, args) was called.
	 *
	 * @param text the text
	 * @param args the format arguments
	 */
	public void error(String text, Object... args) {
		statusBar.setLabelText("message", StatusBar.TextType.FAILURE, text, args);
	}

	/**
	 * Updates the 'message' label with the message of the {@code exception}.
	 *
	 * @param exception the Exception
	 */
	public void error(Exception exception) {
		error("%s", exception.getMessage());
	}
}
