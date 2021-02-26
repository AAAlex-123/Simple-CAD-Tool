package components;

import javax.swing.JComponent;

@SuppressWarnings("javadoc")
abstract public class Drawable extends JComponent {

	boolean active = false;
	boolean changeable = true;

	public Drawable() {
		// TODO Auto-generated constructor stub
	}

	void setActive(boolean newActive) {
		throw new RuntimeException("lmao");
	}

	boolean getActive() {
		throw new RuntimeException("lmao");
	}

	void checkChangeable() {
		if (!changeable)
			throw new RuntimeException("Can't modify component hidden inside another gate");
	}

	void deactivate() {
		changeable = false;
	}

	// @Override
	// public abstract void paintComponent(Graphics g);
}
