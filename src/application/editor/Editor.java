package application.editor;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JOptionPane;

import application.Application;
import command.Command;
import components.Component;
import components.ComponentFactory;
import myUtil.Utility;

/**
 * An Editor to edit a file.
 *
 * @author alexm
 */
public final class Editor extends JComponent {

	private final Application app;
	private final UI          editorUI;
	private final EditorTab   editorTab;
	private final EditorFile  file;
	private final StatusBar   statusBar;

	private final Map<Integer, Component>  componentMap;
	private final UndoableHistory<Command> undoableHistory;

	/**
	 * Constructs the editor with the given context.
	 *
	 * @param context     the Editor's context
	 * @param initialFile the default file name
	 */
	public Editor(Application context, String initialFile) {
		app = context;
		editorUI = new UI();
		editorTab = new EditorTab();
		file = new EditorFile();
		statusBar = new StatusBar();

		componentMap = new HashMap<>();
		undoableHistory = new UndoableHistory<>();

		setFile(initialFile);
		statusBar.addLabel("message");
		statusBar.addLabel("count");

		setLayout(new BorderLayout());
		setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		add(editorUI, BorderLayout.CENTER);
	}

	/**
	 * Closes the Editor asking for confirmation to save if dirty.
	 *
	 * @return false if cancel was selected when prompted to save, true otherwise
	 */
	public boolean close() {
		int res = JOptionPane.YES_OPTION;
		if (file.isDirty()) {
			res = JOptionPane.showConfirmDialog(null, "Would you like keep unsaved changed?",
			        "Close " + file.get(),
			        JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

			if (res == JOptionPane.YES_OPTION) {
				Actions.SAVE.specify("filename", file.get()).context(this).execute();
			}
		}
		return res == JOptionPane.YES_OPTION;
	}

	/** @return the Editor's context */
	Application context() {
		return app;
	}

	/**
	 * Adds a Component to the Editor.
	 *
	 * @param c the Component
	 */
	public void addComponent(Component c) {
		componentMap.put(c.UID(), c);
		editorUI.addComponent(c);
		statusBar.setLabelText("count", "Component count: %d", componentMap.size());
	}

	/**
	 * Removes a Component from the Editor.
	 *
	 * @param c the Component
	 */
	public void removeComponent(Component c) {
		componentMap.remove(c.UID());
		editorUI.removeComponent(c);
		statusBar.setLabelText("count", "Component count: %d", componentMap.size());
	}

	/**
	 * Returns the Component with the given ID.
	 *
	 * @param UID the ID
	 *
	 * @return the Component
	 *
	 * @throws Editor.MissingComponentException if no Component with the UID exists
	 */
	public Component getComponent_(int UID) throws Editor.MissingComponentException {
		final Component c = componentMap.get(UID);
		if (c == null)
			throw new Editor.MissingComponentException(UID);

		return c;
	}

	/** Clears the Editor resetting it to its original state */
	void clear() {
		final List<Component> ls = new ArrayList<>(getComponents_());

		Utility.foreach(ls, this::removeComponent);

		undoableHistory.clear();

		// Component.resetGlobalID();
	}

	/**
	 * Executes a Command.
	 *
	 * @param c the Command to execute
	 *
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

	/**
	 * Returns a list of the Editor's Components.
	 *
	 * @return the list
	 */
	public List<Component> getComponents_() {
		return new LinkedList<>(componentMap.values());
	}

	/**
	 * Returns a list of the Editor's deleted Components.
	 *
	 * @return the list
	 */
	public List<Component> getDeletedComponents_() {
		final List<Component> ls = new LinkedList<>();
		Utility.foreach(componentMap.values(), c -> {
			if (ComponentFactory.toRemove(c))
				ls.add(c);
		});
		return ls;
	}

	/**
	 * Returns a list with the Commands already executed on this Editor.
	 *
	 * @return the list
	 *
	 * @see UndoableHistory#getPast()
	 */
	public List<Undoable> getPastCommands() {
		return new Vector<>(undoableHistory.getPast());
	}

	/**
	 * Returns the statusBar.
	 *
	 * @return the statusBar
	 */
	public StatusBar getStatusBar() {
		return statusBar;
	}

	/**
	 * Returns the editorTab.
	 *
	 * @return the editorTab
	 */
	public EditorTab getEditorTab() {
		return editorTab;
	}

	/**
	 * Returns the name of the file that is being edited.
	 *
	 * @return the file name
	 */
	public String getFile() {
		return file.get();
	}

	/**
	 * Sets the name of the file that is being edited.
	 *
	 * @param filename the file name
	 */
	void setFile(String filename) {
		file.set(filename);
		updateTitle();
	}

	/**
	 * Returns the dirty-ness of the editor.
	 *
	 * @return the dirty-ness
	 */
	boolean isDirty() {
		return file.isDirty();
	}

	/**
	 * Sets the dirty-ness of the Editor.
	 *
	 * @param newDirty the new dirty-ness
	 */
	void setDirty(boolean newDirty) {
		file.setDirty(newDirty);
		updateTitle();
	}

	private void updateTitle() {
		editorTab.updateTitle(getFile(), isDirty());
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

	/** Thrown when no Component with the {@code ID} exists */
	public static class MissingComponentException extends Exception {

		private static final long serialVersionUID = 1L;

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
