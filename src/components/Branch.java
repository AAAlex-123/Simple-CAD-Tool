package components;

import exceptions.MalformedComponentException;
import exceptions.UnsupportedMethodException;

class Branch extends Component {

	public Branch(Pin in, Pin out) {
		super(new Pin[] { in }, new Pin[] { out });

		if (getIn() == null)
			throw new MalformedComponentException("Invalid Branch Input");
		if (getOut() == null)
			throw new MalformedComponentException("Invalid Branch Output");

		in.setOut(this);
		out.setIn(this);
	}

	@Override
	protected void wake_up() {

		boolean prevState = getActive();

		active[0] = getIn().getActive();

		if (prevState != getActive()) {
			getOut().wake_up();
		}
	}

	@Override
	protected void setIn(Component c) {
		throw new UnsupportedMethodException("Can't change input of Branch");
	}

	@Override
	protected void setOut(Component c) {
		throw new UnsupportedMethodException("Can't change output of Branch");
	}
}


/*
	private final Gate g1, g2;
	private int x1, y1, x2, y2;
	private int w, h;
	private boolean active;

	public Branch(Gate g1, Gate g2) {
		this.g1 = g1;
		this.g2 = g2;
		g1.outputpins[0] = this;
		g2.inputpins[0] = this;
		active = false;
		move();
		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				active = !active;
			}
		});
	}

	public void move() {
		int[] coords1 = g1.getBranchCoords(this);
		int[] coords2 = g2.getBranchCoords(this);
		x1 = coords1[0];
		y1 = coords1[1];
		x2 = coords2[0];
		y2 = coords2[1];
		w = Math.abs(x2 - x1);
		h = Math.abs(y2 - y1);
		setBounds(Math.min(x1, x2), Math.min(y1, y2), w, h);
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.setColor(active ? Color.green : Color.red);
		g.drawLine(x1 < x2 ? 0 : w, y1 < y2 ? 0 : h, x1 < x2 ? w : 0, y1 < y2 ? h : 0);
		System.out.printf("drawing branch: %d-%d, %d-%d%n", x1, y1, x2, y2);
	}
 */
