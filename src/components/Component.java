package components;

import exceptions.ComponentNotAccessibleException;
import exceptions.InvalidIndexException;

@SuppressWarnings("javadoc")
public abstract class Component {

	boolean active, changeable;

	public Component() {
		active = false;
		changeable = true;
	}

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

	final void checkChangeable() {
		if (!changeable)
			throw new ComponentNotAccessibleException();
	}

	final void checkIndex(int index, int indexMax) {
		if ((index < 0) || (index >= indexMax))
			throw new InvalidIndexException(this, index);
	}

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
