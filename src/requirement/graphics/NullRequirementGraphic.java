package requirement.graphics;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import localisation.Languages;
import requirement.requirements.AbstractRequirement;

/**
 * A Graphic that should be used when no other Graphic is available or suitable
 * for use. It doesn't provide any functionality other than displaying a message
 * informing the user about the Requirement that doesn't support a Graphic along
 * with the reason for it.
 *
 * @author alexm
 */
public class NullRequirementGraphic
extends AbstractRequirementGraphic<AbstractRequirement> {

	private final JPanel mainPanel;
	private final JLabel icon, key, explanation;


	/**
	 * Constructs the NullGraphic.
	 *
	 * @param requirement the Requirement that doesn't have a Graphic
	 * @param reason      the reason that a Graphic isn't supported
	 */
	public NullRequirementGraphic(AbstractRequirement requirement, String reason, boolean error) {
		super(requirement);
		setLayout(new BorderLayout());

		icon = new JLabel(UIManager.getIcon("OptionPane." + (error ? "error" : "information") + "Icon"));
		key = new JLabel(String.format(Languages.getString("NullRequirementGraphic.0"), requirement.key())); //$NON-NLS-1$
		explanation = new JLabel(String.format(Languages.getString("NullRequirementGraphic.1"), reason)); //$NON-NLS-1$

		key.setHorizontalAlignment(SwingConstants.CENTER);
		explanation.setHorizontalAlignment(SwingConstants.CENTER);

		mainPanel = new JPanel(new GridLayout(2, 1, 10, 0));
		mainPanel.add(key);
		mainPanel.add(explanation);

		add(icon, BorderLayout.WEST);
		add(mainPanel, BorderLayout.CENTER);
	}

	@Override
	public void update() {}

	@Override
	public void reset() {}

	@Override
	public void fulfilRequirement() {}

	@Override
	public void onNotFulfilled() {}

	@Override
	protected void onFocusGained() {}
}
