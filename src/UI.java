
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.WindowConstants;

@SuppressWarnings({ "serial", "javadoc", "unused" })
public class UI extends JFrame {

	//	private final Gate[] gates;
	//	private final Branch[] branches;
	private String sprite;

	// controls
	private boolean showmouse = false;

	public static void main(String[] args) {
		UI ui = new UI();
		ui.setSize(500, 500);
		ui.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		ui.setVisible(true);
	}

	private JLabel l2;
	/*
	public UI() {
		super("owo");
		setLayout(new BorderLayout());
		gates = new Gate[3];
		branches = new Branch[3];

		gates[0] = new Gate(".\\assets\\images\\and.png", 50, 100);
		gates[1] = new Gate(".\\assets\\images\\or.png", 125, 100, 100, 100);
		gates[2] = new Gate(".\\assets\\images\\not.png", 250, 100, 150, 150);

		branches[0] = new Branch(gates[0], gates[1]);
		branches[1] = new Branch(gates[1], gates[2]);
		branches[2] = new Branch(gates[0], gates[2]);

		gates[0].outputpins[0] = branches[0];
		gates[1].inputpins[0] = branches[0];
		gates[1].outputpins[0] = branches[1];
		gates[2].inputpins[0] = branches[1];
		gates[0].outputpins[1] = branches[2];
		gates[2].inputpins[3] = branches[2];

		JPanel aaa = new JPanel(null);
		aaa.add(branches[0]);
		aaa.add(branches[1]);
		aaa.add(branches[2]);
		aaa.add(gates[0]);
		aaa.add(gates[1]);
		aaa.add(gates[2]);
		add(aaa);

		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (showmouse)
					System.out.printf("mouse: %d-%d%n", e.getX(), e.getY());
			}
		});


		// contrïl buttons
		JPanel controlButtons = new JPanel();
		JButton showMouseButton = new JButton("Show Mouse");

		showMouseButton.addActionListener((e) -> {
			showmouse = !showmouse;
			((JButton) e.getSource()).setBackground(showmouse ? Color.green : Color.red);
		});

		controlButtons.add(showMouseButton);

		add(controlButtons, BorderLayout.NORTH);
	}
	 */
}
