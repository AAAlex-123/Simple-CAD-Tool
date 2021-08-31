package requirement.graphics;

import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.ComboBoxEditor;
import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import javax.swing.text.PlainDocument;

/**
 * Searching for an item in a combo box can be cumbersome if the box contains a
 * lot of items. If the user knows what he is looking for, it should be
 * sufficient to enter the desired item. The user should be supported with
 * automatic completion. This article shows how to implement this feature using
 * the standard JComboBox without modifying it or inheriting from it.
 *
 * @param <E> the type of items present in the JComboBox
 *
 * @author Thomas Bierhance
 *
 * @see <a href="http://www.orbital-computer.de/JComboBox/">website</a>
 * @see <a href=
 *      "http://www.orbital-computer.de/JComboBox/source/AutoCompletion.java">source
 *      code</a>
 */
/*
 * This work is hereby released into the Public Domain. To view a copy of the
 * public domain dedication, visit
 * http://creativecommons.org/licenses/publicdomain/
 */
class AutoCompletion<E> extends PlainDocument {
	private final JComboBox<E> comboBox;
	private ComboBoxModel<E>   model;
	private JTextComponent     editor;

	/**
	 * Flag to indicate if setSelectedItem has been called. Subsequent calls to
	 * remove/insertString should be ignored
	 */
	private boolean selecting               = false;
	private boolean hitBackspace            = false;
	private boolean hitBackspaceOnSelection = false;

	private final KeyListener   editorKeyListener;
	private final FocusListener editorFocusListener;

	/**
	 * Enables AutoCompletion in a {@code JComboBox}.
	 *
	 * @param <E>      the type of items the JComboBox stores
	 * @param comboBox the JComboBox to enable AutoCompletion
	 */
	@SuppressWarnings("unused")
	public static <E> void enable(JComboBox<E> comboBox) {
		// has to be editable
		comboBox.setEditable(true);
		// change the editor's document
		new AutoCompletion<>(comboBox);
	}

	private AutoCompletion(final JComboBox<E> comboBox) {
		this.comboBox = comboBox;
		model = comboBox.getModel();
		comboBox.addActionListener(e -> {
			if (!selecting)
				highlightCompletedText(0);
		});
		comboBox.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			@SuppressWarnings("unchecked")
			public void propertyChange(PropertyChangeEvent e) {
				if (e.getPropertyName().equals("editor")) //$NON-NLS-1$
					configureEditor((ComboBoxEditor) e.getNewValue());
				if (e.getPropertyName().equals("model")) //$NON-NLS-1$
					model = (ComboBoxModel<E>) e.getNewValue();
			}
		});
		editorKeyListener = new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (comboBox.isDisplayable())
					comboBox.setPopupVisible(true);
				hitBackspace = false;
				switch (e.getKeyCode()) {
				// determine if the pressed key is backspace (needed by the remove method)
				case KeyEvent.VK_BACK_SPACE:
					hitBackspace = true;
					hitBackspaceOnSelection = editor.getSelectionStart() != editor
					        .getSelectionEnd();
					break;
				// ignore delete key
				case KeyEvent.VK_DELETE:
					e.consume();
					comboBox.getToolkit().beep();
					break;
				default:
					break;
				}
			}
		};
		// Highlight whole text when gaining focus
		editorFocusListener = new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				highlightCompletedText(0);
			}
		};
		configureEditor(comboBox.getEditor());
		// Handle initially selected object
		final Object selected = comboBox.getSelectedItem();
		if (selected != null)
			setText(selected.toString());
		highlightCompletedText(0);
	}


	@Override
	public void remove(int offs, int len) throws BadLocationException {
		// return immediately when selecting an item
		if (selecting)
			return;

		int newOffset = offs;

		if (hitBackspace) {
			// user hit backspace => move the selection backwards
			// old item keeps being selected
			if (offs > 0) {
				if (hitBackspaceOnSelection)
					newOffset--;
			} else
				// User hit backspace with the cursor positioned on the start => beep
				comboBox.getToolkit().beep(); // when available use: UIManager.getLookAndFeel().provideErrorFeedback(comboBox);
			highlightCompletedText(newOffset);
		} else
			super.remove(newOffset, len);
	}

	@Override
	public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
		// return immediately when selecting an item
		if (selecting)
			return;

		// insert the string into the document
		super.insertString(offs, str, a);

		int newOffset = offs;

		// lookup and select a matching item
		Object item = lookupItem(getText(0, getLength()));
		if (item != null)
			setSelectedItem(item);
		else {
			// keep old item selected if there is no match
			item = comboBox.getSelectedItem();
			// imitate no insert (later on offs will be incremented by str.length(): selection won't move forward)
			newOffset = offs - str.length();
			// provide feedback to the user that his input has been received but can not be accepted
			comboBox.getToolkit().beep(); // when available use: UIManager.getLookAndFeel().provideErrorFeedback(comboBox);
		}
		setText(item.toString());
		// select the completed part
		highlightCompletedText(newOffset + str.length());
	}

	private void configureEditor(ComboBoxEditor newEditor) {
		if (editor != null) {
			editor.removeKeyListener(editorKeyListener);
			editor.removeFocusListener(editorFocusListener);
		}

		if (newEditor != null) {
			editor = (JTextComponent) newEditor.getEditorComponent();
			editor.addKeyListener(editorKeyListener);
			editor.addFocusListener(editorFocusListener);
			editor.setDocument(this);
		}
	}

	private void setText(String text) {
		try {
			// remove all text and insert the completed string
			super.remove(0, getLength());
			super.insertString(0, text, null);
		} catch (final BadLocationException e) {
			throw new RuntimeException(e.toString());
		}
	}

	private void highlightCompletedText(int start) {
		editor.setCaretPosition(getLength());
		editor.moveCaretPosition(start);
	}

	private void setSelectedItem(Object item) {
		selecting = true;
		model.setSelectedItem(item);
		selecting = false;
	}

	private Object lookupItem(String pattern) {
		final Object selectedItem = model.getSelectedItem();
		// only search for a different item if the currently selected does not match
		if ((selectedItem != null)
		        && AutoCompletion.startsWithIgnoreCase(selectedItem.toString(), pattern))
			return selectedItem;
		// iterate over all items
		for (int i = 0, n = model.getSize(); i < n; i++) {
			final Object currentItem = model.getElementAt(i);
			// current item starts with the pattern?
			if ((currentItem != null)
			        && AutoCompletion.startsWithIgnoreCase(currentItem.toString(), pattern))
				return currentItem;
		}
		// no item starts with the pattern => return null
		return null;
	}

	// checks if str1 starts with str2 - ignores case
	private static boolean startsWithIgnoreCase(String str1, String str2) {
		return str1.toUpperCase().startsWith(str2.toUpperCase());
	}
}
