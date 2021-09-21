package requirement.graphics;

import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import requirement.requirements.StringRequirement;

/**
 * Graphic for a {@link StringRequirement}. It consists of a JLabel to display
 * the key and a JTextField where the user types the Requirement's value.
 *
 * @author Alex Mandelias
 */
public final class StringRequirementGraphic extends AbstractRequirementGraphic<StringRequirement> {

	private final JLabel     label;
	private final JTextField textField;

	/**
	 * Constructs a Graphic using a {@code StringRequirement}.
	 *
	 * @param requirement the Requirement that will be associated with this Graphic
	 */
	public StringRequirementGraphic(StringRequirement requirement) {
		super(requirement);
		setLayout(new GridLayout(1, 2, 15, 0));

		label = new JLabel(requirement.key());
		textField = new JTextField(10);

		label.setHorizontalAlignment(SwingConstants.RIGHT);
		textField.setMaximumSize(textField.getPreferredSize());

		add(label);
		add(textField);
	}

	@Override
	public void update() {
		if (requirement.finalised())
			textField.setEnabled(false);
	}

	@Override
	public void reset() {
		textField.setText((String) requirement.defaultValue());
	}

	@Override
	public void fulfilRequirement() {
		requirement.fulfil(textField.getText());
	}

	@Override
	public void onNotFulfilled() {
		textField.setText(requirement.stringType.getDescription());
	}

	@Override
	protected void onFocusGained() {
		textField.requestFocusInWindow();
		textField.setSelectionStart(0);
		textField.setSelectionEnd(textField.getText().length());
	}
}
