package requirement.graphics;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.NoSuchElementException;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;

import localisation.Languages;
import myUtil.MutableColorBorder;
import requirement.requirements.ListRequirement;

/**
 * Graphic for a {@link ListRequirement}. It consists of a drop-down list to let
 * the user choose an option from a list. The available options are obtained
 * from its associated {@code ListRequirement}. The selected option will fulfil
 * it.
 *
 * @param <T> the type of items the list contains
 *
 * @author dimits
 */
public class ListRequirementGraphic<T> extends AbstractRequirementGraphic<ListRequirement<T>> {

	private final JComboBox<T>       optionBox;
	private Vector<T>                currentOptions;
	private final MutableColorBorder border;

	/**
	 * Constructs the Graphic using the {@code ListRequirement} associated with it.
	 *
	 * @param requirement the Requirement
	 */
	public ListRequirementGraphic(ListRequirement<T> requirement) {
		super(requirement);
		setLayout(new GridLayout(2, 1, 10, 0));

		optionBox = new JComboBox<>();
		currentOptions = new Vector<>();
		border = new MutableColorBorder(Color.BLUE);

		optionBox.setBorder(border);
		optionBox.setMaximumSize(new Dimension(200, 30));
		AutoCompletion.enable(optionBox);

		final String promptString = Languages.getString("ListRequirementGraphic.0"); //$NON-NLS-1$
		add(new JLabel(String.format(promptString, requirement.key())));
		add(optionBox);
	}

	@Override
	public void update() {
		final Vector<T> newOptions = new Vector<>(requirement.getOptions());

		if (!newOptions.equals(currentOptions)) {
			currentOptions = newOptions;
			if (currentOptions.isEmpty())
				throw new NoSuchElementException(
				        String.format("No options for ListRequirement with key '%s'", //$NON-NLS-1$
				                requirement.key()));

			optionBox.setModel(new DefaultComboBoxModel<>(currentOptions = newOptions));
		}

		if (requirement.finalised())
			optionBox.setEnabled(false);
	}

	@Override
	public void reset() {
		optionBox.setSelectedItem(requirement.defaultValue());
		border.setColor(Color.BLUE);
	}

	@Override
	public void fulfilRequirement() {
		requirement.fulfil(optionBox.getSelectedItem());
		border.setColor(Color.BLUE);
	}

	@Override
	public void onNotFulfilled() {
		optionBox.setSelectedIndex(-1);
		border.setColor(Color.RED);
	}

	@Override
	protected void onFocusGained() {
		optionBox.requestFocus();
	}
}
