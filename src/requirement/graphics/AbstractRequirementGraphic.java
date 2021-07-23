package requirement.graphics;

import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import javax.swing.JPanel;

import requirement.requirements.AbstractRequirement;

/**
 * A class providing a GUI that can be used to fulfil an
 * {@link requirement.requirements.AbstractRequirement AbstractRequirement}. The
 * Graphic can use information from its components (the text of a text area, the
 * selected item of a drop-down list, etc.) to fulfil the Requirement and can
 * also fetch information from it in order to set its default value.
 *
 * @author alexm
 */
public abstract class AbstractRequirementGraphic extends JPanel {

	/** The {@code AbstractRequirement} associated with this Graphic */
	protected final AbstractRequirement req;

	/**
	 * Constructs the Graphic using the Requirement associated with it.
	 *
	 * @param requirement the Requirement
	 */
	public AbstractRequirementGraphic(AbstractRequirement requirement) {
		this.req = requirement;

		this.setFocusTraversalKeysEnabled(true);
		this.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				onFocusGained();
			}
		});
	}

	/** Fetches information from the {@code Requirement} to update this Graphic */
	public abstract void update();

	/**
	 * Resets this Graphic to its initial state optionally displaying to the user
	 * the default value of the {@code Requirement}, if any.
	 *
	 * @see AbstractRequirement#defaultValue
	 */
	public abstract void reset();

	/**
	 * Uses the information of this Graphic to fulfil the {@code Requirement}
	 * associated with this Graphics object.
	 *
	 * @see AbstractRequirement#fulfil(Object)
	 */
	public abstract void fulfilRequirement();

	/**
	 * Defines how this Graphic should react when the {@code Requirement} is not
	 * fulfilled. Since the Graphic is merely a JPanel that is meant to be placed
	 * inside another Container (e.g. a dialog), this method can be used when the
	 * surrounding Container checks if the set of Requirements it displays are all
	 * fulfilled. For any that aren't, it can call this method to change this
	 * Graphic's display and inform the user about it.
	 */
	public abstract void onNotFulfilled();

	/** Defines how the Graphic should react when it gains focus */
	protected abstract void onFocusGained();
}
