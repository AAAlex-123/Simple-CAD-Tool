package components;

@SuppressWarnings({ "javadoc", "unused" })
public class Pin extends Component {

	protected boolean active = false;
	protected Component in, out;

	public Pin() {
		in = null;
		out = null;
	}

	public Pin(boolean active) {
		in = null;
		out = null;
		this.active = active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	@Override
	protected void wake_up() {
		boolean lastActive = active;
		if (in != null)
			active = in.getActive();
		if ((out != null))// && (lastActive != active))
			out.wake_up();
	}

	@Override
	public boolean getActive(int index) {
		return active;
	}

	public void setIn(Component c) {
		in = c;
	}

	public Component getIn() {
		return in;
	}

	public void setOut(Component c) {
		out = c;
	}

	public Component getOut() {
		return out;
	}
}
