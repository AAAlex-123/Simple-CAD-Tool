package application;

import java.awt.Frame;
import java.io.Serializable;

/** An implementation of the Undoable interface, specific to this Application */
abstract class Command implements Undoable, Serializable {

	private static final long serialVersionUID = 2L;

	/** The Command's requirements; what it needs to execute. */
	Requirements<String> requirements;

	/** The Command's context; where it will act. */
	transient Application context;

	/**
	 * Constructs the Command with the given {@code Application}.
	 *
	 * @param app the Command's context
	 */
	Command(Application app) {
		context = app;
		requirements = new Requirements<>();
	}

	/**
	 * Fulfils the Command's requirements with a dialog.
	 * 
	 * @param parent the parent of the dialog
	 */
	final void fillRequirements(Frame parent) {
		requirements.fulfillWithDialog(parent, desc());
	}

	/**
	 * Clones the Command so that the future execution of this Command doesn't
	 * affect future executions. Should be the same as myclone(false).
	 *
	 * @return the cloned Command
	 */
	abstract Command myclone();

	/**
	 * Clones the command but keeps some information.
	 * <p>
	 * yes this is spaghetti
	 *
	 * @param keepIdAndReqs whether or not to retain some information
	 * @return the cloned command
	 */
	Command myclone(@SuppressWarnings("unused") boolean keepIdAndReqs) {
		throw new RuntimeException("not used lmao");
	}

	/** @return a description for this Command */
	abstract String desc();

	@Override
	public String toString() {
		return desc();
	}
}
