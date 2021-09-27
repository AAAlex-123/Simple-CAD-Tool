package requirement.graphics;

import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import javax.swing.JPanel;

import requirement.requirements.AbstractRequirement;
import requirement.util.RequirementsDialog;

/**
 * A GUI that can be used to fulfil an {@link AbstractRequirement}. The Graphic
 * uses information from its JComponents (the text of a text area, the selected
 * item of a drop-down list etc.) to fulfil the {@code Requirement} associated
 * with it and also fetches information from it in order to reset itself and
 * update its display.
 * <p>
 * While Graphics can be used as-is by adding them to another swing Component,
 * doing so would require providing additional functionality to the Component
 * for calling the Graphic's public methods in order to interact with it and use
 * it to fulfil its {@code Requirement}. A complete implementation of this
 * functionality can be found in the {@link RequirementsDialog}.
 *
 * @param <T> the concrete subclass of {@code AbstractRequirement} this Graphic
 *            is associated with
 *
 * @author Alex Mandelias
 */
public abstract class AbstractRequirementGraphic<T extends AbstractRequirement> extends JPanel {

	/**
	 * The {@code Requirement} associated with this Graphic. The Graphic fulfils it
	 * and requests information from it in order to update its state and display.
	 */
	protected final T requirement;

	/**
	 * Constructs a Graphic using a {@code Requirement}.
	 *
	 * @param requirement the Requirement that will be associated with this Graphic
	 *
	 * @see #requirement
	 */
	public AbstractRequirementGraphic(T requirement) {
		this.requirement = requirement;

		setFocusable(true);
		addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				// TODO: fix the focus spaghetti
				onFocusGained();
			}
		});
	}

	/**
	 * Fetches information from its associated {@code Requirement} to update itself.
	 * <p>
	 * <b>Warning:</b> the state of a Requirement must always align with its
	 * Graphic. Failing to call this method after any changes to the underlying
	 * Requirement may result in undefined behaviour.
	 *
	 * @implSpec after calling this method, this Graphic shall fully reflect the
	 *           state of its associated {@code Requirement}. If the Requirement's
	 *           state doesn't allow this Graphic to function any more, a
	 *           RuntimeException shall be thrown.
	 */
	public abstract void update();

	/**
	 * Resets this Graphic to its initial state.
	 *
	 * @see AbstractRequirement#defaultValue
	 *
	 * @implSpec after calling this method, this Graphic shall return to its first
	 *           layout (the one after construction) and show to the user the
	 *           default value of the {@code Requirement}, if any.
	 */
	public abstract void reset();

	/**
	 * Uses the information of this Graphic to fulfil the {@code Requirement}
	 * associated with it.
	 *
	 * @see AbstractRequirement#fulfil(Object)
	 *
	 * @implSpec this method shall call the {@code fulfil(Object)} method of the
	 *           associated Requirement using information from the JComonents of
	 *           this JPanel to construct the parameter
	 */
	public abstract void fulfilRequirement();

	/**
	 * Defines how this Graphic should react when the {@code Requirement} is not
	 * fulfilled.
	 *
	 * @implSpec after calling this method, this Graphic shall inform the user
	 *           (ideally without disrupting the rest of the application via a
	 *           pop-up window) that its {@code Requirement} is not fulfilled
	 *           (irrespectively of whether or not it is actually fulfilled).
	 */
	public abstract void onNotFulfilled();

	/**
	 * Defines how the Graphic should react when it gains focus.
	 *
	 * @implSpec after calling this method the JComponent responsible for getting
	 *           user input shall be the focused one in this JPanel
	 */
	protected abstract void onFocusGained();

	@Override
	public final String toString() {
		return String.format("Graphic for Requirement: %s", requirement); //$NON-NLS-1$
	}
}
