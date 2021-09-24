package myUtil;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;

import application.StringConstants;
import localisation.Languages;

/**
 * A convenient way to display an unexpected error message to the user providing
 * them with the means for forwarding that message to the developer who is
 * responsible for fixing the underlying problem.
 *
 * @author Alex Mandelias
 */
public final class ErrorDumpDialog extends JDialog {

	private static final String titleString           = Languages.getString("ErrorDumpDialog.0"); //$NON-NLS-1$
	private static final String messageString         = Languages.getString("ErrorDumpDialog.1"); //$NON-NLS-1$
	private static final String clipboardButtonString = Languages.getString("ErrorDumpDialog.2"); //$NON-NLS-1$
	private static final String logButtonString       = Languages.getString("ErrorDumpDialog.3"); //$NON-NLS-1$

	/**
	 * Constructs and shows a Dialog for an Exception.
	 *
	 * @param frame the parent frame of the Dialog
	 * @param e     the Exception whose Stack Trace will be dumped
	 */
	public static void showDialog(Frame frame, Exception e) {
		final StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw));
		ErrorDumpDialog.showDialog(frame, sw.toString());
	}

	/**
	 * Constructs and shows a Dialog for an error message.
	 *
	 * @param frame        the parent frame of the Dialog
	 * @param errorMessage the error message that will be dumped
	 */
	public static void showDialog(Frame frame, String errorMessage) {
		final JDialog dialog = new ErrorDumpDialog(frame, errorMessage);
		dialog.setVisible(true);
	}

	private final JPanel    mainPanel, topPanel, midPanel;
	private final JLabel    messageLabel;
	private final JTextArea textArea;
	private final JButton   copyButton, logButton;

	private ErrorDumpDialog(Frame frame, String errorMessage) {
		super(frame, ErrorDumpDialog.titleString, true);

		// top panel: text + button
		topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 0));

		messageLabel = new JLabel(
		        String.format("<html><center>%s</center></html>", ErrorDumpDialog.messageString)); //$NON-NLS-1$
		copyButton = new JButton(ErrorDumpDialog.clipboardButtonString);
		logButton = new JButton(ErrorDumpDialog.logButtonString);

		topPanel.add(new JLabel(UIManager.getIcon("OptionPane.errorIcon"))); //$NON-NLS-1$
		topPanel.add(messageLabel);
		topPanel.add(copyButton);
		topPanel.add(logButton);
		topPanel.add(new JLabel(UIManager.getIcon("OptionPane.errorIcon"))); //$NON-NLS-1$

		// mid panel: text area
		midPanel = new JPanel();
		midPanel.setLayout(new BoxLayout(midPanel, BoxLayout.Y_AXIS));

		textArea = new JTextArea(errorMessage);
		textArea.setEditable(false);

		final JScrollPane jsp = new JScrollPane(textArea);
		jsp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		jsp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		jsp.setBorder(BorderFactory.createEmptyBorder());
		jsp.setWheelScrollingEnabled(true);

		midPanel.add(jsp);

		// main panel: the whole thing
		mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout(0, 15));
		mainPanel.add(topPanel, BorderLayout.NORTH);
		mainPanel.add(midPanel, BorderLayout.CENTER);
		mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
		add(mainPanel);

		pack();
		setLocationRelativeTo(frame);

		// limit size to fit within parent frame, if it exists
		if (frame != null)
			setSize(new Dimension(Math.min(getWidth() + 30, frame.getWidth()),
			        Math.min(getHeight(), frame.getHeight())));

		// listeners
		copyButton.addActionListener(e -> {
			Toolkit.getDefaultToolkit().getSystemClipboard()
			        .setContents(new StringSelection(errorMessage), null);

			ErrorDumpDialog.this.dispose();
		});

		logButton.addActionListener(e -> {
			final String logDirectory = StringConstants.LOG_PATH;
			final String logFileName  = StringConstants.LOG_FILE_NAME.replace("{date}",              //$NON-NLS-1$
			        LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).toString()
			                .replace(":", "-"));                                                     //$NON-NLS-1$ //$NON-NLS-2$

			final Path dir = Paths.get(logDirectory);

			try {
				// create directory if it doesn't exist
				if (!Files.exists(dir))
					Files.createDirectory(dir);

				final String outputFile = String.format("%s%s%s", logDirectory, //$NON-NLS-1$
				        System.getProperty("file.separator"), logFileName); //$NON-NLS-1$

				try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
					writer.write(errorMessage);
				}

				ErrorDumpDialog.this.dispose();

			} catch (final Exception e1) {
				error(e);
			}
		});
	}

	private void error(ActionEvent e) {
		final String popupMsgString = Languages.getString("ErrorDumpDialog.4");                     //$NON-NLS-1$
		final String message        = String.format("%s %s", e.getActionCommand(), popupMsgString); //$NON-NLS-1$
		final String title          = Languages.getString("ErrorDumpDialog.5");                     //$NON-NLS-1$
		final int    messageType    = JOptionPane.ERROR_MESSAGE;

		JOptionPane.showMessageDialog(this, message, title, messageType);
	}
}
