package myUtil;

import java.io.Serializable;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Supplier;

/**
 * A generator of a sequence of {@code Strings}. Given a String to be formatted
 * that contains at least one '{@code %d}' (e.g. {@code item-%d}), the Generator
 * produces Strings by replacing the first '{@code %d}' with a number, producing
 * the sequence: {@code item-0}, {@code item-1}, ..., {@code item-n}.
 * <p>
 * The first and last number of the sequence may optionally be defined. If they
 * are not, they are assigned their default values:
 * <ul>
 * <li>{@code start} defaults to {@code 0}</li>
 * <li>{@code end} defaults to {@code Integer.MAX_VALUE}</li>
 * </ul>
 * <p>
 * The Generator follows the specifications of each interface it implements.
 *
 * @author Alex Mandelias
 */
public final class StringGenerator
        implements Supplier<String>, Iterator<String>, Iterable<String>, Serializable {

	private final String format;
	private final int    start, end;
	private int          current;

	/**
	 * Constructs a {@code Generator}.
	 *
	 * @param format the text
	 */
	public StringGenerator(String format) {
		this(format, 0, Integer.MAX_VALUE);
	}

	/**
	 * Constructs a {@code Generator}.
	 *
	 * @param format the text
	 * @param start  the initial value for the counter
	 */
	public StringGenerator(String format, int start) {
		this(format, start, Integer.MAX_VALUE);
	}

	/**
	 * Constructs a {@code Generator}.
	 *
	 * @param format the text
	 * @param start  the initial value for the counter
	 * @param end    the final value for the counter
	 */
	public StringGenerator(String format, int start, int end) {
		this.format = format;
		this.start = current = start;
		this.end = end;
	}

	@Override
	public String get() {
		return canProduceMore() ? String.format(format, current++) : ""; //$NON-NLS-1$
	}

	@Override
	public boolean hasNext() {
		return canProduceMore();
	}

	@Override
	public String next() {
		if (!canProduceMore())
			throw new NoSuchElementException();
		return get();
	}

	@Override
	public Iterator<String> iterator() {
		return new StringGenerator(format, start, end);
	}

	private boolean canProduceMore() {
		return current <= end;
	}
}
