package exceptions;

import components.Component;

/** Thrown when a Branch could not be normally created */
public final class MalformedBranchException extends RuntimeException {

	/**
	 * Constructs the exception using information about the two {@code Component}s.
	 *
	 * @param in  the input of the Branch
	 * @param out the output of the Branch
	 */
	public MalformedBranchException(Component in, Component out) {
		super(formatMessage(in, out));
	}

	private static String formatMessage(Component in, Component out) {
		String s1 = in == null ? "null" : in.type().description();
		String s2 = out == null ? "null" : out.type().description();
		return String.format("Can't create Branch from %s to %s", s1, s2);
	}
}
