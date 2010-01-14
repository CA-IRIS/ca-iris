/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2000-2010  Minnesota Department of Transportation
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

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.util.Iterator;
import java.util.List;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import us.mn.state.dot.sched.ActionJob;
import us.mn.state.dot.sonar.Name;
import us.mn.state.dot.sonar.Namespace;
import us.mn.state.dot.sonar.User;
import us.mn.state.dot.tms.Base64;
import us.mn.state.dot.tms.BitmapGraphic;
import us.mn.state.dot.tms.DeviceRequest;
import us.mn.state.dot.tms.DMS;
import us.mn.state.dot.tms.DMSHelper;
import us.mn.state.dot.tms.DMSMessagePriority;
import us.mn.state.dot.tms.MultiString;
import us.mn.state.dot.tms.PixelMapBuilder;
import us.mn.state.dot.tms.SignMessage;
import us.mn.state.dot.tms.SystemAttrEnum;
import us.mn.state.dot.tms.client.Session;
import us.mn.state.dot.tms.client.SonarState;
import us.mn.state.dot.tms.client.proxy.ProxySelectionListener;
import us.mn.state.dot.tms.client.proxy.ProxySelectionModel;
import us.mn.state.dot.tms.client.toast.FormPanel;
import us.mn.state.dot.tms.client.toast.WrapperComboBoxModel;
import us.mn.state.dot.tms.client.widget.IButton;
import us.mn.state.dot.tms.utils.I18N;

/**
 * The DMSDispatcher is a GUI component for creating and deploying DMS messages.
 * It contains several other components and keeps their state synchronized.
 * It uses a number of optional controls which appear or do not appear on screen
 * as a function of system attributes.
 * @see SignMessage, DMSPanelPager, SignMessageComposer
 *
 * @author Erik Engstrom
 * @author Douglas Lau
 * @author Michael Darter
 */
public class DMSDispatcher extends JPanel implements ProxySelectionListener<DMS>
{
	/** SONAR namespace */
	protected final Namespace namespace;

	/** Selection model */
	protected final ProxySelectionModel<DMS> selectionModel;

	/** Selection tab pane */
	protected final JTabbedPane tabPane = new JTabbedPane();

	/** Single sign tab */
	protected final SingleSignTab singleTab;

	/** Multiple sign tab */
	protected final MultipleSignTab multipleTab;

	/** Message composer widget */
	protected final SignMessageComposer composer;

	/** Used to select the expires time for a message (optional) */
	protected final JComboBox durationCmb =
		new JComboBox(Expiration.values());

	/** Combobox used to select a quick library message (optional). */
	protected final QLibCBox qlibCmb;

	/** Button used to send a message to the DMS */
	protected final IButton sendBtn = new IButton("dms.send");

	/** Button used to blank the DMS */
	protected final JButton blankBtn = new IButton("dms.blank");

	/** Action to blank selected DMS */
	protected final BlankDmsAction blankAction;

	/** Button to query the DMS message (optional) */
	protected final IButton queryBtn = new IButton("dms.query.msg",
		SystemAttrEnum.DMS_QUERYMSG_ENABLE);

	/** Card layout for alert panel */
	protected final CardLayout cards = new CardLayout();

	/** Card panel for alert panels */
	protected final JPanel card_panel = new JPanel(cards);

	/** AMBER Alert checkbox */
	protected final JCheckBox alertCbx =
		new JCheckBox(I18N.get("dms.alert"));

	/** Currently logged in user */
	protected final User user;

	/** Sign message creator */
	protected final SignMessageCreator creator;

	/** Pixel map builder */
	protected PixelMapBuilder builder;

	/** Current message MULTI string */
	protected String message = "";

	/** Create a new DMS dispatcher */
	public DMSDispatcher(Session session, DMSManager manager) {
		super(new BorderLayout());
		SonarState st = session.getSonarState();
		DmsCache dms_cache = st.getDmsCache();
		namespace = st.getNamespace();
		user = session.getUser();
		creator = new SignMessageCreator(st, user);
		selectionModel = manager.getSelectionModel();
		blankAction = new BlankDmsAction(selectionModel, this, user);
		qlibCmb = new QLibCBox(this);
		qlibCmb.setModel(new WrapperComboBoxModel(
			st.getDmsCache().getQuickMessageModel()));
		blankBtn.setAction(blankAction);
		manager.setBlankAction(blankAction);
		composer = new SignMessageComposer(session, this);
		singleTab = new SingleSignTab(this, dms_cache.getDMSs());
		multipleTab = new MultipleSignTab(dms_cache, selectionModel);
		tabPane.addTab("Single", singleTab);
		tabPane.addTab("Multiple", multipleTab);
		add(tabPane, BorderLayout.CENTER);
		add(createDeployBox(), BorderLayout.SOUTH);
		clearSelected();
		selectionModel.addProxySelectionListener(this);
	}

	/** Create a component to deploy signs */
	protected Box createDeployBox() {
		durationCmb.setSelectedIndex(0);
		FormPanel panel = new FormPanel(true);
		if(SystemAttrEnum.DMS_DURATION_ENABLE.getBoolean())
			panel.addRow("Duration", durationCmb);
		panel.addRow(card_panel);
		card_panel.add(new JLabel(), "Blank");
		card_panel.add(alertCbx, "Alert");
		panel.setCenter();
		if(QLibCBox.getIEnabled())
			panel.addRow(buildQuickLibPanel());
		panel.addRow(buildButtonPanel());
		Box deployBox = Box.createHorizontalBox();
		deployBox.add(composer);
		deployBox.add(panel);
		return deployBox;
	}

	/** Get the selected DMS (if a single sign is selected) */
	protected DMS getSingleSelection() {
		if(selectionModel.getSelectedCount() == 1) {
			for(DMS dms: selectionModel.getSelected())
				return dms;
		}
		return null;
	}

	/** Dispose of the dispatcher */
	public void dispose() {
		selectionModel.removeProxySelectionListener(this);
		clearSelected();
		singleTab.dispose();
		multipleTab.dispose();
		composer.dispose();
		qlibCmb.dispose();
		removeAll();
	}

	/** Build the quick lib panel */
	protected Box buildQuickLibPanel() {
		Box box = Box.createHorizontalBox();
		box.add(Box.createHorizontalGlue());
		JLabel label = new JLabel();
		label.setLabelFor(qlibCmb);
		label.setDisplayedMnemonic('Q');
		label.setText("<html><p align=\"right\"><u>Q</u>uick" +
			"<br>Message</p></html>");
		box.add(label);
		box.add(box.createHorizontalStrut(4));
		box.add(qlibCmb);
		return box;
	}

	/** Build the button panel */
	protected Box buildButtonPanel() {
		new ActionJob(sendBtn) {
			public void perform() {
				if(sendConfirm())
					sendMessage();
			}
		};
		new ActionJob(queryBtn) {
			public void perform() {
				queryMessage();
			}
		};
		Box box = Box.createHorizontalBox();
		box.add(Box.createHorizontalGlue());
		box.add(sendBtn);
		box.add(Box.createHorizontalStrut(4));
		box.add(blankBtn);
		if(queryBtn.getIEnabled()) {
			box.add(Box.createHorizontalStrut(4));
			box.add(queryBtn);
		}
		box.add(Box.createHorizontalGlue());
		return box;
	}

	/** If enabled, prompt the user with a send confirmation.
	 *  @return True to send the message else false to cancel. */
	protected boolean sendConfirm() {
		// send confirmation is not enabled
		if(!SystemAttrEnum.DMS_SEND_CONFIRMATION_ENABLE.getBoolean())
			return true;
		int res = 0;
		try {
			String m = buildConfirmMsg();
			if(!m.isEmpty())
				res = JOptionPane.showConfirmDialog(null, m, 
					"Send Confirmation",
					JOptionPane.OK_CANCEL_OPTION);
		} catch(Exception ex) {}
		return res == 0;
	}

	/** Return a string which is a list of selected DMS.
	 *  @return An empty string if no DMS selected else the message. */
	protected String buildConfirmMsg() {
		StringBuilder ret = new StringBuilder("Send message to ");
		List<DMS> sel = selectionModel.getSelected();
		if(sel.size() <= 0)
			return "";
		if(sel.size() == 1)
			ret.append(((DMS)sel.get(0)).getName());
		else {
			for(Iterator iter = sel.iterator(); iter.hasNext();) {
			   DMS dms = (DMS) iter.next();
				if(checkDimensions(dms))
					ret.append(dms.getName());
				if(iter.hasNext())
					ret.append(", ");
			}
		}
		ret.append("?");
		return ret.toString();
	}

	/** Called whenever a sign is added to the selection */
	public void selectionAdded(DMS s) {
		updateSelected();
	}

	/** Called whenever a sign is removed from the selection */
	public void selectionRemoved(DMS s) {
		updateSelected();
	}

	/** Update the selected sign(s) */
	protected void updateSelected() {
		List<DMS> selected = selectionModel.getSelected();
		if(selected.size() == 0)
			clearSelected();
		else if(selected.size() == 1) {
			for(DMS dms: selected)
				setSelected(dms);
		} else {
			// FIXME: fix multi-selection problems
			singleTab.clearSelected();
			setMessage("");
			enableWidgets();
			selectMultipleTab();
		}
	}

	/** Clear the selection */
	protected void clearSelected() {
		disableWidgets();
		singleTab.clearSelected();
		selectSingleTab();
	}

	/** Set a single selected DMS */
	protected void setSelected(DMS dms) {
		if(DMSHelper.isActive(dms)) {
			builder = DMSHelper.createPixelMapBuilder(dms);
			composer.setSign(dms, builder);
			enableWidgets();
			SignMessage sm = dms.getMessageCurrent();
			if(sm != null)
				setMessage(sm.getMulti());
		} else
			disableWidgets();
		singleTab.setSelected(dms);
		selectSingleTab();
	}

	/** Select the single selection tab */
	protected void selectSingleTab() {
		if(tabPane.getSelectedComponent() != singleTab) {
			alertCbx.setSelected(false);
			tabPane.setSelectedComponent(singleTab);
		}
		cards.show(card_panel, "Blank");
	}

	/** Select the multiple selection tab */
	protected void selectMultipleTab() {
		if(tabPane.getSelectedComponent() != multipleTab)
			tabPane.setSelectedComponent(multipleTab);
		cards.show(card_panel, "Alert");
		qlibCmb.setSelectedItem("");
	}

	/** Disable the dispatcher widgets */
	protected void disableWidgets() {
		composer.setEnabled(false);
		composer.clearSelections();
		durationCmb.setEnabled(false);
		durationCmb.setSelectedItem(null);
		sendBtn.setEnabled(false);
		blankBtn.setEnabled(false);
		queryBtn.setEnabled(false);
		qlibCmb.setEnabled(false);
		qlibCmb.setSelectedItem("");
		builder = null;
	}

	/** Enable the dispatcher widgets */
	protected void enableWidgets() {
		composer.setEnabled(true);
		durationCmb.setEnabled(true);
		durationCmb.setSelectedIndex(0);
		sendBtn.setEnabled(canSend());
		blankBtn.setEnabled(canSend());
		queryBtn.setEnabled(canRequest());
		qlibCmb.setEnabled(true);
		selectPreview(false);
	}

	/** Set the fully composed message.  This will update all the widgets
	 * on the dispatcher with the specified message. */
	public void setMessage(String ms) {
		if(ms != null) {
			message = ms;
			singleTab.setMessage();
			composer.setMessage(ms);
			qlibCmb.setMessage(ms);
		}
	}

	/** Get the current selected message */
	public String getMessage() {
		return message;
	}

	/** Send a new message to the selected DMS */
	protected void sendMessage() {
		List<DMS> sel = selectionModel.getSelected();
		if(sel.size() > 0) {
			SignMessage sm = createMessage();
			if(sm != null)
				sendMessage(sm, sel);
			composer.updateMessageLibrary();
			selectPreview(false);
		}
	}

	/** Send a message to a list of signs */
	protected void sendMessage(SignMessage sm, List<DMS> sel) {
		for(DMS dms: sel) {
			if(checkDimensions(dms)) {
				dms.setOwnerNext(user);
				dms.setMessageNext(sm);
			} else {
				// NOTE: this sign does not match the proper
				//       dimensions, so deselect it.
				selectionModel.removeSelected(dms);
			}
		}
	}

	/** Query the current message on all selected signs */
	private void queryMessage() {
		List<DMS> sel = selectionModel.getSelected();
		if(sel.size() < 1)
			return;
		for(DMS dms: sel) {
			if(checkDimensions(dms)) {
				dms.setDeviceRequest(DeviceRequest.
					QUERY_MESSAGE.ordinal());
			} else {
				// NOTE: this sign does not match the proper
				//       dimensions, so deselect it.
				selectionModel.removeSelected(dms);
			}
		}
		composer.updateMessageLibrary();
		selectPreview(false);
	}

	/** Create a new message from the widgets.
	 * @return A newly created SignMessage else null. */
	protected SignMessage createMessage() {
		String multi = message;	// Avoid races
		if(multi.isEmpty())
			return null;
		else
			return createMessage(multi);
	}

	/** Create a new message using the specified MULTI */
	protected SignMessage createMessage(String multi) {
		String bitmaps = createBitmaps(multi);
		if(bitmaps != null) {
			return creator.create(multi, bitmaps, getPriority(),
				getPriority(), getDuration());
		} else
			return null;
	}

	/** Create a new blank message */
	protected SignMessage createBlankMessage() {
		String multi = "";
		String bitmaps = createBitmaps(multi);
		if(bitmaps != null) {
			return creator.create(multi, bitmaps,
			       DMSMessagePriority.OVERRIDE,
			       DMSMessagePriority.BLANK, null);
		} else
			return null;
	}

	/** Create bitmap graphics for a MULTI string */
	protected String createBitmaps(String multi) {
		PixelMapBuilder b = builder;
		if(b != null) {
			MultiString ms = new MultiString(multi);
			return encodeBitmaps(b.createPixmaps(ms));
		} else
			return null;
	}

	/** Encode the bitmaps to Base64 */
	protected String encodeBitmaps(BitmapGraphic[] bmaps) {
		int blen = bmaps[0].length();
		byte[] bitmaps = new byte[bmaps.length * blen];
		for(int i = 0; i < bmaps.length; i++) {
			byte[] pix = bmaps[i].getPixels();
			System.arraycopy(pix, 0, bitmaps, i * blen, blen);
		}
		return Base64.encode(bitmaps);
	}

	/** Get the selected priority */
	protected DMSMessagePriority getPriority() {
		if(alertCbx.isSelected())
		       return DMSMessagePriority.ALERT;
		else
		       return DMSMessagePriority.OPERATOR;
	}

	/** Get the selected duration */
	protected Integer getDuration() {
		Expiration e = (Expiration)durationCmb.getSelectedItem();
		if(e != null)
			return e.duration;
		else
			return null;
	}

	/** Check the dimensions of a sign against the pixel map builder */
	protected boolean checkDimensions(DMS dms) {
		PixelMapBuilder b = builder;
		if(b != null) {
			Integer w = dms.getWidthPixels();
			Integer h = dms.getHeightPixels();
			if(w != null && h != null)
				return b.width == w && b.height == h;
		}
		return false;
	}

	/** Select the preview mode */
	public void selectPreview(boolean p) {
		singleTab.selectPreview(p);
	}

	/** Get the bitmap graphic array for the current message */
	public BitmapGraphic[] getBitmaps() {
		PixelMapBuilder b = builder;
		if(b != null) {
			MultiString multi = new MultiString(message);
			try {
				return b.createPixmaps(multi);
			}
			catch(ArrayIndexOutOfBoundsException e) {
				// oh well, no graphic to display
			}
		}
		return null;
	}

	/** Can a message be sent to all selected DMS? */
	public boolean canSend() {
		List<DMS> sel = selectionModel.getSelected();
		if(sel.isEmpty())
			return false;
		for(DMS dms: sel) {
			if(!canSend(dms))
				return false;
		}
		return true;
	}

	/** Can a message be sent to the specified DMS? */
	public boolean canSend(DMS dms) {
		return dms != null &&
		       namespace.canUpdate(user, new Name(dms, "ownerNext")) &&
		       namespace.canUpdate(user, new Name(dms, "messageNext"));
	}

	/** Can a device request be sent to all selected DMS? */
	public boolean canRequest() {
		List<DMS> sel = selectionModel.getSelected();
		if(sel.isEmpty())
			return false;
		for(DMS dms: sel) {
			if(!canRequest(dms))
				return false;
		}
		return true;
	}

	/** Can a device request be sent to the specified DMS? */
	public boolean canRequest(DMS dms) {
		return dms != null && namespace.canUpdate(user,
			new Name(dms, "deviceRequest"));
	}

	/** Check if AWS is allowed and user has permission to change */
	public boolean isAwsPermitted(DMS dms) {
		Name name = new Name(dms, "awsControlled");
		return dms.getAwsAllowed() && namespace.canUpdate(user, name);
	}
}
