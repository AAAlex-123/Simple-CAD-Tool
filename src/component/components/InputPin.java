package component.components;

import static myUtil.Utility.foreach;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import exceptions.ComponentNotFoundException;
import exceptions.MalformedGateException;

/**
 * Corresponds to the {@link ComponentType#INPUT_PIN INPUT_PIN} type.
 *
 * @author alexm
 */
final class InputPin extends Component {

	private static final long serialVersionUID = 4L;

	private final Vector<Component> outputBranches;
	private boolean              active;

	private final ComponentGraphic g;

	/** Constructs an InputPin */
	InputPin() {
		g = new InputPinGraphic(this);
		outputBranches = new Vector<>(1, 1);
		active = false;
	}

	@Override
	public ComponentType type() {
		return ComponentType.INPUT_PIN;
	}

	@Override
	protected void wake_up(boolean newActive, int index, boolean prevHidden) {
		checkIndex(index, 1);

		// once hidden cannot be un-hidden
		if (hidden() && !prevHidden)
			throw new MalformedGateException(this);

		if (prevHidden)
			hideComponent();

		// propagate signal only if it's different
		if (active != newActive) {
			active = newActive;
			getGraphics().repaint();
			foreach(outputBranches, b -> b.wake_up(active, hidden()));
		}
	}

	@Override
	protected int inCount() {
		return 0;
	}

	/**
	 * Proper way for the client (the Factory) to set input. This method, unlike
	 * wake_up, will throw when it is called on a hidden {@code InputPin}.
	 *
	 * @param newActive the new value for the active state of this InputPin
	 */
	void setActive(boolean newActive) {
		checkChangeable();
		wake_up(newActive);
	}

	@Override
	protected boolean getActive(int index) {
		checkIndex(index, outCount());
		return active;
	}

	@Override
	protected void addOut(Branch b, int index) {
		checkIndex(index, outCount());
		checkChangeable();
		outputBranches.add(b);
	}

	@Override
	protected void removeOut(Branch b, int index) {
		checkIndex(index, outCount());
		checkChangeable();
		if (!outputBranches.remove(b))
			throw new ComponentNotFoundException(b, this);
	}

	/**
	 * Marks this Component as unchangeable because it's hidden in a {@code Gate}.
	 * Normally should only be called during the construction of a {@code Gate}.
	 */
	void setOuterGate() {
		checkChangeable();
		hideComponent();
	}

	@Override
	protected void destroySelf() {
		foreach(getInputs(), Component::destroy);
		outputBranches.clear();
	}

	@Override
	protected void restoreDeletedSelf() {}

	@Override
	protected void restoreSerialisedSelf() {}

	@Override
	protected List<Component> getInputs() {
		return Collections.unmodifiableList(Collections.emptyList());
	}

	@Override
	protected List<List<Component>> getOutputs() {
		List<List<Component>> ls = new ArrayList<>();
		ls.add(Collections.unmodifiableList(outputBranches));
		return Collections.unmodifiableList(ls);
	}

	@Override
	public ComponentGraphic getGraphics() {
		return g;
	}
}
