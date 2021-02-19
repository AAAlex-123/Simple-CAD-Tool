package components;

@SuppressWarnings({ "javadoc" })
public abstract class Component {

	protected abstract void wake_up();

	public boolean getActive() {
		return getActive(0);
	}

	public abstract boolean getActive(int index);

}
