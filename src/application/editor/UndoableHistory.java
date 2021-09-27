package application.editor;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * A wrapper for implementing undo and redo functionality.
 *
 * @param <T> the type of {@code Undoable} object that will be stored
 *
 * @author Alex Mandelias
 *
 * @see Undoable
 */
final class UndoableHistory<T extends Undoable> {

	private final Stack<T> past, future;

	/** Constructs an empty UndoableHistory object */
	public UndoableHistory() {
		past = new Stack<>();
		future = new Stack<>();
	}

	/**
	 * Adds an {@code Undoable} to the history without executing it.
	 *
	 * @param undoable the undoable
	 */
	public void add(T undoable) {
		past.push(undoable);

		// flush the redo history
		if (!future.isEmpty())
			future.clear();
	}

	/** Undoes the most recently executed {@code Undoable}, if one exists */
	public void undo() {
		if (canUndo()) {
			final T last = past.pop();
			last.unexecute();
			future.push(last);
		}
	}

	/** Re-does the most recently undone {@code Undoable}, if one exists */
	public void redo() {
		if (canRedo()) {
			final T first = future.pop();

			// this Undoable has executed successfully before; this statement can't throw
			try {
				first.execute();
			} catch (final Exception e) {
				throw new RuntimeException(e);
			}
			past.push(first);
		}
	}

	/** Empties this UndoableHistory */
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
	 * Returns the past part of this history.
	 *
	 * @return a List with the previously executed Undoables
	 */
	public List<T> getPast() {
		return new ArrayList<>(past);
	}

	/**
	 * Returns the future part of this history.
	 *
	 * @return a List with the previously un-executed Undoables
	 */
	public List<T> getFuture() {
		return new ArrayList<>(future);
	}

	@Override
	public String toString() {
		return String.format("{%s, %s}", past, future); //$NON-NLS-1$
	}
}
