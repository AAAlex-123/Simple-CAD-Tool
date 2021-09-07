package application;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

import application.editor.Editor;
import command.Command;
import component.ComponentType;
import localisation.Languages;
import myUtil.StringGenerator;
import myUtil.Utility;

/**
 * This class aggregates all of the individual components that comprise an
 * Application and handles the communications between them. The client creates
 * an {@code Application} then call its {@link #run()} method to start it.
 *
 * @author Alex Mandelias
 */
public final class Application {

	private final JFrame window;
	private final MyMenu menuBar;

	private final JTabbedPane     editorPane;
	private final List<Editor>    editorList;
	private final StringGenerator editorNameGenerator;

	/** Constructs the Application */
	public Application() {
		window = new JFrame();
		menuBar = new MyMenu(this);
		editorList = new ArrayList<>();
		editorPane = new JTabbedPane(SwingConstants.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
		editorNameGenerator = new StringGenerator(Languages.getString("Application.0")); //$NON-NLS-1$
	}

	/** Configures the UI and launches the Application */
	public void run() {

		// configure the frame
		window.setLayout(new BorderLayout());
		window.setTitle(Languages.getString("Application.1")); //$NON-NLS-1$
		window.setSize(1000, 600);
		window.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		window.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				terminate();
			}
		});

		// add components to the frame
		window.setJMenuBar(menuBar);
		window.add(editorPane, BorderLayout.CENTER);

		// add the first editor on start-up
		addEditor();

		// configure the editor pane
		editorPane.addChangeListener(l -> {
			final Editor e = (Editor) editorPane.getSelectedComponent();
			if (e != null)
				setActiveEditor(e);
		});

		// add all of the Create Commands
		for (final ComponentType type : ComponentType.values())
			if (type != ComponentType.GATE)
				addCreateCommand(Command.create(type));

		window.setVisible(true);
	}

	/**
	 * Returns the Application's {@code Frame}, the window that is displayed on the
	 * user's screen.
	 *
	 * @return the Application's Frame
	 */
	public JFrame getFrame() {
		return window;
	}

	/**
	 * Adds a {@code Command} for creating {@code Components} to the Application.
	 *
	 * @param command the Command
	 */
	public void addCreateCommand(Command command) {
		menuBar.addCreateCommand(command);
	}

	private void terminate() {
		Utility.foreach(new ArrayList<>(editorList), this::removeEditor);

		// dispose window if and only if all Editors are closed
		if (getActiveEditor() == null)
			window.dispose();
	}

	/**
	 * Returns the Application's active {@code Editor}, the Editor the user is
	 * currently working with or {@code null} if there aren't any open Editors.
	 *
	 * @return the active Editor
	 */
	Editor getActiveEditor() {
		return (Editor) editorPane.getSelectedComponent();
	}

	private void addEditor() {
		final Editor newEditor = new Editor(this, editorNameGenerator.get());

		editorList.add(newEditor);
		editorPane.addTab("", newEditor); //$NON-NLS-1$
		editorPane.setTabComponentAt(editorPane.getTabCount() - 1,
		        newEditor.getFileLabel());

		setActiveEditor(newEditor);
	}

	private void removeEditor(Editor e) {
		if (e.close()) {
			editorList.remove(e);
			editorPane.remove(e);
		}
	}

	private void setActiveEditor(Editor editor) {
		editorPane.setSelectedComponent(editor);
		window.add(editor.getStatusBar(), BorderLayout.SOUTH);
		window.repaint();
	}

	/**
	 * An enum-strategy for the different Actions the Application may take. Actions
	 * have {@code context} that must be set using the {@link #context(Application)}
	 * method before the Action can be executed.
	 *
	 * @author Alex Mandelias
	 */
	enum Actions {

		/** Action for creating an {@code Editor} */
		NEW {
			@Override
			protected void execute() {
				context.addEditor();
				context = null;
			}
		},

		/** Action for closing an {@code Editor} */
		CLOSE {
			@Override
			protected void execute() {
				context.removeEditor(context.getActiveEditor());
				context = null;
			}
		},

		/** Action for editing non-language Settings */
		EDIT_SETTINGS {
			@Override
			protected void execute() {
				final Frame  frame = context.getFrame();
				final String file  = StringConstants.FILE;

				try {
					final boolean settingsChanged = StringConstants.editAndWriteToFile(frame);
					if (settingsChanged)
						Actions.message(frame, file, true);
				} catch (final IOException e) {
					Actions.message(frame, file, false);
				}
				context = null;
			}
		},

		/** Action for editing language-related Settings */
		EDIT_LANGUAGE {
			@Override
			protected void execute() {
				final Frame  frame = context.getFrame();
				final String file  = Languages.FILE;

				try {
					final boolean languageChanged = Languages.editAndWriteToFile(frame);
					if (languageChanged)
						Actions.message(frame, file, true);
				} catch (final IOException e) {
					Actions.message(frame, file, false);
				}
				context = null;
			}
		};

		/** The context of the Action, the Application whose state it changes */
		protected Application context;

		/**
		 * Executes the Action and clears its context.
		 *
		 * @throws NullPointerException if its {@code context} has not been set prior to
		 *                              execution
		 *
		 * @see #context
		 */
		protected abstract void execute();

		/**
		 * Specifies the Action's context.
		 *
		 * @param application the context
		 *
		 * @return this (used for chaining)
		 *
		 * @see #context
		 */
		protected final Actions context(Application application) {
			context = application;
			return this;
		}

		/**
		 * Displays a pop-up window informing the user about the success or failure of a
		 * save operation to a file.
		 *
		 * @param frame   the parent frame for the pop-up
		 * @param file    the file where the settings were saved
		 * @param success {@code true} if the operation was successful, {@code false}
		 *                otherwise
		 */
		private static void message(Frame frame, String file, boolean success) {
			if (success)
				JOptionPane.showMessageDialog(frame, String.format(
				        Languages.getString("Application.3"), file), //$NON-NLS-1$
				        Languages.getString("Application.4"), //$NON-NLS-1$
				        JOptionPane.INFORMATION_MESSAGE);
			else
				JOptionPane.showMessageDialog(frame, String.format(
				        Languages.getString("Application.5"), file), //$NON-NLS-1$
				        Languages.getString("Application.6"), //$NON-NLS-1$
				        JOptionPane.ERROR_MESSAGE);
		}
	}
}
