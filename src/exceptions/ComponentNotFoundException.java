package exceptions;

import components.Component;

@SuppressWarnings("javadoc")
public final class ComponentNotFoundException extends RuntimeException {

	public ComponentNotFoundException(Component c) {
		super(String.format("Attempted to remove a %s that couldn't be found", c));
	}
}
