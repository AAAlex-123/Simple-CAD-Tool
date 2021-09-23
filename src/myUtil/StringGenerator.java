package myUtil;

import java.io.Serializable;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Supplier;

/**
 * A generator of a sequence of {@code Strings}. Given a String to be formatted
 * that contains at least one '{@code %d}' (e.g. {@code item-%d}), the Generator
 * produces Strings by replacing the first '{@code %d}' with a number, producing
 * the sequence: {@code item-0}, {@code item-1}, {@code item-2}, ...
 * <p>
 * The first and last number of the sequence may optionally be defined. If they
 * are not, they are assigned their default values:
 * <ul>
 * <li>{@code start} defaults to {@code 0}</li>
 * <li>{@code end} defaults to {@code Integer.MAX_VALUE}</li>
 * </ul>
 * <p>
 * The generator can be used as a {@code Supplier}, {@code Iterator} or an
 * {@code Iterable} and will follow the specifications of each class for each of
 * the methods available.
 *
 * @author alexm
 */
public final class StringGenerator
        implements Supplier<String>, Iterator<String>, Iterable<String>, Serializable {

	private final String text;
	private final int start, end;
	private int current;

	/**
	 * Constructs the {@code Generator} with the {@code text} that will be
	 * formatted.
	 *
	 * @param textToFormat the text
	 */
	public StringGenerator(String textToFormat) {
		this(textToFormat, 0, Integer.MAX_VALUE);
	}

	/**
	 * Constructs the {@code Generator} with the {@code text} that will be formatted
	 * and the {@code start} value.
	 *
	 * @param textToFormat the text
	 * @param start        the initial value for the counter
	 */
	public StringGenerator(String textToFormat, int start) {
		this(textToFormat, start, Integer.MAX_VALUE);
	}

	/**
	 * Constructs the {@code Generator} with the {@code text} that will be
	 * formatted, the {@code start} value and the {@code end} value.
	 *
	 * @param textToFormat the text
	 * @param start        the initial value for the counter
	 * @param end          the final value for the counter
	 */
	public StringGenerator(String textToFormat, int start, int end) {
		text = textToFormat;
		this.start = current = start;
		this.end = end;
	}

	@Override
	public String get() {
		return canProduceMore() ? String.format(text, current++) : ""; //$NON-NLS-1$
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
		return new StringGenerator(text, start, end);
	}

	private boolean canProduceMore() {
		return current < end;
	}
}
