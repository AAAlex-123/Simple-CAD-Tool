package component.components;

import static components.ComponentType.BRANCH;
import static components.ComponentType.INPUT_PIN;
import static components.ComponentType.OUTPUT_PIN;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import exceptions.InvalidIndexException;
import exceptions.MalformedBranchException;
import exceptions.MalformedGateException;

/**
 * Corresponds to the {@link ComponentType#BRANCH BRANCH} type.
 *
 * @author alexm
 */
final class Branch extends Component {

	private static final long serialVersionUID = 4L;

	private final Component	in, out;
	private final int		indexIn, indexOut;

	private boolean active = false;

	private final ComponentGraphic g;

	/**
	 * Constructs a {@code Branch} between two Components at the specified indexes.
	 *
	 * @param inComponent  the Branch's input
	 * @param inIndex      the index of the pin on the {@code in} component
	 * @param outComponent the Branch's output
	 * @param outIndex     the index of the pin on the {@code out} component
	 *
	 * @throws MalformedBranchException in the case of an invalid connection
	 */
	Branch(Component inComponent, int inIndex, Component outComponent, int outIndex)
			throws MalformedBranchException {
		if (
				(inComponent == null) || (outComponent == null) || (inComponent.type() == OUTPUT_PIN)
				|| (outComponent.type() == INPUT_PIN)
				|| (inComponent.type() == BRANCH) || (outComponent.type() == BRANCH)
				)
			throw new MalformedBranchException(inComponent, outComponent);
		if (inIndex >= inComponent.outCount())
			throw new MalformedBranchException(inComponent, inIndex);
		if (outIndex >= outComponent.inCount())
			throw new MalformedBranchException(outComponent, outIndex);

		g = new BranchGraphic(this);
		in = inComponent;
		out = outComponent;
		indexIn = inIndex;
		indexOut = outIndex;

		connect();
	}

	@Override
	public ComponentType type() {
		return ComponentType.BRANCH;
	}

	@Override
	protected void wake_up(boolean newActive, int index, boolean prevHidden) {
		checkIndex(index, inCount());

		// once hidden cannot be un-hidden
		if (hidden() && !prevHidden)
			throw new MalformedGateException(this);

		if (prevHidden)
			hideComponent();

		// repaint and propagate signal only if it's different
		if (active != newActive) {
			active = newActive;
			getGraphics().repaint();
			out.wake_up(active, indexOut, hidden());
		}
	}

	@Override
	protected boolean getActive(int index) {
		checkIndex(index, inCount());
		return active;
	}

	/**
	 * Informs its {@code in} and {@code out} Components that this Branch connects
	 * to them. This method is called once during construction and can be called
	 * again after a branch has been destroyed to reconnect it.
	 */
	void connect() {
		try {
			in.addOut(this, indexIn);
			out.setIn(this, indexOut);
		} catch (UnsupportedOperationException | InvalidIndexException e) {
			// don't leave hanging connections
			destroy();
			throw e;
		}

		// set active to false so `wake_up` always propagates
		active = !in.getActive(indexIn);
		wake_up(in.getActive(indexIn));

		// update the Branch's graphics
		g.updateOnMovement();
	}

	@Override
	protected void destroySelf() {
		checkChangeable();
		in.removeOut(this, indexIn);
		out.removeIn(this, indexOut);

		// inform `out` that there is no longer an input
		out.wake_up(false, indexOut);
	}

	@Override
	protected void restoreDeletedSelf() {
		connect();
	}

	@Override
	protected void restoreSerialisedSelf() {}

	@Override
	protected List<Component> getInputs() {
		List<Component> ls = new ArrayList<>();
		ls.add(in);
		return Collections.unmodifiableList(ls);
	}

	@Override
	protected List<List<Component>> getOutputs() {
		List<List<Component>> ls  = new ArrayList<>();
		List<Component>       ls1 = new ArrayList<>();
		ls1.add(out);
		ls.add(Collections.unmodifiableList(ls1));
		return Collections.unmodifiableList(ls);
	}

	@Override
	public ComponentGraphic getGraphics() {
		return g;
	}
}
