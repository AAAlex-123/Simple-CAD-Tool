package exceptions;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import components.Component;
import localisation.Languages;

/** Thrown when a Branch could not be normally created */
public final class MalformedBranchException extends Exception {

	/**
	 * Constructs the exception using information about the two {@code Component}s.
	 *
	 * @param in  the input of the Branch
	 * @param out the output of the Branch
	 */
	public MalformedBranchException(Component in, Component out) {
		super(formatMessage(in, out));
	}

	/**
	 * Constructs the exception using information about the {@code Component} and
	 * the {@code index}.
	 *
	 * @param c     a Component where the Branch will connect
	 * @param index the index on the Component
	 */
	public MalformedBranchException(Component c, int index) {
		super(formatMessage(c, index));
	}

	private static String formatMessage(Component in, Component out) {
		String s1 = in == null ? "null" : in.type().description(); //$NON-NLS-1$
		String s2 = out == null ? "null" : out.type().description(); //$NON-NLS-1$
		return String.format(Languages.getString("MalformedBranchException.2"), s1, s2); //$NON-NLS-1$
	}

	private static String formatMessage(Component c, int index) {
		Pattern p = Pattern.compile("(.*?): (\\d+)-(\\d+), UID: (\\w+), hidden: (true|false)"); //$NON-NLS-1$
		Matcher m = p.matcher(c.toString());
		m.find();
		String comp = m.group(1);
		int i1 = Integer.valueOf(m.group(2));
		int i2 = Integer.valueOf(m.group(3));
		String id = m.group(4);
		String s1 = i1 != 1 ? Languages.getString("MalformedBranchException.4") : Languages.getString("MalformedBranchException.5"); //$NON-NLS-1$ //$NON-NLS-2$
		String s2 = i2 != 1 ? Languages.getString("MalformedBranchException.6") : Languages.getString("MalformedBranchException.7"); //$NON-NLS-1$ //$NON-NLS-2$
		return String.format(Languages.getString("MalformedBranchException.8"), //$NON-NLS-1$
				index, comp, id, i1, s1, i2, s2);
	}
}
