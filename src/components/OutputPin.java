package components;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import exceptions.MalformedGateException;

/**
 * Corresponds to the {@link ComponentType#OUTPUT_PIN OUTPUT_PIN} type
 *
 * @author alexm
 */
final class OutputPin extends Component {

	private static final long serialVersionUID = 4L;

	private Component inputBranch;
	private boolean active;

	private final ComponentGraphic g;

	// information about the enclosing Gate necessary for signal transmission
	private Gate outerGate;
	private int  outerGateIndex;

	/** Constructs an OuputPin */
	OutputPin() {
		g = new OutputPinGraphic(this);
		active = false;
	}

	@Override
	public ComponentType type() {
		return ComponentType.OUTPUT_PIN;
	}
	
	@Override
	public List<Component> getInputs() {
		if (inputBranch == null)
			return Collections.emptyList();

		List<Component> ls = new ArrayList<>();
		ls.add(inputBranch);
		return Collections.unmodifiableList(ls);
	}

	@Override
	public List<List<Component>> getOutputs() {
		return Collections.emptyList();
	}

	@Override
	protected void wake_up(boolean newActive, int index, boolean prevHidden) {
		checkIndex(index, inCount());

		// once hidden cannot be un-hidden
		if (hidden() && !prevHidden)
			throw new MalformedGateException(this);

		if (prevHidden)
			hideComponent();

		// propagate signal only if it's different
		if (active != newActive) {
			active = newActive;
			getGraphics().repaint();

			// inform the enclosing Gate that an output has changed
			if (outerGate != null)
				outerGate.outputChanged(outerGateIndex);
		}
	}

	@Override
	protected int outCount() {
		return 0;
	}

	/**
	 * Proper way for the client (the Factory) to get output.
	 *
	 * @return the active state of this OutputPin
	 */
	boolean getActive() {
		checkChangeable();
		return active;
	}

	@Override
	protected boolean getActive(int index) {
		checkIndex(index, inCount());
		return active;
	}

	@Override
	protected void setIn(Branch b, int index) {
		checkIndex(index, inCount());
		checkChangeable();

		if (inputBranch != null) {
			// declare that the connected branches should be destroyed
			// the application should take care of destroying the Branch
			inputBranch.toBeRemoved = true;
		}

		inputBranch = b;
	}

	@Override
	protected void removeIn(Branch b, int index) {
		checkIndex(index, inCount());
		checkChangeable();

		if ((inputBranch == b)) {
			inputBranch = null;
		} else {
			// throw new ComponentNotFoundException(b, this);

			// when a Branch is created, setIn is called
			// this component has in the new branch but
			// the old branch has out this component
			// therefore the old branch must be destroyed
			// but it will call removeIn on this component
			// but it isn't the in of this component :)
		}
	}

	/**
	 * Marks this Component as unchangeable because it's hidden in a {@code Gate}
	 * and sets the {@code gate} as the next component to be woken up. Normally
	 * should only be called during the construction of the {@code gate}.
	 *
	 * @param gate  the next component to be woken up
	 * @param index the pin's index in the gate
	 */
	void setOuterGate(Gate gate, int index) {
		if (outerGate != null)
			checkChangeable();

		hideComponent();

		outerGate = gate;
		outerGateIndex = index;
	}

	@Override
	protected void destroySelf() {
		if (inputBranch != null) {
			inputBranch.destroy();
			inputBranch = null;
		}
	}

	@Override
	protected void restoreDeletedSelf() {}

	@Override
	protected void restoreSerialisedSelf() {}

	@Override
	public ComponentGraphic getGraphics() {
		return g;
	}
}
