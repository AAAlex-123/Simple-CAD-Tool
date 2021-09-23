package component.components;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import component.ComponentType;
import component.exceptions.ComponentNotAccessibleException;
import component.exceptions.GraphicMismatchException;
import component.exceptions.InvalidIndexException;
import component.graphics.ComponentGraphic;

/**
 * A class representing a Component that is connected to other Components,
 * carries a signal and can be drawn.
 * <p>
 * Each Component has a {@link ComponentType Type} and is {@link Identifiable}
 * by a String that can be altered. A Component may optionally be accompanied by
 * a {@link ComponentGraphic} to allow users to interact with it using a GUI.
 * <p>
 * Almost all of the Component's methods are protected therefore the client may
 * use the {@link ComponentFactory} to interact with them.
 * <p>
 * Components may be used in the context of an {@link application.editor.Editor
 * Editor} or {@code Application} which manages when they are created and
 * deleted. This Application will be referenced throughout the documentation.
 *
 * @author Alex Mandelias
 */
public abstract class Component implements Identifiable<String>, Serializable {

	private static final long serialVersionUID = 6L;

	// 4 private fields

	/** ID for this Component */
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
	 * {@link #destroy()} is called, this flag is set to true. The application is
	 * responsible to check, using {@link #toRemove()}, if a Component is destroyed
	 * and must therefore be removed.
	 */
	protected boolean toBeRemoved;

	/** {@code Graphic} for this Component, created lazily, on-demand */
	private transient ComponentGraphic g;

	// 6 core methods

	/** Constructs the Component */
	protected Component() {
		componentID = ""; //$NON-NLS-1$
		hidden = false;
		toBeRemoved = false;
	}

	/**
	 * Returns the Component's {@code Type}.
	 *
	 * @return the type
	 *
	 * @see ComponentType
	 */
	public abstract ComponentType type();

	/**
	 * Returns the Component's {@code description}, an extremely short string that
	 * briefly describes the purpose of this Component or its function. It is meant
	 * to be the technical name of the Component and is displayed in its Graphic. To
	 * avoid cluttering the Graphic, this method should be overridden when the
	 * Graphic alone doesn't convey enough information in order to understand what a
	 * built-in Component does.
	 *
	 * @return the Component's description
	 */
	protected String description() {
		return ""; //$NON-NLS-1$
	}

	/**
	 * Returns the {@code Graphic} object associated with this Component. The first
	 * time this method is called a {@code Graphic} object is created referencing
	 * this {@code Component}, effectively synchronising them with one another.
	 *
	 * @return the ComponentGraphics object
	 */
	public final ComponentGraphic getGraphics() {
		if (g == null)
			g = ComponentGraphic.forComponent(this);
		return g;
	}

	/**
	 * Sets the {@code Graphic} object associated with this Component.
	 *
	 * @param g the new Graphic object for this Component
	 *
	 * @throws GraphicMismatchException if this Component can't use the Graphic
	 */
	protected final void setGraphics(ComponentGraphic g) {
		if (type() != g.type())
			throw new GraphicMismatchException(this.type(), g.type());

		if (!g.matchesComponent(this))
			throw new GraphicMismatchException(this, g);

		this.g = g;
	}

	/**
	 * Repaints the {@code Graphic} object of this Component only if it exists. This
	 * method, unlike the previous {@code getGraphics().repaint()} statement, avoids
	 * creating a {@code Graphic} object to repaint if it doesn't exist.
	 */
	protected final void repaintGraphicIfExists() {
		if (g != null)
			g.repaint();
	}

	/**
	 * The core of the library: all Components are able to propagate a received
	 * signal to other components.
	 * <p>
	 * Specifically, when the signal (a boolean) changes to the {@code newActive}
	 * value in the Component's input at a specific {@code index}, the Component may
	 * propagate it to all of the Components it is connected to. The
	 * {@code "hiddenness"} of the previous Component is also propagated so that a
	 * chain of Components can all update their "hiddenness" when one is altered.
	 *
	 * @param newActive  the new signal received
	 * @param index      the index at which it was received
	 * @param prevHidden the "hiddenness" of the previous component
	 */
	protected abstract void wake_up(boolean newActive, int index, boolean prevHidden);

	/**
	 * Returns the active state of the Component's input pin at the {@code index}.
	 *
	 * @param index the Component's input pin index
	 *
	 * @return {@code true} if active, {@code false} otherwise
	 */
	protected abstract boolean getActiveIn(int index);

	/**
	 * Returns the active state of the Component's output pin at the {@code index}.
	 *
	 * @param index the Component's output pin index
	 *
	 * @return {@code true} if active, {@code false} otherwise
	 */
	protected abstract boolean getActiveOut(int index);

	// 4 methods to change the inputs and outputs of a Component.
	// Only Branches should call them.
	// - set/add are called by Branch.connect()
	// - remove in/out are called by Branch.destroy()

	/**
	 * Sets the {@code branch} as the Component's Input at the {@code index}.
	 *
	 * @param branch the Branch
	 * @param index  the index the Branch should connect to
	 */
	@SuppressWarnings("unused")
	protected void setIn(Branch branch, int index) {
		throw new UnsupportedOperationException(String.format(
		        "Components of type %s don't support setIn(Branch, int)", type().description())); //$NON-NLS-1$
	}

	/**
	 * Adds the {@code branch} to the Component's Outputs at the {@code index}.
	 *
	 * @param branch the Branch
	 * @param index  the index the Branch should connect to
	 */
	@SuppressWarnings("unused")
	protected void addOut(Branch branch, int index) {
		throw new UnsupportedOperationException(String.format(
		        "Components of type %s don't support addOut(Branch, int)", type().description())); //$NON-NLS-1$
	}

	/**
	 * Removes the {@code branch} from the Component's Input at the {@code index}.
	 *
	 * @param branch the Branch
	 * @param index  the index the Branch is connected to
	 */
	@SuppressWarnings("unused")
	protected void removeIn(Branch branch, int index) {
		throw new UnsupportedOperationException(String.format(
		        "Components of type %s don't support removeIn(Branch, int)", type().description())); //$NON-NLS-1$
	}

	/**
	 * Removes the {@code branch} from the Component's Output at the {@code index}.
	 *
	 * @param branch the Branch
	 * @param index  the index the Branch is connected to
	 */
	@SuppressWarnings("unused")
	protected void removeOut(Branch branch, int index) {
		throw new UnsupportedOperationException(String.format(
		        "Components of type %s don't support removeOut(Branch, int)", //$NON-NLS-1$
		        type().description()));
	}

	// 2 + 4 methods for destroying and restoring Components

	/**
	 * Specifies what this Component should do when it is destroyed. Subclasses
	 * specify how Components that are connected to this Component should react
	 * using the template method {@link #destroySelf()}.
	 * <p>
	 * In principle, a destroyed Component isn't referenced by any other Component
	 * and when the application removes it, it should be garbage collected.
	 *
	 * @see #toBeRemoved
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
		if (g != null)
			g.restoreDeleted();
	}

	/** Each Component specifies how it is restored after destruction */
	protected abstract void restoreDeletedSelf();

	/** Restores the state of the Component after it was deserialised */
	protected final void restoreSerialised() {
		if (g != null)
			g.restoreSerialised();
	}

	// 4 methods to access specific parts and information of the Component

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
	 * Returns the number of available inputs of this Component
	 *
	 * @return the number of available inputs
	 *
	 * @implNote default is {@code 1}
	 */
	protected int inCount() {
		return 1;
	}

	/**
	 * Returns the number of available outputs of this Component
	 *
	 * @return the number of available outputs
	 *
	 * @implNote default is {@code 1}
	 */
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
	 * Checks if the {@code index} given by another Component wishing to access this
	 * component does not exceed {@code indexMax} (specified by this Component).
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
	 * Returns whether or not this Component is {@code hidden} inside another
	 * Component.
	 *
	 * @return {@code true} if it is hidden, {@code false} otherwise
	 *
	 * @see #hidden
	 */
	protected final boolean hidden() {
		return hidden;
	}

	/**
	 * Marks this Component as hidden
	 *
	 * @see #hidden
	 */
	protected final void hideComponent() {
		hidden = true;
	}

	/**
	 * Returns whether or not the application should remove this Component.
	 *
	 * @return {@code true} if the Component should be removed, {@code false}
	 *         otherwise.
	 *
	 * @see #toBeRemoved
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

	/**
	 * @implNote this implementation is more like a debug String. For a
	 *           user-friendly description of this Component
	 *           {@code type().description()} should be used.
	 */
	@Override
	public final String toString() {
		return String.format("%s: %d-%d, UID: %s, hidden: %s", type().description(), inCount(), //$NON-NLS-1$
		        outCount(), getID(), hidden());
	}
}
