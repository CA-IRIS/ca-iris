/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2008-2016  Minnesota Department of Transportation
 * Copyright (C) 2010-2015  AHMCT, University of California
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */
package us.mn.state.dot.tms.client.dms;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Iterator;
import java.util.TreeSet;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.text.AbstractDocument;

import us.mn.state.dot.tms.DMS;
import us.mn.state.dot.tms.DmsSignGroup;
import us.mn.state.dot.tms.DmsSignGroupHelper;
import us.mn.state.dot.tms.QuickMessage;
import us.mn.state.dot.tms.QuickMessageHelper;
import us.mn.state.dot.tms.SignGroup;
import us.mn.state.dot.tms.SystemAttrEnum;
import us.mn.state.dot.tms.utils.NumericAlphaComparator;
import us.mn.state.dot.tms.utils.UppercaseDocumentFilter;

/**
 * The quick message combobox is a widget which allows the user to select
 * a precomposed "quick" message. When the user changes a quick message
 * selection via this combobox, the dispatcher is flagged that it should update
 * its widgets with the newly selected message.
 *
 * @see DMSDispatcher, QuickMessage
 * @author Michael Darter
 * @author Douglas Lau
 * @author Travis Swanston
 * @author Dan Rossiter
 */
public class QuickMessageCBox extends JComboBox<QuickMessage>
{

	/** Prototype sign text */
	static private final QuickMessage PROTOTYPE_OBJ = new QuickMessage() {
		@Override
		public String getTypeName() {
			return QuickMessage.SONAR_TYPE;
		}

		@Override
		public String getName() {
			return "123456789012";
		}

		@Override
		public SignGroup getSignGroup() {
			throw new UnsupportedOperationException();
		}

		@Override
		public void setSignGroup(SignGroup sg) {
			throw new UnsupportedOperationException();
		}

		@Override
		public String getMulti() {
			throw new UnsupportedOperationException();
		}

		@Override
		public void setMulti(String multi) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void destroy() {
			throw new UnsupportedOperationException();
		}

		@Override
        public String toString() {
            return getName();
        }
	};

	/** Given a QuickMessage or String, return the corresponding quick
	 * message name or an empty string if none exists. */
	static private String getQuickLibMsgName(Object obj) {
		if (obj instanceof String)
			return (String) obj;
		else if (obj instanceof QuickMessage)
			return ((QuickMessage) obj).getName();
		else
			return "";
	}

	/** Combo box model for quick messages */
	private final DefaultComboBoxModel<QuickMessage> model =
		new DefaultComboBoxModel<>();

	/** DMS dispatcher */
	private final DMSDispatcher dispatcher;

	/** Item listener for combo box */
	private final ItemListener item_listener;

	/** Key listener for combo box */
	private final KeyListener key_listener;

    /** The combo box editor component */
	private final JTextField editor_component;

    /** The full message set */
	private TreeSet<QuickMessage> msgs;

	/** Counter to indicate we're adjusting widgets.  This needs to be
	 * incremented before calling dispatcher methods which might cause
	 * callbacks to this class.  This prevents infinite loops. */
	protected int adjusting = 0;

	/** Create a new quick message combo box */
	public QuickMessageCBox(DMSDispatcher d) {
		setModel(model);
		dispatcher = d;
		// Use a prototype display value so that the UI doesn't become
		// unusable when quick messages with long names are used.
		setPrototypeDisplayValue(PROTOTYPE_OBJ);
		key_listener = new KeyAdapter() {
			public void keyReleased(KeyEvent ke) {
				applyFilter();
			}
		};
		editor_component = (JTextField) getEditor().getEditorComponent();
		editor_component.addKeyListener(key_listener);
		setEditable(true);
		item_listener = new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    updateDispatcher();
                }
			}
		};
		addItemListener(item_listener);

		JTextField jtf = (JTextField)(getEditor()
			.getEditorComponent());
		if (SystemAttrEnum.DMS_QUICKMSG_UPPERCASE_NAMES.getBoolean())
			((AbstractDocument)jtf.getDocument())
				.setDocumentFilter(
				new UppercaseDocumentFilter());
	}

	/** Update the dispatcher with the selected quick message */
	protected void updateDispatcher() {
		QuickMessage qm = getSelectedProxy();
		if(qm != null)
			updateDispatcher(qm);
	}

	/** Get the currently selected proxy */
	protected QuickMessage getSelectedProxy() {
		Object obj = getSelectedItem();
		if(obj instanceof QuickMessage)
			return (QuickMessage)obj;
		else
			return null;
	}

	/** Update the dispatcher with the specified quick message */
	protected void updateDispatcher(QuickMessage qm) {
		String ms = qm.getMulti();
		if(adjusting == 0 && !ms.isEmpty()) {
			dispatcher.setMessage(ms);
			dispatcher.selectPreview(true);
		}
	}

	/** Set the current message MULTI string */
	public void setMessage(String ms) {
		adjusting++;
		if(ms.isEmpty())
			setSelectedItem(null);
		else
			setSelectedItem(QuickMessageHelper.find(ms));
		adjusting--;
	}

	/** Set selected item, but only if it is different from the 
	 * currently selected item. Triggers a call to actionPerformed().
	 * @param obj May be a String, or QuickMessage. */
	public void setSelectedItem(Object obj) {
		String nametoset = getQuickLibMsgName(obj);
		String namecur = getSelectedName();
		if (!namecur.equals(nametoset)) {
			if (nametoset.isEmpty())
				super.setSelectedItem(null);
			else {
				QuickMessage qm = QuickMessageHelper.lookup(
					nametoset);
				super.setSelectedItem(qm);
			}
		}
	}

	/** Get the name of the currently selected quick message */
	private String getSelectedName() {
		return getQuickLibMsgName(getSelectedItem());
	}

	/** Populate the quick message model, with sorted quick messages */
	public void populateModel(DMS dms) {
		msgs = createMessageSet(dms);
		adjusting++;
		model.removeAllElements();
		for (QuickMessage qm: msgs)
			model.addElement(qm);
		adjusting--;
	}

	/** Create a set of quick messages for the specified DMS */
	private TreeSet<QuickMessage> createMessageSet(DMS dms) {
		TreeSet<QuickMessage> msgs = new TreeSet<QuickMessage>(
			new NumericAlphaComparator<QuickMessage>());
		Iterator<DmsSignGroup> it = DmsSignGroupHelper.iterator();
		while (it.hasNext()) {
			DmsSignGroup dsg = it.next();
			if (dsg.getDms() == dms) {
				SignGroup sg = dsg.getSignGroup();
				Iterator<QuickMessage> qit =
					QuickMessageHelper.iterator();
				while (qit.hasNext()) {
					QuickMessage qm = qit.next();
					if (qm.getSignGroup() == sg)
						msgs.add(qm);
				}
			}
		}
		return msgs;
	}

	/** Filters combo box members based on typed text. */
	private void applyFilter() {
		adjusting++;
		if (!isPopupVisible()) {
			showPopup();
		}

		QuickMessage selected = getSelectedProxy();
        setSelectedIndex(-1);

		// find all QM with names containing typed text (case insensitive)
		String enteredText = editor_component.getText().toLowerCase();
		for (QuickMessage msg : msgs) {
			if (!msg.getName().toLowerCase().contains(enteredText)) {
				model.removeElement(msg);
			} else if (model.getIndexOf(msg) == -1) {
			    // insert does not set selection if selection is null, unlike add.
				model.insertElementAt(msg, model.getSize());
			}
		}

		// if only one item, go ahead and select it
		if (model.getSize() == 1)
		    setSelectedIndex(0);

        // if selection changed, trigger dispatcher update
        if (selected != getSelectedProxy())
            updateDispatcher();
        adjusting--;
	}

	/** Set the enabled status */
	@Override
	public void setEnabled(boolean e) {
		super.setEnabled(e);
		if(!e) {
			setSelectedItem(null);
			removeAllItems();
		}
	}

	/** Dispose */
	public void dispose() {
		removeItemListener(item_listener);
		editor_component.removeKeyListener(key_listener);
	}
}
