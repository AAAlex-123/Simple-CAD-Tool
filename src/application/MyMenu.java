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

import components.Component;
import components.ComponentFactory;
import exceptions.InvalidComponentException;

/** Custom Menu bar for the Application. */
final class MyMenu extends JMenuBar {

	private final Application context;

	private final JMenu m_file, m_edit, m_create, m_delete, m_help;
	private final JMenuItem f_save, f_save_as, f_open, f_clear, f_import, f_undo, f_redo, e_activate, e_move,
	d_component, h_help;

	private final Action a_undo, a_redo, a_save, a_save_as, a_open, a_clear, a_import, a_delete, a_help;

	private int commandCounter = 1, customCommandCounter = 1;

	/**
	 * Constructs the Menu with a related {@code Application}.
	 *
	 * @param app the context of this Menu
	 */
	@SuppressWarnings("unused")
	MyMenu(Application app) {

		// very big block of actions :)
		{
			// --- actions ---
			a_undo = new AbstractAction() {
				@Override
				public void actionPerformed(ActionEvent e) {
					Application.Actions.UNDO.context(context).execute();
				}
			};

			a_redo = new AbstractAction() {
				@Override
				public void actionPerformed(ActionEvent e) {
					Application.Actions.REDO.context(context).execute();
				}
			};

			a_save = new AbstractAction() {
				@Override
				public void actionPerformed(ActionEvent e) {

					// when first time saving ask for file dialog
					if (context.current_file == null) {
						Application.Actions.SAVE.reqs.fulfillWithDialog(app.getFrame(), "Save file");
					} else {
						Application.Actions.SAVE.reqs.get("filename").fulfil(context.current_file);
					}

					Application.Actions.SAVE.context(context).execute();
				}
			};

			a_save_as = new AbstractAction() {
				@Override
				public void actionPerformed(ActionEvent e) {
					Application.Actions.SAVE.reqs.fulfillWithDialog(app.getFrame(), "Save file");
					Application.Actions.SAVE.context(context).execute();
				}
			};

			a_open = new AbstractAction() {
				@Override
				public void actionPerformed(ActionEvent e) {
					Application.Actions.OPEN.reqs.get("gatename").fulfil("N/A");
					Application.Actions.OPEN.reqs.get("filetype").fulfil("circuit");
					Application.Actions.OPEN.reqs.fulfillWithDialog(app.getFrame(), "Open file");
					Application.Actions.OPEN.context(context).execute();
				}
			};

			a_clear = new AbstractAction() {
				@Override
				public void actionPerformed(ActionEvent e) {
					Application.Actions.CLEAR.context(context).execute();
				}
			};

			a_import = new AbstractAction() {
				@Override
				public void actionPerformed(ActionEvent e) {
					Application.Actions.OPEN.reqs.get("filetype").fulfil("component");
					Application.Actions.OPEN.reqs.fulfillWithDialog(app.getFrame(), "Import file");
					Application.Actions.OPEN.context(context).execute();
				}
			};

			a_delete = new AbstractAction() {
				@Override
				public void actionPerformed(ActionEvent e) {
					Command c = Command.delete(context);
					c.fillRequirements(app.getFrame());
					Application.Actions.DELETE.context(context).specify("command", c).execute();
				}
			};

			a_help = new AbstractAction() {
				@Override
				public void actionPerformed(ActionEvent e) {
					Application.Actions.HELP.context(context).execute();
				}
			};
		}

		context = app;

		// --- file ---
		m_file = new JMenu("File");
		f_open = new JMenuItem("Open");
		f_save = new JMenuItem("Save");
		f_save_as = new JMenuItem("Save as");
		f_clear = new JMenuItem("Clear");
		f_import = new JMenuItem("Import");
		f_undo = new JMenuItem("Undo");
		f_redo = new JMenuItem("Redo");

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
		e_move = new JMenuItem("Move");
		m_edit.add(e_activate);
		m_edit.add(e_move);
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

		// make menu usable
		mnemonics();
		listeners();
		editMenuListeners();
		accelerators();
		icons();
	}

	/**
	 * Adds a {@code Command} that creates a Component to the Menu.
	 *
	 * @param c the Command
	 */
	void addCreateCommand(Command c) {
		JMenuItem jmic = new JMenuItem();

		if (c.desc().startsWith("Create") || c.desc().startsWith("Delete")) {
			jmic.setText(c.desc().substring(6));
			setAccel(jmic, String.format("control %d", commandCounter++));
		} else {
			jmic.setText(c.desc());
			setAccel(jmic, String.format("control shift %d", customCommandCounter++));
		}

		jmic.addActionListener(e -> {
			Command cloned = c.myclone();
			cloned.fillRequirements(context.getFrame());
			Application.Actions.CREATE.context(context).specify("command", cloned).execute();
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
		f_save.addActionListener(a_save);
		f_save_as.addActionListener(a_save_as);
		f_open.addActionListener(a_open);
		f_clear.addActionListener(a_clear);
		f_import.addActionListener(a_import);
		f_undo.addActionListener(a_undo);
		f_redo.addActionListener(a_redo);
		d_component.addActionListener(a_delete);
		h_help.addActionListener(a_help);
	}

	private void editMenuListeners() {
		e_activate.addActionListener(e -> {
			Requirements<String> reqs = new Requirements<>();
			reqs.add("id", Requirement.StringType.NON_NEG_INTEGER);
			reqs.add("active", Requirement.StringType.ON_OFF);
			reqs.fulfillWithDialog(context.getFrame(), "Turn Input Pin on/off");
			if (reqs.fulfilled()) {
				int id = Integer.valueOf(reqs.get("id").value());
				Component comp = context.getComponent(id);

				if (comp == null) {
					context.error("Component with ID %d not found", id);
					return;
				}

				boolean active = reqs.get("active").value().equals("on");
				try {
					ComponentFactory.setActive(comp, active);
				} catch (InvalidComponentException e1) {
					context.error(e1.getMessage());
					return;
				}
				context.status("Activate Input Pin successful");
			} else {
				context.status("Activate Input Pin cancelled");
			}
		});

		e_move.addActionListener(e -> {
			Requirements<String> reqs = new Requirements<>();
			reqs.add("id", Requirement.StringType.NON_NEG_INTEGER);
			reqs.fulfillWithDialog(context.getFrame(), "Move component");
			if (reqs.fulfilled()) {
				int id = Integer.valueOf(reqs.get("id").value());
				Component comp = context.getComponent(id);

				if (comp == null) {
					context.error("Component with ID %d not found", id);
					return;
				}
				comp.requestFocus();
				comp.repaint();
				context.status("Moving component");
			} else {
				context.status("Move component cancelled");
			}
		});
	}

	private void accelerators() {
		setAccel(f_save, "control S");
		setAccel(f_save_as, "control shift S");
		setAccel(f_open, "control O");
		setAccel(f_clear, "control shift C");
		setAccel(f_import, "control I");
		setAccel(f_undo, "control Z");
		setAccel(f_redo, "control Y");
		setAccel(e_activate, "shift A");
		setAccel(e_move, "shift M");
		setAccel(d_component, "control D");
		setAccel(h_help, "F1");
	}

	private void icons() {
		setIcon(m_file, "file");
		setIcon(f_save, "save");
		setIcon(f_save_as, "save_as");
		setIcon(f_open, "open");
		setIcon(f_clear, "clear");
		setIcon(f_undo, "undo");
		setIcon(f_redo, "redo");
		setIcon(f_import, "import");
		setIcon(m_edit, "edit");
		setIcon(e_activate, "activate");
		setIcon(e_move, "move");
		setIcon(m_create, "create");
		setIcon(m_delete, "delete");
		setIcon(m_help, "help");
	}

	private static void setAccel(JMenuItem jmi, String s) {
		jmi.setAccelerator(KeyStroke.getKeyStroke(s));
	}

	private static void setIcon(JMenuItem jmi, String desc) {
		jmi.setIcon(new ImageIcon(Application.menu_icon_path + desc + "_icon.png", desc + " icon"));
	}
}
