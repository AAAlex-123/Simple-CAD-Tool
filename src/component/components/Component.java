package component.components;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import exceptions.ComponentNotAccessibleException;
import exceptions.InvalidIndexException;

/**
 * A class representing a component that is connected to other Components,
 * carries a signal and can be drawn.
 * <p>
 * The Component's methods are package-private therefore the client may use the
 * {@link components.ComponentFactory ComponentFactory} to interact with them.
 * <p>
 * Components may be used in the context of an Application which manages when
 * they are created and deleted. This Application will be referenced throughout
 * the documentation.
 *
 * @author alexm
 */
public abstract class Component implements Identifiable<String>, Serializable {

	private static final long serialVersionUID = 6L;

	// 3 private fields

	/** ID for this Component, to implement the {@link Identifiable} interface */
	private String componentID;

	/**
	 * Indicates whether or not this Component is hidden inside another Component. A
	 * hidden Component cannot be altered in any way, including making it not
	 * hidden. Components become hidden when a Gate is constructed and they are part
	 * of its inner circuit. This property of Components will be referred to as its
	 * "hiddenness".
	 */
	private boolean hidden;

	/**
	 * Indicates whether or not this Component should be removed. When the method
	 * {@link Component#destroy() destroy()} is called, this flag is set to true.
	 * The application is responsible to check, using {@link Component#toRemove()
	 * toRemove()}, if a Component is destroyed and must therefore be removed.
	 */
	protected boolean toBeRemoved;

	// 4 core methods

	/** Constructs the Component */
	protected Component() {
		componentID = ""; //$NON-NLS-1$
		hidden = false;
		toBeRemoved = false;
	}

	/**
	 * Returns the Component's type, as described by {@link ComponentType}.
	 *
	 * @return the type
	 */
	public abstract ComponentType type();

	/**
	 * The core of the library: all Components are able to propagate a received
	 * signal to other components.
	 * <p>
	 * Specifically, when the signal (a boolean value) changes to the
	 * {@code newActive} value in the Component's input at a specific {@code index},
	 * the Component may propagate it to all of the Components it is connected to.
	 * The {@code "hiddenness"} of the previous Component is also propagated so that
	 * a chain of Components can all update their "hiddenness" when one is altered.
	 *
	 * @param newActive  the new signal received
	 * @param index      the index at which it was received
	 * @param prevHidden the "hiddenness" of the previous component
	 */
	protected abstract void wake_up(boolean newActive, int index, boolean prevHidden);

	/**
	 * Returns the active state of the Component's pin at the specified index.
	 *
	 * @param index the Component's Pin index
	 *
	 * @return true if active, false otherwise
	 */
	protected abstract boolean getActive(int index);

	// 4 methods to change the inputs and outputs of a Component.
	// Only Branches should call them.
	// - set/add are called by Branch.connect()
	// - remove in/out are called by Branch.destroy()

	/**
	 * Sets the {@code branch} as the Component's Input at {@code index}.
	 *
	 * @param branch the Branch
	 * @param index  the index the Branch should connect to
	 */
	protected void setIn(Branch branch, int index) {
		throw new UnsupportedOperationException(String.format(
				"Components of type %s don't support setIn(Branch, int)", type().description())); //$NON-NLS-1$
	}

	/**
	 * Adds the {@code branch} to the Component's Outputs at {@code index}.
	 *
	 * @param branch the Branch
	 * @param index  the index the Branch should connect to
	 */
	protected void addOut(Branch branch, int index) {
		throw new UnsupportedOperationException(String.format(
				"Components of type %s don't support addOut(Branch, int)", type().description())); //$NON-NLS-1$
	}

	/**
	 * Removes the {@code branch} from the Component's Input at {@code index}.
	 *
	 * @param branch the Branch
	 * @param index  the index the Branch is connected to
	 */
	protected void removeIn(Branch branch, int index) {
		throw new UnsupportedOperationException(String.format(
				"Components of type %s don't support removeIn(Branch, int)", type().description())); //$NON-NLS-1$
	}

	/**
	 * Removes the {@code branch} from the Component's Output at {@code index}.
	 *
	 * @param branch the Branch
	 * @param index  the index the Branch is connected to
	 */
	protected void removeOut(Branch branch, int index) {
		throw new UnsupportedOperationException(String.format(
				"Components of type %s don't support removeOut(Branch, int)", //$NON-NLS-1$
				type().description()));
	}

	// 2 + 4 methods for destroying and restoring Components

	/**
	 * Specifies what this Component should do when it is destroyed. Subclasses
	 * specify how Components that are connected to this Component should react
	 * using the template method {@link Component#destroySelf destroySelf}.
	 * <p>
	 * In principle, a destroyed Component isn't referenced by any other Component
	 * and when the application removes it, it should be garbage collected.
	 *
	 * @see Component#toBeRemoved
	 */
	protected final void destroy() {
		toBeRemoved = true;
		destroySelf();
	}

	/** Each Component specifies how it should destroy itself */
	protected abstract void destroySelf();

	/** Restores the state of the Component after it was destroyed */
	final void restoreDeleted() {
		restoreDeletedSelf();
		toBeRemoved = false;
		getGraphics().restoreDeleted();
	}

	/** Each Component specifies how it is restored after destruction */
	protected abstract void restoreDeletedSelf();

	/** Restores the state of the Component after it was serialised */
	final void restoreSerialised() {
		restoreSerialisedSelf();
		getGraphics().restoreSerialised();
	}

	/** Each Component specifies how it is restored after serialisation */
	protected abstract void restoreSerialisedSelf();

	// 3 methods to access specific parts and information of the Component

	/**
	 * Returns an unmodifiable list with the inputs of this Component as specified
	 * by {@link Collections#unmodifiableList(List)}
	 *
	 * @return the List
	 */
	protected abstract List<Component> getInputs();

	/**
	 * Returns an unmodifiable list with a list of outputs of this Component as
	 * specified by {@link Collections#unmodifiableList(List)}
	 *
	 * @return the List
	 */
	protected abstract List<List<Component>> getOutputs();

	/**
	 * Returns the {@code ComponentGraphics} object associated with this Component.
	 *
	 * @return the ComponentGraphics object
	 */
	public abstract ComponentGraphic getGraphics();

	/** @return the number of incoming connections */
	protected int inCount() {
		return 1;
	}

	/** @return the number of outgoing connections */
	protected int outCount() {
		return 1;
	}

	// 2 general purpose methods

	/**
	 * Checks if this Component is not hidden inside another gate. If it is, it
	 * cannot be modified or accessed in any way, and this method throws.
	 * <p>
	 * This method should be called in every method that changes a Component (e.g. a
	 * method that creates a connection). If everything is designed correctly, this
	 * method should never throw.
	 */
	protected final void checkChangeable() {
		if (hidden())
			throw new ComponentNotAccessibleException(this);
	}

	/**
	 * Checks if the {@code index} given by another component wishing to access this
	 * component does not exceed {@code indexMax} (specified by this component).
	 * <p>
	 * This method should be called in every method that is index-sensitive. If
	 * everything is designed correctly, this method should never throw.
	 *
	 * @param index    the index to check
	 * @param indexMax its maximum value
	 */
	protected final void checkIndex(int index, int indexMax) {
		if ((index < 0) || (index >= indexMax))
			throw new InvalidIndexException(this, index);
	}

	// 5 getters and setters for private fields

	@Override
	public final String getID() {
		return componentID;
	}

	@Override
	public final void setID(String id) {
		componentID = id;
	}

	/**
	 * Returns whether or not this Component is {@link Component#hidden hidden}
	 * inside another Component.
	 *
	 * @return true if it is hidden, false otherwise
	 */
	protected final boolean hidden() {
		return hidden;
	}

	/** Marks this Component as hidden */
	protected final void hideComponent() {
		hidden = true;
	}

	/**
	 * Returns whether or not the application should remove this Component.
	 *
	 * @return true if the Component should be removed, false otherwise.
	 *
	 * @see Component#toBeRemoved
	 */
	final boolean toRemove() {
		return toBeRemoved;
	}

	// 3 convenience methods for `wake_up`

	/**
	 * Same as wake_up, but "hiddenness" is assumed to be the same (e.g. when
	 * connecting a Branch, "hiddenness" can't have changed)
	 *
	 * @param newActive the new signal received
	 * @param index     the index at which it was received
	 */
	protected final void wake_up(boolean newActive, int index) {
		wake_up(newActive, index, hidden());
	}

	/**
	 * Same as wake_up, but index is assumed to be 0 (e.g. for Branch it's always 0)
	 *
	 * @param newActive  the new signal received
	 * @param prevHidden the "hiddenness" of the previous component
	 */
	protected final void wake_up(boolean newActive, boolean prevHidden) {
		wake_up(newActive, 0, prevHidden);
	}

	/**
	 * Same as wake_up, but both index and "hiddenness" are both assumed to be 0 and
	 * the same (e.g. see two above e.g.)
	 *
	 * @param newActive the new signal received
	 */
	protected final void wake_up(boolean newActive) {
		wake_up(newActive, 0, hidden());
	}

	// toString

	@Override
	public final String toString() {
		return String.format("%s: %d-%d, UID: %s, hidden: %s", type().description(), inCount(), //$NON-NLS-1$
				outCount(), getID(), hidden());
	}
}
