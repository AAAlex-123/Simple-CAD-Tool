package application;

import static myUtil.Utility.all;
import static myUtil.Utility.foreach;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

/**
 * A wrapper for a collection of {@code Requirements} that acts as a wrapper.
 * For method details refer to the Requirement class.
 *
 * @param <V> the type of the Requirement objects
 * @see application.Requirement Requirement
 */
final class Requirements<V> implements Iterable<Requirement<V>>, Serializable {

	private static final long serialVersionUID = 2L;

	private final Map<String, Requirement<V>> requirements;

	/** Constructs the collection of Requirements */
	Requirements() {
		// map for simpler lookup, linked to retain the order
		requirements = new LinkedHashMap<>(1, 1);
	}

	/**
	 * Adds a Requirement with a specific {@code key}.
	 * 
	 * @param key the key
	 */
	void add(String key) {
		requirements.put(key, new Requirement<V>(key));
	}

	/**
	 * Adds a Requirement with a specific {@code key} and {@code stringType}.
	 * 
	 * @param key        the key
	 * @param stringType the type
	 */
	void add(String key, Requirement.StringType stringType) {
		requirements.put(key, new Requirement<V>(key, stringType));
	}

	/**
	 * Returns the Requirement with the {@code key} given.
	 * 
	 * @param key the key
	 * @return the Requirement with that key or {@code null}
	 */
	Requirement<V> get(String key) {
		return requirements.get(key);
	}

	/**
	 * Returns {@code true} if all the Requirements are fulfilled.
	 * 
	 * @return {@code true} if all the Requirements are fulfilled
	 */
	boolean fulfilled() {
		return all(this, Requirement::fulfilled);
	}

	/**
	 * Attempts to fulfil the Requirements in this collection using a pop-up dialog.
	 * If the type of the Requirements isn't String, an error will be printed. Note
	 * that this shouldn't happen, like ever.
	 * 
	 * @param frame       the parent of the dialog
	 * @param description the text that will be displayed
	 */
	@SuppressWarnings("unchecked")
	void fulfillWithDialog(Frame frame, String description) {
		if (fulfilled())
			return;

		try {
			(new RequirementsDialog("Fill the parameters", description, (Requirements<String>) this, frame)).run();
		} catch (ClassCastException e) {
			System.err.printf("Inform the developer about error: %s%n", e.getMessage());
		}
	}

	/** Clears all the Requirements in this collection. */
	void clear() {
		foreach(this, Requirement::clear);
	}

	@Override
	public Iterator<Requirement<V>> iterator() {
		return new RequirementsIterator();
	}

	// fancy iterator stuff. totally didn't just use the iterator of the underlying map.
	private class RequirementsIterator implements Iterator<Requirement<V>> {

		private Iterator<Requirement<V>> iter;

		RequirementsIterator() {
			iter = requirements.values().iterator();
		}

		@Override
		public boolean hasNext() {
			return iter.hasNext();
		}

		@Override
		public Requirement<V> next() {
			return iter.next();
		}
	}

	/** The dialog with which the user fulfils the Requirements */
	private final class RequirementsDialog extends JDialog {

		private final JPanel mainPanel, infoPanel, optionsPanel, buttonsPanel, lowerPanel;
		private final JButton okButton, cancelButton;
		private final JLabel[] labels;
		private final JTextField[] textAreas;
		private final StatusBar sb;

		private final Requirements<String> reqs;

		RequirementsDialog(String title, String desc, Requirements<String> reqs, Frame parent) {
			super(parent, title, true);
			this.reqs = reqs;

			setLayout(new BorderLayout());
			setResizable(false);

			mainPanel = new JPanel(new GridLayout(1, 2, 0, 20));
			add(mainPanel, BorderLayout.CENTER);

			infoPanel = new JPanel();
			infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
			JLabel l1 = new JLabel("Parameters for:");
			JLabel l2 = new JLabel(desc);
			l1.setAlignmentX(CENTER_ALIGNMENT);
			l2.setAlignmentX(CENTER_ALIGNMENT);
			infoPanel.add(l1);
			infoPanel.add(Box.createVerticalStrut(8));
			infoPanel.add(l2);
			mainPanel.add(infoPanel);

			optionsPanel = new JPanel(new GridLayout(reqs.requirements.size(), 2, 15, 15));
			labels = new JLabel[reqs.requirements.size()];
			textAreas = new JTextField[reqs.requirements.size()];
			int i = 0;
			for (Requirement<String> r : reqs) {
				labels[i] = new JLabel(r.key());
				textAreas[i] = new JTextField(7);
				textAreas[i].setText(r.value());
				if (r.value() != null)
					textAreas[i].setEnabled(false);
				labels[i].setHorizontalAlignment(SwingConstants.RIGHT);
				textAreas[i].setMaximumSize(textAreas[i].getPreferredSize());
				optionsPanel.add(labels[i]);
				optionsPanel.add(textAreas[i]);
				++i;
			}
			mainPanel.add(optionsPanel);

			buttonsPanel = new JPanel(new FlowLayout());
			okButton = new JButton("OK");
			cancelButton = new JButton("Cancel");
			buttonsPanel.add(okButton);
			buttonsPanel.add(cancelButton);

			sb = new StatusBar();
			sb.addLabel("message");

			lowerPanel = new JPanel(new BorderLayout());
			lowerPanel.add(buttonsPanel, BorderLayout.CENTER);
			lowerPanel.add(sb, BorderLayout.SOUTH);
			add(lowerPanel, BorderLayout.SOUTH);

			addListeners();

			setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
			mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
			pack();
			setLocationRelativeTo(parent);
		}

		void run() {
			setVisible(true);
		}

		private boolean validateInput() {
			// backwards so that the last (first) wrong label has focus
			for (int i = labels.length - 1; i >= 0; --i) {
				Requirement<String> r = reqs.get(labels[i].getText());
				r.fulfil(textAreas[i].getText());

				if (!r.fulfilled()) {
					textAreas[i].setText(r.stringType.regex);
					sb.setLabelText("message", "Match the regex!");
					textAreas[i].requestFocus();
				}
			}

			return reqs.fulfilled();
		}

		@SuppressWarnings("unused")
		private void addListeners() {

			// --- ACTIONS ---

			Action pressOK = new AbstractAction() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (!validateInput())
						return;
					dispose();
				}
			};

			Action pressCancel = new AbstractAction() {
				@Override
				public void actionPerformed(ActionEvent e) {
					dispose();
				}
			};

			for (int j = 0; j < textAreas.length; ++j) {
				final int i = j;
				textAreas[i].setFocusTraversalKeysEnabled(true);
				textAreas[i].setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, null);
				textAreas[i].setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, null);
				textAreas[i].addFocusListener(new FocusAdapter() {

					@Override
					public void focusGained(FocusEvent e) {
						textAreas[i].setSelectionStart(0);
						textAreas[i].setSelectionEnd(textAreas[i].getText().length());
						textAreas[i].addKeyListener(new KeyAdapter() {

							@Override
							public void keyPressed(KeyEvent e1) {
								if (e1.getKeyCode() == KeyEvent.VK_ENTER) {
									pressOK.actionPerformed(null);
									e1.consume();
								} else if (e1.getKeyCode() == KeyEvent.VK_ESCAPE) {
									pressCancel.actionPerformed(null);
									e1.consume();
								}
							}
						});
					}
				});
			}

			okButton.addActionListener(pressOK);
			cancelButton.addActionListener(pressCancel);

			addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent e) {
					pressCancel.actionPerformed(null);
				}
			});

			addWindowFocusListener(new WindowAdapter() {
				@Override
				public void windowGainedFocus(WindowEvent e) {
					// give focus to the first empty text field...
					for (int i = 0; i < textAreas.length; ++i)
						if (textAreas[i].getText().equals("")) {
							textAreas[i].requestFocus();
							return;
						}

					// ... or the button if none exist
					okButton.requestFocus();
				}
			});
		}
	}
}