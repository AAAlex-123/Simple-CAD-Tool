package exceptions;

/**
 * Thrown when a fatal error occurs during program initialization, if for example important 
 * data files are corrupted, missing or in a wrong format.
 * 
 * As with all errors catch only to notify the user and perform cleanup. Recovery is extremely hard or impossible.
 * 
 * @author dimits
 *
 */
public class InitializationError extends Error {

	private static final long serialVersionUID = 9001891336327126028L;

	public InitializationError(String message) {
		super(message);
	}
}
