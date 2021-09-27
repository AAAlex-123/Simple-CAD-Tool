package myUtil;

import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * A collection of simple algorithms for arrays and Iterables.
 *
 * @author Alex Mandelias
 */
public final class Utility {

	/* Don't let anyone instantiate this class */
	private Utility() {}

	/**
	 * Accepts each item in an {@code iterable} with a {@code consumer}.
	 *
	 * @param <T>      the type of Objects the algorithm will act upon
	 * @param iterable the Iterable whose items will be consumed
	 * @param consumer the Consumer that will consume the items
	 */
	public static <T> void foreach(Iterable<T> iterable, Consumer<T> consumer) {
		for (final T item : iterable)
			if (item != null)
				consumer.accept(item);
	}

	/**
	 * Accepts each item in an {@code array} with an {@code consumer}.
	 *
	 * @param <T>      the type of Objects the algorithm will act upon
	 * @param array    the array whose items will be consumed
	 * @param consumer the Consumer that will consume the items
	 */
	public static <T> void foreach(T[] array, Consumer<T> consumer) {
		for (final T item : array)
			if (item != null)
				consumer.accept(item);
	}

	/**
	 * Evaluates a {@code predicate} on each item in an {@code iterable} and returns
	 * {@code true} if at least one item evaluates to {@code true}.
	 *
	 * @param <T>       the type of Objects the algorithm will act upon
	 * @param iterable  the Iterable whose items will be tested
	 * @param predicate the Predicate that will test the items
	 *
	 * @return {@code true} if at least one item evaluates to true, {@code false}
	 *         otherwise
	 */
	public static <T> boolean any(Iterable<T> iterable, Predicate<T> predicate) {
		for (final T item : iterable)
			if (predicate.test(item))
				return true;
		return false;
	}

	/**
	 * Evaluates a {@code predicate} on each item in an {@code array} and returns
	 * {@code true} if at least one item evaluates to {@code true}.
	 *
	 * @param <T>       the type of Objects the algorithm will act upon
	 * @param array     the array whose items will be tested
	 * @param predicate the Predicate that will test the items
	 *
	 * @return {@code true} if at least one item evaluates to true, {@code false}
	 *         otherwise
	 */
	public static <T> boolean any(T[] array, Predicate<T> predicate) {
		for (final T item : array)
			if (predicate.test(item))
				return true;
		return false;
	}

	/**
	 * Evaluates a {@code predicate} on each item in an {@code iterable} and returns
	 * {@code true} if no items evaluate to {@code false}.
	 *
	 * @param <T>       the type of Objects the algorithm will act upon
	 * @param iterable  the Iterable whose items will be tested
	 * @param predicate the Predicate that will test the items
	 *
	 * @return {@code true} if no items evaluate to false, {@code false} otherwise.
	 */
	public static <T> boolean all(Iterable<T> iterable, Predicate<T> predicate) {
		for (final T item : iterable)
			if (!predicate.test(item))
				return false;
		return true;
	}

	/**
	 * Evaluates a {@code predicate} on each item in an {@code array} and returns
	 * {@code true} if no items evaluate to {@code false}.
	 *
	 * @param <T>       the type of Objects the algorithm will act upon
	 * @param array     the array whose items will be tested
	 * @param predicate the Predicate that will test the items
	 *
	 * @return {@code true} if no items evaluate to false, {@code false} otherwise.
	 */
	public static <T> boolean all(T[] array, Predicate<T> predicate) {
		for (final T item : array)
			if (!predicate.test(item))
				return false;
		return true;
	}
}
