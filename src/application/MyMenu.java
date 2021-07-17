package application;

import static localisation.CommandStrings.CREATE_STR;
import static localisation.CommandStrings.DELETE_STR;
import static localisation.CommandStrings.ID;

import java.awt.event.ActionEvent;
import java.util.function.Supplier;

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
import application.editor.EditorStrings;
import application.editor.MissingComponentException;
import command.Command;
import components.Component;
import components.ComponentFactory;
import exceptions.InvalidComponentException;
import localisation.Languages;
import myUtil.StringGenerator;
import requirement.Requirements;
import requirement.StringType;

/**
 * Menu bar for the Application. This class also handles the mnemonics and
 * accelerators with which the user interacts with the menu. The Actions for the
 * listeners of each menu item are also defined in this class and they are the
 * ones that call the Actions of the Editor and the Application.
 *
 * @author alexm
 */
final class MyMenu extends JMenuBar {

	private final Application context;

	private final JMenu     m_file, m_edit, m_create, m_delete, m_preferences, m_help;
	private final JMenuItem f_new, f_close, f_save, f_save_as, f_open, f_clear, f_import, f_undo,
	        f_redo, e_activate, e_focus, d_component, p_settings, h_help;

	private final Action a_new, a_close, a_undo, a_redo, a_save, a_save_as, a_open, a_clear,
	        a_import, a_delete, a_edit, a_help;

	private final Supplier<String> builtin_command_gen, custom_command_gen;

	/**
	 * Constructs the Menu with the given {@code Application}.
	 *
	 * @param application the context of this Menu
	 */
	MyMenu(Application application) {

		context = application;
		builtin_command_gen = new StringGenerator(
		        String.format("%s %%d", StringConstants.BUILTIN_COMMAND_ACCEL_PREFIX), 1, 10); //$NON-NLS-1$

		custom_command_gen = new StringGenerator(
		        String.format("%s %%d", StringConstants.USER_COMMAND_ACCEL_PREFIX), 1, 10); //$NON-NLS-1$

		// block of actions (they need context, therefore they must be placed inside the constructor)
		{
			// Application Actions
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

			a_edit = new AbstractAction() {
				@Override
				public void actionPerformed(ActionEvent e) {
					Application.Actions.EDIT_SETTINGS.context(context).execute();
				}
			};

			// Editor Actions
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

			a_save = new AbstractAction() {
				@Override
				public void actionPerformed(ActionEvent e) {
					final Editor activeEditor = context.getActiveEditor();
					Actions.SAVE.specify(EditorStrings.FILENAME, activeEditor.getFile())
					        .context(activeEditor)
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
					Actions.OPEN.specify(EditorStrings.GATENAME, EditorStrings.NA)
					        .specify(EditorStrings.FILETYPE, EditorStrings.CIRCUIT)
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
					Actions.OPEN.specify(EditorStrings.FILETYPE, EditorStrings.COMPONENT)
					        .specifyWithDialog(context.getActiveEditor())
					        .context(context.getActiveEditor()).execute();
				}
			};

			a_delete = new AbstractAction() {
				@Override
				public void actionPerformed(ActionEvent e) {
					final Editor  activeEditor = context.getActiveEditor();
					final Command c            = Command.delete();
					c.fillRequirements(application.getFrame(), activeEditor);
					Actions.DELETE.specify(EditorStrings.COMMAND, c).context(activeEditor)
					        .execute();
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
		m_file = new JMenu(Languages.getString("MyMenu.10")); //$NON-NLS-1$
		f_new = new JMenuItem(Languages.getString("MyMenu.11")); //$NON-NLS-1$
		f_close = new JMenuItem(Languages.getString("MyMenu.12")); //$NON-NLS-1$
		f_open = new JMenuItem(Languages.getString("MyMenu.13")); //$NON-NLS-1$
		f_save = new JMenuItem(Languages.getString("MyMenu.14")); //$NON-NLS-1$
		f_save_as = new JMenuItem(Languages.getString("MyMenu.15")); //$NON-NLS-1$
		f_clear = new JMenuItem(Languages.getString("MyMenu.16")); //$NON-NLS-1$
		f_import = new JMenuItem(Languages.getString("MyMenu.17")); //$NON-NLS-1$
		f_undo = new JMenuItem(Languages.getString("MyMenu.18")); //$NON-NLS-1$
		f_redo = new JMenuItem(Languages.getString("MyMenu.19")); //$NON-NLS-1$

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
		m_edit = new JMenu(Languages.getString("MyMenu.20")); //$NON-NLS-1$
		e_activate = new JMenuItem(Languages.getString("MyMenu.21")); //$NON-NLS-1$
		e_focus = new JMenuItem(Languages.getString("MyMenu.22")); //$NON-NLS-1$
		m_edit.add(e_activate);
		m_edit.add(e_focus);
		add(m_edit);

		// --- create ---
		m_create = new JMenu(Languages.getString("MyMenu.23")); //$NON-NLS-1$
		add(m_create);

		// --- delete ---
		m_delete = new JMenu(Languages.getString("MyMenu.24")); //$NON-NLS-1$
		d_component = new JMenuItem(Languages.getString("MyMenu.25")); //$NON-NLS-1$
		m_delete.add(d_component);
		add(m_delete);

		// --- preferences ---
		m_preferences = new JMenu(Languages.getString("MyMenu.26")); //$NON-NLS-1$
		p_settings = new JMenuItem(Languages.getString("MyMenu.27")); //$NON-NLS-1$
		m_preferences.add(p_settings);
		add(m_preferences);

		// --- help ---
		m_help = new JMenu(Languages.getString("MyMenu.28")); //$NON-NLS-1$
		h_help = new JMenuItem(Languages.getString("MyMenu.29")); //$NON-NLS-1$
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
		if (c.toString().matches(String.format("^(?:%s|%s).*", CREATE_STR, DELETE_STR))) {
			jmic.setText(c.toString().substring(7));
			MyMenu.setAccel(jmic, builtin_command_gen.get());
		} else {
			jmic.setText(c.toString());
			MyMenu.setAccel(jmic, custom_command_gen.get());
		}

		jmic.addActionListener(e -> {
			final Command cloned = c.clone();
			cloned.fillRequirements(context.getFrame(), context.getActiveEditor());
			Actions.CREATE.specify(EditorStrings.COMMAND, cloned).context(context.getActiveEditor())
			        .execute();
		});

		m_create.add(jmic);
	}

	private void mnemonics() {
		m_file.setMnemonic(StringConstants.M_FILE_MNEMONIC);
		m_edit.setMnemonic(StringConstants.M_EDIT_MNEMONIC);
		m_create.setMnemonic(StringConstants.M_CREATE_MNEMONIC);
		m_delete.setMnemonic(StringConstants.M_DELETE_MNEMONIC);
		m_preferences.setMnemonic(StringConstants.M_PREFERENCES_MNEMONIC);
		m_help.setMnemonic(StringConstants.M_HELP_MNEMONIC);
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
		p_settings.addActionListener(a_edit);
		h_help.addActionListener(a_help);
	}

	private void editMenuListeners() {
		e_activate.addActionListener(e -> {
			final Editor activeEditor = context.getActiveEditor();

			final Requirements<String> reqs = new Requirements<>();
			reqs.add(ID, StringType.ANY);
			reqs.add("active", StringType.ON_OFF); //$NON-NLS-1$ TODO: externalise
			reqs.fulfillWithDialog(context.getFrame(), Languages.getString("MyMenu.34")); //$NON-NLS-1$

			if (reqs.fulfilled()) {
				final String id = reqs.getV(ID);
				final Component comp;
				try {
					comp = context.getActiveEditor().getComponent_(id);
				} catch (final MissingComponentException e1) {
					activeEditor.error(e1);
					return;
				}

				final boolean active = reqs.getV("active").equals("on"); //$NON-NLS-1$ //$NON-NLS-2$ TODO: externalise
				try {
					ComponentFactory.setActive(comp, active);
				} catch (final InvalidComponentException e1) {
					activeEditor.error(e1);
					return;
				}
				activeEditor.status(Languages.getString("MyMenu.38")); //$NON-NLS-1$
			} else {
				activeEditor.status(Languages.getString("MyMenu.39")); //$NON-NLS-1$
			}
		});

		e_focus.addActionListener(e -> {
			final Editor activeEditor = context.getActiveEditor();

			final Requirements<String> reqs = new Requirements<>();
			reqs.add(ID, StringType.ANY);
			reqs.fulfillWithDialog(context.getFrame(), Languages.getString("MyMenu.41")); //$NON-NLS-1$

			if (reqs.fulfilled()) {
				final String id = reqs.getV(ID);
				Component    comp;
				try {
					comp = activeEditor.getComponent_(id);
				} catch (final MissingComponentException e1) {
					activeEditor.error(e1);
					return;
				}
				comp.getGraphics().requestFocus();
				activeEditor.status(Languages.getString("MyMenu.43")); //$NON-NLS-1$

			} else {
				activeEditor.status(Languages.getString("MyMenu.44")); //$NON-NLS-1$
			}
		});
	}

	private void accelerators() {
		MyMenu.setAccel(f_new, StringConstants.F_NEW_ACCEL);
		MyMenu.setAccel(f_close, StringConstants.F_CLOSE_ACCEL);
		MyMenu.setAccel(f_save, StringConstants.F_SAVE_ACCEL);
		MyMenu.setAccel(f_save_as, StringConstants.F_SAVE_AS_ACCEL);
		MyMenu.setAccel(f_open, StringConstants.F_OPEN_ACCEL);
		MyMenu.setAccel(f_clear, StringConstants.F_CLEAR_ACCEL);
		MyMenu.setAccel(f_import, StringConstants.F_IMPORT_ACCEL);
		MyMenu.setAccel(f_undo, StringConstants.F_UNDO_ACCEL);
		MyMenu.setAccel(f_redo, StringConstants.F_REDO_ACCEL);
		MyMenu.setAccel(e_activate, StringConstants.E_ACTIVATE_ACCEL);
		MyMenu.setAccel(e_focus, StringConstants.E_FOCUS_ACCEL);
		MyMenu.setAccel(d_component, StringConstants.D_COMPONENT_ACCEL);
		MyMenu.setAccel(p_settings, StringConstants.P_SETTINGS_ACCEL);
		MyMenu.setAccel(h_help, StringConstants.H_HELP_ACCEL);
	}

	private void icons() {
		MyMenu.setIcon(m_file, "file"); //$NON-NLS-1$
		MyMenu.setIcon(f_new, "new"); //$NON-NLS-1$
		MyMenu.setIcon(f_close, "close"); //$NON-NLS-1$
		MyMenu.setIcon(f_save, "save"); //$NON-NLS-1$
		MyMenu.setIcon(f_save_as, "save_as"); //$NON-NLS-1$
		MyMenu.setIcon(f_open, "open"); //$NON-NLS-1$
		MyMenu.setIcon(f_clear, "clear"); //$NON-NLS-1$
		MyMenu.setIcon(f_undo, "undo"); //$NON-NLS-1$
		MyMenu.setIcon(f_redo, "redo"); //$NON-NLS-1$
		MyMenu.setIcon(f_import, "import"); //$NON-NLS-1$
		MyMenu.setIcon(m_edit, "edit"); //$NON-NLS-1$
		MyMenu.setIcon(e_activate, "activate"); //$NON-NLS-1$
		MyMenu.setIcon(e_focus, "focus"); //$NON-NLS-1$
		MyMenu.setIcon(m_create, "create"); //$NON-NLS-1$
		MyMenu.setIcon(m_delete, "delete"); //$NON-NLS-1$
		MyMenu.setIcon(m_preferences, "preferences"); //$NON-NLS-1$
		MyMenu.setIcon(p_settings, "settings"); //$NON-NLS-1$
		MyMenu.setIcon(m_help, "help"); //$NON-NLS-1$
	}

	private static void setAccel(JMenuItem jmi, String s) {
		jmi.setAccelerator(KeyStroke.getKeyStroke(s));
	}

	private static void setIcon(JMenuItem jmi, String desc) {
		final String filename    = String.format("%s%s_icon.png", StringConstants.MENU_ICON_PATH,              //$NON-NLS-1$
		        desc);
		final String description = String.format("%s icon", desc);                                             //$NON-NLS-1$
		jmi.setIcon(new ImageIcon(filename, description));
	}
}
