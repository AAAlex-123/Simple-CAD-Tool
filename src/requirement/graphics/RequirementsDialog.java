package requirement.graphics;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

import application.editor.StatusBar;
import localisation.Languages;

/**
 * A dialog with which the user fulfils a {@link Requirements} object. After
 * construction, the call {@code setVisible(true)} shows the dialog and the user
 * can provide a value for every {@code Requirement} using the text areas. The
 * dialog has a few buttons that are also associated with keyboard shortcuts:
 * <ul>
 * <li><b>OK {@code (ENTER)}:</b> Attempts to fulfil the {@code Requirements}
 * with the values provided. If a {@code Requirement} cannot be fulfilled (the
 * {@code value} doesn't match its {@code regex}) the dialog doesn't close and
 * instead prompts the user to provide a correct value.
 * <li><b>CANCEL {@code (ESCAPE)}:</b> Closes the dialog without altering the
 * {@code Requirements}
 * <li><b>RESET {@code (CTRL+R)}:</b> Resets the text areas to the default value
 * of the corresponding {@code Requirements}.
 * </ul>
 *
 * @author alexm
 */
final class RequirementsDialog extends JDialog {

	private final JPanel       mainPanel, lowerPanel, optionsPanel, buttonsPanel;
	private final JButton      okButton, cancelButton, resetButton;
	private final JLabel[]     labels;
	private final JTextField[] textAreas;
	private final StatusBar    sb;

	private final Requirements<String> reqs;

	/**
	 * Constructs the dialog.
	 *
	 * @param title       the window's title
	 * @param dialogsReqs the Requirements that the dialog will fulfil
	 * @param parent      the parent frame of the dialog
	 */
	RequirementsDialog(String title, Requirements<String> dialogsReqs, Frame parent) {
		super(parent, title, true);
		reqs = dialogsReqs;

		setLayout(new BorderLayout());
		setResizable(false);

		// --- options panel (middle) ---
		final int numReq = reqs.size();
		optionsPanel = new JPanel(new GridLayout(numReq, 2, 15, 15));
		labels = new JLabel[numReq];
		textAreas = new JTextField[numReq];

		int i = 0;
		for (final Requirement<String> r : reqs) {
			labels[i] = new JLabel(r.key());
			textAreas[i] = new JTextField(r.defaultValue(), 10);
			if (r.finalised())
				textAreas[i].setEnabled(false);

			labels[i].setHorizontalAlignment(SwingConstants.RIGHT);
			textAreas[i].setMaximumSize(textAreas[i].getPreferredSize());
			optionsPanel.add(labels[i]);
			optionsPanel.add(textAreas[i]);
			++i;
		}

		// --- buttons panel (bottom) ---
		buttonsPanel = new JPanel(new FlowLayout());
		buttonsPanel.add(okButton = new JButton(Languages.getString("RequirementsDialog.0"))); //$NON-NLS-1$
		buttonsPanel.add(cancelButton = new JButton(Languages.getString("RequirementsDialog.1"))); //$NON-NLS-1$
		buttonsPanel.add(resetButton = new JButton(Languages.getString("RequirementsDialog.2"))); //$NON-NLS-1$

		sb = new StatusBar();
		sb.addLabel("message"); //$NON-NLS-1$

		final JScrollPane jsp = new JScrollPane(optionsPanel);
		jsp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		jsp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		jsp.setBorder(BorderFactory.createEmptyBorder());
		jsp.setWheelScrollingEnabled(true);

		mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.X_AXIS));
		mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
		mainPanel.add(jsp);

		lowerPanel = new JPanel(new BorderLayout());
		lowerPanel.add(buttonsPanel, BorderLayout.CENTER);
		lowerPanel.add(sb, BorderLayout.SOUTH);

		add(mainPanel, BorderLayout.CENTER);
		add(lowerPanel, BorderLayout.SOUTH);

		addListeners();

		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		pack();
		setSize(new Dimension(Math.min(getWidth() + 30, parent.getWidth()),
		        Math.min(getHeight(), parent.getHeight())));
		setLocationRelativeTo(parent);
	}

	private boolean validateInput() {
		// backwards so that the last (first) wrong text area has focus
		for (int i = reqs.size() - 1; i >= 0; --i) {
			final Requirement<String> r = reqs.get(labels[i].getText());
			if (!r.finalised())
				r.fulfil(textAreas[i].getText());

			if (!r.fulfilled()) {
				textAreas[i].setText(r.stringType.desc);
				textAreas[i].requestFocus();
			}
		}

		if (!reqs.fulfilled())
			sb.setLabelText("message", Languages.getString("RequirementsDialog.5")); //$NON-NLS-1$ //$NON-NLS-2$

		return reqs.fulfilled();
	}

	private void addListeners() {

		final Action pressOK = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (validateInput())
					dispose();
			}
		};

		final Action pressCancel = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		};

		final Action pressReset = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int i = 0;
				for (final Requirement<String> r : reqs) {
					r.reset();
					textAreas[i].setText(r.defaultValue());
					++i;
				}
			}
		};

		okButton.addActionListener(pressOK);
		cancelButton.addActionListener(pressCancel);
		resetButton.addActionListener(pressReset);

		getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
		        .put(KeyStroke.getKeyStroke("ENTER"), "pressOK"); //$NON-NLS-1$ //$NON-NLS-2$
		getRootPane().getActionMap().put("pressOK", pressOK); //$NON-NLS-1$

		getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
		        .put(KeyStroke.getKeyStroke("ESCAPE"), "pressCancel"); //$NON-NLS-1$ //$NON-NLS-2$
		getRootPane().getActionMap().put("pressCancel", pressCancel); //$NON-NLS-1$

		getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
		        .put(KeyStroke.getKeyStroke("control R"), "pressReset"); //$NON-NLS-1$ //$NON-NLS-2$
		getRootPane().getActionMap().put("pressReset", pressReset); //$NON-NLS-1$

		for (int j = 0; j < textAreas.length; ++j) {
			final int i = j;
			textAreas[i].setFocusTraversalKeysEnabled(true);
			textAreas[i].addFocusListener(new FocusAdapter() {
				@Override
				public void focusGained(FocusEvent e) {
					textAreas[i].setSelectionStart(0);
					textAreas[i].setSelectionEnd(textAreas[i].getText().length());
				}
			});
		}

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				pressCancel.actionPerformed(null);
			}
		});

		addWindowFocusListener(new WindowAdapter() {
			@Override
			public void windowGainedFocus(WindowEvent e) {
				// give focus to the first non-fulfilled field...
				int i = 0;
				for (final Requirement<String> r : reqs) {
					if (!r.fulfilled()) {
						textAreas[i].requestFocus();
						return;
					}
					++i;
				}

				// ... or the ok button if none exist
				okButton.requestFocus();
			}
		});
	}
}
