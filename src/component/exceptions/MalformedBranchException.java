package component.exceptions;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import component.components.Component;
import localisation.Languages;

/**
 * Thrown when a {@code Branch} could not be normally created.
 *
 * @author Alex Mandelias
 */
public final class MalformedBranchException extends Exception {

	private static final Pattern p = Pattern
	        .compile(
	                "(?<type>.*?): (?<inCount>\\d+)-(?<outCount>\\d+), UID: (?<id>\\w+), hidden: (?<not_used>true|false)"); //$NON-NLS-1$

	/**
	 * Constructs the Exception with the two {@code Components}.
	 *
	 * @param in  the input of the Branch
	 * @param out the output of the Branch
	 */
	public MalformedBranchException(Component in, Component out) {
		super(MalformedBranchException.formatMessage(in, out));
	}

	/**
	 * Constructs the exception using information about a {@code Component} and the
	 * {@code index}.
	 *
	 * @param component a Component where the Branch would connect
	 * @param index     the index on the Component
	 */
	public MalformedBranchException(Component component, int index) {
		super(MalformedBranchException.formatMessage(component, index));
	}

	private static String formatMessage(Component in, Component out) {
		final String s1 = nullOrGetType(in);
		final String s2 = nullOrGetType(out);
		return String.format(Languages.getString("MalformedBranchException.2"), s1, s2); //$NON-NLS-1$
	}

	private static String formatMessage(Component component, int index) {
		final Matcher m = MalformedBranchException.p.matcher(component.toString());
		m.find();

		final String type     = m.group("type");                       //$NON-NLS-1$
		final int    inCount  = Integer.parseInt(m.group("inCount"));  //$NON-NLS-1$
		final int    outCount = Integer.parseInt(m.group("outCount")); //$NON-NLS-1$
		final String id       = m.group("id");                         //$NON-NLS-1$

		final String inputsSingularOrPlural  = inCount != 1
		        ? Languages.getString("MalformedBranchException.4")              //$NON-NLS-1$
		        : Languages.getString("MalformedBranchException.5");             //$NON-NLS-1$
		final String outputsSingularOrPlural = outCount != 1
		        ? Languages.getString("MalformedBranchException.6")              //$NON-NLS-1$
		        : Languages.getString("MalformedBranchException.7");             //$NON-NLS-1$

		return String.format(Languages.getString("MalformedBranchException.8"), //$NON-NLS-1$
		        index, type, id, inCount, inputsSingularOrPlural, outCount,
		        outputsSingularOrPlural);
	}

	private static String nullOrGetType(Component component) {
		if (component == null)
			return "null"; //$NON-NLS-1$

		return component.type().description();
	}
}
