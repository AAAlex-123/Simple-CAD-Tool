package application;

import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import component.components.Component;
import component.components.ComponentFactory;
import component.exceptions.InvalidComponentException;
import localisation.CommandStrings;
import localisation.EditorStrings;
import localisation.Languages;
import localisation.RequirementStrings;
import myUtil.StringGenerator;
import requirement.requirements.ComponentRequirement;
import requirement.requirements.ComponentRequirement.Policy;
import requirement.util.Requirements;

/**
 * Menu bar for an {@code Application}. This class also handles the mnemonics
 * and accelerators with which the user interacts with the menu. The Actions for
 * the listeners of each menu item are also defined in this class and they call
 * the Actions of the {@code Editor} and the {@code Application}.
 *
 * @author Alex Mandelias
 *
 * @see Application
 * @see Application.Actions
 * @see Editor
 * @see Actions
 */
final class MyMenu extends JMenuBar {

	private final Application context;

	// prefixes:
	// m : top-level menu
	// f/e/d/p/h: file / edit / delete / preferences / help sub-menu
	// a: action

	private final JMenu     m_file, m_edit, m_create, m_delete, m_preferences, m_help;
	private final JMenuItem f_new, f_close, f_save, f_save_as, f_open, f_clear, f_import,
	        f_undo, f_redo, e_activate, e_focus, d_component, p_settings, p_language, h_help;

	private final Supplier<String> builtin_command_gen, custom_command_gen;

	private final Action a_new, a_close, a_undo, a_redo, a_save, a_save_as, a_open,
	        a_clear, a_import, a_delete, a_settings, a_language, a_help;

	{
		builtin_command_gen = new StringGenerator(
		        String.format("%s %%d", StringConstants.BUILTIN_COMMAND_ACCEL_PREFIX), 1, 9); //$NON-NLS-1$

		custom_command_gen = new StringGenerator(
		        String.format("%s %%d", StringConstants.USER_COMMAND_ACCEL_PREFIX), 1, 9); //$NON-NLS-1$

		// --- Application Actions ---

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

		a_settings = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Application.Actions.EDIT_SETTINGS.context(context).execute();
			}
		};

		a_language = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Application.Actions.EDIT_LANGUAGE.context(context).execute();
			}
		};

		// --- Editor Actions ---

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
				Actions.SAVE
				        .specify(EditorStrings.FILENAME, activeEditor.getFileInfo().getFile())
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
				final Command command      = Command.delete();
				command.fillRequirements(context.getFrame(), activeEditor);
				Actions.DELETE.specify(EditorStrings.COMMAND, command).context(activeEditor)
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

	/**
	 * Constructs a Menu.
	 *
	 * @param application the context of this Menu
	 */
	MyMenu(Application application) {

		context = application;

		// Construct Menus

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

		// --- create --- (populated by the addCreateCommand(Command) method)
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
		p_language = new JMenuItem(Languages.getString("MyMenu.1")); //$NON-NLS-1$
		m_preferences.add(p_settings);
		m_preferences.add(p_language);
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
	 * @param command the Command to add
	 */
	void addCreateCommand(Command command) {

		final JMenuItem jmi = new JMenuItem();

		// different text and accelerator depending on command type (build-in vs user-created)
		final String  patternString = String.format("^(%s|%s).*", CommandStrings.CREATE_STR,              //$NON-NLS-1$
		        CommandStrings.DELETE_STR);
		final Pattern pattern       = Pattern.compile(patternString);
		final String  description   = command.description();
		final Matcher matcher       = pattern.matcher(description);

		if (matcher.find()) {
			jmi.setText(description.substring(matcher.group(1).length() + 1));
			MyMenu.setAccel(jmi, builtin_command_gen.get());
		} else {
			jmi.setText(description);
			MyMenu.setAccel(jmi, custom_command_gen.get());
		}

		jmi.addActionListener(e -> {
			final Command cloned = command.clone();
			cloned.fillRequirements(context.getFrame(), context.getActiveEditor());
			Actions.CREATE.specify(EditorStrings.COMMAND, cloned).context(context.getActiveEditor())
			        .execute();
		});

		m_create.add(jmi);
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
		p_settings.addActionListener(a_settings);
		p_language.addActionListener(a_language);
		h_help.addActionListener(a_help);
	}

	private void editMenuListeners() {
		e_activate.addActionListener(e -> {
			final Editor activeEditor = context.getActiveEditor();

			final String ACTIVE = Languages.getString("MyMenu.0"); //$NON-NLS-1$

			final Requirements         reqs = new Requirements();
			final ComponentRequirement req  = new ComponentRequirement(CommandStrings.NAME,
			        activeEditor.getComponents_(), Policy.INPUT_PIN);
			req.setCaseOfNullGraphic(false, Languages.getString("MyMenu.2")); //$NON-NLS-1$
			reqs.add(req);
			reqs.add(ACTIVE, Arrays.asList(RequirementStrings.ON, RequirementStrings.OFF));
			reqs.fulfillWithDialog(context.getFrame(), Languages.getString("MyMenu.34")); //$NON-NLS-1$

			if (reqs.fulfilled()) {
				final String id = reqs.getValue(CommandStrings.NAME, String.class);
				// because of the ComponentRequirement this component for sure exists
				final Component component = activeEditor.getComponentOrNull(id);

				final boolean active = reqs.getValue(ACTIVE).equals(RequirementStrings.ON);
				try {
					ComponentFactory.setActive(component, active);
				} catch (final InvalidComponentException e1) {
					activeEditor.error(e1);
					return;
				}
				activeEditor.status(Languages.getString("MyMenu.38")); //$NON-NLS-1$
			} else
				activeEditor.status(Languages.getString("MyMenu.39")); //$NON-NLS-1$
		});

		e_focus.addActionListener(e -> {
			final Editor activeEditor = context.getActiveEditor();

			final Requirements         reqs = new Requirements();
			final ComponentRequirement req  = new ComponentRequirement(CommandStrings.NAME,
			        activeEditor.getComponents_(), Policy.NONBRANCH);
			req.setCaseOfNullGraphic(false, Languages.getString("MyMenu.3")); //$NON-NLS-1$
			reqs.add(req);
			reqs.fulfillWithDialog(context.getFrame(), Languages.getString("MyMenu.41")); //$NON-NLS-1$

			if (reqs.fulfilled()) {
				final String id = reqs.getValue(CommandStrings.NAME, String.class);
				// because of the ComponentRequirement this component for sure exists
				final Component component = activeEditor.getComponentOrNull(id);

				component.getGraphics().requestFocus();
				activeEditor.status(Languages.getString("MyMenu.43")); //$NON-NLS-1$

			} else
				activeEditor.status(Languages.getString("MyMenu.44")); //$NON-NLS-1$
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
		MyMenu.setAccel(p_language, StringConstants.P_LANGUAGE_ACCEL);
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
		MyMenu.setIcon(p_language, "language"); //$NON-NLS-1$
		MyMenu.setIcon(m_help, "help"); //$NON-NLS-1$
	}

	private static void setAccel(JMenuItem jmi, String acceleratorKeyStroke) {
		jmi.setAccelerator(KeyStroke.getKeyStroke(acceleratorKeyStroke));
	}

	private static void setIcon(JMenuItem jmi, String desc) {
		final String filename    = String.format("%s%s_icon.png", StringConstants.MENU_ICON_PATH,              //$NON-NLS-1$
		        desc);
		final String description = String.format("%s icon", desc);                                             //$NON-NLS-1$
		jmi.setIcon(new ImageIcon(filename, description));
	}
}
