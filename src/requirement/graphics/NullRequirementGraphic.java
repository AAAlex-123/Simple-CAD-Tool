package requirement.graphics;

import java.awt.GridLayout;

import javax.swing.JLabel;

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

	/**
	 * Constructs the NullGraphic.
	 *
	 * @param requirement the Requirement that doesn't have a Graphic
	 * @param reason      the reason that a Graphic isn't supported
	 */
	public NullRequirementGraphic(AbstractRequirement requirement, String reason) {
		super(requirement);
		setLayout(new GridLayout(2, 1, 10, 0));

		add(new JLabel(String.format("No Graphic for Requirement: %s", requirement.key())));
		add(new JLabel(String.format("Reason: %s", reason)));
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
