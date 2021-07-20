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
import components.ComponentType;
import localisation.Languages;
import myUtil.StringGenerator;
import myUtil.Utility;

/**
 * A class representing an Application. It aggregates all of the individual
 * components and handles the communications between them. The client has to
 * create an {@code Application} object then call its {@code run} method to
 * start the program.
 *
 * @author alexm
 */
public class Application {
	private final JFrame window;
	private final MyMenu menu;

	private final JTabbedPane     editorPane;
	private final List<Editor>    editors;
	private final StringGenerator nameGenerator;

	/** Constructs the Application */
	public Application() {
		window = new JFrame();
		menu = new MyMenu(this);
		editors = new ArrayList<>();
		editorPane = new JTabbedPane(SwingConstants.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
		nameGenerator = new StringGenerator(Languages.getString("Application.0")); //$NON-NLS-1$
	}

	/** Runs the application and configures the UI */
	public void run() {
		window.setLayout(new BorderLayout());
		window.add(editorPane, BorderLayout.CENTER);
		window.setJMenuBar(menu);
		window.setTitle(Languages.getString("Application.1")); //$NON-NLS-1$
		window.setSize(1000, 600);
		window.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		window.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				terminate();
			}
		});

		addEditor();

		editorPane.addChangeListener(l -> {
			final Editor e = (Editor) editorPane.getSelectedComponent();
			if (e != null)
				setActiveEditor(e);
		});

		addCreateCommand(Command.create(ComponentType.INPUT_PIN));
		addCreateCommand(Command.create(ComponentType.OUTPUT_PIN));
		addCreateCommand(Command.create(ComponentType.BRANCH));
		addCreateCommand(Command.create(ComponentType.GATEAND));
		addCreateCommand(Command.create(ComponentType.GATEOR));
		addCreateCommand(Command.create(ComponentType.GATENOT));
		addCreateCommand(Command.create(ComponentType.GATEXOR));

		window.setVisible(true);
	}

	/** Terminates the Application */
	public void terminate() {
		Utility.foreach(new ArrayList<>(editors), this::removeEditor);

		if (getActiveEditor() == null)
			window.dispose();
	}

	/**
	 * Returns the Application's frame
	 *
	 * @return the Application's frame
	 */
	public JFrame getFrame() {
		return window;
	}

	/**
	 * Returns the active Editor.
	 *
	 * @return the active Editor
	 */
	public Editor getActiveEditor() {
		return (Editor) editorPane.getSelectedComponent();
	}

	/**
	 * Sets the active Editor.
	 *
	 * @param e the active Editor
	 */
	public void setActiveEditor(Editor e) {
		editorPane.setSelectedComponent(e);
		window.add(e.getStatusBar(), BorderLayout.SOUTH);
		window.repaint();
	}

	private void addEditor() {
		final Editor newEditor = new Editor(this, nameGenerator.get());
		editors.add(newEditor);
		editorPane.addTab("", newEditor); //$NON-NLS-1$
		editorPane.setTabComponentAt(editorPane.getTabCount() - 1,
		        newEditor.getFileLabel());
		setActiveEditor(newEditor);
	}

	private void removeEditor(Editor e) {
		if (e.close()) {
			editors.remove(e);
			editorPane.remove(e);
		}
	}

	/**
	 * Adds a Command for creating Components to the Application.
	 *
	 * @param c the Command
	 */
	public void addCreateCommand(Command c) {
		menu.addCreateCommand(c);
	}

	/** An enum-strategy for the different Actions the Application may take */
	enum Actions {

		/** Action for creating an Editor */
		NEW {
			@Override
			void execute() {
				context.addEditor();
			}
		},

		/** Action for closing an Editor */
		CLOSE {
			@Override
			void execute() {
				context.removeEditor(context.getActiveEditor());
			}
		},

		/** Action for editing settings */
		EDIT_SETTINGS {
			@Override
			void execute() {
				final String file  = StringConstants.FILE;
				final Frame  frame = context.getFrame();

				try {
					final boolean settingsChanged = StringConstants.editAndWriteToFile(frame);
					if (settingsChanged)
						message(frame, file, true);
				} catch (final IOException e) {
					message(frame, file, false);
				}
			}
		},

		/** Action for editing settings */
		EDIT_LANGUAGE {
			@Override
			void execute() {
				final String file  = StringConstants.FILE;
				final Frame  frame = context.getFrame();

				try {
					final boolean languageChanged = Languages.editAndWriteToFile(frame);
					if (languageChanged)
						message(frame, file, true);
				} catch (final IOException e) {
					message(frame, file, false);
				}
			}
		};

		/**
		 * Displays a pop-up window informing the user about the success or failure of a
		 * save operation to a file.
		 *
		 * @param frame   the parent frame for the pop-up
		 * @param file    the file where the settings were saved
		 * @param success {@code true} if the operation was successful, {@code false}
		 *                otherwise
		 */
		protected void message(Frame frame, String file, boolean success) {
			if (success) {
				JOptionPane.showMessageDialog(frame, String.format(
				        Languages.getString("Application.3"), file), //$NON-NLS-1$
				        Languages.getString("Application.4"), //$NON-NLS-1$
				        JOptionPane.INFORMATION_MESSAGE);
			} else {
				JOptionPane.showMessageDialog(frame, String.format(
				        Languages.getString("Application.5"), file), //$NON-NLS-1$
				        Languages.getString("Application.6"), //$NON-NLS-1$
				        JOptionPane.ERROR_MESSAGE);
			}
		}

		/** The context of the Action */
		Application context;

		/** Executes the Action */
		abstract void execute();

		/**
		 * Specifies the Action's context.
		 *
		 * @param app the context
		 *
		 * @return this (used for chaining)
		 */
		final Actions context(Application app) {
			context = app;
			return this;
		}
	}
}
