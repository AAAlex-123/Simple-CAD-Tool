package application;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.HashSet;
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
public class EditorManager<T extends Component & EditorInterface> {

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
				setActiveEditor(selectedEditor);
		});

		mainPanel.add(editorTabbedPane, BorderLayout.CENTER);

		editorSet = new HashSet<>();
	}

	/**
	 * Returns a JPanel with a graphical representation of this Manager.
	 * <p>
	 * It consists of a JTabbedPane in the centre, which displays information about
	 * each Editor's file as well as the graphical representation the of active
	 * Editor, and the StatusBar of the active Editor, located at the bottom.
	 *
	 * @return the JPanel
	 *
	 * @see EditorInterface#getFileInfo()
	 * @see EditorInterface#getStatusBar()
	 */
	public JPanel getGraphics() {
		return mainPanel;
	}

	/**
	 * Returns the {@code Editor} the user is currently working with, the selected
	 * {@code Editor} of the JTabbedPane.
	 *
	 * @return the selected Editor
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
	 */
	public void addEditor(T newEditor) {
		editorSet.add(newEditor);

		editorTabbedPane.addTab("", newEditor); //$NON-NLS-1$
		editorTabbedPane.setTabComponentAt(editorTabbedPane.getTabCount() - 1,
		        newEditor.getFileInfo().getGraphic());

		setActiveEditor(newEditor);
	}

	/**
	 * Attempts to {@code close} an {@code Editor}. If the operation succeeds, it is
	 * removed from this Manager.
	 *
	 * @param editor the Editor to close
	 *
	 * @throws MissingEditorException if the Editor given as an argument is not
	 *                                present in this Manager
	 *
	 * @see EditorInterface#close()
	 */
	public void removeEditor(T editor) {
		if (!editorSet.contains(editor))
			throw new MissingEditorException(editor, this);

		if (editor.close()) {
			editorSet.remove(editor);
			editorTabbedPane.remove(editor);
			mainPanel.remove(editor.getStatusBar());
		}
	}

	/** Calls {@link #removeEditor(Component)} for every {@code Editor} */
	public void removeAllEditors() {
		editorSet.forEach(this::removeEditor);
	}

	private void setActiveEditor(T editor) {
		editorTabbedPane.setSelectedComponent(editor);
		mainPanel.add(editor.getStatusBar(), BorderLayout.SOUTH);
		mainPanel.repaint();
	}
}
