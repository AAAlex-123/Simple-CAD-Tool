package application;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu.Separator;
import javax.swing.KeyStroke;

import application.editor.Actions;
import application.editor.Editor;
import command.Command;
import components.Component;
import components.ComponentFactory;
import exceptions.InvalidComponentException;
import requirement.Requirements;
import requirement.StringType;

/**
 * Menu bar for the Application.
 *
 * @author alexm
 */
final class MyMenu extends JMenuBar {

	private final Application context;

	private final JMenu     m_file, m_edit, m_create, m_delete, m_help;
	private final JMenuItem f_new, f_close, f_save, f_save_as, f_open, f_clear, f_import, f_undo,
	        f_redo, e_activate, e_focus, d_component, h_help;

	private final Action a_new, a_close, a_undo, a_redo, a_save, a_save_as, a_open, a_clear,
	        a_import, a_delete, a_help;

	private int commandCounter = 1, customCommandCounter = 1;

	/**
	 * Constructs the Menu with the given {@code Application}.
	 *
	 * @param application the context of this Menu
	 */
	MyMenu(Application application) {

		context = application;

		// block of actions
		{
			a_undo = new AbstractAction() {
				@Override
				public void actionPerformed(ActionEvent e) {
					Actions.UNDO.context(context.getActiveEditor()).execute();
				}
			};

			a_redo = new AbstractAction() {
				@Override
				public void actionPerformed(ActionEvent e) {
					Actions.REDO.context(context.getActiveEditor()).execute();
				}
			};

			a_new = new AbstractAction() {
				@Override
				public void actionPerformed(ActionEvent e) {
					Application.Actions.NEW.context(context).execute();
				}
			};

			a_close = new AbstractAction() {
				@Override
				public void actionPerformed(ActionEvent e) {
					Application.Actions.CLOSE.context(context).execute();
				}
			};

			a_save = new AbstractAction() {
				@Override
				public void actionPerformed(ActionEvent e) {
					final Editor activeEditor = context.getActiveEditor();
					Actions.SAVE.specify("filename", activeEditor.getFile()).context(activeEditor)
					        .execute();
				}
			};

			a_save_as = new AbstractAction() {
				@Override
				public void actionPerformed(ActionEvent e) {
					Actions.SAVE.specifyWithDialog(context.getActiveEditor())
					        .context(context.getActiveEditor()).execute();
				}
			};

			a_open = new AbstractAction() {
				@Override
				public void actionPerformed(ActionEvent e) {
					Actions.OPEN.specify("gatename", "N/A").specify("filetype", "circuit")
					        .specifyWithDialog(context.getActiveEditor())
					        .context(context.getActiveEditor()).execute();
				}
			};

			a_clear = new AbstractAction() {
				@Override
				public void actionPerformed(ActionEvent e) {
					Actions.CLEAR.context(context.getActiveEditor()).execute();
				}
			};

			a_import = new AbstractAction() {
				@Override
				public void actionPerformed(ActionEvent e) {
					Actions.OPEN.specify("filetype", "component")
					        .specifyWithDialog(context.getActiveEditor())
					        .context(context.getActiveEditor()).execute();
				}
			};

			a_delete = new AbstractAction() {
				@Override
				public void actionPerformed(ActionEvent e) {
					final Editor activeEditor = context.getActiveEditor();
					final Command c = Command.delete();
					c.fillRequirements(application.getFrame(), activeEditor);
					Actions.DELETE.specify("command", c).context(activeEditor).execute();
				}
			};

			a_help = new AbstractAction() {
				@Override
				public void actionPerformed(ActionEvent e) {
					Actions.HELP.context(context.getActiveEditor()).execute();
				}
			};
		}

		// --- file ---
		m_file = new JMenu("File");
		f_new = new JMenuItem("New");
		f_close = new JMenuItem("Close");
		f_open = new JMenuItem("Open");
		f_save = new JMenuItem("Save");
		f_save_as = new JMenuItem("Save as");
		f_clear = new JMenuItem("Clear");
		f_import = new JMenuItem("Import");
		f_undo = new JMenuItem("Undo");
		f_redo = new JMenuItem("Redo");

		m_file.add(f_new);
		m_file.add(f_close);
		m_file.add(new Separator());
		m_file.add(f_open);
		m_file.add(f_save);
		m_file.add(f_save_as);
		m_file.add(f_clear);
		m_file.add(new Separator());
		m_file.add(f_import);
		m_file.add(new Separator());
		m_file.add(f_undo);
		m_file.add(f_redo);

		add(m_file);

		// --- edit ---
		m_edit = new JMenu("Edit");
		e_activate = new JMenuItem("Turn on/off");
		e_focus = new JMenuItem("Focus");
		m_edit.add(e_activate);
		m_edit.add(e_focus);
		add(m_edit);

		// --- create ---
		m_create = new JMenu("Create");
		add(m_create);

		// --- delete ---
		m_delete = new JMenu("Delete");
		d_component = new JMenuItem("Component");
		m_delete.add(d_component);
		add(m_delete);

		// --- help ---
		m_help = new JMenu("Help");
		h_help = new JMenuItem("Help I can't use this application :(");
		m_help.add(h_help);
		add(m_help);

		// make menus usable
		mnemonics();
		listeners();
		editMenuListeners();
		accelerators();
		icons();
	}

	/**
	 * Adds a {@code Command} that creates a {@code Component} to the Menu.
	 *
	 * @param c the Command
	 */
	void addCreateCommand(Command c) {
		final JMenuItem jmic = new JMenuItem();

		// different text and accelerator depending on command type (build-in vs user-created)
		if (c.toString().matches("^(?:Create|Delete).*")) {
			jmic.setText(c.toString().substring(7));
			MyMenu.setAccel(jmic, "control " + commandCounter++);
		} else {
			jmic.setText(c.toString());
			MyMenu.setAccel(jmic, "control shift " + customCommandCounter++);
		}

		jmic.addActionListener(e -> {
			final Command cloned = c.clone();
			cloned.fillRequirements(context.getFrame(), context.getActiveEditor());
			Actions.CREATE.specify("command", cloned).context(context.getActiveEditor()).execute();
		});

		m_create.add(jmic);
	}

	private void mnemonics() {
		m_file.setMnemonic('f');
		m_edit.setMnemonic('e');
		m_create.setMnemonic('c');
		m_delete.setMnemonic('d');
		m_help.setMnemonic('h');
	}

	private void listeners() {
		f_new.addActionListener(a_new);
		f_open.addActionListener(a_open);
		f_close.addActionListener(a_close);
		f_save.addActionListener(a_save);
		f_save_as.addActionListener(a_save_as);
		f_clear.addActionListener(a_clear);
		f_import.addActionListener(a_import);
		f_undo.addActionListener(a_undo);
		f_redo.addActionListener(a_redo);
		d_component.addActionListener(a_delete);
		h_help.addActionListener(a_help);
	}

	private void editMenuListeners() {
		e_activate.addActionListener(e -> {
			final Editor activeEditor = context.getActiveEditor();

			final Requirements<String> reqs = new Requirements<>();
			reqs.add("id", StringType.NON_NEG_INTEGER);
			reqs.add("active", StringType.ON_OFF);
			reqs.fulfillWithDialog(context.getFrame(), "Turn Input Pin on/off");

			if (reqs.fulfilled()) {
				final int id = Integer.parseInt(reqs.getV("id"));
				Component comp;
				try {
					comp = context.getActiveEditor().getComponent_(id);
				} catch (final Editor.MissingComponentException e1) {
					activeEditor.error(e1);
					return;
				}

				final boolean active = reqs.getV("active").equals("on");
				try {
					ComponentFactory.setActive(comp, active);
				} catch (final InvalidComponentException e1) {
					activeEditor.error(e1);
					return;
				}
				activeEditor.status("Activated Input Pin");
			} else {
				activeEditor.status("Activate Input Pin cancelled");
			}
		});

		e_focus.addActionListener(e -> {
			final Editor activeEditor = context.getActiveEditor();

			final Requirements<String> reqs = new Requirements<>();
			reqs.add("id", StringType.NON_NEG_INTEGER);
			reqs.fulfillWithDialog(context.getFrame(), "Focus Component");

			if (reqs.fulfilled()) {
				final int id = Integer.parseInt(reqs.getV("id"));
				Component comp;
				try {
					comp = activeEditor.getComponent_(id);
				} catch (final Editor.MissingComponentException e1) {
					activeEditor.error(e1);
					return;
				}
				comp.requestFocus();
				activeEditor.status("Focusing Component");

			} else {
				activeEditor.status("Focus Component cancelled");
			}
		});
	}

	private void accelerators() {
		MyMenu.setAccel(f_new, "control N");
		MyMenu.setAccel(f_close, "control W");
		MyMenu.setAccel(f_save, "control S");
		MyMenu.setAccel(f_save_as, "control shift S");
		MyMenu.setAccel(f_open, "control O");
		MyMenu.setAccel(f_clear, "control shift C");
		MyMenu.setAccel(f_import, "control I");
		MyMenu.setAccel(f_undo, "control Z");
		MyMenu.setAccel(f_redo, "control Y");
		MyMenu.setAccel(e_activate, "shift A");
		MyMenu.setAccel(e_focus, "shift M");
		MyMenu.setAccel(d_component, "control D");
		MyMenu.setAccel(h_help, "F1");
	}

	private void icons() {
		MyMenu.setIcon(m_file, "file");
		MyMenu.setIcon(f_new, "new");
		MyMenu.setIcon(f_close, "close");
		MyMenu.setIcon(f_save, "save");
		MyMenu.setIcon(f_save_as, "save_as");
		MyMenu.setIcon(f_open, "open");
		MyMenu.setIcon(f_clear, "clear");
		MyMenu.setIcon(f_undo, "undo");
		MyMenu.setIcon(f_redo, "redo");
		MyMenu.setIcon(f_import, "import");
		MyMenu.setIcon(m_edit, "edit");
		MyMenu.setIcon(e_activate, "activate");
		MyMenu.setIcon(e_focus, "focus");
		MyMenu.setIcon(m_create, "create");
		MyMenu.setIcon(m_delete, "delete");
		MyMenu.setIcon(m_help, "help");
	}

	private static void setAccel(JMenuItem jmi, String s) {
		jmi.setAccelerator(KeyStroke.getKeyStroke(s));
	}

	private static void setIcon(JMenuItem jmi, String desc) {
		final String filename = String.format("%s%s_icon.png", StringConstants.menu_icon_path,
		        desc);
		final String description = String.format("%s icon", desc);
		jmi.setIcon(new ImageIcon(filename, description));
	}
}
