package requirement.graphics;

import java.awt.Color;
import java.awt.Dimension;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JFrame;
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
	
	public static void main(String[] args) {
		JFrame uwuFrame = new JFrame("uwu test uwu");
		List<Object> list = new LinkedList<Object>();
		list.add("1");
		list.add("2");
		list.add("3");
		
		uwuFrame.add(new ListRequirementGraphic(new ListRequirement("keylol",list)));
		uwuFrame.setSize(new Dimension(500, 500));
		uwuFrame.setVisible(true);
	}

	public ListRequirementGraphic(ListRequirement<T> requirement) {
		super(requirement);
		
		Vector<T> list = new Vector<T>();
		for(T option : requirement.getOptions())
			list.add(option);
		
		border = new MutableColorBorder(Color.BLUE);
		this.optionBox = new JComboBox<T>(new DefaultComboBoxModel<T>(list)); 
		this.optionBox.setBorder(border);
		AutoCompletion.enable(optionBox);
	
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		add(new JLabel("Choose an option for " + req.key() +":")); //idk how to write loc lolololol
		add(Box.createRigidArea(new Dimension(10, 1)));
		add(this.optionBox);
		optionBox.setMaximumSize(new Dimension(200,30));
	}

	@Override
	public void update() {
		if (req.finalised())
			options.setEnabled(false);
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

}
