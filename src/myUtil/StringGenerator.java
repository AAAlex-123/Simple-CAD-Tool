package myUtil;

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
 *
 * @author alexm
 */
public final class StringGenerator implements Supplier<String>, Iterator<String> {

	private final String text;
	private int       start;
	private final int end;

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
		this.text = textToFormat;
		this.start = start;
		this.end = end;
	}

	@Override
	public String get() {
		if (!hasNext())
			throw new NoSuchElementException();
		return String.format(text, start++);
	}

	@Override
	public boolean hasNext() {
		return start < end;
	}

	@Override
	public String next() {
		return get();
	}
}
