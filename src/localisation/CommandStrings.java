package localisation;

/**
 * Externalises every String of the {@link command} package.
 *
 * @author Alex Mandelias
 */
public final class CommandStrings {

	/** Key of the {@code NAME} Requirement of a {@code Command} */
	public static final String NAME = Languages.getString("CommandStrings.0"); //$NON-NLS-1$

	/** Key of the {@code IN_NAME} Requirement of a {@code Create Command} */
	public static final String IN_NAME = Languages.getString("CommandStrings.1"); //$NON-NLS-1$

	/** Key of the {@code IN_INDEX} Requirement of a {@code Create Command} */
	public static final String IN_INDEX = Languages.getString("CommandStrings.2"); //$NON-NLS-1$

	/** Key of the {@code OUT_NAME} Requirement of a {@code Create Command} */
	public static final String OUT_NAME = Languages.getString("CommandStrings.3"); //$NON-NLS-1$

	/** Key of the {@code OUT_INDEX} Requirement of a {@code Create Command} */
	public static final String OUT_INDEX = Languages.getString("CommandStrings.4"); //$NON-NLS-1$

	/** Key of the {@code IN_COUNT} Requirement of a {@code Create Command} */
	public static final String IN_COUNT = Languages.getString("CommandStrings.5"); //$NON-NLS-1$

	/** The literal word "{@code create}", toString of a {@code Create Command} */
	public static final String CREATE_STR = Languages.getString("CommandStrings.6"); //$NON-NLS-1$

	/** The literal word "{@code delete}", toString of a {@code Delete Command} */
	public static final String DELETE_STR = Languages.getString("CommandStrings.7"); //$NON-NLS-1$

	/* Don't let anyone initialise this class */
	private CommandStrings() {}
}
