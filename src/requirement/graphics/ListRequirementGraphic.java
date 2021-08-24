package requirement.graphics;

import java.awt.Color;
import java.awt.Dimension;
import java.util.NoSuchElementException;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;

import myUtil.MutableColorBorder;
import requirement.requirements.ListRequirement;

/**
 * A graphic component utilizing a drop-down menu to let the user choose options from a list. 
 * Opens a window containing a GUI list with the values provided by the {@link ListRequirement}.
 *
 * @author dimits
 */
public class ListRequirementGraphic<T> extends AbstractRequirementGraphic {
	private final JComboBox<T> optionBox;
	private final MutableColorBorder border;
	
	/**
	 * Constructs a graphics panel containing a drop-down menu
	 * of all the available options for a given 
	 * {@link requirement.requirements.ListRequirement ListRequirement}.
	 * The selected option will fulfill that requirement.
	 * 
	 * @param requirement the {@link requirement.requirements.ListRequirement ListRequirement} 
	 * whose options will be displayed.
	 */
	public ListRequirementGraphic(ListRequirement<T> requirement) {
		super(requirement);
		
		border = new MutableColorBorder(Color.BLUE);
		optionBox = new JComboBox<T>();
		updateOptionBox();
		optionBox.setBorder(border);
		AutoCompletion.enable(optionBox);
	
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		add(new JLabel("Choose an option for " + req.key() +":")); //idk how to write loc lolololol
		add(Box.createRigidArea(new Dimension(10, 1)));
		add(this.optionBox);
		optionBox.setMaximumSize(new Dimension(200,30));
	}

	@Override
	public void update() {
		updateOptionBox();
		
		if(optionBox.getItemCount() == 0)
			throw new NoSuchElementException("There are no available options for the field `" + req.key() +"`");
		
		if (req.finalised())
			optionBox.setEnabled(false);
	}

	@Override
	public void reset() {
		optionBox.setSelectedIndex(-1);
		border.setColor(Color.BLUE);
	}

	@Override
	public void fulfilRequirement() {
		req.fulfil(optionBox.getSelectedItem());
		border.setColor(Color.BLUE);
	}

	@Override
	public void onNotFulfilled() {
		optionBox.setSelectedIndex(-1);
		border.setColor(Color.BLUE);
	}

	@Override
	protected void onFocusGained() {
		optionBox.requestFocusInWindow();
	}
	
	private void updateOptionBox() {
		@SuppressWarnings("unchecked")
		ListRequirement<T> requirement = ((ListRequirement<T>) req);
		Vector<T> list = new Vector<T>();
		for(T option : requirement.getOptions())
			list.add(option);

		this.optionBox.setModel(new DefaultComboBoxModel<T>(list));
	}


}
