package components;

import exceptions.ComponentNotAccessibleException;
import exceptions.InvalidIndexException;

/**
 * A class representing a component that is connected to other Components,
 * carries a signal and can also be drawn onto the screen.
 * <p>
 * The Component's methods are package-private therefore the client may use the
 * {@link components.ComponentFactory ComponentFactory} to interact with them.
 */
public abstract class Component {

	boolean active = false, changeable = true;

	// ===== CIRCUITING =====

	// the main method: carries forward the signal is has just received
	abstract void wake_up(boolean newActive, int index, boolean prevChangeable);

	void setIn(Branch b, int index) {
		throw new UnsupportedOperationException(String.format(
				"Components of type %s don't support setIn(Branch, int)",
				this.getClass().getSimpleName()));
	}

	void addOut(Branch b, int index) {
		throw new UnsupportedOperationException(String.format(
				"Components of type %s don't support addOut(Branch, int)",
				this.getClass().getSimpleName()));
	}

	void removeIn(Branch d, int index) {
		throw new UnsupportedOperationException(String.format(
				"Components of type %s don't support removeIn(Branch, int)",
				this.getClass().getSimpleName()));
	}

	void removeOut(Branch d, int index) {
		throw new UnsupportedOperationException(String.format(
				"Components of type %s don't support removeOut(Branch, int)",
				this.getClass().getSimpleName()));
	}

	// checks if the Component is not "hidden" inside another gate
	// if it is, it cannot be modified or accessed in any way.
	// This should should never throw.
	final void checkChangeable() {
		if (!changeable)
			throw new ComponentNotAccessibleException();
	}

	// checks the `index` (given by another object) to the `indexMax`
	// (specified by this component) for validity.
	// This should should never throw.
	final void checkIndex(int index, int indexMax) {
		if ((index < 0) || (index >= indexMax))
			throw new InvalidIndexException(this, index);
	}

	// more fancy ways to propagate a signal

	final void wake_up(boolean newActive, int index) {
		wake_up(newActive, index, changeable);
	}

	final void wake_up(boolean newActive, boolean prevChangeable) {
		wake_up(newActive, 0, prevChangeable);
	}

	final void wake_up(boolean newActive) {
		wake_up(newActive, 0, changeable);
	}

	@Override
	public String toString() {
		String str = String.format("%s", getClass().getSimpleName());
		return changeable ? str : "(" + str + ")";
	}
}
