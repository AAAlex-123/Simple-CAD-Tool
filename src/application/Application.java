package application;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

import application.editor.Editor;
import command.Command;
import components.ComponentType;

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

	private final JTabbedPane  editorPane;
	private final List<Editor> editors;
	private Editor             activeEditor;
	private int                unsavedEditors = 0;

	/** Constructs the Application */
	public Application() {
		window = new JFrame();
		menu = new MyMenu(this);
		editors = new Vector<>();
		editorPane = new JTabbedPane(SwingConstants.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
	}

	/** Runs the application and configures the UI */
	public void run() {

		window.setLayout(new BorderLayout());
		window.setJMenuBar(menu);
		window.setTitle("Simple CAD Tool");
		window.setSize(1000, 600);
		window.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

		window.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				terminate();
			}
		});

		window.add(editorPane, BorderLayout.CENTER);
		addEditor();

		editorPane.addChangeListener((l) -> {
			Editor e = (Editor) editorPane.getSelectedComponent();
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

	/** Terminates the application */
	public void terminate() {
		for (Editor e : new ArrayList<>(editors))
			removeEditor(e);

		window.dispose();
	}

	/**
	 * Returns the application's frame
	 *
	 * @return the application's frame
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
		return activeEditor;
	}

	/**
	 * Sets the active Editor.
	 *
	 * @param e the active Editor
	 */
	public void setActiveEditor(Editor e) {
		activeEditor = e;
		window.add(e.getStatusBar(), BorderLayout.SOUTH);
		editorPane.setSelectedComponent(e);
	}

	private void addEditor() {
		Editor newEditor = new Editor(this, "new-" + unsavedEditors++);
		editors.add(newEditor);
		editorPane.addTab("", newEditor);
		editorPane.setTabComponentAt(editorPane.getTabCount() - 1,
				newEditor.getEditorTab());
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
		};

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
			this.context = app;
			return this;
		}
	}
}
