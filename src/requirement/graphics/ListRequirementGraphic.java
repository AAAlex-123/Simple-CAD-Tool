package requirement.graphics;

import java.awt.Color;
import java.awt.Dimension;
import java.util.LinkedList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;

import requirement.requirements.ListRequirement;

/**
 * A "default" implementation of a graphic for lists. 
 * Opens a window containing a GUI list with the values provided by the {@link ListRequirement}.
 *
 * @author dimits
 */
public class ListRequirementGraphic extends AbstractRequirementGraphic {
	private final JList<String> options;
	private final JLabel errorLabel;
	
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

	public ListRequirementGraphic(ListRequirement requirement) {
		super(requirement);
		
		String[] optionStrings = new String[requirement.getOptions().size()]; //build string list from objects
		int index = 0;
		for(Object obj : requirement.getOptions()) {
			optionStrings[index] = obj.toString();
			index++;
		}
		
		this.options = new JList<String>(optionStrings); 
		this.options.setBorder(BorderFactory.createLineBorder(Color.BLUE, 3, true));
		
		this.errorLabel = new JLabel("No option chosen.");			//idk how to write loc lolololol
		this.errorLabel.setForeground(java.awt.Color.RED);
		this.errorLabel.setVisible(false);
		
		this.setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
		add(new JLabel("Choose an option for " + req.key() +":")); //idk how to write loc lolololol
		add(this.options);
		add(this.errorLabel);
	}

	@Override
	public void update() {
		if (req.finalised())
			options.setEnabled(false);
	}

	@Override
	public void reset() {
		options.clearSelection();
		errorLabel.setVisible(false);
	}

	@Override
	public void fulfilRequirement() {
		ListRequirement lsreq = (ListRequirement) req;
		req.fulfil(lsreq.getOptions().get(options.getSelectedIndex())); //get selected object
		errorLabel.setVisible(false);
	}

	@Override
	public void onNotFulfilled() {
		errorLabel.setVisible(true);
	}

	@Override
	protected void onFocusGained() {
		options.requestFocusInWindow();
	}

}
