package requirement.graphics;

import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import requirement.requirements.AbstractRequirement;
import requirement.requirements.StringRequirement;

/**
 * Graphic for a {@link StringRequirement}. It consists of a JLabel (the key)
 * and a JTextField where the user types the value.
 *
 * @author alexm
 */
public final class StringRequirementGraphic extends AbstractRequirementGraphic {

	private final JLabel     label;
	private final JTextField textArea;

	/**
	 * Constructs the Graphic using the Requirement associated with it.
	 *
	 * @param requirement the Requirement
	 */
	public StringRequirementGraphic(AbstractRequirement requirement) {
		super(requirement);
		setLayout(new GridLayout(1, 2, 15, 0));

		label = new JLabel(req.key());
		textArea = new JTextField(10);

		label.setHorizontalAlignment(SwingConstants.RIGHT);
		textArea.setMaximumSize(textArea.getPreferredSize());

		add(label);
		add(textArea);
	}

	@Override
	public void update() {
		textArea.setText((String) req.defaultValue());

		if (req.finalised())
			textArea.setEnabled(false);
	}

	@Override
	public void reset() {
		textArea.setText((String) req.defaultValue());
	}

	@Override
	public void fulfilRequirement() {
		req.fulfil(textArea.getText());
	}

	@Override
	public void onNotFulfilled() {
		textArea.setText(((StringRequirement) req).stringType.getDescription());
	}

	@Override
	protected void onFocusGained() {
		textArea.requestFocus();
		textArea.setSelectionStart(0);
		textArea.setSelectionEnd(textArea.getText().length());
	}
}
