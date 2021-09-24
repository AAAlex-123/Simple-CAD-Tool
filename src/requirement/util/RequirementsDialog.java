package requirement.util;

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
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
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
import requirement.graphics.AbstractRequirementGraphic;
import requirement.graphics.NullRequirementGraphic;
import requirement.requirements.AbstractRequirement;

/**
 * A dialog with which the user fulfils a {@link Requirements} object. Calling
 * the {@link #showDialog(String, Requirements, Frame)} method constructs and
 * shows the dialog with which the user fulfils the individual Requirements in
 * the collection. {@code Graphic} objects are used internally to display the
 * Requirements and to enable uniform operation on them. If not all Requirements
 * have a Graphic, the user may only press {@code CANCEL} or {@code X} to close
 * the dialog. Additionally, if at least one Requirement has a graphic error, a
 * pop-up is shown informing the user about what action to take.
 * <p>
 * The dialog has some buttons that can be used with keyboard shortcuts:
 * <ul>
 * <li><b>OK {@code (ENTER)}:</b> Attempts to fulfil every {@code Requirement}
 * with the values provided. If at least one cannot be fulfilled, the dialog
 * doesn't close and instead prompts the user to provide a correct value.</li>
 * <li><b>CANCEL {@code (ESCAPE)}:</b> Closes the dialog without performing any
 * operations on the {@code Requirements}.</li>
 * <li><b>RESET {@code (CTRL+R)}:</b> Resets every Requirement.</li>
 * </ul>
 *
 * @author Alex Mandelias
 *
 * @see AbstractRequirement
 * @see AbstractRequirement#hasGraphic()
 * @see AbstractRequirement#graphicError()
 * @see AbstractRequirementGraphic
 * @see NullRequirementGraphic
 */
public final class RequirementsDialog extends JDialog {

	private final JPanel    mainPanel, lowerPanel, optionsPanel, buttonsPanel;
	private final JButton   okButton, cancelButton, resetButton;
	private final StatusBar sb;

	private final Requirements requirements;

	private final Map<AbstractRequirement, AbstractRequirementGraphic<?>> map;

	private boolean allReqsHaveGraphics = true;
	private boolean runtimeGraphicError = false;

	/**
	 * Constructs and shows a dialog. If at least one Requirement has a graphic
	 * error, a pop-up is shown to inform the user about what action to take.
	 *
	 * @param title       the dialog's title
	 * @param dialogsReqs the Requirements that the dialog will fulfil
	 * @param parent      the parent frame of the dialog that is used to position
	 *                    and resize it
	 *
	 * @see AbstractRequirement#graphicError()
	 */
	public static void showDialog(String title, Requirements dialogsReqs, Frame parent) {

		final RequirementsDialog dialog = new RequirementsDialog(title, dialogsReqs, parent);

		if (dialog.runtimeGraphicError) {
			dialog.setModal(false);
			dialog.setVisible(true);

			final String messageString = Languages.getString("RequirementsDialog.0"); //$NON-NLS-1$
			final String titleString   = Languages.getString("RequirementsDialog.2"); //$NON-NLS-1$

			JOptionPane.showMessageDialog(dialog,
			        String.format("<html><center>%s</center></html>", messageString), //$NON-NLS-1$
			        titleString, JOptionPane.ERROR_MESSAGE);

			dialog.setModal(true);
		} else {
			dialog.setModal(true);
			dialog.setVisible(true);
		}
	}

	private RequirementsDialog(String title, Requirements dialogsReqs, Frame parent) {
		super(parent, title);

		// TODO: fix the focus spaghetti

		requirements = dialogsReqs;
		map = new HashMap<>();

		setLayout(new BorderLayout());
		setResizable(false);

		// --- buttons panel (bottom) ---
		buttonsPanel = new JPanel(new FlowLayout());
		buttonsPanel.add(okButton = new JButton(Languages.getString("RequirementsDialog.3"))); //$NON-NLS-1$
		buttonsPanel.add(cancelButton = new JButton(Languages.getString("RequirementsDialog.4"))); //$NON-NLS-1$
		buttonsPanel.add(resetButton = new JButton(Languages.getString("RequirementsDialog.5"))); //$NON-NLS-1$

		sb = new StatusBar();
		sb.addLabel(RequirementStrings.MESSAGE);

		// --- options panel (middle) ---
		optionsPanel = new JPanel(new GridLayout(requirements.size(), 1, 0, 15));
		Utility.foreach(requirements, req -> {
			final AbstractRequirementGraphic<?> graphic = req.constructAndGetGraphic();
			map.put(req, graphic);
			optionsPanel.add(graphic);
			allReqsHaveGraphics &= req.hasGraphic();
			runtimeGraphicError |= req.graphicError();
		});

		if (!allReqsHaveGraphics || runtimeGraphicError) {
			okButton.setEnabled(false);
			resetButton.setEnabled(false);
			sb.setLabelText(RequirementStrings.MESSAGE, MessageType.FAILURE,
			        Languages.getString("RequirementsDialog.6")); //$NON-NLS-1$
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
		setLocationRelativeTo(parent);

		// limit size to fit within parent frame, if it exists
		if (parent != null)
			setSize(new Dimension(Math.min(getWidth() + 30, parent.getWidth()),
			        Math.min(getHeight(), parent.getHeight())));
	}

	/**
	 * Attempts to fulfil every non-finalised {@code Requirement} in this dialog's
	 * collection with the values the user provided. Returns whether or not every
	 * {@code Requirement} is fulfilled.
	 *
	 * @return {@code true} if every Requirement is fulfilled, {@code false}
	 *         otherwise
	 *
	 * @see AbstractRequirement#fulfilled()
	 */
	private boolean validateInput() {
		Utility.foreach(requirements, req -> {
			final AbstractRequirementGraphic<?> g = map.get(req);

			if (!req.finalised())
				g.fulfilRequirement();

			if (!req.fulfilled())
				g.onNotFulfilled();
		});

		final boolean fulfilled = requirements.fulfilled();

		if (!fulfilled) {
			sb.setLabelText(RequirementStrings.MESSAGE,
			        Languages.getString("RequirementsDialog.7")); //$NON-NLS-1$

			// give focus to the first non-fulfilled Requirement
			for (final AbstractRequirement req : requirements)
				if (!req.fulfilled()) {
					map.get(req).requestFocus();
					break;
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
				Utility.foreach(requirements, AbstractRequirement::reset);
			}
		};

		okButton.addActionListener(pressOK);
		cancelButton.addActionListener(pressCancel);
		resetButton.addActionListener(pressReset);

		final InputMap  inputMap  = getRootPane()
		        .getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		final ActionMap actionMap = getRootPane().getActionMap();

		inputMap.put(KeyStroke.getKeyStroke("ENTER"), "pressOK"); //$NON-NLS-1$ //$NON-NLS-2$
		actionMap.put("pressOK", pressOK); //$NON-NLS-1$

		inputMap.put(KeyStroke.getKeyStroke("ESCAPE"), "pressCancel"); //$NON-NLS-1$ //$NON-NLS-2$
		actionMap.put("pressCancel", pressCancel); //$NON-NLS-1$

		inputMap.put(KeyStroke.getKeyStroke("control R"), "pressReset"); //$NON-NLS-1$ //$NON-NLS-2$
		actionMap.put("pressReset", pressReset); //$NON-NLS-1$

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
				for (final AbstractRequirement r : requirements)
					if (!r.fulfilled()) {
						r.getCachedGraphic().requestFocus();
						return;
					}

				// ... or the ok button if none exist
				okButton.requestFocus();
			}
		});
	}
}
