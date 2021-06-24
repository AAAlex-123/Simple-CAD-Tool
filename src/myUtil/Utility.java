package myUtil;

import java.util.Iterator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * A collection of simple algorithms for arrays and Iterables such as foreach,
 * any, all. For some reason java doesn't have them (?)
 *
 * @author alexm
 */
public final class Utility {

    private Utility() {}

    /**
     * Accepts each item in the {@code iter} with the {@code consumer}.
     *
     * @param <T>      the type of Objects the algorithm will act upon
     * @param iter     the Iterable
     * @param consumer the Predicate
     */
    public static <T> void foreach(Iterable<T> iter, Consumer<T> consumer) {
        for (final T item : iter)
            consumer.accept(item);
    }

    /**
     * Accepts each item in the {@code array} with the {@code consumer}.
     *
     * @param <T>      the type of Objects the algorithm will act upon
     * @param array    the array
     * @param consumer the Predicate
     */
    public static <T> void foreach(T[] array, Consumer<T> consumer) {
        for (final T item : array)
            if (item != null)
                consumer.accept(item);
    }

    /**
     * Evaluates the {@code predicate} on each item in the {@code iter} and returns
     * true if at least one item evaluates to true.
     *
     * @param <T>       the type of Objects the algorithm will act upon
     * @param iter      the Iterable
     * @param predicate the Predicate
     *
     * @return {@code true} if at least one item evaluates to true, {@code false}
     *         otherwise
     */
    public static <T> boolean any(Iterable<T> iter, Predicate<T> predicate) {
        for (final T item : iter)
            if (predicate.test(item))
                return true;
        return false;
    }

    /**
     * Evaluates the {@code predicate} on each item in the {@code array} and returns
     * true if at least one item evaluates to true.
     * <p>
     * <b>Note:</b> {@code null-checks} will fail because the {@code predicate} will
     * be evaluated only on the non-null elements of the array.
     *
     * @param <T>       the type of Objects the algorithm will act upon
     * @param array     the array
     * @param predicate the Predicate
     *
     * @return {@code true} if at least one item evaluates to true, {@code false}
     *         otherwise
     */
    public static <T> boolean any(T[] array, Predicate<T> predicate) {
        for (final T item : array)
            if ((item != null) && predicate.test(item))
                return true;
        return false;
    }

    /**
     * Evaluates the {@code predicate} on each item in the {@code iter} and returns
     * true if no items evaluate to {@code false}.
     *
     * @param <T>       the type of Objects the algorithm will act upon
     * @param iter      the Iterable
     * @param predicate the Predicate
     *
     * @return {@code true} if no items evaluate to false, {@code false} otherwise.
     */
    public static <T> boolean all(Iterable<T> iter, Predicate<T> predicate) {
        for (final T item : iter)
            if (!predicate.test(item))
                return false;
        return true;
    }

    /**
     * Evaluates the {@code predicate} on each item in the {@code array} and returns
     * true if no items evaluate to {@code false}.
     * <p>
     * <b>Note:</b> {@code null-checks} will fail because the {@code predicate} will
     * be evaluated only on the non-null elements of the array.
     *
     * @param <T>       the type of Objects the algorithm will act upon
     * @param array     the array
     * @param predicate the Predicate
     *
     * @return {@code true} if no items evaluate to false, {@code false} otherwise.
     */
    public static <T> boolean all(T[] array, Predicate<T> predicate) {
        for (final T item : array)
            if ((item != null) && !predicate.test(item))
                return false;
        return true;
    }

    /**
     * Compares the value of the {@code function} of each item in the {@code iter}
     * and returns the item with the maximum value.
     *
     * @param <T>      the type of Objects the algorithm will act upon
     * @param <V>      the type of the values that will be compared
     * @param iter     the Iterable
     * @param function the Function
     *
     * @return the item with the maximum value or {@code null} if the {@code iter}
     *         does not contain any items.
     */
    public static <T, V extends Comparable<V>> T max(Iterable<T> iter, Function<T, V> function) {
        final Iterator<T> iterator = iter.iterator();
        if (!iterator.hasNext())
            return null;

        // smallest value is that of the first item
        T maxitem = iterator.next();
        V maxval = function.apply(maxitem);

        for (final T item : iter) {
            final V value = function.apply(item);
            if (value.compareTo(maxval) > 0) {
                maxval = value;
                maxitem = item;
            }
        }
        return maxitem;
    }

    /**
     * Compares the value of the {@code function} of each item in the {@code array}
     * and returns the item with the maximum value.
     *
     * @param <T>      the type of Objects the algorithm will act upon
     * @param <V>      the type of the values that will be compared
     * @param array    the array
     * @param function the Function
     *
     * @return the item with the maximum value or {@code null} if the {@code array}
     *         does not contain any items.
     */
    public static <T, V extends Comparable<V>> T max(T[] array, Function<T, V> function) {
        if (array.length == 0)
            return null;

        // smallest value is that of the first item
        T maxitem = array[0];
        V maxval = function.apply(maxitem);

        for (final T item : array) {
            final V value = function.apply(item);
            if (value.compareTo(maxval) > 0) {
                maxval = value;
                maxitem = item;
            }
        }
        return maxitem;
    }
}
