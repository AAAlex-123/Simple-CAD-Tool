package requirement.graphics;

import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import requirement.requirements.StringRequirement;

/**
 * Graphic for a {@link StringRequirement}. It consists of a JLabel to display
 * the key and a JTextField where the user types the value.
 *
 * @author alexm
 */
public final class StringRequirementGraphic extends AbstractRequirementGraphic<StringRequirement> {

	private final JLabel     label;
	private final JTextField textArea;

	/**
	 * Constructs the Graphic using the {@code StringRequirement} associated with
	 * it.
	 *
	 * @param requirement the Requirement
	 */
	public StringRequirementGraphic(StringRequirement requirement) {
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
		textArea.setText(req.stringType.getDescription());
	}

	@Override
	protected void onFocusGained() {
		textArea.requestFocus();
		textArea.setSelectionStart(0);
		textArea.setSelectionEnd(textArea.getText().length());
	}
}
