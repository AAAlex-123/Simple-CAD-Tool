package requirement.graphics;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.ScrollPaneConstants;
import javax.swing.WindowConstants;

import application.editor.StatusBar;
import application.editor.StatusBar.MessageType;
import localisation.Languages;
import localisation.RequirementStrings;
import myUtil.Utility;
import requirement.requirements.AbstractRequirement;
import requirement.requirements.Requirements;

/**
 * A dialog with which the user fulfils a {@link Requirements collection} of
 * {@code Requirements}. After construction, the call {@code setVisible(true)}
 * shows the dialog and the user can use it in order to fulfil the individual
 * Requirements. {@link requirement.graphics.AbstractRequirementGraphic
 * Graphics} are used in order to display every Requirement in the collection of
 * Requirements and to uniformly call the necessary methods on them. If a
 * Requirement doesn't support a Graphic, the user cannot use the Dialog to
 * fulfil the Requirements and may only press {@code CANCEL} or {@code X} to
 * close it.
 * <p>
 * The dialog has a few buttons that can be used with keyboard shortcuts:
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
public final class RequirementsDialog extends JDialog {

	private final JPanel    mainPanel, lowerPanel, optionsPanel, buttonsPanel;
	private final JButton   okButton, cancelButton, resetButton;
	private final StatusBar sb;

	private final Requirements reqs;
	private final Map<AbstractRequirement, AbstractRequirementGraphic<?>> map;

	/**
	 * Constructs the dialog.
	 *
	 * @param title       the window's title
	 * @param dialogsReqs the Requirements that the dialog will fulfil
	 * @param parent      the parent frame of the dialog, that is used to position
	 *                    and resize it
	 *
	 * @throws NullPointerException if {@code parent == null}
	 */
	public RequirementsDialog(String title, Requirements dialogsReqs, Frame parent) {
		super(parent, title, true);

		if (parent == null)
			throw new NullPointerException("The parent frame of the dialog cannot be null"); //$NON-NLS-1$

		reqs = dialogsReqs;
		map = new HashMap<>();

		setLayout(new BorderLayout());
		setResizable(false);

		// --- buttons panel (bottom) ---
		buttonsPanel = new JPanel(new FlowLayout());
		buttonsPanel.add(okButton = new JButton(Languages.getString("RequirementsDialog.0"))); //$NON-NLS-1$
		buttonsPanel.add(cancelButton = new JButton(Languages.getString("RequirementsDialog.1"))); //$NON-NLS-1$
		buttonsPanel.add(resetButton = new JButton(Languages.getString("RequirementsDialog.2"))); //$NON-NLS-1$

		sb = new StatusBar();
		sb.addLabel(RequirementStrings.MESSAGE);

		// --- options panel (middle) ---
		optionsPanel = new JPanel(new GridLayout(reqs.size(), 1, 0, 15));
		boolean allGraphics = true;
		for (AbstractRequirement req : reqs) {
			AbstractRequirementGraphic<?> graphic = req.constructAndGetGraphic();
			map.put(req, graphic);
			optionsPanel.add(graphic);
			allGraphics &= req.hasGraphic();
		}

		if (!allGraphics) {
			sb.setLabelText(RequirementStrings.MESSAGE, MessageType.FAILURE,
			        Languages.getString("RequirementsDialog.3")); //$NON-NLS-1$
			okButton.setEnabled(false);
			resetButton.setEnabled(false);
		}

		// --- scroll pane (for options panel) ---
		final JScrollPane jsp = new JScrollPane(optionsPanel);
		jsp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		jsp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		jsp.setBorder(BorderFactory.createEmptyBorder());
		jsp.setWheelScrollingEnabled(true);

		// --- main panel ---
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

	/**
	 * Attempts to fulfil the {@code Requirements} in this collection with the
	 * values the user provided. Returns whether or not every {@code Requirement} is
	 * fulfilled.
	 *
	 * @return {@code true} if every Requirement is fulfilled, {@code false}
	 *         otherwise
	 *
	 * @see AbstractRequirement#fulfilled()
	 */
	private boolean validateInput() {
		Utility.foreach(reqs, r -> {
			final AbstractRequirementGraphic<?> g = map.get(r);

			if (!r.finalised())
				g.fulfilRequirement();

			if (!r.fulfilled())
				g.onNotFulfilled();
		});

		final boolean fulfilled = reqs.fulfilled();

		if (!fulfilled) {
			sb.setLabelText("message", Languages.getString("RequirementsDialog.5")); //$NON-NLS-1$ //$NON-NLS-2$

			// give focus to the first non-fulfilled Requirement
			for (final AbstractRequirement req : reqs) {
				if (!req.fulfilled()) {
					map.get(req).requestFocus();
					break;
				}
			}
		}

		return fulfilled;
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
				Utility.foreach(reqs, AbstractRequirement::reset);
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


		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				pressCancel.actionPerformed(null);
			}
		});

		addWindowFocusListener(new WindowAdapter() {
			@Override
			public void windowGainedFocus(WindowEvent e) {
				// give focus to the first non-fulfilled Requirement...
				for (final AbstractRequirement r : reqs) {
					if (!r.fulfilled()) {
						r.getCachedGraphic().requestFocus();
						return;
					}
				}

				// ... or the ok button if none exist
				okButton.requestFocus();
			}
		});
	}
}
