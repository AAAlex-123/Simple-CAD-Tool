package localisation;

/**
 * Externalises every String of the {@link requirement} package, as well as
 * other Strings that are related to Requirements.
 *
 * @author Alex Mandelias
 */
public final class RequirementStrings {

	/** The literal word "{@code on}" */
	public static final String ON = Languages.getString("RequirementStrings.0"); //$NON-NLS-1$

	/** The literal word "{@code off}" */
	public static final String OFF = Languages.getString("RequirementStrings.1"); //$NON-NLS-1$

	/** The literal word "{@code yes}" */
	public static final String YES = Languages.getString("RequirementStrings.2"); //$NON-NLS-1$

	/** The literal word "{@code no}" */
	public static final String NO = Languages.getString("RequirementStrings.3"); //$NON-NLS-1$

	/** Key of a {@code label} of the {@code RequirementsDialog} */
	public static final String MESSAGE = Languages.getString("RequirementStrings.4"); //$NON-NLS-1$

	/* Don't let anyone initialise this class */
	private RequirementStrings() {}
}
