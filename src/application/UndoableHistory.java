package application;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * A wrapper for implementing undo and redo functionality.
 * 
 * @param <T> the type of object that will be stored
 */
final class UndoableHistory<T extends Undoable> {

	private final Stack<T> past, future;

	/** Initialises the UndoableHistory */
	UndoableHistory() {
		past = new Stack<>();
		future = new Stack<>();
	}

	/**
	 * Executes the given {@code Undoable}.
	 * 
	 * @param c the undoable
	 */
	void add(T c) {
		past.push(c);

		// flush the redo history
		if (!future.isEmpty())
			future.clear();
	}

	/** Undoes the last {@code Undoable} */
	void undo() {
		if (!past.isEmpty()) {
			T last = past.pop();
			last.unexecute();
			future.push(last);
		}
	}

	/** Re-does the last {@code Undoable} */
	void redo() {
		if (!future.isEmpty()) {
			T first = future.pop();
			first.execute();
			past.push(first);
		}
	}

	/**
	 * Returns the past part of the history.
	 * <p>
	 * Note that this does not return a copy of the Undoables. Therefore any changes
	 * to the items will be reflected in the UndoableHistory object.
	 * 
	 * @return a List with the previously executed Undoables
	 */
	List<Undoable> getHistory() {
		return new ArrayList<>(past);
	}

	/** Empties the history */
	void clear() {
		past.clear();
		future.clear();
	}
}
