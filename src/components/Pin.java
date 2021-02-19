package components;

class Pin extends Component {

	public Pin() {
		super(new Component[1], new Component[1]);
	}

	@Override
	protected void wake_up() {
		boolean prevState = getActive();

		active[0] = getIn().getActive();

		if (prevState != getActive())
			getOut().wake_up();
	}
}
