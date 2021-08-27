package requirement.graphics;

import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import javax.swing.JPanel;

import requirement.requirements.AbstractRequirement;

/**
 * A class that provides a GUI that can be used to fulfil an
 * {@link requirement.requirements.AbstractRequirement AbstractRequirement}. The
 * Graphic can use information from its JComponents (the text of a text area,
 * the selected item of a drop-down list etc.) to fulfil the {@code Requirement}
 * associated with it and can fetch information from it in order to reset itself
 * and update its display.
 * <p>
 * While Graphics can be used as-is by adding them to another swing component,
 * this would require providing additional functionality to the component for
 * calling the Graphic's public methods in order to interact with it and use it
 * to fulfil its {@code Requirement}. A complete implementation of this
 * functionality can be found in the {@link RequirementsDialog Dialog}.
 *
 * @param <T> the concrete subclass of
 *            {@link requirement.requirements.AbstractRequirement
 *            AbstractRequirement} this Graphic is associated with
 *
 * @author alexm
 */
public abstract class AbstractRequirementGraphic<T extends AbstractRequirement> extends JPanel {

	/**
	 * The {@code AbstractRequirement} associated with this Graphic. The Graphic
	 * fulfils this {@code Requirement} and requests information from it in order to
	 * update its state and display.
	 */
	protected final T req;

	/**
	 * Constructs the Graphic using the {@code Requirement} that will be associated
	 * with it.
	 *
	 * @param requirement the Requirement
	 */
	public AbstractRequirementGraphic(T requirement) {
		req = requirement;

		setFocusTraversalKeysEnabled(true);
		addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				onFocusGained();
			}
		});
	}

	/**
	 * Fetches information from the {@code Requirement} to update this Graphic.
	 * After calling this method, the Graphic should fully reflect the state of its
	 * associated {@code Requirement}.
	 * <p>
	 * <b>Warning:</b> the state of a Requirement must always align with its
	 * Graphic. Failing to call this method after any changes to the underlying
	 * Requirement may result in undefined behaviour.
	 */
	public abstract void update();

	/**
	 * Using the available information, resets this Graphic to its initial state
	 * displaying to the user the default value of the {@code Requirement}, if any.
	 *
	 * @see AbstractRequirement#defaultValue
	 */
	public abstract void reset();

	/**
	 * Uses the information of this Graphic to fulfil the {@code Requirement}
	 * associated with it.
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
