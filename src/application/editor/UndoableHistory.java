package application.editor;

import java.util.List;
import java.util.Stack;
import java.util.Vector;

/**
 * A wrapper for implementing undo and redo functionality.
 *
 * @param <T> the type of {@code Undoable} object that will be stored
 *
 * @author Alex Mandelias
 *
 * @see Undoable
 */
public final class UndoableHistory<T extends Undoable> {

	private final Stack<T> past, future;

	/** Initialises the UndoableHistory */
	public UndoableHistory() {
		past = new Stack<>();
		future = new Stack<>();
	}

	/**
	 * Adds the given {@code Undoable} to the history <i>without</i> executing it.
	 *
	 * @param undoable the undoable
	 */
	public void add(T undoable) {
		past.push(undoable);

		// flush the redo history
		if (!future.isEmpty())
			future.clear();
	}

	/**
	 * Undoes the last {@code Undoable}. If there are no {@code Undoables} to be
	 * undone this method does nothing.
	 */
	public void undo() {
		if (canUndo()) {
			T last = past.pop();
			last.unexecute();
			future.push(last);
		}
	}

	/**
	 * Re-does the last {@code Undoable}. If there are no {@code Undoables} to be
	 * re-done this method does nothing
	 */
	public void redo() {
		if (canRedo()) {
			T first = future.pop();

			// this Undoable has executed successfully before; this statement can't throw
			try {
				first.execute();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			past.push(first);
		}
	}

	/** Clears the past and future parts of this UndoableHistory */
	public void clear() {
		past.clear();
		future.clear();
	}

	/**
	 * Returns whether or not there is an {@code Undoable} to be undone.
	 *
	 * @return {@code true} if there is, {@code false} otherwise
	 */
	public boolean canUndo() {
		return !past.isEmpty();
	}

	/**
	 * Returns whether or not there is an {@code Undoable} to be redone.
	 *
	 * @return {@code true} if there is, {@code false} otherwise
	 */
	public boolean canRedo() {
		return !future.isEmpty();
	}

	/**
	 * Returns a <i>copy</i> of the past part of this history.
	 *
	 * @return a List with the previously executed Undoables
	 */
	public List<T> getPast() {
		return new Vector<>(past);
	}

	/**
	 * Returns a <i>copy</i> of the future part of this history.
	 *
	 * @return a List with the previously un-executed Undoables
	 */
	public List<T> getFuture() {
		return new Vector<>(future);
	}
}
