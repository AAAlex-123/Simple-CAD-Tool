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
 * A Graphic that should be used when no other Graphic is suitable for use. It
 * doesn't provide any functionality other than displaying a message informing
 * the user about the {@code Requirement} that doesn't support a Graphic along
 * with the {@code cause} for it.
 * <p>
 * A NullGraphic can be the result of:
 * <ul>
 * <li>programming errors</li>
 * <li>application state errors</li>
 * </ul>
 * <p>
 * <i>Programming errors</i> are the equivalent of Runtime Exceptions, they
 * represent problems cannot be resolved without the developer intervening. For
 * this reason the {@code cause} should be something that the developer can
 * easily recognise, locate and possibly fix. Programming errors include:
 * <ul>
 * <li>requesting a Graphic while it is not supported by the specific subclass
 * of Requirement</li>
 * <li>omitting to set some of the Requirement's parameters before constructing
 * the Graphic.</li>
 * </ul>
 * <i>Application state errors</i> are errors that the user themselves causes
 * and they are the ones that are responsible for resolving. For this reason the
 * {@code cause} should be simple yet clear enough so that the average user is
 * able to resolve the problem. Application state errors include:
 * <ul>
 * <li>opening a file when there are no suitable files</li>
 * <li>using a bad, user-defined range to chose a number (e.g. 50 - 20)</li>
 * </ul>
 * <p>
 * NullGraphics consist of an icon, which changes according to the type of the
 * error that caused the NullGraphic to be constructed in the first place, and
 * two lines of text containing the name of the Requirement and the cause of the
 * error. For this reason, the {@code cause} shall be as short and descriptive
 * as possible
 *
 * @author Alex Mandelias
 */
public class NullRequirementGraphic extends AbstractRequirementGraphic<AbstractRequirement> {

	private final JPanel mainPanel;
	private final JLabel icon, key, reason;

	/**
	 * Constructs a NullGraphic.
	 *
	 * @param requirement the Requirement that doesn't support a Graphic
	 * @param cause       the short, descriptive cause for the Graphic's not being
	 *                    supported. The cause shall be developer-friendly if the
	 *                    error is a programming error or user-friendly if it is an
	 *                    application state error.
	 * @param error       {@code true} if the cause of this NullGraphic is a
	 *                    programming error, {@code false} otherwise
	 */
	public NullRequirementGraphic(AbstractRequirement requirement, String cause, boolean error) {
		super(requirement);
		setLayout(new BorderLayout());

		final String iconName     = String.format("OptionPane.%sIcon",               //$NON-NLS-1$
		        error ? "error" : "information");                                    //$NON-NLS-1$ //$NON-NLS-2$
		final String keyString    = Languages.getString("NullRequirementGraphic.0"); //$NON-NLS-1$
		final String reasonString = Languages.getString("NullRequirementGraphic.1"); //$NON-NLS-1$

		icon = new JLabel(UIManager.getIcon(iconName));
		key = new JLabel(String.format(keyString, requirement.key()));
		reason = new JLabel(String.format(reasonString, cause));

		key.setHorizontalAlignment(SwingConstants.CENTER);
		reason.setHorizontalAlignment(SwingConstants.CENTER);

		mainPanel = new JPanel(new GridLayout(2, 1, 10, 0));
		mainPanel.add(key);
		mainPanel.add(reason);

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
