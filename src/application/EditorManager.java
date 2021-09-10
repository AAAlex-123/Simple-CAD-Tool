package application;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;

/**
 * Manages and displays a collection of {@code Editors}, which must both extend
 * {@code java.awt.Component}, and implement {@code EditorInterface}, so that
 * they can be represented and function properly in the context of this Manager.
 * <p>
 * Details about how the {@code Editors} are displayed can be found in the
 * documentation of the {@link #getGraphics()} method.
 *
 * @param <T> the type of Editor this Manager manages
 *
 * @author Alex Mandelias
 *
 * @see EditorInterface
 */
class EditorManager<T extends Component & EditorInterface> {

	private final JPanel      mainPanel;
	private final JTabbedPane editorTabbedPane;
	private final Set<T>      editorSet;

	/** Constructs the EditorManager and its graphical components */
	public EditorManager() {
		mainPanel = new JPanel(new BorderLayout());
		editorTabbedPane = new JTabbedPane(SwingConstants.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
		editorTabbedPane.addChangeListener(l -> {
			final T selectedEditor = getSelectedEditor();
			if (selectedEditor != null)
				editorTabbedPane.setSelectedComponent(selectedEditor);
		});

		mainPanel.add(editorTabbedPane, BorderLayout.CENTER);

		editorSet = new LinkedHashSet<>();
	}

	/**
	 * Returns a JPanel with a graphical representation of this Manager.
	 * <p>
	 * It consists of a JTabbedPane, which displays information about each Editor's
	 * file as well as the graphical representation the of active {@code Editor}.
	 *
	 * @return the JPanel
	 *
	 * @see EditorInterface#getFileInfo()
	 */
	public JPanel getGraphics() {
		return mainPanel;
	}

	/**
	 * Returns the {@code Editor} the user is currently working with, the selected
	 * Editor of the JTabbedPane, or {@code null} if there are no open Editors.
	 *
	 * @return the selected Editor or {@code null}
	 */
	@SuppressWarnings("unchecked")
	public T getSelectedEditor() {
		// only addEditor(T) adds objects to the editorPane, so they must be of type T
		return (T) editorTabbedPane.getSelectedComponent();
	}

	/**
	 * Adds an {@code Editor} to this Manager.
	 *
	 * @param newEditor the Editor to add
	 *
	 * @throws NullPointerException if {@code newEditor == null}
	 */
	public void addEditor(T newEditor) {
		if (newEditor == null)
			throw new NullPointerException("newEditor is null"); //$NON-NLS-1$

		editorSet.add(newEditor);

		editorTabbedPane.addTab("", newEditor); //$NON-NLS-1$
		editorTabbedPane.setTabComponentAt(editorTabbedPane.getTabCount() - 1,
		        newEditor.getFileInfo().getGraphic());

		editorTabbedPane.setSelectedComponent(newEditor);
	}

	/**
	 * Attempts to {@code close} an {@code Editor}. If the operation succeeds, it is
	 * removed from this Manager.
	 *
	 * @param editor the Editor to close
	 *
	 * @throws NullPointerException   if {@code editor == null}
	 * @throws MissingEditorException if the Editor given as an argument is not
	 *                                present in this Manager
	 *
	 * @see EditorInterface#close()
	 */
	public void removeEditor(T editor) {
		if (editor == null)
			throw new NullPointerException("editor is null"); //$NON-NLS-1${

		if (!editorSet.contains(editor))
			throw new MissingEditorException(editor, this);

		editorTabbedPane.setSelectedComponent(editor);
		if (editor.close()) {
			editorSet.remove(editor);
			editorTabbedPane.remove(editor);
		}
	}

	/**
	 * Calls {@link #removeEditor(Component)} with the active {@code Editor}. This
	 * method does nothing if there are no open {@code Editors}.
	 */
	public void removeActiveEditor() {
		final T activeEditor = getSelectedEditor();
		if (editorSet != null)
			removeEditor(activeEditor);
	}

	/** Calls {@link #removeEditor(Component)} for every open {@code Editor} */
	public void removeAllEditors() {
		new LinkedHashSet<>(editorSet).forEach(this::removeEditor);
		editorSet.clear();
	}
}
