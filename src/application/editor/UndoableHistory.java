package application.editor;

import java.util.List;
import java.util.Stack;
import java.util.Vector;

/**
 * A wrapper for implementing undo and redo functionality.
 *
 * @param <T> the type of object that will be stored
 *
 * @author alexm
 */
public final class UndoableHistory<T extends Undoable> {

	private final Stack<T> past, future;

	/** Initialises the UndoableHistory */
	public UndoableHistory() {
		past = new Stack<>();
		future = new Stack<>();
	}

	/**
	 * Adds the given {@code Undoable} to the history without executing it.
	 *
	 * @param c the undoable
	 */
	public void add(T c) {
		past.push(c);

		// flush the redo history
		if (!future.isEmpty())
			future.clear();
	}

	/** Undoes the last {@code Undoable} */
	public void undo() {
		if (!past.isEmpty()) {
			T last = past.pop();
			last.unexecute();
			future.push(last);
		}
	}

	/** Re-does the last {@code Undoable} */
	public void redo() {
		if (!future.isEmpty()) {
			T first = future.pop();
			try {
				// this Undoable has executed successfully before; this statement can't throw
				first.execute();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			past.push(first);
		}
	}

	/** Empties the history */
	public void clear() {
		past.clear();
		future.clear();
	}

	/**
	 * Returns the past part of the history.
	 * <p>
	 * Note that this does not return a copy of the history. Any changes to the
	 * items will be reflected in this UndoableHistory object.
	 *
	 * @return a List with the previously executed Undoables
	 */
	public List<Undoable> getPast() {
		return new Vector<>(past);
	}

	/**
	 * Returns the future part of the history.
	 * <p>
	 * Note that this does not return a copy of the history. Any changes to the
	 * items will be reflected in this UndoableHistory object.
	 *
	 * @return a List with the previously unexecuted Undoables
	 */
	public List<Undoable> getFuture() {
		return new Vector<>(future);
	}
}
